package co.com.crediya.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.model.gateway.ValidacionDocumentoGateway;
import co.com.crediya.usecase.registrarsolicitud.RegistrarSolicitudUseCase;

class UseCasesConfigTest {

    @Test
    void testUseCaseRegistrarBeansExist() {
        SolicitudGateway solicitudRepository = mock(SolicitudGateway.class);
        ValidacionDocumentoGateway validacionDocumentoGateway = mock(ValidacionDocumentoGateway.class);
        RegistrarSolicitudUseCase registrarSolicitudUseCase = new RegistrarSolicitudUseCase(solicitudRepository, validacionDocumentoGateway);

        assertNotNull(registrarSolicitudUseCase, "RegistrarSolicitudUseCase should not be null");
        assertNotNull(solicitudRepository, "SolicitudRepository should not be null");
        assertNotNull(validacionDocumentoGateway, "ValidacionDocumentoGateway should not be null");
    }
}