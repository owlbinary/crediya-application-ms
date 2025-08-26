package co.com.crediya.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SwaggerConfig - Configuración de Documentación API")
class SwaggerConfigTest {

    private SwaggerConfig swaggerConfig;

    @BeforeEach
    void setUp() {
        swaggerConfig = new SwaggerConfig();
    }

    @Test
    @DisplayName("Debe configurar OpenAPI correctamente")
    void debeConfigurarOpenAPICorrectamente() {
        OpenAPI openAPI = swaggerConfig.configurarDocumentacionApi();

        assertThat(openAPI).isNotNull();
        
        Info info = openAPI.getInfo();
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Microservicio de solicitudes - CrediYa");
        assertThat(info.getDescription()).isEqualTo("API para gestión de solicitudes de préstamos");
        assertThat(info.getVersion()).isEqualTo("1.0.0");
        assertThat(info.getContact()).isNotNull();
        assertThat(info.getContact().getName()).isEqualTo("CrediYa");
    }

    @Test
    @DisplayName("Debe crear instancias diferentes de OpenAPI")
    void debeCrearInstanciasDiferentesDeOpenAPI() {
        OpenAPI openAPI1 = swaggerConfig.configurarDocumentacionApi();
        OpenAPI openAPI2 = swaggerConfig.configurarDocumentacionApi();

        assertThat(openAPI1)
                .isNotNull()
                .isNotSameAs(openAPI2);
        assertThat(openAPI2).isNotNull();
    }

    @Test
    @DisplayName("Debe tener información de contacto válida")
    void debeTenerInformacionDeContactoValida() {
        OpenAPI openAPI = swaggerConfig.configurarDocumentacionApi();

        assertThat(openAPI.getInfo().getContact())
                .isNotNull()
                .satisfies(contact -> {
                    assertThat(contact.getName()).isNotBlank();
                });
    }
}
