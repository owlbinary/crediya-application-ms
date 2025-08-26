package co.com.crediya.usecase.registrarsolicitud;

import co.com.crediya.model.exception.DocumentoNoValidoException;
import co.com.crediya.model.exception.TipoPrestamoNoExisteException;
import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.model.gateway.ValidacionDocumentoGateway;
import co.com.crediya.model.Solicitud;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Caso de uso para registrar nuevas solicitudes de préstamo.
 */
@RequiredArgsConstructor
public class RegistrarSolicitudUseCase {
    
    private final SolicitudGateway solicitudRepository;
    private final ValidacionDocumentoGateway validacionDocumentoGateway;
    
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
        
        try {
            Solicitud.validarDatosParaCreacion(documentoIdentidad, monto, plazo, tipoPrestamoId);
        } catch (Exception e) {
            return Mono.error(e);
        }
        
        return validarDocumentoEnSistema(documentoIdentidad, authorizationToken)
            .then(validarDisponibilidadTipoPrestamo(tipoPrestamoId))
            .then(crearYPersistirSolicitud(documentoIdentidad, monto, plazo, tipoPrestamoId));
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
    
    private Mono<Solicitud> crearYPersistirSolicitud(String documentoIdentidad, BigDecimal monto, 
                                                    Integer plazo, String tipoPrestamoId) {
        return Mono.fromCallable(() -> Solicitud.crearNueva(documentoIdentidad, monto, plazo, tipoPrestamoId))
            .flatMap(solicitudRepository::guardar);
    }
}