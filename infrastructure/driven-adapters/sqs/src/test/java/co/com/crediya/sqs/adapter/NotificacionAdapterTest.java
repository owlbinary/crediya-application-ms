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
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import co.com.crediya.model.EstadoSolicitud;

import javax.management.RuntimeErrorException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class NotificacionAdapterTest {
    
    private static final String TEST_EMAIL = "test@mail.com";
    private static final String TEST_JUSTIFICACION = "justificacion";
    private static final String TEST_TOKEN = "token";
    private static final String PLAN_PAGO = "planPago";
    private static final String ESTADO_APROBADO = "\"estado\":\"APROBADO\"";
    private static final String ESTADO_RECHAZADO = "\"estado\":\"RECHAZADO\"";
    private static final String MAIL_TEST = "mail@test.com";
    private static final String APELLIDO_PEREZ = "Perez";
    private static final String MONTO_10000 = "10000";
    private static final String TIPO_PRESTAMO_ID = "TIPO1";
    private static final String DOCUMENTO_TEST = "12345678";
    private static final String EMAIL_JUAN_TEST = "juan@test.com";
    
    private NotificacionAdapter adapter;

    @BeforeEach
    void setUp() {
        SqsAsyncClient mockSqsClient = Mockito.mock(SqsAsyncClient.class);
        
        SendMessageResponse mockResponse = SendMessageResponse.builder()
                .messageId("test-message-id")
                .build();
        
        when(mockSqsClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));
        
        adapter = new NotificacionAdapter(mockSqsClient, "dummy-notificaciones-queue-url", "dummy-capacidad-queue-url");
    }

    @Test
    void testEnviarNotificacionEstadoSinPlanPago() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(null);
        Mono<Void> result = adapter.enviarNotificacionEstado(solicitud, TEST_EMAIL, TEST_JUSTIFICACION);
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
        Mono<Void> result = adapter.enviarNotificacionEstado(solicitud, TEST_EMAIL, TEST_JUSTIFICACION, planPago);
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void testConstruirMensajeEstadoRechazadoSinPlanPago() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(co.com.crediya.model.EstadoSolicitud.RECHAZADO);
        String mensaje = adapter.construirMensaje(solicitud, TEST_EMAIL, TEST_JUSTIFICACION, null);
        assertTrue(mensaje.contains(ESTADO_RECHAZADO));
        assertFalse(mensaje.contains(PLAN_PAGO));
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
        String mensaje = adapter.construirMensaje(solicitud, TEST_EMAIL, TEST_JUSTIFICACION, planPago);
        assertTrue(mensaje.contains(PLAN_PAGO));
        assertTrue(mensaje.contains(ESTADO_APROBADO));
    }

    @Test
    void testEnviarValidacionAutomaticaSolicitud() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        DetalleUsuario usuario = DetalleUsuario.builder().nombre("Juan").apellido(APELLIDO_PEREZ).email("jp@mail.com").build();
        TipoPrestamo tipoPrestamo = Mockito.mock(TipoPrestamo.class);
        Mono<Void> result = adapter.enviarValidacionAutomaticaSolicitud(solicitud, usuario, tipoPrestamo, TEST_TOKEN);
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void testConstruirMensajeEstadoAprobadoSinPlanPago() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(EstadoSolicitud.APROBADO);
        String mensaje = adapter.construirMensaje(solicitud, MAIL_TEST, "just", null);
        assertTrue(mensaje.contains(ESTADO_APROBADO));
        assertFalse(mensaje.contains(PLAN_PAGO));
    }

    @Test
    void testConstruirMensajeEstadoAprobadoPlanPagoVacio() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(EstadoSolicitud.APROBADO);
        String mensaje = adapter.construirMensaje(solicitud, MAIL_TEST, "just", Collections.emptyList());
        assertTrue(mensaje.contains(ESTADO_APROBADO));
        assertFalse(mensaje.contains(PLAN_PAGO));
    }

    @Test
    void testConstruirMensajeEstadoNull() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(null);
        String mensaje = adapter.construirMensaje(solicitud, MAIL_TEST, "just", null);
        assertTrue(mensaje.contains("\"estado\":\"\""));
        assertFalse(mensaje.contains(PLAN_PAGO));
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
        String mensaje = adapter.construirMensaje(solicitud, MAIL_TEST, "just", planPago);
        assertTrue(mensaje.contains(ESTADO_RECHAZADO));
        assertFalse(mensaje.contains(PLAN_PAGO));
    }
    

    @Test
    void testEnviarValidacionAutomaticaSolicitudDetalleUsuarioCamposNull() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        DetalleUsuario usuario = DetalleUsuario.builder().nombre(null).apellido(null).email(null).salarioBase(null).build();
        TipoPrestamo tipoPrestamo = Mockito.mock(TipoPrestamo.class);
        Mono<Void> result = adapter.enviarValidacionAutomaticaSolicitud(solicitud, usuario, tipoPrestamo, TEST_TOKEN);
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void testEnviarValidacionAutomaticaSolicitudTipoPrestamoNull() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        DetalleUsuario usuario = DetalleUsuario.builder().nombre("Juan").apellido(APELLIDO_PEREZ).email("jp@mail.com").build();
        Mono<Void> result = adapter.enviarValidacionAutomaticaSolicitud(solicitud, usuario, null, TEST_TOKEN);
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

    @Test
    void testConstruirMensajeConJustificacionNull() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(EstadoSolicitud.RECHAZADO);
        Mockito.when(solicitud.getMonto()).thenReturn(new BigDecimal(MONTO_10000));
        Mockito.when(solicitud.getPlazo()).thenReturn(12);
        
        String mensaje = adapter.construirMensaje(solicitud, TEST_EMAIL, null, null);
        
        assertTrue(mensaje.contains("\"justificacion\":\"null\""));
        assertTrue(mensaje.contains(ESTADO_RECHAZADO));
    }

    @Test
    void testConstruirMensajeConCamposCompletos() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(EstadoSolicitud.APROBADO);
        Mockito.when(solicitud.getMonto()).thenReturn(new BigDecimal("15000.50"));
        Mockito.when(solicitud.getPlazo()).thenReturn(24);
        
        String mensaje = adapter.construirMensaje(solicitud, TEST_EMAIL, "Aprobado por buenos ingresos", null);
        
        assertTrue(mensaje.contains("\"monto\":\"15000.50\""));
        assertTrue(mensaje.contains("\"plazo\":\"24\""));
        assertTrue(mensaje.contains("\"justificacion\":\"Aprobado por buenos ingresos\""));
        assertTrue(mensaje.contains(ESTADO_APROBADO));
    }

    @Test
    void testFormatearPlanPagoConMultiplesCuotas() {
        PlanPagoCuota cuota1 = Mockito.mock(PlanPagoCuota.class);
        Mockito.when(cuota1.getNumeroCuota()).thenReturn(1);
        Mockito.when(cuota1.getCuota()).thenReturn(1000.0);
        Mockito.when(cuota1.getAbonoCapital()).thenReturn(800.0);
        Mockito.when(cuota1.getInteres()).thenReturn(200.0);
        Mockito.when(cuota1.getSaldoRestante()).thenReturn(9000.0);
        
        PlanPagoCuota cuota2 = Mockito.mock(PlanPagoCuota.class);
        Mockito.when(cuota2.getNumeroCuota()).thenReturn(2);
        Mockito.when(cuota2.getCuota()).thenReturn(1000.0);
        Mockito.when(cuota2.getAbonoCapital()).thenReturn(820.0);
        Mockito.when(cuota2.getInteres()).thenReturn(180.0);
        Mockito.when(cuota2.getSaldoRestante()).thenReturn(8180.0);
        
        List<PlanPagoCuota> planPago = Arrays.asList(cuota1, cuota2);
        String result = adapter.formatearPlanPagoParaSNS(planPago);
        
        assertTrue(result.contains("\"numero_cuota\":1"));
        assertTrue(result.contains("\"numero_cuota\":2"));
        assertTrue(result.contains("\"saldo_restante\":9000.00"));
        assertTrue(result.contains("\"saldo_restante\":8180.00"));
        assertTrue(result.contains("},{"));
    }

    @Test
    void testConstruirMensajeValidacionAutomaticaConTokenNull() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getDocumentoIdentidad()).thenReturn("12345678");
        Mockito.when(solicitud.getMonto()).thenReturn(new BigDecimal("10000"));
        Mockito.when(solicitud.getPlazo()).thenReturn(12);
        Mockito.when(solicitud.getDeudaTotalMensual()).thenReturn(new BigDecimal("500"));
        
        DetalleUsuario usuario = DetalleUsuario.builder()
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@test.com")
                .salarioBase(new BigDecimal("3000"))
                .build();
        
        TipoPrestamo tipoPrestamo = Mockito.mock(TipoPrestamo.class);
        Mockito.when(tipoPrestamo.getId()).thenReturn("TIPO1");
        Mockito.when(tipoPrestamo.getNombre()).thenReturn("Préstamo Personal");
        
        Mono<Void> result = adapter.enviarValidacionAutomaticaSolicitud(solicitud, usuario, tipoPrestamo, null);
        
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void testConstruirMensajeValidacionAutomaticaConTokenConBearer() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getDocumentoIdentidad()).thenReturn("12345678");
        Mockito.when(solicitud.getMonto()).thenReturn(new BigDecimal("10000"));
        Mockito.when(solicitud.getPlazo()).thenReturn(12);
        Mockito.when(solicitud.getDeudaTotalMensual()).thenReturn(new BigDecimal("500"));
        
        DetalleUsuario usuario = DetalleUsuario.builder()
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@test.com")
                .salarioBase(new BigDecimal("3000"))
                .build();
        
        TipoPrestamo tipoPrestamo = Mockito.mock(TipoPrestamo.class);
        Mockito.when(tipoPrestamo.getId()).thenReturn("TIPO1");
        
        String tokenConBearer = "Bearer abc123 def456";
        Mono<Void> result = adapter.enviarValidacionAutomaticaSolicitud(solicitud, usuario, tipoPrestamo, tokenConBearer);
        
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void testConstruirTipoPrestamoJsonCompleto() {
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getDocumentoIdentidad()).thenReturn("12345678");
        Mockito.when(solicitud.getMonto()).thenReturn(new BigDecimal(MONTO_10000));
        Mockito.when(solicitud.getPlazo()).thenReturn(12);
        Mockito.when(solicitud.getDeudaTotalMensual()).thenReturn(new BigDecimal("500"));
        
        DetalleUsuario usuario = DetalleUsuario.builder()
                .nombre("Juan")
                .apellido(APELLIDO_PEREZ)
                .email("juan@test.com")
                .salarioBase(new BigDecimal("3000"))
                .build();
                
        TipoPrestamo tipoPrestamo = Mockito.mock(TipoPrestamo.class);
        Mockito.when(tipoPrestamo.getId()).thenReturn(TIPO_PRESTAMO_ID);
        Mockito.when(tipoPrestamo.getNombre()).thenReturn("Préstamo Personal");
        Mockito.when(tipoPrestamo.getDescripcion()).thenReturn("Préstamo para gastos personales");
        Mockito.when(tipoPrestamo.getMontoMinimo()).thenReturn(new BigDecimal("1000"));
        Mockito.when(tipoPrestamo.getMontoMaximo()).thenReturn(new BigDecimal("50000"));
        Mockito.when(tipoPrestamo.getPlazoMinimoMeses()).thenReturn(6);
        Mockito.when(tipoPrestamo.getPlazoMaximoMeses()).thenReturn(60);
        Mockito.when(tipoPrestamo.getTasaInteres()).thenReturn(new BigDecimal("15.5"));
        Mockito.when(tipoPrestamo.getActivo()).thenReturn(Boolean.TRUE);
        Mockito.when(tipoPrestamo.getValidacionAutomatica()).thenReturn(Boolean.FALSE);
        
        Mono<Void> result = adapter.enviarValidacionAutomaticaSolicitud(solicitud, usuario, tipoPrestamo, TEST_TOKEN);
        
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void testEnviarNotificacionEstadoConError() {
        SqsAsyncClient mockSqsClient = Mockito.mock(SqsAsyncClient.class);
        RuntimeException sqsError = new RuntimeException("SQS connection failed");
        
        when(mockSqsClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(sqsError));
        
        NotificacionAdapter adapterWithError = new NotificacionAdapter(mockSqsClient, "dummy-queue", "dummy-queue2");
        
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        Mockito.when(solicitud.getEstado()).thenReturn(EstadoSolicitud.RECHAZADO);
        
        Mono<Void> result = adapterWithError.enviarNotificacionEstado(solicitud, TEST_EMAIL, TEST_JUSTIFICACION);
        
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testEnviarValidacionAutomaticaConError() {
        SqsAsyncClient mockSqsClient = Mockito.mock(SqsAsyncClient.class);
        RuntimeException sqsError = new RuntimeException("SQS validation failed");
        
        when(mockSqsClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(sqsError));
        
        NotificacionAdapter adapterWithError = new NotificacionAdapter(mockSqsClient, "dummy-queue", "dummy-queue2");
        
        Solicitud solicitud = Mockito.mock(Solicitud.class);
        Mockito.when(solicitud.getId()).thenReturn("123");
        
        DetalleUsuario usuario = DetalleUsuario.builder().nombre("Juan").build();
        TipoPrestamo tipoPrestamo = Mockito.mock(TipoPrestamo.class);
        
        Mono<Void> result = adapterWithError.enviarValidacionAutomaticaSolicitud(solicitud, usuario, tipoPrestamo, TEST_TOKEN);
        
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}
