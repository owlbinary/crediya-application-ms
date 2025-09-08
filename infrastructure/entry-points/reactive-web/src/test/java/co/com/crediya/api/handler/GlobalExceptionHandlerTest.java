package co.com.crediya.api.handler;

import co.com.crediya.api.dto.response.ErrorResponse;
import co.com.crediya.model.exception.AutenticacionException;
import co.com.crediya.model.exception.DocumentoNoValidoException;
import co.com.crediya.model.exception.InfraestructuraException;
import co.com.crediya.model.exception.TipoPrestamoNoExisteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RequestPath requestPath;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        lenient().when(exchange.getRequest()).thenReturn(request);
        lenient().when(request.getPath()).thenReturn(requestPath);
        lenient().when(requestPath.value()).thenReturn("/api/v1/solicitudes");
    }

    @Test
    void deberiaProcesarTipoPrestamoInexistente() {
        TipoPrestamoNoExisteException excepcion = new TipoPrestamoNoExisteException("PERSONAL");

        StepVerifier.create(globalExceptionHandler.procesarTipoPrestamoInexistente(excepcion, exchange))
            .expectNextMatches(errorResponse -> {
                assertThat(errorResponse.getCodigo()).isEqualTo("TIPO_PRESTAMO_NO_EXISTE");
                assertThat(errorResponse.getMensaje()).isEqualTo("Tipo de préstamo con ID PERSONAL no existe");
                assertThat(errorResponse.getPath()).isEqualTo("/api/v1/solicitudes");
                assertThat(errorResponse.getTimestamp()).isNotNull();
                return true;
            })
            .verifyComplete();
    }

    @Test
    void deberiaProcesarErrorAutenticacion() {
        AutenticacionException excepcion = new AutenticacionException("Error de autenticación");

        StepVerifier.create(globalExceptionHandler.procesarErrorAutenticacion(excepcion, exchange))
            .expectNextMatches(errorResponse -> {
                assertThat(errorResponse.getCodigo()).isEqualTo("ERROR_INTERNO");
                assertThat(errorResponse.getMensaje()).isEqualTo("Error de autenticación con servicio externo");
                assertThat(errorResponse.getPath()).isEqualTo("/api/v1/solicitudes");
                assertThat(errorResponse.getTimestamp()).isNotNull();
                return true;
            })
            .verifyComplete();
    }

    @Test
    void deberiaProcesarErrorInfraestructura() {
        InfraestructuraException excepcion = new InfraestructuraException("Error de infraestructura");

        StepVerifier.create(globalExceptionHandler.procesarErrorInfraestructura(excepcion, exchange))
            .expectNextMatches(errorResponse -> {
                assertThat(errorResponse.getCodigo()).isEqualTo("ERROR_INTERNO");
                assertThat(errorResponse.getMensaje()).isEqualTo("Error en infraestructura");
                assertThat(errorResponse.getPath()).isEqualTo("/api/v1/solicitudes");
                assertThat(errorResponse.getTimestamp()).isNotNull();
                return true;
            })
            .verifyComplete();
    }

    @Test
    void deberiaProcesarDocumentoNoValido() {
        DocumentoNoValidoException excepcion = new DocumentoNoValidoException("Documento no válido");

        StepVerifier.create(globalExceptionHandler.procesarDocumentoNoValido(excepcion, exchange))
            .expectNextMatches(errorResponse -> {
                assertThat(errorResponse.getCodigo()).isEqualTo("VALIDACION_FALLIDA");
                assertThat(errorResponse.getMensaje()).isEqualTo("Documento no válido");
                assertThat(errorResponse.getPath()).isEqualTo("/api/v1/solicitudes");
                assertThat(errorResponse.getTimestamp()).isNotNull();
                return true;
            })
            .verifyComplete();
    }

    @Test
    void deberiaProcesarErrorGenerico() {
        RuntimeException excepcion = new RuntimeException("Error genérico");

        StepVerifier.create(globalExceptionHandler.procesarErrorGenerico(excepcion, exchange))
            .expectNextMatches(errorResponse -> {
                assertThat(errorResponse.getCodigo()).isEqualTo("ERROR_INTERNO");
                assertThat(errorResponse.getMensaje()).isEqualTo("Se presentó un fallo interno en el sistema");
                assertThat(errorResponse.getPath()).isEqualTo("/api/v1/solicitudes");
                assertThat(errorResponse.getTimestamp()).isNotNull();
                return true;
            })
            .verifyComplete();
    }

    @Test
    void deberiaCrearErrorResponseCorrectamente() {
        ErrorResponse errorResponse = ErrorResponse.of("TEST_ERROR", "Mensaje de prueba", "/test/path");

        assertThat(errorResponse.getCodigo()).isEqualTo("TEST_ERROR");
        assertThat(errorResponse.getMensaje()).isEqualTo("Mensaje de prueba");
        assertThat(errorResponse.getPath()).isEqualTo("/test/path");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

        @Test
    void deberiaProcesarTokenInvalido() {
        co.com.crediya.model.exception.TokenInvalidoException excepcion = new co.com.crediya.model.exception.TokenInvalidoException("Token expirado");
        StepVerifier.create(globalExceptionHandler.procesarTokenInvalido(excepcion, exchange))
            .expectNextMatches(errorResponse -> {
                assertThat(errorResponse.getCodigo()).isEqualTo("TOKEN_INVALIDO");
                assertThat(errorResponse.getMensaje()).isEqualTo("Token expirado");
                assertThat(errorResponse.getPath()).isEqualTo("/api/v1/solicitudes");
                assertThat(errorResponse.getTimestamp()).isNotNull();
                return true;
            })
            .verifyComplete();
    }

    @Test
    void deberiaProcesarSolicitudNoEncontrada() {
        co.com.crediya.model.exception.SolicitudNoEncontradaException excepcion = new co.com.crediya.model.exception.SolicitudNoEncontradaException("No encontrada");
        StepVerifier.create(globalExceptionHandler.procesarSolicitudNoEncontrada(excepcion, exchange))
            .expectNextMatches(errorResponse -> {
                assertThat(errorResponse.getCodigo()).isEqualTo("SOLICITUD_NO_ENCONTRADA");
                assertThat(errorResponse.getMensaje()).isEqualTo("No encontrada");
                assertThat(errorResponse.getPath()).isEqualTo("/api/v1/solicitudes");
                assertThat(errorResponse.getTimestamp()).isNotNull();
                return true;
            })
            .verifyComplete();
    }

    @Test
    void deberiaProcesarEstadoSolicitudNoValido() {
        co.com.crediya.model.exception.EstadoSolicitudNoValidoException excepcion = new co.com.crediya.model.exception.EstadoSolicitudNoValidoException("Estado no válido");
        StepVerifier.create(globalExceptionHandler.procesarEstadoSolicitudNoValido(excepcion, exchange))
            .expectNextMatches(errorResponse -> {
                assertThat(errorResponse.getCodigo()).isEqualTo("ESTADO_SOLICITUD_NO_VALIDO");
                assertThat(errorResponse.getMensaje()).isEqualTo("Estado no válido");
                assertThat(errorResponse.getPath()).isEqualTo("/api/v1/solicitudes");
                assertThat(errorResponse.getTimestamp()).isNotNull();
                return true;
            })
            .verifyComplete();
    }

    @Test
    void deberiaProcesarErrorNotificacionEstado() {
        co.com.crediya.model.exception.NotificacionEstadoException excepcion = new co.com.crediya.model.exception.NotificacionEstadoException("Error notificando");
        StepVerifier.create(globalExceptionHandler.procesarErrorNotificacionEstado(excepcion, exchange))
            .expectNextMatches(errorResponse -> {
                assertThat(errorResponse.getCodigo()).isEqualTo("ERROR_NOTIFICACION_ESTADO");
                assertThat(errorResponse.getMensaje()).isEqualTo("Error notificando");
                assertThat(errorResponse.getPath()).isEqualTo("/api/v1/solicitudes");
                assertThat(errorResponse.getTimestamp()).isNotNull();
                return true;
            })
            .verifyComplete();
    }

    @Test
    void deberiaProcesarErroresValidacion() {
        org.springframework.web.bind.support.WebExchangeBindException excepcion = org.mockito.Mockito.mock(org.springframework.web.bind.support.WebExchangeBindException.class);
        java.util.List<org.springframework.validation.FieldError> fieldErrors = java.util.Collections.singletonList(
            new org.springframework.validation.FieldError("objectName", "field", "Mensaje de error de validación")
        );
        org.mockito.Mockito.when(excepcion.getFieldErrors()).thenReturn(fieldErrors);
        org.mockito.Mockito.when(excepcion.getMessage()).thenReturn("Error de validación");
        StepVerifier.create(globalExceptionHandler.procesarErroresValidacion(excepcion, exchange))
            .expectNextMatches(errorResponse -> {
                assertThat(errorResponse.getCodigo()).isEqualTo("VALIDACION_FALLIDA");
                assertThat(errorResponse.getMensaje()).isEqualTo("Mensaje de error de validación");
                assertThat(errorResponse.getPath()).isEqualTo("/api/v1/solicitudes");
                assertThat(errorResponse.getTimestamp()).isNotNull();
                return true;
            })
            .verifyComplete();
    }
}
