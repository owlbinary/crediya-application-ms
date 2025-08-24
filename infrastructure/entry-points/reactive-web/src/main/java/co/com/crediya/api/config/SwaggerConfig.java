package co.com.crediya.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI configurarDocumentacionApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Microservicio de solicitudes - CrediYa")
                .description("API para gestión de solicitudes de préstamos")
                .version("1.0.0")
                .contact(new Contact()
                    .name("CrediYa")));
    }
}