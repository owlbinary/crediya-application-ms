package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.response.SolicitudResponse;
import co.com.crediya.model.EstadoSolicitud;
import co.com.crediya.model.Solicitud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SolicitudMapperTest {

    private SolicitudMapper solicitudMapper;

    @BeforeEach
    void setUp() {
        solicitudMapper = new SolicitudMapperImpl();
    }

    @Test
    void deberiaMapearSolicitudAResponse() {
        LocalDateTime now = LocalDateTime.now();
        Solicitud solicitud = Solicitud.builder()
            .id("123")
            .documentoIdentidad("12345678")
            .monto(new BigDecimal("1500000"))
            .plazo(24)
            .tipoPrestamoId("2")
            .estado(EstadoSolicitud.PENDIENTE_REVISION)
            .fechaCreacion(now)
            .fechaActualizacion(now)
            .build();

        SolicitudResponse response = solicitudMapper.toResponse(solicitud);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("123");
        assertThat(response.getDocumentoIdentidad()).isEqualTo("12345678");
        assertThat(response.getMonto()).isEqualTo(new BigDecimal("1500000"));
        assertThat(response.getPlazo()).isEqualTo(24);
        assertThat(response.getTipoPrestamoId()).isEqualTo("2");
        assertThat(response.getEstado()).isEqualTo("PENDIENTE_REVISION");
        assertThat(response.getEstadoDescripcion()).isEqualTo("Solicitud pendiente de revisión");
        assertThat(response.getFechaCreacion()).isEqualTo(now);
        assertThat(response.getFechaActualizacion()).isEqualTo(now);
    }

    @Test
    void deberiaMapearTodosLosEstados() {
        assertThat(solicitudMapper.estadoToString(EstadoSolicitud.PENDIENTE_REVISION))
            .isEqualTo("PENDIENTE_REVISION");
        assertThat(solicitudMapper.estadoToString(EstadoSolicitud.APROBADO))
            .isEqualTo("APROBADO");
        assertThat(solicitudMapper.estadoToString(EstadoSolicitud.RECHAZADO))
            .isEqualTo("RECHAZADO");
        assertThat(solicitudMapper.estadoToString(EstadoSolicitud.REVISION_MANUAL))
            .isEqualTo("REVISION_MANUAL");
        assertThat(solicitudMapper.estadoToString(null)).isNull();
    }

    @Test
    void deberiaMapearTodasLasDescripcionesDeEstado() {
        assertThat(solicitudMapper.estadoToDescripcion(EstadoSolicitud.PENDIENTE_REVISION))
            .isEqualTo("Solicitud pendiente de revisión");
        assertThat(solicitudMapper.estadoToDescripcion(EstadoSolicitud.APROBADO))
            .isEqualTo("Solicitud aprobada");
        assertThat(solicitudMapper.estadoToDescripcion(EstadoSolicitud.RECHAZADO))
            .isEqualTo("Solicitud rechazada");
        assertThat(solicitudMapper.estadoToDescripcion(EstadoSolicitud.REVISION_MANUAL))
            .isEqualTo("Solicitud en revisión manual");
        assertThat(solicitudMapper.estadoToDescripcion(null)).isNull();
    }

    @Test
    void deberiaRetornarNullCuandoSolicitudEsNull() {
        SolicitudResponse response = solicitudMapper.toResponse(null);

        assertThat(response).isNull();
    }

    @Test
    void deberiaMapearSolicitudConEstadoAprobado() {
        Solicitud solicitud = Solicitud.builder()
            .id("456")
            .documentoIdentidad("87654321")
            .monto(new BigDecimal("2000000"))
            .plazo(36)
            .tipoPrestamoId("3")
            .estado(EstadoSolicitud.APROBADO)
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .build();

        SolicitudResponse response = solicitudMapper.toResponse(solicitud);

        assertThat(response.getEstado()).isEqualTo("APROBADO");
        assertThat(response.getEstadoDescripcion()).isEqualTo("Solicitud aprobada");
    }

    @Test
    void deberiaMapearSolicitudConEstadoRechazado() {
        Solicitud solicitud = Solicitud.builder()
            .id("789")
            .documentoIdentidad("11223344")
            .monto(new BigDecimal("500000"))
            .plazo(6)
            .tipoPrestamoId("1")
            .estado(EstadoSolicitud.RECHAZADO)
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .build();

        SolicitudResponse response = solicitudMapper.toResponse(solicitud);

        assertThat(response.getEstado()).isEqualTo("RECHAZADO");
        assertThat(response.getEstadoDescripcion()).isEqualTo("Solicitud rechazada");
    }

    @Test
    void deberiaMapearSolicitudConEstadoRevisionManual() {
        Solicitud solicitud = Solicitud.builder()
            .id("101")
            .documentoIdentidad("99887766")
            .monto(new BigDecimal("3000000"))
            .plazo(48)
            .tipoPrestamoId("4")
            .estado(EstadoSolicitud.REVISION_MANUAL)
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .build();

        SolicitudResponse response = solicitudMapper.toResponse(solicitud);

        assertThat(response.getEstado()).isEqualTo("REVISION_MANUAL");
        assertThat(response.getEstadoDescripcion()).isEqualTo("Solicitud en revisión manual");
    }
}
