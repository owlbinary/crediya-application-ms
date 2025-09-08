package co.com.crediya.model.gateway;

import co.com.crediya.model.Solicitud;
import co.com.crediya.model.EstadoSolicitud;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SolicitudGateway {
    Mono<Solicitud> guardar(Solicitud solicitud);
    Mono<Solicitud> buscarPorId(String id);
    Mono<Boolean> existeTipoPrestamo(String tipoPrestamoId);
    Flux<Solicitud> obtenerSolicitudesPendientesRevision(int pagina, int tamano, String estado);
    Mono<Solicitud> actualizar(Solicitud solicitud);
    Flux<Solicitud> findByDocumentoIdentidadAndEstado(String documentoIdentidad, EstadoSolicitud estado);
}