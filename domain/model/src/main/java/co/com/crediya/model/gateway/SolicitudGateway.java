package co.com.crediya.model.gateway;

import co.com.crediya.model.Solicitud;
import reactor.core.publisher.Mono;

public interface SolicitudGateway {
    Mono<Solicitud> guardar(Solicitud solicitud);
    Mono<Solicitud> buscarPorId(String id);
    Mono<Boolean> existeTipoPrestamo(String tipoPrestamoId);
}