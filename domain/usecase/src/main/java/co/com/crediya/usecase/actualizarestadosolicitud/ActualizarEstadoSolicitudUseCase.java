package co.com.crediya.usecase.actualizarestadosolicitud;

import co.com.crediya.model.EstadoSolicitud;
import co.com.crediya.model.Solicitud;
import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.model.gateway.NotificacionGateway;
import co.com.crediya.model.gateway.ValidacionDocumentoGateway;
import co.com.crediya.model.PlanPagoCuota;
import java.util.List;
import co.com.crediya.model.exception.SolicitudNoEncontradaException;
import co.com.crediya.model.exception.EstadoSolicitudNoValidoException;
import co.com.crediya.model.exception.NotificacionEstadoException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ActualizarEstadoSolicitudUseCase {

    private final SolicitudGateway solicitudGateway;
    private final NotificacionGateway notificacionGateway;
    private final ValidacionDocumentoGateway validacionDocumentoGateway;

    public Mono<Solicitud> ejecutar(String solicitudId, String nuevoEstado, String justificacion,
            String authorizationToken, List<PlanPagoCuota> planPago) {
        return solicitudGateway.buscarPorId(solicitudId)
                .switchIfEmpty(Mono.error(new SolicitudNoEncontradaException("Solicitud no encontrada")))
                .flatMap(solicitud -> {
                    if (solicitud.getEstado() == EstadoSolicitud.APROBADO ||
                            solicitud.getEstado() == EstadoSolicitud.RECHAZADO ||
                            solicitud.getEstado() == EstadoSolicitud.REVISION_MANUAL) {
                        return Mono.error(new EstadoSolicitudNoValidoException(
                                "La solicitud no puede ser actualizada porque ya se encuentra en estado final: "
                                        + solicitud.getEstado().name()));
                    }
                    EstadoSolicitud estado;
                    try {
                        estado = EstadoSolicitud.valueOf(nuevoEstado.toUpperCase());
                    } catch (Exception e) {
                        return Mono.error(new EstadoSolicitudNoValidoException("Estado no válido: " + nuevoEstado));
                    }
                    if (estado != EstadoSolicitud.APROBADO && estado != EstadoSolicitud.RECHAZADO) {
                        return Mono.error(new EstadoSolicitudNoValidoException("Solo se permite aprobar o rechazar"));
                    }
                    solicitud.setEstado(estado);
                    solicitud.setFechaActualizacion(java.time.LocalDateTime.now());
                    
                    if (authorizationToken == null || authorizationToken.trim().isEmpty()) {
                        return solicitudGateway.actualizar(solicitud)
                                .flatMap(s -> notificacionGateway
                                        .enviarNotificacionEstado(s, solicitud.getEmail(), justificacion, planPago)
                                        .thenReturn(s))
                                .onErrorResume(e -> Mono.error(
                                        new NotificacionEstadoException("Error notificando al solicitante")));
                    }
                    
                    return validacionDocumentoGateway
                            .obtenerDetalleUsuario(solicitud.getDocumentoIdentidad(), authorizationToken)
                            .flatMap(detalleUsuario -> solicitudGateway.actualizar(solicitud)
                                    .flatMap(s -> notificacionGateway
                                            .enviarNotificacionEstado(s, detalleUsuario.getEmail(), justificacion,
                                                    planPago)
                                            .thenReturn(s))
                                    .onErrorResume(e -> Mono.error(
                                            new NotificacionEstadoException("Error notificando al solicitante"))));
                });
    }
}