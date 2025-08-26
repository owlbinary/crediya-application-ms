package co.com.crediya.validacion.adapter;

import co.com.crediya.model.ValidacionDocumento;
import co.com.crediya.model.gateway.ValidacionDocumentoGateway;
import co.com.crediya.model.exception.AutenticacionException;
import co.com.crediya.model.exception.DocumentoNoValidoException;
import co.com.crediya.model.exception.InfraestructuraException;
import co.com.crediya.validacion.dto.ValidacionDocumentoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Adapter para validación de documentos usando servicios externos.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ValidacionDocumentoAdapter implements ValidacionDocumentoGateway {
    
    @Qualifier("validacionWebClient")
    private final WebClient webClient;
    
    @Value("${validacion.documento.url}")
    private String validacionDocumentoUrl;
    
    private static final String ENDPOINT_VALIDACION = "/api/v1/validaciones/documento/{documentoIdentidad}";
    private static final String MENSAJE_INICIANDO_VALIDACION = "Iniciando validación de documento: {}";
    private static final String MENSAJE_VALIDACION_EXITOSA = "Validación exitosa para documento: {} - Existe: {}";
    private static final String MENSAJE_ERROR_VALIDACION = "Error al validar documento {}: {}";
    
    @Override
    public Mono<ValidacionDocumento> validarDocumento(String documentoIdentidad, String authorizationToken) {
        log.debug(MENSAJE_INICIANDO_VALIDACION, documentoIdentidad);
        
        String fullUrl = validacionDocumentoUrl + ENDPOINT_VALIDACION;
        log.info("URL completa para validación: {}", fullUrl);
        
        WebClient.RequestHeadersSpec<?> requestSpec = webClient.get()
            .uri(fullUrl, documentoIdentidad);
            
        if (authorizationToken != null && !authorizationToken.trim().isEmpty()) {
            log.info("Agregando token de autorización desde petición: {}", 
                authorizationToken.substring(0, Math.min(20, authorizationToken.length())) + "...");
            requestSpec = requestSpec.headers(httpHeaders -> 
                httpHeaders.set("Authorization", authorizationToken)
            );
        } else {
            log.warn("No hay token de autenticación en la petición para el servicio de validación");
        }
        
        return requestSpec
            .retrieve()
            .bodyToMono(ValidacionDocumentoResponse.class)
            .map(this::mapearRespuesta)
            .doOnNext(validacion -> 
                log.info(MENSAJE_VALIDACION_EXITOSA, validacion.getDocumentoIdentidad(), validacion.getExiste()))
            .doOnError(excepcion -> 
                log.error(MENSAJE_ERROR_VALIDACION, documentoIdentidad, excepcion.getMessage()))
            .onErrorMap(WebClientResponseException.class, this::mapearExcepcionHttp)
            .onErrorMap(excepcion -> !(excepcion instanceof AutenticacionException) && !(excepcion instanceof DocumentoNoValidoException), 
                excepcion -> new InfraestructuraException("Error al comunicarse con el servicio de validación", excepcion));
    }
    
    private ValidacionDocumento mapearRespuesta(ValidacionDocumentoResponse response) {
        return ValidacionDocumento.builder()
            .documentoIdentidad(response.getDocumentoIdentidad())
            .existe(response.getExiste())
            .mensaje(response.getMensaje())
            .build();
    }
    
    private RuntimeException mapearExcepcionHttp(WebClientResponseException excepcion) {
        HttpStatus status = HttpStatus.valueOf(excepcion.getStatusCode().value());
        String responseBody = excepcion.getResponseBodyAsString();
        
        log.error("Error HTTP en servicio de validación - Status: {}, Body: {}, Headers: {}", 
            status, responseBody, excepcion.getHeaders());
        
        if (status == HttpStatus.UNAUTHORIZED) {
            log.error("Error de autenticación en servicio de validación: Token inválido o expirado");
            return new AutenticacionException("Error de autenticación: Token inválido o expirado", excepcion);
        }
        
        if (status == HttpStatus.FORBIDDEN) {
            log.error("Error de autorización en servicio de validación: Token sin permisos suficientes");
            return new AutenticacionException("Error de autorización: Token sin permisos suficientes", excepcion);
        }
        
        String mensaje = String.format("Error en servicio de validación - Código: %d, Respuesta: %s", 
            excepcion.getStatusCode().value(), excepcion.getResponseBodyAsString());
        return new InfraestructuraException(mensaje, excepcion);
    }
}
