package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.response.SolicitudConDetalleResponse;
import co.com.crediya.model.EstadoSolicitud;
import co.com.crediya.usecase.listarsolicitudes.SolicitudConDetalle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SolicitudConDetalleMapperTest {

    private SolicitudConDetalleMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SolicitudConDetalleMapperImpl();
    }

    @Test
    void deberiaMapearSolicitudConDetalleAResponse() {
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

        SolicitudConDetalleResponse response = mapper.toResponse(solicitudConDetalle);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("1");
        assertThat(response.getDocumentoIdentidad()).isEqualTo("12345678");
        assertThat(response.getMonto()).isEqualTo(BigDecimal.valueOf(1000000));
        assertThat(response.getPlazo()).isEqualTo(12);
        assertThat(response.getTipoPrestamoId()).isEqualTo("1");
        assertThat(response.getDescripcionTipoPrestamo()).isEqualTo("Préstamo Personal");
        assertThat(response.getTasaInteres()).isEqualTo(BigDecimal.valueOf(1.5));
        assertThat(response.getEstado()).isEqualTo("PENDIENTE_REVISION");
        assertThat(response.getEstadoDescripcion()).isEqualTo("Solicitud pendiente de revisión");
        assertThat(response.getFechaCreacion()).isEqualTo(fechaCreacion);
        assertThat(response.getFechaActualizacion()).isEqualTo(fechaActualizacion);
        assertThat(response.getEmail()).isEqualTo("juan@test.com");
        assertThat(response.getNombre()).isEqualTo("Juan");
        assertThat(response.getApellido()).isEqualTo("Pérez");
        assertThat(response.getSalarioBase()).isEqualTo(BigDecimal.valueOf(5000000));
        assertThat(response.getDeudaTotalMensualSolicitud()).isEqualTo(BigDecimal.valueOf(500000));
    }

    @Test
    void deberiaMapearTodosLosEstados() {
        SolicitudConDetalle pendiente = SolicitudConDetalle.builder()
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .build();
        
        SolicitudConDetalle aprobado = SolicitudConDetalle.builder()
                .estado(EstadoSolicitud.APROBADO)
                .build();
        
        SolicitudConDetalle rechazado = SolicitudConDetalle.builder()
                .estado(EstadoSolicitud.RECHAZADO)
                .build();
        
        SolicitudConDetalle revisionManual = SolicitudConDetalle.builder()
                .estado(EstadoSolicitud.REVISION_MANUAL)
                .build();

        assertThat(mapper.toResponse(pendiente).getEstado()).isEqualTo("PENDIENTE_REVISION");
        assertThat(mapper.toResponse(aprobado).getEstado()).isEqualTo("APROBADO");
        assertThat(mapper.toResponse(rechazado).getEstado()).isEqualTo("RECHAZADO");
        assertThat(mapper.toResponse(revisionManual).getEstado()).isEqualTo("REVISION_MANUAL");
    }

    @Test
    void deberiaMapearTodasLasDescripcionesDeEstado() {
        assertThat(mapper.estadoToDescripcion(EstadoSolicitud.PENDIENTE_REVISION))
                .isEqualTo("Solicitud pendiente de revisión");
        assertThat(mapper.estadoToDescripcion(EstadoSolicitud.APROBADO))
                .isEqualTo("Solicitud aprobada");
        assertThat(mapper.estadoToDescripcion(EstadoSolicitud.RECHAZADO))
                .isEqualTo("Solicitud rechazada");
        assertThat(mapper.estadoToDescripcion(EstadoSolicitud.REVISION_MANUAL))
                .isEqualTo("Solicitud en revisión manual");
        assertThat(mapper.estadoToDescripcion(null)).isNull();
    }

    @Test
    void deberiaRetornarNullCuandoSolicitudEsNull() {
        SolicitudConDetalleResponse response = mapper.toResponse(null);

        assertThat(response).isNull();
    }

    @Test
    void deberiaMapearSolicitudConValoresNulos() {
        SolicitudConDetalle solicitud = SolicitudConDetalle.builder()
                .id("2")
                .documentoIdentidad("87654321")
                .monto(BigDecimal.valueOf(2000000))
                .plazo(24)
                .tipoPrestamoId("2")
                .descripcionTipoPrestamo(null)
                .tasaInteres(null)
                .estado(EstadoSolicitud.APROBADO)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(null)
                .email(null)
                .nombre(null)
                .apellido(null)
                .salarioBase(null)
                .deudaTotalMensualSolicitud(null)
                .build();

        SolicitudConDetalleResponse response = mapper.toResponse(solicitud);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("2");
        assertThat(response.getDocumentoIdentidad()).isEqualTo("87654321");
        assertThat(response.getMonto()).isEqualTo(BigDecimal.valueOf(2000000));
        assertThat(response.getDescripcionTipoPrestamo()).isNull();
        assertThat(response.getTasaInteres()).isNull();
        assertThat(response.getFechaActualizacion()).isNull();
        assertThat(response.getEmail()).isNull();
        assertThat(response.getNombre()).isNull();
        assertThat(response.getApellido()).isNull();
        assertThat(response.getSalarioBase()).isNull();
        assertThat(response.getDeudaTotalMensualSolicitud()).isNull();
        assertThat(response.getEstado()).isEqualTo("APROBADO");
        assertThat(response.getEstadoDescripcion()).isEqualTo("Solicitud aprobada");
    }

    @Test
    void deberiaMapearEstadoAString() {
        assertThat(mapper.estadoToString(EstadoSolicitud.PENDIENTE_REVISION)).isEqualTo("PENDIENTE_REVISION");
        assertThat(mapper.estadoToString(EstadoSolicitud.APROBADO)).isEqualTo("APROBADO");
        assertThat(mapper.estadoToString(EstadoSolicitud.RECHAZADO)).isEqualTo("RECHAZADO");
        assertThat(mapper.estadoToString(EstadoSolicitud.REVISION_MANUAL)).isEqualTo("REVISION_MANUAL");
        assertThat(mapper.estadoToString(null)).isNull();
    }

    @Test
    void deberiaMapearSolicitudConDatosCompletos() {
        SolicitudConDetalle solicitud = SolicitudConDetalle.builder()
                .id("3")
                .documentoIdentidad("11223344")
                .monto(BigDecimal.valueOf(3000000))
                .plazo(36)
                .tipoPrestamoId("3")
                .descripcionTipoPrestamo("Préstamo Hipotecario")
                .tasaInteres(BigDecimal.valueOf(0.8))
                .estado(EstadoSolicitud.REVISION_MANUAL)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .email("maria@test.com")
                .nombre("María")
                .apellido("García")
                .salarioBase(BigDecimal.valueOf(8000000))
                .deudaTotalMensualSolicitud(BigDecimal.valueOf(800000))
                .build();

        SolicitudConDetalleResponse response = mapper.toResponse(solicitud);

        assertThat(response.getId()).isEqualTo("3");
        assertThat(response.getDocumentoIdentidad()).isEqualTo("11223344");
        assertThat(response.getMonto()).isEqualTo(BigDecimal.valueOf(3000000));
        assertThat(response.getPlazo()).isEqualTo(36);
        assertThat(response.getTipoPrestamoId()).isEqualTo("3");
        assertThat(response.getDescripcionTipoPrestamo()).isEqualTo("Préstamo Hipotecario");
        assertThat(response.getTasaInteres()).isEqualTo(BigDecimal.valueOf(0.8));
        assertThat(response.getEstado()).isEqualTo("REVISION_MANUAL");
        assertThat(response.getEstadoDescripcion()).isEqualTo("Solicitud en revisión manual");
        assertThat(response.getEmail()).isEqualTo("maria@test.com");
        assertThat(response.getNombre()).isEqualTo("María");
        assertThat(response.getApellido()).isEqualTo("García");
        assertThat(response.getSalarioBase()).isEqualTo(BigDecimal.valueOf(8000000));
        assertThat(response.getDeudaTotalMensualSolicitud()).isEqualTo(BigDecimal.valueOf(800000));
    }

    @Test
    void deberiaMapearSolicitudConEstadoRechazado() {
        SolicitudConDetalle solicitud = SolicitudConDetalle.builder()
                .id("4")
                .documentoIdentidad("55667788")
                .monto(BigDecimal.valueOf(500000))
                .plazo(6)
                .tipoPrestamoId("1")
                .estado(EstadoSolicitud.RECHAZADO)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        SolicitudConDetalleResponse response = mapper.toResponse(solicitud);

        assertThat(response.getEstado()).isEqualTo("RECHAZADO");
        assertThat(response.getEstadoDescripcion()).isEqualTo("Solicitud rechazada");
    }
}
