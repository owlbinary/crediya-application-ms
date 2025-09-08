package co.com.crediya.sqs.adapter;

import co.com.crediya.model.Solicitud;
import co.com.crediya.model.DetalleUsuario;
import co.com.crediya.model.PlanPagoCuota;
import co.com.crediya.model.TipoPrestamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.math.BigDecimal;
import co.com.crediya.model.EstadoSolicitud;

import javax.management.RuntimeErrorException;

import static org.junit.jupiter.api.Assertions.*;

class NotificacionAdapterTest {
    private NotificacionAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new NotificacionAdapter("dummy-queue-url");
    }

    @Test
    void testEnviarNotificacionEstadoSinPlanPago() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(null);
        Mono<Void> result = adapter.enviarNotificacionEstado(solicitud, "test@mail.com", "justificacion");
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void testEnviarNotificacionEstadoConPlanPagoAprobado() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(co.com.crediya.model.EstadoSolicitud.APROBADO);
        PlanPagoCuota cuota = Mockito.mock(PlanPagoCuota.class);
        Mockito.when(cuota.getNumeroCuota()).thenReturn(1);
        Mockito.when(cuota.getCuota()).thenReturn(1000.0);
        Mockito.when(cuota.getAbonoCapital()).thenReturn(800.0);
        Mockito.when(cuota.getInteres()).thenReturn(200.0);
        Mockito.when(cuota.getSaldoRestante()).thenReturn(0.0);
        List<PlanPagoCuota> planPago = Arrays.asList(cuota);
        Mono<Void> result = adapter.enviarNotificacionEstado(solicitud, "test@mail.com", "justificacion", planPago);
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void testConstruirMensajeEstadoRechazadoSinPlanPago() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(co.com.crediya.model.EstadoSolicitud.RECHAZADO);
        String mensaje = adapter.construirMensaje(solicitud, "test@mail.com", "justificacion", null);
        assertTrue(mensaje.contains("\"estado\":\"RECHAZADO\""));
        assertFalse(mensaje.contains("planPago"));
    }

    @Test
    void testConstruirMensajeEstadoAprobadoConPlanPago() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(co.com.crediya.model.EstadoSolicitud.APROBADO);
        PlanPagoCuota cuota = Mockito.mock(PlanPagoCuota.class);
        Mockito.when(cuota.getNumeroCuota()).thenReturn(1);
        Mockito.when(cuota.getCuota()).thenReturn(1000.0);
        Mockito.when(cuota.getAbonoCapital()).thenReturn(800.0);
        Mockito.when(cuota.getInteres()).thenReturn(200.0);
        Mockito.when(cuota.getSaldoRestante()).thenReturn(0.0);
        List<PlanPagoCuota> planPago = Collections.singletonList(cuota);
        String mensaje = adapter.construirMensaje(solicitud, "test@mail.com", "justificacion", planPago);
        assertTrue(mensaje.contains("planPago"));
        assertTrue(mensaje.contains("\"estado\":\"APROBADO\""));
    }

    @Test
    void testEnviarValidacionAutomaticaSolicitud() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        DetalleUsuario usuario = DetalleUsuario.builder().nombre("Juan").apellido("Perez").email("jp@mail.com").build();
        TipoPrestamo tipoPrestamo = Mockito.mock(TipoPrestamo.class);
        Mono<Void> result = adapter.enviarValidacionAutomaticaSolicitud(solicitud, usuario, tipoPrestamo, "token");
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void testConstruirMensajeEstadoAprobadoSinPlanPago() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(EstadoSolicitud.APROBADO);
        String mensaje = adapter.construirMensaje(solicitud, "mail@test.com", "just", null);
        assertTrue(mensaje.contains("\"estado\":\"APROBADO\""));
        assertFalse(mensaje.contains("planPago"));
    }

    @Test
    void testConstruirMensajeEstadoAprobadoPlanPagoVacio() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(EstadoSolicitud.APROBADO);
        String mensaje = adapter.construirMensaje(solicitud, "mail@test.com", "just", Collections.emptyList());
        assertTrue(mensaje.contains("\"estado\":\"APROBADO\""));
        assertFalse(mensaje.contains("planPago"));
    }

    @Test
    void testConstruirMensajeEstadoNull() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(null);
        String mensaje = adapter.construirMensaje(solicitud, "mail@test.com", "just", null);
        assertTrue(mensaje.contains("\"estado\":\"\""));
        assertFalse(mensaje.contains("planPago"));
    }

    @Test
    void testConstruirMensajeEstadoNoAprobadoConPlanPago() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(EstadoSolicitud.RECHAZADO);
        PlanPagoCuota cuota = Mockito.mock(PlanPagoCuota.class);
        Mockito.when(cuota.getNumeroCuota()).thenReturn(1);
        Mockito.when(cuota.getCuota()).thenReturn(1000.0);
        Mockito.when(cuota.getAbonoCapital()).thenReturn(800.0);
        Mockito.when(cuota.getInteres()).thenReturn(200.0);
        Mockito.when(cuota.getSaldoRestante()).thenReturn(0.0);
        List<PlanPagoCuota> planPago = Collections.singletonList(cuota);
        String mensaje = adapter.construirMensaje(solicitud, "mail@test.com", "just", planPago);
        assertTrue(mensaje.contains("\"estado\":\"RECHAZADO\""));
        assertFalse(mensaje.contains("planPago"));
    }
    

    @Test
    void testEnviarValidacionAutomaticaSolicitudDetalleUsuarioCamposNull() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        DetalleUsuario usuario = DetalleUsuario.builder().nombre(null).apellido(null).email(null).salarioBase(null).build();
        TipoPrestamo tipoPrestamo = Mockito.mock(TipoPrestamo.class);
        Mono<Void> result = adapter.enviarValidacionAutomaticaSolicitud(solicitud, usuario, tipoPrestamo, "token");
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void testEnviarValidacionAutomaticaSolicitudTipoPrestamoNull() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        DetalleUsuario usuario = DetalleUsuario.builder().nombre("Juan").apellido("Perez").email("jp@mail.com").build();
        Mono<Void> result = adapter.enviarValidacionAutomaticaSolicitud(solicitud, usuario, null, "token");
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void testFormatearPlanPagoParaSNSListaVacia() {
        List<PlanPagoCuota> planPago = Collections.emptyList();
        String result = adapter.formatearPlanPagoParaSNS(planPago);
        assertEquals("[]", result);
    }

    @Test
    void testSafeBigDecimalNull() {
        String result = NotificacionUtils.safeBigDecimal(null);
        assertEquals("null", result);
    }

    @Test
    void testSafeBigDecimalNotNull() {
        String result = NotificacionUtils.safeBigDecimal(new BigDecimal("99.99"));
        assertEquals("99.99", result);
    }

    @Test
    void testSafeEnumNull() {
        String result = NotificacionUtils.safeEnum(null);
        assertEquals("", result);
    }

    @Test
    void testSafeEnumNotNull() {
        String result = NotificacionUtils.safeEnum(EstadoSolicitud.APROBADO);
        assertEquals("APROBADO", result);
    }

    @Test
    void testSafeIntegerNull() {
        String result = NotificacionUtils.safeInteger(null);
        assertEquals("null", result);
    }

    @Test
    void testSafeIntegerNotNull() {
        String result = NotificacionUtils.safeInteger(42);
        assertEquals("42", result);
    }

    @Test
    void testSafeBooleanNull() {
        String result = NotificacionUtils.safeBoolean(null);
        assertEquals("null", result);
    }

    @Test
    void testSafeBooleanTrue() {
        String result = NotificacionUtils.safeBoolean(Boolean.TRUE);
        assertEquals("true", result);
    }

    @Test
    void testSafeBooleanFalse() {
        String result = NotificacionUtils.safeBoolean(Boolean.FALSE);
        assertEquals("false", result);
    }

    @Test
    void testGetUsuarioCampoUsuarioNull() {
        String result = NotificacionUtils.getUsuarioCampo(null, DetalleUsuario::getNombre);
        assertEquals("", result);
    }

    @Test
    void testGetUsuarioCampoGetterNull() {
        DetalleUsuario usuario = DetalleUsuario.builder().nombre(null).build();
        String result = NotificacionUtils.getUsuarioCampo(usuario, DetalleUsuario::getNombre);
        assertEquals("", result);
    }

    @Test
    void testGetUsuarioCampoGetterNotNull() {
        DetalleUsuario usuario = DetalleUsuario.builder().nombre("Pepe").build();
        String result = NotificacionUtils.getUsuarioCampo(usuario, DetalleUsuario::getNombre);
        assertEquals("Pepe", result);
    }

    @Test
    void testGetUsuarioCampoBigDecimalGetterNullUtils() {
        DetalleUsuario usuario = DetalleUsuario.builder().salarioBase(null).build();
        String result = NotificacionUtils.getUsuarioCampoBigDecimal(usuario, DetalleUsuario::getSalarioBase);
        assertEquals("null", result);
    }

    @Test
    void testGetUsuarioCampoBigDecimalGetterNotNullUtilsVariant() {
        DetalleUsuario usuario = DetalleUsuario.builder().salarioBase(new BigDecimal("123.45")).build();
        String result = NotificacionUtils.getUsuarioCampoBigDecimal(usuario, DetalleUsuario::getSalarioBase);
        assertEquals("123.45", result);
    }
}
