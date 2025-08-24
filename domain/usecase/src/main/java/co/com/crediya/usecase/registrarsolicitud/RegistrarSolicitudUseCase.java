package co.com.crediya.usecase.registrarsolicitud;

import co.com.crediya.model.exception.DatosInvalidosException;
import co.com.crediya.model.exception.TipoPrestamoNoExisteException;
import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.model.Solicitud;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Caso de uso para registrar nuevas solicitudes de préstamo.
 * Implementa las reglas de negocio para la creación de solicitudes.
 */
@RequiredArgsConstructor
public class RegistrarSolicitudUseCase {
    
    private final SolicitudGateway solicitudRepository;
    
    /**
     * Ejecuta el proceso de registro de una nueva solicitud.
     * 
     * @param documentoIdentidad Documento de identidad del solicitante
     * @param monto Monto solicitado para el préstamo
     * @param plazo Plazo en meses para el préstamo
     * @param tipoPrestamoId Identificador del tipo de préstamo
     * @return Mono con la solicitud creada
     */
    public Mono<Solicitud> ejecutar(String documentoIdentidad, BigDecimal monto, 
                                   Integer plazo, String tipoPrestamoId) {
        
        try {
            validarDatosEntrada(documentoIdentidad, monto, plazo, tipoPrestamoId);
        } catch (Exception e) {
            return Mono.error(e);
        }
        
        return validarReglasNegocio(tipoPrestamoId)
            .then(crearYPersistirSolicitud(documentoIdentidad, monto, plazo, tipoPrestamoId));
    }
    
    private void validarDatosEntrada(String documentoIdentidad, BigDecimal monto, 
                                   Integer plazo, String tipoPrestamoId) {
        validarDocumentoIdentidad(documentoIdentidad);
        validarMonto(monto);
        validarPlazo(plazo);
        validarTipoPrestamoId(tipoPrestamoId);
    }
    
    private void validarDocumentoIdentidad(String documentoIdentidad) {
        if (documentoIdentidad == null || documentoIdentidad.trim().isEmpty()) {
            throw new DatosInvalidosException("Documento de identidad es obligatorio");
        }
    }
    
    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DatosInvalidosException("El monto debe ser mayor a cero");
        }
    }
    
    private void validarPlazo(Integer plazo) {
        if (plazo == null || plazo <= 0) {
            throw new DatosInvalidosException("El plazo en meses debe ser mayor a cero");
        }
    }
    
    private void validarTipoPrestamoId(String tipoPrestamoId) {
        if (tipoPrestamoId == null || tipoPrestamoId.trim().isEmpty()) {
            throw new DatosInvalidosException("Tipo de préstamo es obligatorio");
        }
    }
    
    private Mono<Void> validarReglasNegocio(String tipoPrestamoId) {
        return validarDisponibilidadTipoPrestamo(tipoPrestamoId);
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
    
    private Mono<Solicitud> crearYPersistirSolicitud(String documentoIdentidad, BigDecimal monto, 
                                                    Integer plazo, String tipoPrestamoId) {
        return Mono.fromCallable(() -> Solicitud.crearNueva(documentoIdentidad, monto, plazo, tipoPrestamoId))
            .flatMap(solicitudRepository::guardar);
    }
}