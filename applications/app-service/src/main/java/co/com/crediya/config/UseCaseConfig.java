package co.com.crediya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.crediya.model.gateway.SolicitudRepository;
import co.com.crediya.usecase.registrarsolicitud.RegistrarSolicitudUseCase;

/**
 * Configuración de los casos de uso de la aplicación.
 * Centraliza la inyección de dependencias para los use cases.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public RegistrarSolicitudUseCase registrarSolicitudUseCase(SolicitudRepository solicitudRepository) {
        return new RegistrarSolicitudUseCase(solicitudRepository);
    }
}
