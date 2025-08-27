package co.com.crediya.model.gateway;

import co.com.crediya.model.DetalleUsuario;
import co.com.crediya.model.ValidacionDocumento;
import reactor.core.publisher.Mono;

/**
 * Gateway para validaciones de documentos de identidad.
 */
public interface ValidacionDocumentoGateway {
    /**
     * Valida si un documento de identidad existe en el sistema.
     * 
     * @param documentoIdentidad Documento de identidad a validar
     * @param authorizationToken Token de autorización para la petición
     * @return Mono con el resultado de la validación
     */
    Mono<ValidacionDocumento> validarDocumento(String documentoIdentidad, String authorizationToken);
    
    /**
     * Obtiene el detalle de un usuario por su documento de identidad.
     * 
     * @param documentoIdentidad Documento de identidad del usuario
     * @param authorizationToken Token de autorización para la petición
     * @return Mono con el detalle del usuario
     */
    Mono<DetalleUsuario> obtenerDetalleUsuario(String documentoIdentidad, String authorizationToken);
}