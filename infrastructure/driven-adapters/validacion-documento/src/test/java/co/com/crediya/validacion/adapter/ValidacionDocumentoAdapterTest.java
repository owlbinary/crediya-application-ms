package co.com.crediya.validacion.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import co.com.crediya.model.exception.AutenticacionException;
import co.com.crediya.model.exception.InfraestructuraException;
import co.com.crediya.validacion.dto.DetalleUsuarioResponse;
import co.com.crediya.validacion.dto.ValidacionDocumentoResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("ValidacionDocumentoAdapter - Pruebas Unitarias")
class ValidacionDocumentoAdapterTest {

    private static final String BEARER_TOKEN = "Bearer jwt-token-123";
    private static final String DOCUMENTO_VALIDO_MSG = "Documento válido";
    private static final String VALIDATION_URL = "http://localhost:8081";

    @Mock
    private WebClient webClient;
    
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ValidacionDocumentoAdapter adapter;

    private String documentoValido;

    @BeforeEach
    void setUp() {
        documentoValido = "12345678";
        ReflectionTestUtils.setField(adapter, "validacionDocumentoUrl", VALIDATION_URL);
    }

    @Test
    void deberiaValidarDocumentoExitosamenteConToken() {
        ValidacionDocumentoResponse response = ValidacionDocumentoResponse.builder()
            .existe(true)
            .mensaje(DOCUMENTO_VALIDO_MSG)
            .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.just(response));

