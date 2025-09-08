package co.com.crediya.model.gateway;


import co.com.crediya.model.Solicitud;
import co.com.crediya.model.DetalleUsuario;
import reactor.core.publisher.Mono;
import co.com.crediya.model.TipoPrestamo;
import java.util.List;
import co.com.crediya.model.PlanPagoCuota;

public interface NotificacionGateway{
    Mono<Void> enviarNotificacionEstado(Solicitud solicitud, String email, String justificacion);
    Mono<Void> enviarNotificacionEstado(Solicitud solicitud, String email, String justificacion, List<PlanPagoCuota> planPago);
    Mono<Void> enviarValidacionAutomaticaSolicitud(Solicitud solicitud, DetalleUsuario detalleUsuario, TipoPrestamo tipoPrestamo, String authorizationToken);
}
