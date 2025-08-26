package co.com.crediya.model;

import co.com.crediya.model.exception.DatosInvalidosException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudTest {

    @Test
    void deberiaCrearNuevaSolicitudConEstadoPendienteRevision() {
        String documentoIdentidad = "12345678";
        BigDecimal monto = new BigDecimal("1000000");
        Integer plazo = 12;
        String tipoPrestamoId = "1";
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        
        Solicitud solicitud = Solicitud.crearNueva(documentoIdentidad, monto, plazo, tipoPrestamoId);
        
        assertThat(solicitud).isNotNull();
        assertThat(solicitud.getDocumentoIdentidad()).isEqualTo(documentoIdentidad);
        assertThat(solicitud.getMonto()).isEqualTo(monto);
        assertThat(solicitud.getPlazo()).isEqualTo(plazo);
        assertThat(solicitud.getTipoPrestamoId()).isEqualTo(tipoPrestamoId);
        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
        assertThat(solicitud.getId()).isNull();
        
        LocalDateTime despues = LocalDateTime.now().plusSeconds(1);
        assertThat(solicitud.getFechaCreacion()).isAfter(antes).isBefore(despues);
        assertThat(solicitud.getFechaActualizacion()).isAfter(antes).isBefore(despues);
        assertThat(solicitud.getFechaCreacion()).isEqualTo(solicitud.getFechaActualizacion());
    }

    @Test
    void deberiaRechazarSolicitudConParametrosNulos() {
        assertThatThrownBy(() -> Solicitud.crearNueva(null, null, null, null))
            .isInstanceOf(DatosInvalidosException.class)
            .hasMessageContaining("Documento de identidad es obligatorio");
    }

    @Test
    void deberiaCrearSolicitudConBuilderCompleto() {
        LocalDateTime fechaCreacion = LocalDateTime.of(2025, 8, 26, 10, 0);
        LocalDateTime fechaActualizacion = LocalDateTime.of(2025, 8, 26, 11, 0);
        
        Solicitud solicitud = Solicitud.builder()
            .id("123")
            .documentoIdentidad("87654321")
            .monto(new BigDecimal("2000000"))
            .plazo(24)
            .tipoPrestamoId("2")
            .estado(EstadoSolicitud.APROBADO)
            .fechaCreacion(fechaCreacion)
            .fechaActualizacion(fechaActualizacion)
            .build();
        
        assertThat(solicitud.getId()).isEqualTo("123");
        assertThat(solicitud.getDocumentoIdentidad()).isEqualTo("87654321");
        assertThat(solicitud.getMonto()).isEqualTo(new BigDecimal("2000000"));
        assertThat(solicitud.getPlazo()).isEqualTo(24);
        assertThat(solicitud.getTipoPrestamoId()).isEqualTo("2");
        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.APROBADO);
        assertThat(solicitud.getFechaCreacion()).isEqualTo(fechaCreacion);
        assertThat(solicitud.getFechaActualizacion()).isEqualTo(fechaActualizacion);
    }

    @Test
    void deberiaPermitirModificacionConToBuilder() {
        Solicitud original = Solicitud.crearNueva("12345678", new BigDecimal("1000000"), 12, "1");
        
        Solicitud modificada = original.toBuilder()
            .estado(EstadoSolicitud.APROBADO)
            .fechaActualizacion(LocalDateTime.now())
            .build();
        
        assertThat(modificada.getId()).isEqualTo(original.getId());
        assertThat(modificada.getDocumentoIdentidad()).isEqualTo(original.getDocumentoIdentidad());
        assertThat(modificada.getMonto()).isEqualTo(original.getMonto());
        assertThat(modificada.getPlazo()).isEqualTo(original.getPlazo());
        assertThat(modificada.getTipoPrestamoId()).isEqualTo(original.getTipoPrestamoId());
        assertThat(modificada.getEstado()).isEqualTo(EstadoSolicitud.APROBADO);
        assertThat(modificada.getFechaCreacion()).isEqualTo(original.getFechaCreacion());
        assertThat(modificada.getFechaActualizacion()).isAfter(original.getFechaActualizacion());
    }

    @Test
    void deberiaManejarEqualsYHashCodeCorrectamente() {
        LocalDateTime fecha = LocalDateTime.now();
        Solicitud solicitud1 = Solicitud.builder()
            .id("1")
            .documentoIdentidad("12345678")
            .monto(new BigDecimal("1000000"))
            .plazo(12)
            .tipoPrestamoId("1")
            .estado(EstadoSolicitud.PENDIENTE_REVISION)
            .fechaCreacion(fecha)
            .fechaActualizacion(fecha)
            .build();

        Solicitud solicitud2 = Solicitud.builder()
            .id("1")
            .documentoIdentidad("12345678")
            .monto(new BigDecimal("1000000"))
            .plazo(12)
            .tipoPrestamoId("1")
            .estado(EstadoSolicitud.PENDIENTE_REVISION)
            .fechaCreacion(fecha)
            .fechaActualizacion(fecha)
            .build();

        Solicitud solicitudDiferente = Solicitud.builder()
            .id("2")
            .documentoIdentidad("87654321")
            .monto(new BigDecimal("2000000"))
            .plazo(24)
            .tipoPrestamoId("2")
            .estado(EstadoSolicitud.APROBADO)
            .fechaCreacion(fecha)
            .fechaActualizacion(fecha)
            .build();

        assertThat(solicitud1)
            .isEqualTo(solicitud2)
            .hasSameHashCodeAs(solicitud2)
            .isNotEqualTo(solicitudDiferente)
            .doesNotHaveSameHashCodeAs(solicitudDiferente);
    }

    @Test
    void deberiaGenerarToStringCorrectamente() {
        Solicitud solicitud = Solicitud.crearNueva("12345678", new BigDecimal("1000000"), 12, "1");
        
        String toString = solicitud.toString();
        
        assertThat(toString)
            .contains("Solicitud")
            .contains("documentoIdentidad=12345678")
            .contains("monto=1000000")
            .contains("plazo=12")
            .contains("tipoPrestamoId=1")
            .contains("estado=PENDIENTE_REVISION");
    }

    @Test
    void deberiaRechazarDocumentoVacio() {
        assertThatThrownBy(() -> Solicitud.crearNueva("   ", new BigDecimal("1000000"), 12, "1"))
            .isInstanceOf(DatosInvalidosException.class)
            .hasMessageContaining("Documento de identidad es obligatorio");
    }

    @Test
    void deberiaRechazarMontoNegativo() {
        BigDecimal montoNegativo = new BigDecimal("-1000");
        
        assertThatThrownBy(() -> Solicitud.crearNueva("12345678", montoNegativo, 12, "1"))
            .isInstanceOf(DatosInvalidosException.class)
            .hasMessageContaining("El monto debe ser mayor a cero");
    }

    @Test
    void deberiaRechazarPlazoNegativo() {
        assertThatThrownBy(() -> Solicitud.crearNueva("12345678", new BigDecimal("1000000"), -1, "1"))
            .isInstanceOf(DatosInvalidosException.class)
            .hasMessageContaining("El plazo en meses debe ser mayor a cero");
    }
}
