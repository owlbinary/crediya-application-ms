package co.com.crediya.validacion.adapter;

import co.com.crediya.model.ValidacionDocumento;
import co.com.crediya.model.exception.AutenticacionException;
import co.com.crediya.model.exception.InfraestructuraException;
import co.com.crediya.validacion.dto.DetalleUsuarioResponse;
import co.com.crediya.validacion.dto.ValidacionDocumentoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
}
