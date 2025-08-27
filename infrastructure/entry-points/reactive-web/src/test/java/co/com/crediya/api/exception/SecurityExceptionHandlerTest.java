package co.com.crediya.api.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityExceptionHandler - Manejo de Excepciones de Seguridad")
class SecurityExceptionHandlerTest {

    private SecurityExceptionHandler securityExceptionHandler;

    @BeforeEach
    void setUp() {
        securityExceptionHandler = new SecurityExceptionHandler();
    }

    @Test
    @DisplayName("Debe manejar correctamente AuthenticationException")
    void debeManejarcorrectamenteAuthenticationException() {
        AuthenticationException exception = new AuthenticationException("Token inválido") {};

        Mono<ResponseEntity<Map<String, Object>>> response = 
                securityExceptionHandler.handleAuthenticationException(exception);

        StepVerifier.create(response)
                .assertNext(responseEntity -> {
                    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    
                    Map<String, Object> body = responseEntity.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);
                    assertThat(body).containsEntry("status", 401);
                    assertThat(body).containsEntry("error", "No autorizado");
                    assertThat(body).containsEntry("message", "Token de autenticación inválido o expirado");
                    assertThat(body).containsEntry("path", "/api/v1/solicitudes");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar correctamente AccessDeniedException")
    void debeManejarcorrectamenteAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("Acceso denegado");

        Mono<ResponseEntity<Map<String, Object>>> response = 
                securityExceptionHandler.handleAccessDeniedException(exception);

        StepVerifier.create(response)
                .assertNext(responseEntity -> {
                    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                    
                    Map<String, Object> body = responseEntity.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);
                    assertThat(body).containsEntry("status", 403);
                    assertThat(body).containsEntry("error", "Acceso denegado");
                    assertThat(body).containsEntry("message", "No tiene permisos suficientes para acceder a este recurso");
                    assertThat(body).containsEntry("path", "/api/v1/solicitudes");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe generar timestamp correcto en AuthenticationException")
    void debeGenerarTimestampCorrectoEnAuthenticationException() {
        AuthenticationException exception = new AuthenticationException("Test exception") {};
        LocalDateTime before = LocalDateTime.now();

        Mono<ResponseEntity<Map<String, Object>>> response = 
                securityExceptionHandler.handleAuthenticationException(exception);

        StepVerifier.create(response)
                .assertNext(responseEntity -> {
                    Map<String, Object> body = responseEntity.getBody();
                    assertThat(body).isNotNull();
                    LocalDateTime timestamp = (LocalDateTime) body.get("timestamp");
                    LocalDateTime after = LocalDateTime.now();
                    
                    assertThat(timestamp).isAfter(before.minusSeconds(1));
                    assertThat(timestamp).isBefore(after.plusSeconds(1));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe generar timestamp correcto en AccessDeniedException")
    void debeGenerarTimestampCorrectoEnAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("Test exception");
        LocalDateTime before = LocalDateTime.now();

        Mono<ResponseEntity<Map<String, Object>>> response = 
                securityExceptionHandler.handleAccessDeniedException(exception);

        StepVerifier.create(response)
                .assertNext(responseEntity -> {
                    Map<String, Object> body = responseEntity.getBody();
                    assertThat(body).isNotNull();
                    LocalDateTime timestamp = (LocalDateTime) body.get("timestamp");
                    LocalDateTime after = LocalDateTime.now();
                    
                    assertThat(timestamp).isAfter(before.minusSeconds(1));
                    assertThat(timestamp).isBefore(after.plusSeconds(1));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar AuthenticationException con mensaje personalizado")
    void debeManejareAuthenticationExceptionConMensajePersonalizado() {
        String mensajeCustom = "Credenciales incorrectas";
        AuthenticationException exception = new AuthenticationException(mensajeCustom) {};

        Mono<ResponseEntity<Map<String, Object>>> response = 
                securityExceptionHandler.handleAuthenticationException(exception);

        StepVerifier.create(response)
                .assertNext(responseEntity -> {
                    Map<String, Object> body = responseEntity.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body).containsEntry("message", "Token de autenticación inválido o expirado");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar AccessDeniedException con mensaje personalizado")
    void debeManejareAccessDeniedExceptionConMensajePersonalizado() {
        String mensajeCustom = "Usuario sin permisos";
        AccessDeniedException exception = new AccessDeniedException(mensajeCustom);

        Mono<ResponseEntity<Map<String, Object>>> response = 
                securityExceptionHandler.handleAccessDeniedException(exception);

        StepVerifier.create(response)
                .assertNext(responseEntity -> {
                    Map<String, Object> body = responseEntity.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body).containsEntry("message", "No tiene permisos suficientes para acceder a este recurso");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar ResponseEntity con estructura completa para AuthenticationException")
    void debeRetornarResponseEntityConEstructuraCompletaParaAuthenticationException() {
        AuthenticationException exception = new AuthenticationException("Test") {};

        Mono<ResponseEntity<Map<String, Object>>> response = 
                securityExceptionHandler.handleAuthenticationException(exception);

        StepVerifier.create(response)
                .assertNext(responseEntity -> {
                    assertThat(responseEntity).isNotNull();
                    assertThat(responseEntity.getBody()).isNotNull();
                    
                    Map<String, Object> body = responseEntity.getBody();
                    assertThat(body).hasSize(5);
                    assertThat(body).containsKeys("timestamp", "status", "error", "message", "path");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar ResponseEntity con estructura completa para AccessDeniedException")
    void debeRetornarResponseEntityConEstructuraCompletaParaAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("Test");

        Mono<ResponseEntity<Map<String, Object>>> response = 
                securityExceptionHandler.handleAccessDeniedException(exception);

        StepVerifier.create(response)
                .assertNext(responseEntity -> {
                    assertThat(responseEntity).isNotNull();
                    assertThat(responseEntity.getBody()).isNotNull();
                    
                    Map<String, Object> body = responseEntity.getBody();
                    assertThat(body).hasSize(5);
                    assertThat(body).containsKeys("timestamp", "status", "error", "message", "path");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar AuthenticationException con mensaje null")
    void debeManejareAuthenticationExceptionConMensajeNull() {
        AuthenticationException exception = new AuthenticationException(null) {};

        Mono<ResponseEntity<Map<String, Object>>> response = 
                securityExceptionHandler.handleAuthenticationException(exception);

        StepVerifier.create(response)
                .assertNext(responseEntity -> {
                    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    Map<String, Object> body = responseEntity.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body).containsEntry("message", "Token de autenticación inválido o expirado");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar AccessDeniedException con mensaje null")
    void debeManejareAccessDeniedExceptionConMensajeNull() {
        AccessDeniedException exception = new AccessDeniedException(null);

        Mono<ResponseEntity<Map<String, Object>>> response = 
                securityExceptionHandler.handleAccessDeniedException(exception);

        StepVerifier.create(response)
                .assertNext(responseEntity -> {
                    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                    Map<String, Object> body = responseEntity.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body).containsEntry("message", "No tiene permisos suficientes para acceder a este recurso");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar Mono no vacío para AuthenticationException")
    void debeRetornarMonoNoVacioParaAuthenticationException() {
        AuthenticationException exception = new AuthenticationException("Test") {};

        Mono<ResponseEntity<Map<String, Object>>> response = 
                securityExceptionHandler.handleAuthenticationException(exception);

        assertThat(response).isNotNull();
        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar Mono no vacío para AccessDeniedException")
    void debeRetornarMonoNoVacioParaAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("Test");

        Mono<ResponseEntity<Map<String, Object>>> response = 
                securityExceptionHandler.handleAccessDeniedException(exception);

        assertThat(response).isNotNull();
        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();
    }
}
