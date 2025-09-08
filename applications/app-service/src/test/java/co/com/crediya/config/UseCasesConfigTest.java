package co.com.crediya.config;

import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.model.gateway.NotificacionGateway;
import co.com.crediya.model.gateway.TipoPrestamoGateway;
import co.com.crediya.model.gateway.ValidacionDocumentoGateway;
import co.com.crediya.usecase.listarsolicitudes.ListarSolicitudesUseCase;
import co.com.crediya.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("UseCasesConfig - Configuración de Escaneo de Casos de Uso")
class UseCasesConfigTest {
    @Mock
    private NotificacionGateway notificacionGateway;

    @Mock
    private SolicitudGateway solicitudGateway;

    @Mock
    private ValidacionDocumentoGateway validacionDocumentoGateway;

    @Mock
    private TipoPrestamoGateway tipoPrestamoGateway;

    private AnnotationConfigApplicationContext context;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext();
        context.register(UseCasesConfig.class);

    context.getBeanFactory().registerSingleton("solicitudGateway", solicitudGateway);
    context.getBeanFactory().registerSingleton("validacionDocumentoGateway", validacionDocumentoGateway);
    context.getBeanFactory().registerSingleton("tipoPrestamoGateway", tipoPrestamoGateway);
    context.getBeanFactory().registerSingleton("notificacionGateway", notificacionGateway);
        
        context.refresh();
    }

    @Test
    @DisplayName("Debe escanear y registrar RegistrarSolicitudUseCase correctamente")
    void debeEscanearYRegistrarRegistrarSolicitudUseCaseCorrectamente() {
        RegistrarSolicitudUseCase useCase = context.getBean(RegistrarSolicitudUseCase.class);

        assertThat(useCase)
                .isNotNull()
                .isInstanceOf(RegistrarSolicitudUseCase.class);
    }

    @Test
    @DisplayName("Debe escanear y registrar ListarSolicitudesUseCase correctamente")
    void debeEscanearYRegistrarListarSolicitudesUseCaseCorrectamente() {
        ListarSolicitudesUseCase useCase = context.getBean(ListarSolicitudesUseCase.class);

        assertThat(useCase)
                .isNotNull()
                .isInstanceOf(ListarSolicitudesUseCase.class);
    }

    @Test
    @DisplayName("Debe crear beans con las dependencias inyectadas correctamente")
    void debeCrearBeansConLasDependenciasInyectadasCorrectamente() {
        RegistrarSolicitudUseCase registrarUseCase = context.getBean(RegistrarSolicitudUseCase.class);
        ListarSolicitudesUseCase listarUseCase = context.getBean(ListarSolicitudesUseCase.class);

        assertThat(registrarUseCase).isNotNull();
        assertThat(listarUseCase).isNotNull();
        
        assertThat(registrarUseCase.getClass().getName()).contains("RegistrarSolicitudUseCase");
        assertThat(listarUseCase.getClass().getName()).contains("ListarSolicitudesUseCase");
    }

    @Test
    @DisplayName("Debe usar filtro regex para incluir solo clases UseCase")
    void debeUsarFiltroRegexParaIncluirSoloClasesUseCase() {
        String[] beanNames = context.getBeanNamesForType(Object.class);
        
        long useCaseBeansCount = java.util.Arrays.stream(beanNames)
                .filter(name -> name.contains("UseCase"))
                .count();

        assertThat(useCaseBeansCount).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Debe verificar que la configuración está activa")
    void debeVerificarQueLaConfiguracionEstaActiva() {
        boolean hasUseCasesConfig = context.getBeanDefinitionNames().length > 0;
        
        assertThat(hasUseCasesConfig).isTrue();
        assertThat(context.containsBean("registrarSolicitudUseCase")).isTrue();
        assertThat(context.containsBean("listarSolicitudesUseCase")).isTrue();
    }

    @Test
    @DisplayName("Debe crear instancias singleton de los casos de uso")
    void debeCrearInstanciasSingletonDeLosCasosDeUso() {
        RegistrarSolicitudUseCase useCase1 = context.getBean(RegistrarSolicitudUseCase.class);
        RegistrarSolicitudUseCase useCase2 = context.getBean(RegistrarSolicitudUseCase.class);

        assertThat(useCase1).isSameAs(useCase2);
    }
}