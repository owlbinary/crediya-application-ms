package co.com.crediya.usecase.registrarsolicitud;

import co.com.crediya.model.exception.DocumentoNoValidoException;
import co.com.crediya.model.exception.DatosInvalidosException;
import co.com.crediya.model.exception.TipoPrestamoNoExisteException;
import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.model.gateway.TipoPrestamoGateway;
import co.com.crediya.model.gateway.ValidacionDocumentoGateway;
import co.com.crediya.model.gateway.NotificacionGateway;
import co.com.crediya.model.gateway.DebtCapacityEventGateway;
import co.com.crediya.model.EstadoSolicitud;
import co.com.crediya.model.Solicitud;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Caso de uso para registrar nuevas solicitudes de préstamo.
 */
@RequiredArgsConstructor
public class RegistrarSolicitudUseCase {

    private final SolicitudGateway solicitudRepository;
    private final ValidacionDocumentoGateway validacionDocumentoGateway;
    private final TipoPrestamoGateway tipoPrestamoGateway;
    private final NotificacionGateway notificacionGateway;
    private final DebtCapacityEventGateway debtCapacityEventGateway;

    private static final EstadoSolicitud ESTADO_APROBADO = EstadoSolicitud.APROBADO;

    /**
     * Ejecuta el caso de uso de registro de solicitud.
     * 
     * @param documentoIdentidad Documento de identidad del solicitante
     * @param monto Monto solicitado
     * @param plazo Plazo en meses
     * @param tipoPrestamoId Tipo de préstamo
     * @param authorizationToken Token de autorización
     * @return Mono con la solicitud creada
     */
    public Mono<Solicitud> ejecutar(String documentoIdentidad, BigDecimal monto,
                                   Integer plazo, String tipoPrestamoId, String authorizationToken) {

        if (tipoPrestamoId == null) {
            return Mono.error(new DatosInvalidosException("El tipo de préstamo no puede ser nulo"));
        }
        return validarDocumentoEnSistema(documentoIdentidad, authorizationToken)
            .then(validarDisponibilidadTipoPrestamo(tipoPrestamoId))
            .then(validacionDocumentoGateway.obtenerDetalleUsuario(documentoIdentidad, authorizationToken))
            .flatMap(detalleUsuario -> {
                try {
                    Solicitud.validarDatosParaCreacion(documentoIdentidad, detalleUsuario.getEmail(), monto, plazo, tipoPrestamoId);
                } catch (Exception e) {
                    return Mono.error(e);
                }
                
                return calcularDeudaTotalMensual(documentoIdentidad)
                    .flatMap(deudaTotalMensual -> {
                        Solicitud nueva = Solicitud.crearNueva(documentoIdentidad, detalleUsuario.getEmail(), monto, plazo, tipoPrestamoId);
                        nueva.setDeudaTotalMensual(deudaTotalMensual);
                        return solicitudRepository.guardar(nueva);
                    })
                    .flatMap(solicitud ->
                        tipoPrestamoGateway.buscarPorId(tipoPrestamoId)
                            .flatMap(tipoPrestamo -> {
                                if (Boolean.TRUE.equals(tipoPrestamo.getValidacionAutomatica())) {
                                    return debtCapacityEventGateway.enviarEvaluacionCapacidadEndeudamiento(solicitud, detalleUsuario, tipoPrestamo)
                                        .then(notificacionGateway.enviarValidacionAutomaticaSolicitud(solicitud, detalleUsuario, tipoPrestamo, authorizationToken))
                                        .thenReturn(solicitud);
                                }
                                return Mono.just(solicitud);
                            })
                    );
            });
    }
    
    private Mono<Void> validarDocumentoEnSistema(String documentoIdentidad, String authorizationToken) {
        return validacionDocumentoGateway.validarDocumento(documentoIdentidad, authorizationToken)
            .flatMap(validacion -> {
                if (Boolean.FALSE.equals(validacion.getExiste())) {
                    return Mono.error(new DocumentoNoValidoException(validacion.getMensaje()));
                }
                return Mono.empty();
            });
    }
    
    private Mono<Void> validarDisponibilidadTipoPrestamo(String tipoPrestamoId) {
        return solicitudRepository.existeTipoPrestamo(tipoPrestamoId)
            .flatMap(existe -> {
                if (Boolean.FALSE.equals(existe)) {
                    return Mono.error(new TipoPrestamoNoExisteException(tipoPrestamoId));
                }
                return Mono.empty();
            });
    }
    
    private Mono<BigDecimal> calcularDeudaTotalMensual(String documentoIdentidad) {
        return solicitudRepository.findByDocumentoIdentidadAndEstado(documentoIdentidad, ESTADO_APROBADO)
            .flatMap(this::calcularCuotaMensualConTipoPrestamo)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    Mono<BigDecimal> calcularCuotaMensualConTipoPrestamo(co.com.crediya.model.Solicitud solicitud) {
        BigDecimal monto = solicitud.getMonto();
        Integer plazo = solicitud.getPlazo();
        String tipoPrestamoId = solicitud.getTipoPrestamoId();
        if (monto == null || plazo == null || plazo == 0 || tipoPrestamoId == null) {
            return Mono.just(BigDecimal.ZERO);
        }
        return tipoPrestamoGateway.buscarPorId(tipoPrestamoId)
            .map(tipoPrestamo -> {
                BigDecimal tasaMensual = tipoPrestamo.getTasaInteres();
                if (tasaMensual == null || tasaMensual.compareTo(BigDecimal.ZERO) == 0) {
                    return BigDecimal.ZERO;
                }
                BigDecimal unoMasR = tasaMensual.add(BigDecimal.ONE);
                BigDecimal pow = unoMasR.pow(plazo);
                BigDecimal numerador = monto.multiply(tasaMensual).multiply(pow);
                BigDecimal denominador = pow.subtract(BigDecimal.ONE);
                if (denominador.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
                return numerador.divide(denominador, 2, RoundingMode.HALF_UP);
            })
            .defaultIfEmpty(BigDecimal.ZERO);
    }
}