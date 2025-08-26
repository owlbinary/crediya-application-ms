package co.com.crediya.validacion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuración para el cliente HTTP usado en validaciones.
 */
@Configuration
public class ValidacionWebClientConfig {
    
    @Bean("validacionWebClient")
    public WebClient validacionWebClient() {
        return WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
            .build();
    }
}
