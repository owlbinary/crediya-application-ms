package co.com.crediya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.model.gateway.TipoPrestamoGateway;
import co.com.crediya.model.gateway.ValidacionDocumentoGateway;
import co.com.crediya.usecase.listarsolicitudes.ListarSolicitudesUseCase;
import co.com.crediya.usecase.registrarsolicitud.RegistrarSolicitudUseCase;

/**
 * Configuración de los casos de uso de la aplicación.
 * Centraliza la inyección de dependencias para los use cases.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public RegistrarSolicitudUseCase registrarSolicitudUseCase(
            SolicitudGateway solicitudRepository,
            ValidacionDocumentoGateway validacionDocumentoGateway) {
        return new RegistrarSolicitudUseCase(solicitudRepository, validacionDocumentoGateway);
    }
    
    @Bean
    public ListarSolicitudesUseCase listarSolicitudesUseCase(
            SolicitudGateway solicitudGateway,
            ValidacionDocumentoGateway validacionDocumentoGateway,
            TipoPrestamoGateway tipoPrestamoGateway) {
        return new ListarSolicitudesUseCase(solicitudGateway, validacionDocumentoGateway, tipoPrestamoGateway);
    }
}
