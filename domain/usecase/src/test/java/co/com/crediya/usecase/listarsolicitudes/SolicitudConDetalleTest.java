package co.com.crediya.usecase.listarsolicitudes;

import co.com.crediya.model.EstadoSolicitud;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudConDetalleTest {

    @Test
    void deberiaCrearSolicitudConDetalleConBuilder() {
        LocalDateTime fechaCreacion = LocalDateTime.now();
        LocalDateTime fechaActualizacion = LocalDateTime.now();

        SolicitudConDetalle solicitudConDetalle = SolicitudConDetalle.builder()
                .id("1")
                .documentoIdentidad("12345678")
                .monto(BigDecimal.valueOf(1000000))
                .plazo(12)
                .tipoPrestamoId("1")
                .descripcionTipoPrestamo("Préstamo Personal")
                .tasaInteres(BigDecimal.valueOf(1.5))
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .fechaCreacion(fechaCreacion)
                .fechaActualizacion(fechaActualizacion)
                .email("juan@test.com")
                .nombre("Juan")
                .apellido("Pérez")
                .salarioBase(BigDecimal.valueOf(5000000))
                .deudaTotalMensualSolicitud(BigDecimal.valueOf(500000))
                .build();

        assertThat(solicitudConDetalle.getId()).isEqualTo("1");
        assertThat(solicitudConDetalle.getDocumentoIdentidad()).isEqualTo("12345678");
        assertThat(solicitudConDetalle.getMonto()).isEqualTo(BigDecimal.valueOf(1000000));
        assertThat(solicitudConDetalle.getPlazo()).isEqualTo(12);
        assertThat(solicitudConDetalle.getTipoPrestamoId()).isEqualTo("1");
        assertThat(solicitudConDetalle.getDescripcionTipoPrestamo()).isEqualTo("Préstamo Personal");
        assertThat(solicitudConDetalle.getTasaInteres()).isEqualTo(BigDecimal.valueOf(1.5));
        assertThat(solicitudConDetalle.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
        assertThat(solicitudConDetalle.getFechaCreacion()).isEqualTo(fechaCreacion);
        assertThat(solicitudConDetalle.getFechaActualizacion()).isEqualTo(fechaActualizacion);
        assertThat(solicitudConDetalle.getEmail()).isEqualTo("juan@test.com");
        assertThat(solicitudConDetalle.getNombre()).isEqualTo("Juan");
        assertThat(solicitudConDetalle.getApellido()).isEqualTo("Pérez");
        assertThat(solicitudConDetalle.getSalarioBase()).isEqualTo(BigDecimal.valueOf(5000000));
        assertThat(solicitudConDetalle.getDeudaTotalMensualSolicitud()).isEqualTo(BigDecimal.valueOf(500000));
    }

    @Test
    void deberiaCrearSolicitudConDetalleVacia() {
        SolicitudConDetalle solicitudConDetalle = SolicitudConDetalle.builder().build();

        assertThat(solicitudConDetalle.getId()).isNull();
        assertThat(solicitudConDetalle.getDocumentoIdentidad()).isNull();
        assertThat(solicitudConDetalle.getMonto()).isNull();
        assertThat(solicitudConDetalle.getPlazo()).isNull();
        assertThat(solicitudConDetalle.getTipoPrestamoId()).isNull();
        assertThat(solicitudConDetalle.getDescripcionTipoPrestamo()).isNull();
        assertThat(solicitudConDetalle.getTasaInteres()).isNull();
        assertThat(solicitudConDetalle.getEstado()).isNull();
        assertThat(solicitudConDetalle.getFechaCreacion()).isNull();
        assertThat(solicitudConDetalle.getFechaActualizacion()).isNull();
        assertThat(solicitudConDetalle.getEmail()).isNull();
        assertThat(solicitudConDetalle.getNombre()).isNull();
        assertThat(solicitudConDetalle.getApellido()).isNull();
        assertThat(solicitudConDetalle.getSalarioBase()).isNull();
        assertThat(solicitudConDetalle.getDeudaTotalMensualSolicitud()).isNull();
    }

    @Test
    void deberiaPermitirModificacionDeAtributos() {
        SolicitudConDetalle solicitudConDetalle = SolicitudConDetalle.builder().build();
        LocalDateTime ahora = LocalDateTime.now();

        solicitudConDetalle.setId("2");
        solicitudConDetalle.setDocumentoIdentidad("87654321");
        solicitudConDetalle.setMonto(BigDecimal.valueOf(2000000));
        solicitudConDetalle.setPlazo(24);
        solicitudConDetalle.setTipoPrestamoId("2");
        solicitudConDetalle.setDescripcionTipoPrestamo("Préstamo Hipotecario");
        solicitudConDetalle.setTasaInteres(BigDecimal.valueOf(0.8));
        solicitudConDetalle.setEstado(EstadoSolicitud.APROBADO);
        solicitudConDetalle.setFechaCreacion(ahora);
        solicitudConDetalle.setFechaActualizacion(ahora);
        solicitudConDetalle.setEmail("maria@test.com");
        solicitudConDetalle.setNombre("María");
        solicitudConDetalle.setApellido("García");
        solicitudConDetalle.setSalarioBase(BigDecimal.valueOf(8000000));
        solicitudConDetalle.setDeudaTotalMensualSolicitud(BigDecimal.valueOf(800000));

        assertThat(solicitudConDetalle.getId()).isEqualTo("2");
        assertThat(solicitudConDetalle.getDocumentoIdentidad()).isEqualTo("87654321");
        assertThat(solicitudConDetalle.getMonto()).isEqualTo(BigDecimal.valueOf(2000000));
        assertThat(solicitudConDetalle.getPlazo()).isEqualTo(24);
        assertThat(solicitudConDetalle.getTipoPrestamoId()).isEqualTo("2");
        assertThat(solicitudConDetalle.getDescripcionTipoPrestamo()).isEqualTo("Préstamo Hipotecario");
        assertThat(solicitudConDetalle.getTasaInteres()).isEqualTo(BigDecimal.valueOf(0.8));
        assertThat(solicitudConDetalle.getEstado()).isEqualTo(EstadoSolicitud.APROBADO);
        assertThat(solicitudConDetalle.getFechaCreacion()).isEqualTo(ahora);
        assertThat(solicitudConDetalle.getFechaActualizacion()).isEqualTo(ahora);
        assertThat(solicitudConDetalle.getEmail()).isEqualTo("maria@test.com");
        assertThat(solicitudConDetalle.getNombre()).isEqualTo("María");
        assertThat(solicitudConDetalle.getApellido()).isEqualTo("García");
        assertThat(solicitudConDetalle.getSalarioBase()).isEqualTo(BigDecimal.valueOf(8000000));
        assertThat(solicitudConDetalle.getDeudaTotalMensualSolicitud()).isEqualTo(BigDecimal.valueOf(800000));
    }

    @Test
    void deberiaCrearSolicitudConDetalleConValoresNulos() {
        SolicitudConDetalle solicitudConDetalle = SolicitudConDetalle.builder()
                .id("3")
                .documentoIdentidad("11223344")
                .monto(BigDecimal.valueOf(1500000))
                .plazo(18)
                .tipoPrestamoId("3")
                .descripcionTipoPrestamo(null)
                .tasaInteres(null)
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(null)
                .email(null)
                .nombre(null)
                .apellido(null)
                .salarioBase(null)
                .deudaTotalMensualSolicitud(null)
                .build();

        assertThat(solicitudConDetalle.getId()).isEqualTo("3");
        assertThat(solicitudConDetalle.getDocumentoIdentidad()).isEqualTo("11223344");
        assertThat(solicitudConDetalle.getMonto()).isEqualTo(BigDecimal.valueOf(1500000));
        assertThat(solicitudConDetalle.getDescripcionTipoPrestamo()).isNull();
        assertThat(solicitudConDetalle.getTasaInteres()).isNull();
        assertThat(solicitudConDetalle.getFechaActualizacion()).isNull();
        assertThat(solicitudConDetalle.getEmail()).isNull();
        assertThat(solicitudConDetalle.getNombre()).isNull();
        assertThat(solicitudConDetalle.getApellido()).isNull();
        assertThat(solicitudConDetalle.getSalarioBase()).isNull();
        assertThat(solicitudConDetalle.getDeudaTotalMensualSolicitud()).isNull();
    }

    @Test
    void deberiaImplementarEqualsYHashCodeCorrectamente() {
        LocalDateTime fechaCreacion = LocalDateTime.now();

        SolicitudConDetalle solicitud1 = SolicitudConDetalle.builder()
                .id("1")
                .documentoIdentidad("12345678")
                .monto(BigDecimal.valueOf(1000000))
                .plazo(12)
                .tipoPrestamoId("1")
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .fechaCreacion(fechaCreacion)
                .email("juan@test.com")
                .nombre("Juan")
                .build();

        SolicitudConDetalle solicitud2 = SolicitudConDetalle.builder()
                .id("1")
                .documentoIdentidad("12345678")
                .monto(BigDecimal.valueOf(1000000))
                .plazo(12)
                .tipoPrestamoId("1")
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .fechaCreacion(fechaCreacion)
                .email("juan@test.com")
                .nombre("Juan")
                .build();

        SolicitudConDetalle solicitud3 = SolicitudConDetalle.builder()
                .id("2")
                .documentoIdentidad("87654321")
                .monto(BigDecimal.valueOf(2000000))
                .estado(EstadoSolicitud.APROBADO)
                .build();

        assertThat(solicitud1)
                .isEqualTo(solicitud2)
                .isNotEqualTo(solicitud3)
                .hasSameHashCodeAs(solicitud2);
        assertThat(solicitud1.hashCode()).isNotEqualTo(solicitud3.hashCode());
    }

    @Test
    void deberiaGenerarToStringCorrectamente() {
        SolicitudConDetalle solicitudConDetalle = SolicitudConDetalle.builder()
                .id("1")
                .documentoIdentidad("12345678")
                .monto(BigDecimal.valueOf(1000000))
                .plazo(12)
                .tipoPrestamoId("1")
                .descripcionTipoPrestamo("Préstamo Personal")
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .email("juan@test.com")
                .nombre("Juan")
                .apellido("Pérez")
                .build();

        String toString = solicitudConDetalle.toString();

        assertThat(toString)
                .contains("SolicitudConDetalle")
                .contains("id=1")
                .contains("documentoIdentidad=12345678")
                .contains("monto=1000000")
                .contains("plazo=12")
                .contains("tipoPrestamoId=1")
                .contains("descripcionTipoPrestamo=Préstamo Personal")
                .contains("estado=PENDIENTE_REVISION")
                .contains("email=juan@test.com")
                .contains("nombre=Juan")
                .contains("apellido=Pérez");
    }

    @Test
    void deberiaCrearSolicitudConTodosLosEstados() {
        SolicitudConDetalle pendiente = SolicitudConDetalle.builder()
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .build();
        
        SolicitudConDetalle aprobada = SolicitudConDetalle.builder()
                .estado(EstadoSolicitud.APROBADO)
                .build();
        
        SolicitudConDetalle rechazada = SolicitudConDetalle.builder()
                .estado(EstadoSolicitud.RECHAZADO)
                .build();

        assertThat(pendiente.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
        assertThat(aprobada.getEstado()).isEqualTo(EstadoSolicitud.APROBADO);
        assertThat(rechazada.getEstado()).isEqualTo(EstadoSolicitud.RECHAZADO);
    }
}
