package co.com.crediya.api.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import co.com.crediya.api.constants.ErrorCodes;
import co.com.crediya.api.dto.response.ErrorResponse;
import co.com.crediya.model.exception.AutenticacionException;
import co.com.crediya.model.exception.DocumentoNoValidoException;
import co.com.crediya.model.exception.InfraestructuraException;
import co.com.crediya.model.exception.TipoPrestamoNoExisteException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TipoPrestamoNoExisteException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ErrorResponse> procesarTipoPrestamoInexistente(TipoPrestamoNoExisteException excepcion, ServerWebExchange intercambio) {
        log.warn("Tipo de préstamo no disponible: {}", excepcion.getMessage());
        
        return Mono.just(ErrorResponse.of(
            ErrorCodes.TIPO_PRESTAMO_NO_EXISTE,
            excepcion.getMessage(),
            intercambio.getRequest().getPath().value()
        ));
    }

    @ExceptionHandler(DocumentoNoValidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponse> procesarDocumentoNoValido(DocumentoNoValidoException excepcion, ServerWebExchange intercambio) {
        log.warn("Documento no válido: {}", excepcion.getMessage());
        
        return Mono.just(ErrorResponse.of(
            ErrorCodes.VALIDACION_FALLIDA,
            excepcion.getMessage(),
            intercambio.getRequest().getPath().value()
        ));
    }

    @ExceptionHandler(AutenticacionException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Mono<ErrorResponse> procesarErrorAutenticacion(AutenticacionException excepcion, ServerWebExchange intercambio) {
        log.error("Error de autenticación en servicio externo: {}", excepcion.getMessage());
        
        return Mono.just(ErrorResponse.of(
            ErrorCodes.ERROR_INTERNO,
            "Error de autenticación con servicio externo",
            intercambio.getRequest().getPath().value()
        ));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponse> procesarErroresValidacion(WebExchangeBindException excepcion, ServerWebExchange intercambio) {
        log.warn("Fallos en validación de campos: {}", excepcion.getMessage());
        
        String mensajeError = excepcion.getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getDefaultMessage())
            .orElse("Error en validación de datos de entrada");
        
        return Mono.just(ErrorResponse.of(
            ErrorCodes.VALIDACION_FALLIDA,
            mensajeError,
            intercambio.getRequest().getPath().value()
        ));
    }

    @ExceptionHandler(InfraestructuraException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorResponse> procesarErrorInfraestructura(InfraestructuraException excepcion, ServerWebExchange intercambio) {
        log.error("Error de infraestructura: {}", excepcion.getMessage(), excepcion);
        
        return Mono.just(ErrorResponse.of(
            ErrorCodes.ERROR_INTERNO,
            "Error en infraestructura",
            intercambio.getRequest().getPath().value()
        ));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorResponse> procesarErrorGenerico(Exception excepcion, ServerWebExchange intercambio) {
        log.error("Error no controlado en aplicación: {}", excepcion.getMessage(), excepcion);
        
        return Mono.just(ErrorResponse.of(
            ErrorCodes.ERROR_INTERNO,
            "Se presentó un fallo interno en el sistema",
            intercambio.getRequest().getPath().value()
        ));
    }
}