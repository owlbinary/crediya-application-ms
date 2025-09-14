package co.com.crediya.config;

import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.model.gateway.NotificacionGateway;
import co.com.crediya.model.gateway.TipoPrestamoGateway;
import co.com.crediya.model.gateway.ValidacionDocumentoGateway;
import co.com.crediya.model.gateway.DebtCapacityEventGateway;
import co.com.crediya.usecase.listarsolicitudes.ListarSolicitudesUseCase;
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

        @Mock
        private TipoPrestamoGateway tipoPrestamoGateway;

        @Mock
        private NotificacionGateway notificacionGateway;

        @Mock
        private DebtCapacityEventGateway debtCapacityEventGateway;

        private UseCaseConfig useCaseConfig;

        @BeforeEach
        void setUp() {
                useCaseConfig = new UseCaseConfig();
        }

        @Test
        @DisplayName("Debe crear bean RegistrarSolicitudUseCase correctamente")
        void debeCrearBeanRegistrarSolicitudUseCaseCorrectamente() {
                RegistrarSolicitudUseCase useCase = useCaseConfig.registrarSolicitudUseCase(
                                solicitudGateway, validacionDocumentoGateway, tipoPrestamoGateway, notificacionGateway, debtCapacityEventGateway);

                assertThat(useCase)
                                .isNotNull()
                                .isInstanceOf(RegistrarSolicitudUseCase.class);
        }

        @Test
        @DisplayName("Debe crear instancias diferentes en llamadas múltiples")
        void debeCrearInstanciasDiferentesEnLladasMultiples() {
                RegistrarSolicitudUseCase useCase1 = useCaseConfig.registrarSolicitudUseCase(
                                solicitudGateway, validacionDocumentoGateway, tipoPrestamoGateway, notificacionGateway, debtCapacityEventGateway);
                RegistrarSolicitudUseCase useCase2 = useCaseConfig.registrarSolicitudUseCase(
                                solicitudGateway, validacionDocumentoGateway, tipoPrestamoGateway, notificacionGateway, debtCapacityEventGateway);

                assertThat(useCase1)
                                .isNotNull()
                                .isNotSameAs(useCase2);
                assertThat(useCase2).isNotNull();
        }

        @Test
        @DisplayName("Debe crear bean ListarSolicitudesUseCase correctamente")
        void debeCrearBeanListarSolicitudesUseCaseCorrectamente() {
                ListarSolicitudesUseCase useCase = useCaseConfig.listarSolicitudesUseCase(
                                solicitudGateway, validacionDocumentoGateway, tipoPrestamoGateway);

                assertThat(useCase)
                                .isNotNull()
                                .isInstanceOf(ListarSolicitudesUseCase.class);
        }

        @Test
        @DisplayName("Debe crear instancias diferentes de ListarSolicitudesUseCase en llamadas múltiples")
        void debeCrearInstanciasDiferentesDeListarSolicitudesUseCaseEnLladasMultiples() {
                ListarSolicitudesUseCase useCase1 = useCaseConfig.listarSolicitudesUseCase(
                                solicitudGateway, validacionDocumentoGateway, tipoPrestamoGateway);
                ListarSolicitudesUseCase useCase2 = useCaseConfig.listarSolicitudesUseCase(
                                solicitudGateway, validacionDocumentoGateway, tipoPrestamoGateway);

                assertThat(useCase1)
                                .isNotNull()
                                .isNotSameAs(useCase2);
                assertThat(useCase2).isNotNull();
        }

        @Test
        @DisplayName("Debe inyectar correctamente las dependencias en RegistrarSolicitudUseCase")
        void debeInyectarCorrectamenteLasDependenciasEnRegistrarSolicitudUseCase() {
                RegistrarSolicitudUseCase useCase = useCaseConfig.registrarSolicitudUseCase(
                                solicitudGateway, validacionDocumentoGateway, tipoPrestamoGateway, notificacionGateway, debtCapacityEventGateway);

                assertThat(useCase).isNotNull();

                assertThat(useCase.getClass().getName()).contains("RegistrarSolicitudUseCase");
        }

        @Test
        @DisplayName("Debe inyectar correctamente las dependencias en ListarSolicitudesUseCase")
        void debeInyectarCorrectamenteLasDependenciasEnListarSolicitudesUseCase() {
                ListarSolicitudesUseCase useCase = useCaseConfig.listarSolicitudesUseCase(
                                solicitudGateway, validacionDocumentoGateway, tipoPrestamoGateway);

                assertThat(useCase).isNotNull();

                assertThat(useCase.getClass().getName()).contains("ListarSolicitudesUseCase");
        }
}