        StepVerifier.create(adapter.validarDocumento(documentoValido, BEARER_TOKEN))
            .expectNextMatches(validacion -> 
                validacion.getExiste().equals(true) &&
                validacion.getMensaje().equals(DOCUMENTO_VALIDO_MSG)
            )
            .verifyComplete();

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(anyString(), anyString());
        verify(requestHeadersSpec).headers(any());
    }

    @Test
    void deberiaValidarDocumentoSinToken() {
        ValidacionDocumentoResponse response = ValidacionDocumentoResponse.builder()
            .existe(true)
            .mensaje(DOCUMENTO_VALIDO_MSG)
            .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.just(response));

        StepVerifier.create(adapter.validarDocumento(documentoValido, null))
            .expectNextMatches(validacion -> 
                validacion.getExiste().equals(true)
            )
            .verifyComplete();

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(anyString(), anyString());
    }

    @Test
    void deberiaValidarDocumentoConDetalleUsuario() {
        DetalleUsuarioResponse detalleUsuario = DetalleUsuarioResponse.builder()
            .idUsuario(1L)
            .nombre("Juan")
            .apellido("Pérez")
            .email("juan.perez@email.com")
            .documentoIdentidad(documentoValido)
            .telefono("3001234567")
            .direccion("Calle 123 #45-67")
            .idRol(2L)
            .salarioBase(new BigDecimal("2500000"))
            .fechaCreacion(LocalDateTime.now())
            .build();

        ValidacionDocumentoResponse response = ValidacionDocumentoResponse.builder()
            .existe(true)
            .mensaje(DOCUMENTO_VALIDO_MSG)
            .detalleUsuario(detalleUsuario)
            .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.just(response));

        StepVerifier.create(adapter.validarDocumento(documentoValido, BEARER_TOKEN))
            .expectNextMatches(validacion -> 
                validacion.getExiste().equals(true) &&
                validacion.getMensaje().equals(DOCUMENTO_VALIDO_MSG) &&
                validacion.getDetalleUsuario() != null &&
                validacion.getDetalleUsuario().getNombre().equals("Juan") &&
                validacion.getDetalleUsuario().getApellido().equals("Pérez") &&
                validacion.getDetalleUsuario().getEmail().equals("juan.perez@email.com")
            )
            .verifyComplete();

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(anyString(), anyString());
        verify(requestHeadersSpec).headers(any());
    }

    @Test
    void deberiaLanzarAutenticacionExceptionCuando401() {
        WebClientResponseException excepcion = WebClientResponseException.create(
            HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, null);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.error(excepcion));

        StepVerifier.create(adapter.validarDocumento(documentoValido, BEARER_TOKEN))
            .expectError(AutenticacionException.class)
            .verify();
    }

    @Test
    void deberiaLanzarAutenticacionExceptionCuando403() {
        WebClientResponseException excepcion = WebClientResponseException.create(
            HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, null);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.error(excepcion));

        StepVerifier.create(adapter.validarDocumento(documentoValido, BEARER_TOKEN))
            .expectError(AutenticacionException.class)
            .verify();
    }

    @Test
    void deberiaLanzarInfraestructuraExceptionParaOtrosErroresHttp() {
        WebClientResponseException excepcion = WebClientResponseException.create(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null, null, null);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.error(excepcion));

        StepVerifier.create(adapter.validarDocumento(documentoValido, BEARER_TOKEN))
            .expectError(InfraestructuraException.class)
            .verify();
    }

    @Test
    @DisplayName("Debe validar documento con token vacío")
    void deberiaValidarDocumentoConTokenVacio() {
        ValidacionDocumentoResponse response = ValidacionDocumentoResponse.builder()
            .existe(true)
            .mensaje(DOCUMENTO_VALIDO_MSG)
            .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.just(response));

        StepVerifier.create(adapter.validarDocumento(documentoValido, "   "))
            .expectNextMatches(validacion -> validacion.getExiste().equals(true))
            .verifyComplete();

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(anyString(), anyString());
    }

    @Test
    @DisplayName("Debe manejar error de comunicación genérico")
    void deberiaManejarErrorDeComunicacionGenerico() {
        RuntimeException excepcion = new RuntimeException("Connection timeout");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.error(excepcion));

        StepVerifier.create(adapter.validarDocumento(documentoValido, BEARER_TOKEN))
            .expectError(InfraestructuraException.class)
            .verify();
    }

    @Test
    @DisplayName("Debe mapear validación con detalle usuario null")
    void deberiaMalearValidacionConDetalleUsuarioNull() {
        ValidacionDocumentoResponse response = ValidacionDocumentoResponse.builder()
            .existe(false)
            .mensaje("Documento no encontrado")
            .detalleUsuario(null)
            .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.just(response));

        StepVerifier.create(adapter.validarDocumento(documentoValido, BEARER_TOKEN))
            .expectNextMatches(validacion -> 
                !validacion.getExiste() &&
                validacion.getMensaje().equals("Documento no encontrado") &&
                validacion.getDetalleUsuario() == null
            )
            .verifyComplete();
    }

    @Test
    @DisplayName("Debe obtener detalle de usuario exitosamente con token")
    void deberiaObtenerDetalleUsuarioExitosamenteConToken() {
        DetalleUsuarioResponse detalleUsuario = DetalleUsuarioResponse.builder()
            .idUsuario(1L)
            .nombre("Juan")
            .apellido("Pérez")
            .email("juan.perez@email.com")
            .documentoIdentidad(documentoValido)
            .telefono("3001234567")
            .direccion("Calle 123 #45-67")
            .idRol(2L)
            .salarioBase(new BigDecimal("2500000"))
            .fechaCreacion(LocalDateTime.now())
            .build();

        ValidacionDocumentoResponse response = ValidacionDocumentoResponse.builder()
            .existe(true)
            .mensaje(DOCUMENTO_VALIDO_MSG)
            .detalleUsuario(detalleUsuario)
            .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.just(response));

        StepVerifier.create(adapter.obtenerDetalleUsuario(documentoValido, BEARER_TOKEN))
            .expectNextMatches(detalle -> 
                detalle != null &&
                detalle.getNombre().equals("Juan") &&
                detalle.getApellido().equals("Pérez") &&
                detalle.getEmail().equals("juan.perez@email.com") &&
                detalle.getDocumentoIdentidad().equals(documentoValido) &&
                detalle.getTelefono().equals("3001234567") &&
                detalle.getDireccion().equals("Calle 123 #45-67") &&
                detalle.getIdRol().equals(2L) &&
                detalle.getSalarioBase().equals(new BigDecimal("2500000"))
            )
            .verifyComplete();

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(anyString(), anyString());
        verify(requestHeadersSpec).headers(any());
    }

    @Test
    @DisplayName("Debe obtener detalle de usuario sin token")
    void deberiaObtenerDetalleUsuarioSinToken() {
        DetalleUsuarioResponse detalleUsuario = DetalleUsuarioResponse.builder()
            .idUsuario(1L)
            .nombre("Juan")
            .apellido("Pérez")
            .email("juan.perez@email.com")
            .documentoIdentidad(documentoValido)
            .build();

        ValidacionDocumentoResponse response = ValidacionDocumentoResponse.builder()
            .existe(true)
            .mensaje(DOCUMENTO_VALIDO_MSG)
            .detalleUsuario(detalleUsuario)
            .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.just(response));

        StepVerifier.create(adapter.obtenerDetalleUsuario(documentoValido, null))
            .expectNextMatches(detalle -> 
                detalle != null &&
                detalle.getNombre().equals("Juan")
            )
            .verifyComplete();

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(anyString(), anyString());
        verify(requestHeadersSpec, never()).headers(any());
    }

    @Test
    @DisplayName("Debe manejar caso cuando no hay detalle de usuario")
    void deberiaManejarCasoCuandoNoHayDetalleUsuario() {
        ValidacionDocumentoResponse response = ValidacionDocumentoResponse.builder()
            .existe(false)
            .mensaje("Usuario no encontrado")
            .detalleUsuario(null)
            .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.just(response));

        StepVerifier.create(adapter.obtenerDetalleUsuario(documentoValido, BEARER_TOKEN))
            .expectError()
            .verify();
    }

    @Test
    @DisplayName("Debe lanzar AutenticacionException cuando 401 en obtenerDetalleUsuario")
    void deberiaLanzarAutenticacionExceptionCuando401EnObtenerDetalle() {
        WebClientResponseException excepcion = WebClientResponseException.create(
            HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, new byte[0], null);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.error(excepcion));

        StepVerifier.create(adapter.obtenerDetalleUsuario(documentoValido, BEARER_TOKEN))
            .expectError(AutenticacionException.class)
            .verify();
    }

    @Test
    @DisplayName("Debe lanzar AutenticacionException cuando 403 en obtenerDetalleUsuario")
    void deberiaLanzarAutenticacionExceptionCuando403EnObtenerDetalle() {
        WebClientResponseException excepcion = WebClientResponseException.create(
            HttpStatus.FORBIDDEN.value(), "Forbidden", null, new byte[0], null);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.error(excepcion));

        StepVerifier.create(adapter.obtenerDetalleUsuario(documentoValido, BEARER_TOKEN))
            .expectError(AutenticacionException.class)
            .verify();
    }

    @Test
    @DisplayName("Debe obtener detalle de usuario con token vacío")
    void deberiaObtenerDetalleUsuarioConTokenVacio() {
        DetalleUsuarioResponse detalleUsuario = DetalleUsuarioResponse.builder()
            .idUsuario(1L)
            .nombre("María")
            .apellido("García")
            .email("maria.garcia@email.com")
            .documentoIdentidad(documentoValido)
            .build();

        ValidacionDocumentoResponse response = ValidacionDocumentoResponse.builder()
            .existe(true)
            .mensaje(DOCUMENTO_VALIDO_MSG)
            .detalleUsuario(detalleUsuario)
            .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.just(response));

        StepVerifier.create(adapter.obtenerDetalleUsuario(documentoValido, "   "))
            .expectNextMatches(detalle -> 
                detalle != null &&
                detalle.getNombre().equals("María") &&
                detalle.getApellido().equals("García")
            )
            .verifyComplete();

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(anyString(), anyString());
        verify(requestHeadersSpec, never()).headers(any());
    }
}
