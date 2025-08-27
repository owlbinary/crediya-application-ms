package co.com.crediya.usecase.listarsolicitudes;

import co.com.crediya.model.DetalleUsuario;
import co.com.crediya.model.Solicitud;
import co.com.crediya.model.TipoPrestamo;
import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.model.gateway.TipoPrestamoGateway;
import co.com.crediya.model.gateway.ValidacionDocumentoGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ListarSolicitudesUseCase {
    
    private final SolicitudGateway solicitudGateway;
    private final ValidacionDocumentoGateway validacionDocumentoGateway;
    private final TipoPrestamoGateway tipoPrestamoGateway;
    
    public Flux<SolicitudConDetalle> ejecutar(int pagina, int tamano, String authorizationToken) {
        return solicitudGateway.obtenerSolicitudesPendientesRevision(pagina, tamano)
                .flatMap(solicitud -> enriquecerConDetalles(solicitud, authorizationToken));
    }
    
    private Mono<SolicitudConDetalle> enriquecerConDetalles(Solicitud solicitud, String authorizationToken) {
        Mono<DetalleUsuario> detalleUsuarioMono = 
            validacionDocumentoGateway.obtenerDetalleUsuario(solicitud.getDocumentoIdentidad(), authorizationToken)
                .onErrorReturn(new DetalleUsuario())
                .switchIfEmpty(Mono.just(new DetalleUsuario()));

        Mono<TipoPrestamo> tipoPrestamoMono = 
            tipoPrestamoGateway.buscarPorId(solicitud.getTipoPrestamoId())
                .onErrorReturn(new TipoPrestamo())
                .switchIfEmpty(Mono.just(new TipoPrestamo()));
        
        return Mono.zip(detalleUsuarioMono, tipoPrestamoMono)
                .map(tuple -> {
                    DetalleUsuario detalleUsuario = tuple.getT1();
                    TipoPrestamo tipoPrestamo = tuple.getT2();
                    
                    boolean tieneDetalleUsuario = detalleUsuario.getEmail() != null;
                    boolean tieneTipoPrestamo = tipoPrestamo.getDescripcion() != null;
                    
                    return SolicitudConDetalle.builder()
                            .id(solicitud.getId())
                            .documentoIdentidad(solicitud.getDocumentoIdentidad())
                            .monto(solicitud.getMonto())
                            .plazo(solicitud.getPlazo())
                            .tipoPrestamoId(solicitud.getTipoPrestamoId())
                            .descripcionTipoPrestamo(tieneTipoPrestamo ? tipoPrestamo.getDescripcion() : null)
                            .tasaInteres(tieneTipoPrestamo ? tipoPrestamo.getTasaInteres() : null)
                            .estado(solicitud.getEstado())
                            .fechaCreacion(solicitud.getFechaCreacion())
                            .fechaActualizacion(solicitud.getFechaActualizacion())
                            .email(tieneDetalleUsuario ? detalleUsuario.getEmail() : null)
                            .nombre(tieneDetalleUsuario ? detalleUsuario.getNombre() : null)
                            .apellido(tieneDetalleUsuario ? detalleUsuario.getApellido() : null)
                            .salarioBase(tieneDetalleUsuario ? detalleUsuario.getSalarioBase() : null)
                            .deudaTotalMensualSolicitud(solicitud.getDeudaTotalMensual())
                            .build();
                });
    }
}
