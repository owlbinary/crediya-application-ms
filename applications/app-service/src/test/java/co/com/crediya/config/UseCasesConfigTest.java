package co.com.crediya.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import co.com.crediya.model.gateway.SolicitudRepository;
import co.com.crediya.usecase.registrarsolicitud.RegistrarSolicitudUseCase;

class UseCasesConfigTest {

    @Test
    void testUseCaseRegistrarBeansExist() {
        SolicitudRepository solicitudRepository = mock(SolicitudRepository.class);
        RegistrarSolicitudUseCase registrarSolicitudUseCase = new RegistrarSolicitudUseCase(solicitudRepository);

        assertNotNull(registrarSolicitudUseCase, "RegistrarSolicitudUseCase should not be null");
        assertNotNull(solicitudRepository, "SolicitudRepository should not be null");
    }
}