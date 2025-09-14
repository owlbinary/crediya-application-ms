package co.com.crediya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.model.gateway.TipoPrestamoGateway;
import co.com.crediya.model.gateway.ValidacionDocumentoGateway;
import co.com.crediya.usecase.listarsolicitudes.ListarSolicitudesUseCase;
import co.com.crediya.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import co.com.crediya.model.gateway.NotificacionGateway;
import co.com.crediya.model.gateway.DebtCapacityEventGateway;
import co.com.crediya.usecase.actualizarestadosolicitud.ActualizarEstadoSolicitudUseCase;

/**
 * Configuración de los casos de uso de la aplicación.
 * Centraliza la inyección de dependencias para los use cases.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public RegistrarSolicitudUseCase registrarSolicitudUseCase(
            SolicitudGateway solicitudRepository,
            ValidacionDocumentoGateway validacionDocumentoGateway,
            TipoPrestamoGateway tipoPrestamoGateway,
            NotificacionGateway notificacionGateway,
            DebtCapacityEventGateway debtCapacityEventGateway) {
        return new RegistrarSolicitudUseCase(solicitudRepository, validacionDocumentoGateway, tipoPrestamoGateway, notificacionGateway, debtCapacityEventGateway);
    }
    
    @Bean
    public ListarSolicitudesUseCase listarSolicitudesUseCase(
            SolicitudGateway solicitudGateway,
            ValidacionDocumentoGateway validacionDocumentoGateway,
            TipoPrestamoGateway tipoPrestamoGateway) {
        return new ListarSolicitudesUseCase(solicitudGateway, validacionDocumentoGateway, tipoPrestamoGateway);
    }

    @Bean
    public ActualizarEstadoSolicitudUseCase actualizarEstadoSolicitudUseCase(
            SolicitudGateway solicitudGateway,
            NotificacionGateway notificacionGateway,
            ValidacionDocumentoGateway validacionDocumentoGateway) {
        return new ActualizarEstadoSolicitudUseCase(solicitudGateway, notificacionGateway, validacionDocumentoGateway);
    }
}
