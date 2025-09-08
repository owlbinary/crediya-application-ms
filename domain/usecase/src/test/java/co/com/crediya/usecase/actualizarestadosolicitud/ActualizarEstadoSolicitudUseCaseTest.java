package co.com.crediya.usecase.actualizarestadosolicitud;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.crediya.model.EstadoSolicitud;
import co.com.crediya.model.Solicitud;
import co.com.crediya.model.exception.EstadoSolicitudNoValidoException;
import co.com.crediya.model.exception.NotificacionEstadoException;
import co.com.crediya.model.exception.SolicitudNoEncontradaException;
import co.com.crediya.model.gateway.NotificacionGateway;
import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.model.gateway.ValidacionDocumentoGateway;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ActualizarEstadoSolicitudUseCaseTest {
    private SolicitudGateway solicitudGateway;
    private NotificacionGateway notificacionGateway;
    private ValidacionDocumentoGateway validacionDocumentoGateway;
    private ActualizarEstadoSolicitudUseCase useCase;

    @BeforeEach
    void setUp() {
        solicitudGateway = mock(SolicitudGateway.class);
        notificacionGateway = mock(NotificacionGateway.class);
        validacionDocumentoGateway = mock(ValidacionDocumentoGateway.class);
        useCase = new ActualizarEstadoSolicitudUseCase(solicitudGateway, notificacionGateway,
                validacionDocumentoGateway);
    }

    @Test
    void testSolicitudNoEncontrada() {
        when(solicitudGateway.buscarPorId("1")).thenReturn(Mono.empty());
        StepVerifier.create(useCase.ejecutar("1", "APROBADO", "just", "token", Collections.emptyList()))
                .expectError(SolicitudNoEncontradaException.class)
                .verify();
    }

    @Test
    void testEstadoFinalNoActualizable() {
        Solicitud solicitud = new Solicitud();
        solicitud.setEstado(EstadoSolicitud.APROBADO);
        when(solicitudGateway.buscarPorId("1")).thenReturn(Mono.just(solicitud));
        StepVerifier.create(useCase.ejecutar("1", "RECHAZADO", "just", "token", Collections.emptyList()))
                .expectError(EstadoSolicitudNoValidoException.class)
                .verify();
    }

    @Test
    void testEstadoNoValido() {
        Solicitud solicitud = new Solicitud();
        solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        when(solicitudGateway.buscarPorId("1")).thenReturn(Mono.just(solicitud));
        StepVerifier.create(useCase.ejecutar("1", "NO_EXISTE", "just", "token", Collections.emptyList()))
                .expectError(EstadoSolicitudNoValidoException.class)
                .verify();
    }

    @Test
    void testSoloAprobarORechazar() {
        Solicitud solicitud = new Solicitud();
        solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        when(solicitudGateway.buscarPorId("1")).thenReturn(Mono.just(solicitud));
        StepVerifier.create(useCase.ejecutar("1", "PENDIENTE_REVISION", "just", "token", Collections.emptyList()))
                .expectError(EstadoSolicitudNoValidoException.class)
                .verify();
    }

    @Test
    void testErrorNotificacion() {
        Solicitud solicitud = new Solicitud();
        solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        when(solicitudGateway.buscarPorId("1")).thenReturn(Mono.just(solicitud));
        co.com.crediya.model.DetalleUsuario detalleUsuario = co.com.crediya.model.DetalleUsuario.builder()
                .email("mail@test.com").build();
        when(validacionDocumentoGateway.obtenerDetalleUsuario(any(), any())).thenReturn(Mono.just(detalleUsuario));
        when(solicitudGateway.actualizar(any())).thenReturn(Mono.just(solicitud));
        when(notificacionGateway.enviarNotificacionEstado(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("fail")));
        StepVerifier.create(useCase.ejecutar("1", "APROBADO", "just", "token", Collections.emptyList()))
                .expectError(NotificacionEstadoException.class)
                .verify();
    }

    @Test
    void testExitoAprobacion() {
        Solicitud solicitud = new Solicitud();
        solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        when(solicitudGateway.buscarPorId("1")).thenReturn(Mono.just(solicitud));
        co.com.crediya.model.DetalleUsuario detalleUsuario = co.com.crediya.model.DetalleUsuario.builder()
                .email("mail@test.com").build();
        when(validacionDocumentoGateway.obtenerDetalleUsuario(any(), any())).thenReturn(Mono.just(detalleUsuario));
        when(solicitudGateway.actualizar(any())).thenReturn(Mono.just(solicitud));
        when(notificacionGateway.enviarNotificacionEstado(any(), any(), any(), any())).thenReturn(Mono.empty());
        StepVerifier.create(useCase.ejecutar("1", "APROBADO", "just", "token", Collections.emptyList()))
                .expectNext(solicitud)
                .verifyComplete();
    }

    @Test
    void testEstadoFinalNoActualizableRechazado() {
        Solicitud solicitud = new Solicitud();
        solicitud.setEstado(EstadoSolicitud.RECHAZADO);
        when(solicitudGateway.buscarPorId("1")).thenReturn(Mono.just(solicitud));
        StepVerifier.create(useCase.ejecutar("1", "APROBADO", "just", "token", Collections.emptyList()))
                .expectError(EstadoSolicitudNoValidoException.class)
                .verify();
    }

    @Test
    void testEstadoFinalNoActualizableRevisionManual() {
        Solicitud solicitud = new Solicitud();
        solicitud.setEstado(EstadoSolicitud.REVISION_MANUAL);
        when(solicitudGateway.buscarPorId("1")).thenReturn(Mono.just(solicitud));
        StepVerifier.create(useCase.ejecutar("1", "APROBADO", "just", "token", Collections.emptyList()))
                .expectError(EstadoSolicitudNoValidoException.class)
                .verify();
    }

    @Test
    void testErrorObtenerDetalleUsuario() {
        Solicitud solicitud = new Solicitud();
        solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        when(solicitudGateway.buscarPorId("1")).thenReturn(Mono.just(solicitud));
        when(validacionDocumentoGateway.obtenerDetalleUsuario(any(), any())).thenReturn(Mono.error(new RuntimeException("fail")));
        StepVerifier.create(useCase.ejecutar("1", "APROBADO", "just", "token", Collections.emptyList()))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testErrorActualizarSolicitud() {
        Solicitud solicitud = new Solicitud();
        solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        co.com.crediya.model.DetalleUsuario detalleUsuario = co.com.crediya.model.DetalleUsuario.builder()
                .email("mail@test.com").build();
        when(solicitudGateway.buscarPorId("1")).thenReturn(Mono.just(solicitud));
        when(validacionDocumentoGateway.obtenerDetalleUsuario(any(), any())).thenReturn(Mono.just(detalleUsuario));
        when(solicitudGateway.actualizar(any())).thenReturn(Mono.error(new RuntimeException("fail")));
        StepVerifier.create(useCase.ejecutar("1", "APROBADO", "just", "token", Collections.emptyList()))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testExitoRechazo() {
        Solicitud solicitud = new Solicitud();
        solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        co.com.crediya.model.DetalleUsuario detalleUsuario = co.com.crediya.model.DetalleUsuario.builder()
                .email("mail@test.com").build();
        when(solicitudGateway.buscarPorId("1")).thenReturn(Mono.just(solicitud));
        when(validacionDocumentoGateway.obtenerDetalleUsuario(any(), any())).thenReturn(Mono.just(detalleUsuario));
        when(solicitudGateway.actualizar(any())).thenReturn(Mono.just(solicitud));
        when(notificacionGateway.enviarNotificacionEstado(any(), any(), any(), any())).thenReturn(Mono.empty());
        StepVerifier.create(useCase.ejecutar("1", "RECHAZADO", "just", "token", Collections.emptyList()))
                .expectNext(solicitud)
                .verifyComplete();
    }
}
