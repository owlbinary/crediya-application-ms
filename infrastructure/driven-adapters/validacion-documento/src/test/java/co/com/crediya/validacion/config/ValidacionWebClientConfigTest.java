package co.com.crediya.validacion.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ValidacionWebClientConfig - Configuración WebClient para Validaciones")
class ValidacionWebClientConfigTest {

    private ValidacionWebClientConfig validacionWebClientConfig;

    @BeforeEach
    void setUp() {
        validacionWebClientConfig = new ValidacionWebClientConfig();
    }

    @Test
    @DisplayName("Debe crear WebClient correctamente")
    void debeCrearWebClientCorrectamente() {
        WebClient webClient = validacionWebClientConfig.validacionWebClient();

        assertThat(webClient).isNotNull();
    }

    @Test
    @DisplayName("Debe crear instancias diferentes de WebClient")
    void debeCrearInstanciasDiferentesDeWebClient() {
        WebClient webClient1 = validacionWebClientConfig.validacionWebClient();
        WebClient webClient2 = validacionWebClientConfig.validacionWebClient();

        assertThat(webClient1)
                .isNotNull()
                .isNotSameAs(webClient2);
        assertThat(webClient2).isNotNull();
    }

    @Test
    @DisplayName("WebClient debe estar configurado como bean")
    void webClientDebeEstarConfiguradoComoBean() {
        WebClient webClient = validacionWebClientConfig.validacionWebClient();

        assertThat(webClient)
                .isNotNull()
                .isInstanceOf(WebClient.class);
    }
}
