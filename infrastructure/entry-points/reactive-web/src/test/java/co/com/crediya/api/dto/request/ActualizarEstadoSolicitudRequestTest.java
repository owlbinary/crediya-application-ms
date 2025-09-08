package co.com.crediya.api.dto.request;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ActualizarEstadoSolicitudRequestTest {
    @Test
    void planPagoEsObligatorioSiEstadoEsAprobado() {
        ActualizarEstadoSolicitudRequest.PlanPagoCuota cuota = ActualizarEstadoSolicitudRequest.PlanPagoCuota.builder()
                .numeroCuota(1)
                .cuota(1000.0)
                .abonoCapital(800.0)
                .interes(200.0)
                .saldoRestante(0.0)
                .build();
        ActualizarEstadoSolicitudRequest req = ActualizarEstadoSolicitudRequest.builder()
                .nuevoEstado("APROBADO")
                .planPago(List.of(cuota))
                .build();
        assertTrue(req.isPlanPagoValid());
    }

    @Test
    void planPagoNoEsValidoSiEsAprobadoYListaVacia() {
        ActualizarEstadoSolicitudRequest req = ActualizarEstadoSolicitudRequest.builder()
                .nuevoEstado("APROBADO")
                .planPago(List.of())
                .build();
        assertFalse(req.isPlanPagoValid());
    }

    @Test
    void planPagoNoEsValidoSiEsAprobadoYNull() {
        ActualizarEstadoSolicitudRequest req = ActualizarEstadoSolicitudRequest.builder()
                .nuevoEstado("APROBADO")
                .planPago(null)
                .build();
        assertFalse(req.isPlanPagoValid());
    }

    @Test
    void planPagoDebeSerNullOSinElementosSiNoEsAprobado() {
        ActualizarEstadoSolicitudRequest req1 = ActualizarEstadoSolicitudRequest.builder()
                .nuevoEstado("RECHAZADO")
                .planPago(null)
                .build();
        assertTrue(req1.isPlanPagoValid());
        ActualizarEstadoSolicitudRequest req2 = ActualizarEstadoSolicitudRequest.builder()
                .nuevoEstado("RECHAZADO")
                .planPago(List.of())
                .build();
        assertTrue(req2.isPlanPagoValid());
    }

    @Test
    void planPagoNoEsValidoSiNoEsAprobadoYListaConElementos() {
        ActualizarEstadoSolicitudRequest.PlanPagoCuota cuota = ActualizarEstadoSolicitudRequest.PlanPagoCuota.builder()
                .numeroCuota(1)
                .cuota(1000.0)
                .abonoCapital(800.0)
                .interes(200.0)
                .saldoRestante(0.0)
                .build();
        ActualizarEstadoSolicitudRequest req = ActualizarEstadoSolicitudRequest.builder()
                .nuevoEstado("RECHAZADO")
                .planPago(List.of(cuota))
                .build();
        assertFalse(req.isPlanPagoValid());
    }

    @Test
    void planPagoCuotaBuilderYGettersFuncionan() {
        ActualizarEstadoSolicitudRequest.PlanPagoCuota cuota = ActualizarEstadoSolicitudRequest.PlanPagoCuota.builder()
                .numeroCuota(2)
                .cuota(500.0)
                .abonoCapital(400.0)
                .interes(100.0)
                .saldoRestante(50.0)
                .build();
        assertEquals(2, cuota.getNumeroCuota());
        assertEquals(500.0, cuota.getCuota());
        assertEquals(400.0, cuota.getAbonoCapital());
        assertEquals(100.0, cuota.getInteres());
        assertEquals(50.0, cuota.getSaldoRestante());
    }
}
