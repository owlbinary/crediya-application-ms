package co.com.crediya.validacion.adapter;

import co.com.crediya.model.ValidacionDocumento;
import co.com.crediya.model.exception.AutenticacionException;
import co.com.crediya.model.exception.InfraestructuraException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidacionDocumentoAdapterTest {

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
    private String urlBase;

    @BeforeEach
    void setUp() {
        documentoValido = "12345678";
        urlBase = "http://localhost:8081";
        ReflectionTestUtils.setField(adapter, "validacionDocumentoUrl", urlBase);
    }

    @Test
    void deberiaValidarDocumentoExitosamenteConToken() {
        ValidacionDocumentoResponse response = ValidacionDocumentoResponse.builder()
            .documentoIdentidad(documentoValido)
            .existe(true)
            .mensaje("Documento válido")
            .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.just(response));

        StepVerifier.create(adapter.validarDocumento(documentoValido, "Bearer jwt-token-123"))
            .expectNextMatches(validacion -> 
                validacion.getDocumentoIdentidad().equals(documentoValido) &&
                validacion.getExiste().equals(true) &&
                validacion.getMensaje().equals("Documento válido")
            )
            .verifyComplete();

        verify(tokenService).hasToken();
        verify(tokenService).getBearerToken();
        verify(requestHeadersSpec).header("Authorization", "Bearer jwt-token-123");
    }

    @Test
    void deberiaValidarDocumentoSinToken() {
        ValidacionDocumentoResponse response = ValidacionDocumentoResponse.builder()
            .documentoIdentidad(documentoValido)
            .existe(true)
            .mensaje("Documento válido")
            .build();

        when(tokenService.hasToken()).thenReturn(false);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.just(response));

        StepVerifier.create(adapter.validarDocumento(documentoValido))
            .expectNextMatches(validacion -> 
                validacion.getDocumentoIdentidad().equals(documentoValido) &&
                validacion.getExiste().equals(true)
            )
            .verifyComplete();

        verify(tokenService).hasToken();
        verify(tokenService, never()).getBearerToken();
        verify(requestHeadersSpec, never()).header(eq("Authorization"), anyString());
    }

    @Test
    void deberiaLanzarAutenticacionExceptionCuando401() {
        WebClientResponseException excepcion = WebClientResponseException.create(
            HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, null);

        when(tokenService.hasToken()).thenReturn(true);
        when(tokenService.getBearerToken()).thenReturn("Bearer jwt-token-123");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.error(excepcion));

        StepVerifier.create(adapter.validarDocumento(documentoValido))
            .expectError(AutenticacionException.class)
            .verify();
    }

    @Test
    void deberiaLanzarAutenticacionExceptionCuando403() {
        WebClientResponseException excepcion = WebClientResponseException.create(
            HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, null);

        when(tokenService.hasToken()).thenReturn(true);
        when(tokenService.getBearerToken()).thenReturn("Bearer jwt-token-123");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.error(excepcion));

        StepVerifier.create(adapter.validarDocumento(documentoValido))
            .expectError(AutenticacionException.class)
            .verify();
    }

    @Test
    void deberiaLanzarInfraestructuraExceptionParaOtrosErroresHttp() {
        WebClientResponseException excepcion = WebClientResponseException.create(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null, null, null);

        when(tokenService.hasToken()).thenReturn(true);
        when(tokenService.getBearerToken()).thenReturn("Bearer jwt-token-123");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidacionDocumentoResponse.class)).thenReturn(Mono.error(excepcion));

        StepVerifier.create(adapter.validarDocumento(documentoValido))
            .expectError(InfraestructuraException.class)
            .verify();
    }
}
