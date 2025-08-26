package co.com.crediya.config;

import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.model.gateway.ValidacionDocumentoGateway;
import co.com.crediya.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("UseCaseConfig - Configuración de Casos de Uso")
class UseCaseConfigTest {

    @Mock
    private SolicitudGateway solicitudGateway;

    @Mock
    private ValidacionDocumentoGateway validacionDocumentoGateway;

    private UseCaseConfig useCaseConfig;

    @BeforeEach
    void setUp() {
        useCaseConfig = new UseCaseConfig();
    }

    @Test
    @DisplayName("Debe crear bean RegistrarSolicitudUseCase correctamente")
    void debeCrearBeanRegistrarSolicitudUseCaseCorrectamente() {
        RegistrarSolicitudUseCase useCase = useCaseConfig.registrarSolicitudUseCase(
                solicitudGateway, validacionDocumentoGateway);

        assertThat(useCase)
                .isNotNull()
                .isInstanceOf(RegistrarSolicitudUseCase.class);
    }

    @Test
    @DisplayName("Debe crear instancias diferentes en llamadas múltiples")
    void debeCrearInstanciasDiferentesEnLladasMultiples() {
        RegistrarSolicitudUseCase useCase1 = useCaseConfig.registrarSolicitudUseCase(
                solicitudGateway, validacionDocumentoGateway);
        RegistrarSolicitudUseCase useCase2 = useCaseConfig.registrarSolicitudUseCase(
                solicitudGateway, validacionDocumentoGateway);

        assertThat(useCase1)
                .isNotNull()
                .isNotSameAs(useCase2);
        assertThat(useCase2).isNotNull();
    }
}
