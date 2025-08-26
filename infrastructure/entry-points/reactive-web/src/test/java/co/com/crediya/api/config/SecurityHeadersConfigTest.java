package co.com.crediya.api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityHeadersConfig - Configuración de Headers de Seguridad")
class SecurityHeadersConfigTest {

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private WebFilterChain chain;

    private SecurityHeadersConfig securityHeadersConfig;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        securityHeadersConfig = new SecurityHeadersConfig();
        headers = new HttpHeaders();
        
        when(exchange.getResponse()).thenReturn(response);
        when(response.getHeaders()).thenReturn(headers);
        when(chain.filter(exchange)).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("Debe aplicar headers de seguridad correctamente")
    void debeAplicarHeadersDeSeguridadCorrectamente() {
        StepVerifier.create(securityHeadersConfig.filter(exchange, chain))
                .verifyComplete();

        verify(exchange).getResponse();
        verify(response).getHeaders();
        verify(chain).filter(exchange);

        assertThat(headers.getFirst("Content-Security-Policy"))
                .isEqualTo("default-src 'self'; frame-ancestors 'self'; form-action 'self'");
        assertThat(headers.getFirst("Strict-Transport-Security"))
                .isEqualTo("max-age=31536000;");
        assertThat(headers.getFirst("X-Content-Type-Options"))
                .isEqualTo("nosniff");
        assertThat(headers.getFirst("Server"))
                .isEmpty();
        assertThat(headers.getFirst("Cache-Control"))
                .isEqualTo("no-store");
        assertThat(headers.getFirst("Pragma"))
                .isEqualTo("no-cache");
        assertThat(headers.getFirst("Referrer-Policy"))
                .isEqualTo("strict-origin-when-cross-origin");
    }

    @Test
    @DisplayName("Debe continuar la cadena de filtros")
    void debeContinuarLaCadenaFiltros() {
        StepVerifier.create(securityHeadersConfig.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
    }
}
