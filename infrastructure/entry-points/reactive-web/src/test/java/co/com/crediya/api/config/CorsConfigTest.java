package co.com.crediya.api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CorsConfig - Configuración CORS")
class CorsConfigTest {

    private CorsConfig corsConfig;

    @BeforeEach
    void setUp() {
        corsConfig = new CorsConfig();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://localhost:3000",
            "http://localhost:3000,http://localhost:4200,https://app.crediya.com",
            ""
    })
    @DisplayName("Debe crear CorsWebFilter con diferentes configuraciones de orígenes")
    void debeCrearCorsWebFilterConDiferentesConfiguraciones(String allowedOrigins) {
        CorsWebFilter corsWebFilter = corsConfig.corsWebFilter(allowedOrigins);

        assertThat(corsWebFilter).isNotNull();
    }

    @Test
    @DisplayName("Debe crear instancias diferentes en llamadas múltiples")
    void debeCrearInstanciasDiferentesEnLlamadasMultiples() {
        String allowedOrigins = "http://localhost:3000";

        CorsWebFilter filter1 = corsConfig.corsWebFilter(allowedOrigins);
        CorsWebFilter filter2 = corsConfig.corsWebFilter(allowedOrigins);

        assertThat(filter1)
                .isNotNull()
                .isNotSameAs(filter2);
        assertThat(filter2).isNotNull();
    }
}
