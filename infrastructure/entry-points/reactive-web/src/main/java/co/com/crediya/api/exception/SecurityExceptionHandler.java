package co.com.crediya.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Manejador global de excepciones de seguridad.
 */
@Slf4j
@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleAuthenticationException(
            AuthenticationException ex) {
        log.warn("Error de autenticación: {}", ex.getMessage());
        
        Map<String, Object> error = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "error", "No autorizado",
                "message", "Token de autenticación inválido o expirado",
                "path", "/api/v1/solicitudes"
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleAccessDeniedException(
            AccessDeniedException ex) {
        log.warn("Error de autorización: {}", ex.getMessage());
        
        Map<String, Object> error = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.FORBIDDEN.value(),
                "error", "Acceso denegado",
                "message", "No tiene permisos suficientes para acceder a este recurso",
                "path", "/api/v1/solicitudes"
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(error));
    }
}
