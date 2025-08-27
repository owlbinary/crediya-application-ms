package co.com.crediya.usecase.listarsolicitudes;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.crediya.model.DetalleUsuario;
import co.com.crediya.model.EstadoSolicitud;
import co.com.crediya.model.Solicitud;
import co.com.crediya.model.TipoPrestamo;
import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.model.gateway.TipoPrestamoGateway;
import co.com.crediya.model.gateway.ValidacionDocumentoGateway;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ListarSolicitudesUseCaseTest {

    @Mock
    private SolicitudGateway solicitudGateway;

    @Mock
    private ValidacionDocumentoGateway validacionDocumentoGateway;

    @Mock
    private TipoPrestamoGateway tipoPrestamoGateway;

    @InjectMocks
    private ListarSolicitudesUseCase useCase;

    private Solicitud solicitudPrueba;
    private DetalleUsuario detalleUsuarioPrueba;
    private TipoPrestamo tipoPresamoPrueba;
    private String authorizationToken;

    @BeforeEach
    void setUp() {
        LocalDateTime ahora = LocalDateTime.now();
        
        solicitudPrueba = Solicitud.builder()
                .id("1")
                .documentoIdentidad("12345678")
                .monto(BigDecimal.valueOf(1000000))
                .plazo(12)
                .tipoPrestamoId("1")
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .fechaCreacion(ahora)
                .fechaActualizacion(ahora)
                .deudaTotalMensual(BigDecimal.valueOf(500000))
                .build();

        detalleUsuarioPrueba = DetalleUsuario.builder()
                .email("test@test.com")
                .nombre("Juan")
                .apellido("Pérez")
                .salarioBase(BigDecimal.valueOf(5000000))
                .build();

        tipoPresamoPrueba = TipoPrestamo.builder()
                .id("1")
                .descripcion("Préstamo Personal")
                .tasaInteres(BigDecimal.valueOf(1.5))
                .build();

        authorizationToken = "Bearer test-token";
    }

    @Test
    void deberiaListarSolicitudesConDetallesCompletos() {
        when(solicitudGateway.obtenerSolicitudesPendientesRevision(anyInt(), anyInt()))
                .thenReturn(Flux.just(solicitudPrueba));
        when(validacionDocumentoGateway.obtenerDetalleUsuario(anyString(), anyString()))
                .thenReturn(Mono.just(detalleUsuarioPrueba));
        when(tipoPrestamoGateway.buscarPorId(anyString()))
                .thenReturn(Mono.just(tipoPresamoPrueba));

        StepVerifier.create(useCase.ejecutar(0, 10, authorizationToken))
                .expectNextMatches(solicitudDetalle ->
                        solicitudDetalle.getId().equals("1") &&
                        solicitudDetalle.getDocumentoIdentidad().equals("12345678") &&
                        solicitudDetalle.getMonto().equals(BigDecimal.valueOf(1000000)) &&
                        solicitudDetalle.getPlazo().equals(12) &&
                        solicitudDetalle.getTipoPrestamoId().equals("1") &&
                        solicitudDetalle.getDescripcionTipoPrestamo().equals("Préstamo Personal") &&
                        solicitudDetalle.getTasaInteres().equals(BigDecimal.valueOf(1.5)) &&
                        solicitudDetalle.getEstado() == EstadoSolicitud.PENDIENTE_REVISION &&
                        solicitudDetalle.getEmail().equals("test@test.com") &&
                        solicitudDetalle.getNombre().equals("Juan") &&
                        solicitudDetalle.getApellido().equals("Pérez") &&
                        solicitudDetalle.getSalarioBase().equals(BigDecimal.valueOf(5000000)) &&
                        solicitudDetalle.getDeudaTotalMensualSolicitud().equals(BigDecimal.valueOf(500000))
                )
                .verifyComplete();
    }

    @Test
    void deberiaListarSolicitudesCuandoNoHayDetalleUsuario() {
        when(solicitudGateway.obtenerSolicitudesPendientesRevision(anyInt(), anyInt()))
                .thenReturn(Flux.just(solicitudPrueba));
        when(validacionDocumentoGateway.obtenerDetalleUsuario(anyString(), anyString()))
                .thenReturn(Mono.empty());
        when(tipoPrestamoGateway.buscarPorId(anyString()))
                .thenReturn(Mono.just(tipoPresamoPrueba));

        StepVerifier.create(useCase.ejecutar(0, 10, authorizationToken))
                .expectNextMatches(solicitudDetalle ->
                        solicitudDetalle.getId().equals("1") &&
                        solicitudDetalle.getDescripcionTipoPrestamo().equals("Préstamo Personal") &&
                        solicitudDetalle.getEmail() == null &&
                        solicitudDetalle.getNombre() == null &&
                        solicitudDetalle.getApellido() == null &&
                        solicitudDetalle.getSalarioBase() == null
                )
                .verifyComplete();
    }

    @Test
    void deberiaListarSolicitudesCuandoNoHayTipoPrestamo() {
        when(solicitudGateway.obtenerSolicitudesPendientesRevision(anyInt(), anyInt()))
                .thenReturn(Flux.just(solicitudPrueba));
        when(validacionDocumentoGateway.obtenerDetalleUsuario(anyString(), anyString()))
                .thenReturn(Mono.just(detalleUsuarioPrueba));
        when(tipoPrestamoGateway.buscarPorId(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.ejecutar(0, 10, authorizationToken))
                .expectNextMatches(solicitudDetalle ->
                        solicitudDetalle.getId().equals("1") &&
                        solicitudDetalle.getEmail().equals("test@test.com") &&
                        solicitudDetalle.getDescripcionTipoPrestamo() == null &&
                        solicitudDetalle.getTasaInteres() == null
                )
                .verifyComplete();
    }

    @Test
    void deberiaListarSolicitudesCuandoFallaServicioDetalleUsuario() {
        when(solicitudGateway.obtenerSolicitudesPendientesRevision(anyInt(), anyInt()))
                .thenReturn(Flux.just(solicitudPrueba));
        when(validacionDocumentoGateway.obtenerDetalleUsuario(anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Error al consultar detalle usuario")));
        when(tipoPrestamoGateway.buscarPorId(anyString()))
                .thenReturn(Mono.just(tipoPresamoPrueba));

        StepVerifier.create(useCase.ejecutar(0, 10, authorizationToken))
                .expectNextMatches(solicitudDetalle ->
                        solicitudDetalle.getId().equals("1") &&
                        solicitudDetalle.getDescripcionTipoPrestamo().equals("Préstamo Personal") &&
                        solicitudDetalle.getEmail() == null &&
                        solicitudDetalle.getNombre() == null
                )
                .verifyComplete();
    }

    @Test
    void deberiaListarSolicitudesCuandoFallaServicioTipoPrestamo() {
        when(solicitudGateway.obtenerSolicitudesPendientesRevision(anyInt(), anyInt()))
                .thenReturn(Flux.just(solicitudPrueba));
        when(validacionDocumentoGateway.obtenerDetalleUsuario(anyString(), anyString()))
                .thenReturn(Mono.just(detalleUsuarioPrueba));
        when(tipoPrestamoGateway.buscarPorId(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Error al consultar tipo préstamo")));

        StepVerifier.create(useCase.ejecutar(0, 10, authorizationToken))
                .expectNextMatches(solicitudDetalle ->
                        solicitudDetalle.getId().equals("1") &&
                        solicitudDetalle.getEmail().equals("test@test.com") &&
                        solicitudDetalle.getDescripcionTipoPrestamo() == null &&
                        solicitudDetalle.getTasaInteres() == null
                )
                .verifyComplete();
    }

    @Test
    void deberiaRetornarFluxVacioCuandoNoHaySolicitudes() {
        when(solicitudGateway.obtenerSolicitudesPendientesRevision(anyInt(), anyInt()))
                .thenReturn(Flux.empty());

        StepVerifier.create(useCase.ejecutar(0, 10, authorizationToken))
                .verifyComplete();
    }

    @Test
    void deberiaListarMultiplesSolicitudes() {
        Solicitud segundaSolicitud = Solicitud.builder()
                .id("2")
                .documentoIdentidad("87654321")
                .monto(BigDecimal.valueOf(2000000))
                .plazo(24)
                .tipoPrestamoId("2")
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        when(solicitudGateway.obtenerSolicitudesPendientesRevision(anyInt(), anyInt()))
                .thenReturn(Flux.just(solicitudPrueba, segundaSolicitud));
        when(validacionDocumentoGateway.obtenerDetalleUsuario(anyString(), anyString()))
                .thenReturn(Mono.just(detalleUsuarioPrueba));
        when(tipoPrestamoGateway.buscarPorId(anyString()))
                .thenReturn(Mono.just(tipoPresamoPrueba));

        StepVerifier.create(useCase.ejecutar(0, 10, authorizationToken))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void deberiaManejarTokenNulo() {
        when(solicitudGateway.obtenerSolicitudesPendientesRevision(anyInt(), anyInt()))
                .thenReturn(Flux.just(solicitudPrueba));
        when(validacionDocumentoGateway.obtenerDetalleUsuario(anyString(), eq(null)))
                .thenReturn(Mono.just(detalleUsuarioPrueba));
        when(tipoPrestamoGateway.buscarPorId(anyString()))
                .thenReturn(Mono.just(tipoPresamoPrueba));

        StepVerifier.create(useCase.ejecutar(0, 10, null))
                .expectNextMatches(solicitudDetalle ->
                        solicitudDetalle.getId().equals("1") &&
                        solicitudDetalle.getEmail().equals("test@test.com")
                )
                .verifyComplete();
    }
}
