package co.com.crediya.r2dbc.mapper;

import co.com.crediya.model.EstadoSolicitud;
import co.com.crediya.model.Solicitud;
import co.com.crediya.r2dbc.entity.SolicitudEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudEntityMapperTest {

    private SolicitudEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SolicitudEntityMapperImpl();
    }

    @Test
    void deberiaMapearEntityToDomain() {
        LocalDateTime now = LocalDateTime.now();
        SolicitudEntity entity = SolicitudEntity.builder()
            .id(123)
            .documentoIdentidad("12345678")
            .monto(new BigDecimal("1500000"))
            .plazo(24)
            .idTipoPrestamo(2)
            .idEstado(1)
            .fechaSolicitud(now)
            .fechaActualizacion(now)
            .email("test@example.com")
            .observaciones("Ninguna")
            .build();

        Solicitud domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo("123");
        assertThat(domain.getDocumentoIdentidad()).isEqualTo("12345678");
        assertThat(domain.getMonto()).isEqualTo(new BigDecimal("1500000"));
        assertThat(domain.getPlazo()).isEqualTo(24);
        assertThat(domain.getTipoPrestamoId()).isEqualTo("2");
        assertThat(domain.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
        assertThat(domain.getFechaCreacion()).isEqualTo(now);
        assertThat(domain.getFechaActualizacion()).isEqualTo(now);
    }

    @Test
    void deberiaMapearDomainToEntity() {
        LocalDateTime now = LocalDateTime.now();
        Solicitud domain = Solicitud.builder()
            .id("456")
            .documentoIdentidad("87654321")
            .email("temp@example.com")
            .monto(new BigDecimal("2000000"))
            .plazo(36)
            .tipoPrestamoId("3")
            .estado(EstadoSolicitud.APROBADO)
            .fechaCreacion(now)
            .fechaActualizacion(now)
            .build();

        SolicitudEntity entity = mapper.toEntity(domain);

    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isEqualTo(456);
        assertThat(entity.getDocumentoIdentidad()).isEqualTo("87654321");
        assertThat(entity.getMonto()).isEqualTo(new BigDecimal("2000000"));
        assertThat(entity.getPlazo()).isEqualTo(36);
        assertThat(entity.getIdTipoPrestamo()).isEqualTo(3);
        assertThat(entity.getIdEstado()).isEqualTo(2);
        assertThat(entity.getFechaSolicitud()).isEqualTo(now);
        assertThat(entity.getFechaActualizacion()).isEqualTo(now);
        assertThat(entity.getEmail()).isEqualTo("temp@example.com");
        assertThat(entity.getObservaciones()).isNull();
    }

    @Test
    void deberiaRetornarNullCuandoEntityEsNull() {
        Solicitud domain = mapper.toDomain(null);

        assertThat(domain).isNull();
    }

    @Test
    void deberiaRetornarNullCuandoDomainEsNull() {
        SolicitudEntity entity = mapper.toEntity(null);

        assertThat(entity).isNull();
    }

    @Test
    void deberiaMapearTodosLosEstadosEntityToDomain() {
        assertThat(mapper.entityEstadoToDomainEstado(1))
            .isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
        assertThat(mapper.entityEstadoToDomainEstado(2))
            .isEqualTo(EstadoSolicitud.APROBADO);
        assertThat(mapper.entityEstadoToDomainEstado(3))
            .isEqualTo(EstadoSolicitud.RECHAZADO);
        assertThat(mapper.entityEstadoToDomainEstado(4))
            .isEqualTo(EstadoSolicitud.REVISION_MANUAL);
        assertThat(mapper.entityEstadoToDomainEstado(null)).isNull();
        assertThat(mapper.entityEstadoToDomainEstado(999))
            .isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
    }

    @Test
    void deberiaMapearTodosLosEstadosDomainToEntity() {
        assertThat(mapper.domainEstadoToEntityEstado(EstadoSolicitud.PENDIENTE_REVISION))
            .isEqualTo(1);
        assertThat(mapper.domainEstadoToEntityEstado(EstadoSolicitud.APROBADO))
            .isEqualTo(2);
        assertThat(mapper.domainEstadoToEntityEstado(EstadoSolicitud.RECHAZADO))
            .isEqualTo(3);
        assertThat(mapper.domainEstadoToEntityEstado(EstadoSolicitud.REVISION_MANUAL))
            .isEqualTo(4);
        assertThat(mapper.domainEstadoToEntityEstado(null)).isEqualTo(1);
    }

    @Test
    void deberiaMapearTipoPrestamoEntityToDomain() {
        assertThat(mapper.entityTipoPrestamoToDomainTipoPrestamo(1)).isEqualTo("1");
        assertThat(mapper.entityTipoPrestamoToDomainTipoPrestamo(999)).isEqualTo("999");
        assertThat(mapper.entityTipoPrestamoToDomainTipoPrestamo(null)).isNull();
    }

    @Test
    void deberiaMapearTipoPrestamoDomainToEntity() {
        assertThat(mapper.domainTipoPrestamoToEntityTipoPrestamo("1")).isEqualTo(1);
        assertThat(mapper.domainTipoPrestamoToEntityTipoPrestamo("999")).isEqualTo(999);
        assertThat(mapper.domainTipoPrestamoToEntityTipoPrestamo("")).isNull();
        assertThat(mapper.domainTipoPrestamoToEntityTipoPrestamo("   ")).isNull();
        assertThat(mapper.domainTipoPrestamoToEntityTipoPrestamo(null)).isNull();
        assertThat(mapper.domainTipoPrestamoToEntityTipoPrestamo("abc")).isNull();
    }

    @Test
    void deberiaMapearIdEntityToDomain() {
        assertThat(mapper.entityIdToDomainId(123)).isEqualTo("123");
        assertThat(mapper.entityIdToDomainId(null)).isNull();
    }

    @Test
    void deberiaMapearCompleteEntityConTodosLosEstados() {
        LocalDateTime now = LocalDateTime.now();

        SolicitudEntity entityPendiente = createSolicitudEntity(1, now);
        Solicitud domainPendiente = mapper.toDomain(entityPendiente);
        assertThat(domainPendiente.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);

        SolicitudEntity entityAprobado = createSolicitudEntity(2, now);
        Solicitud domainAprobado = mapper.toDomain(entityAprobado);
        assertThat(domainAprobado.getEstado()).isEqualTo(EstadoSolicitud.APROBADO);

        SolicitudEntity entityRechazado = createSolicitudEntity(3, now);
        Solicitud domainRechazado = mapper.toDomain(entityRechazado);
        assertThat(domainRechazado.getEstado()).isEqualTo(EstadoSolicitud.RECHAZADO);

        SolicitudEntity entityRevision = createSolicitudEntity(4, now);
        Solicitud domainRevision = mapper.toDomain(entityRevision);
        assertThat(domainRevision.getEstado()).isEqualTo(EstadoSolicitud.REVISION_MANUAL);
    }

    @Test
    void deberiaMapearCompleteDomainConTodosLosEstados() {
        LocalDateTime now = LocalDateTime.now();

        Solicitud domainPendiente = createSolicitudDomain(EstadoSolicitud.PENDIENTE_REVISION, now);
        SolicitudEntity entityPendiente = mapper.toEntity(domainPendiente);
        assertThat(entityPendiente.getIdEstado()).isEqualTo(1);

        Solicitud domainAprobado = createSolicitudDomain(EstadoSolicitud.APROBADO, now);
        SolicitudEntity entityAprobado = mapper.toEntity(domainAprobado);
        assertThat(entityAprobado.getIdEstado()).isEqualTo(2);

        Solicitud domainRechazado = createSolicitudDomain(EstadoSolicitud.RECHAZADO, now);
        SolicitudEntity entityRechazado = mapper.toEntity(domainRechazado);
        assertThat(entityRechazado.getIdEstado()).isEqualTo(3);

        Solicitud domainRevision = createSolicitudDomain(EstadoSolicitud.REVISION_MANUAL, now);
        SolicitudEntity entityRevision = mapper.toEntity(domainRevision);
        assertThat(entityRevision.getIdEstado()).isEqualTo(4);
    }

    private SolicitudEntity createSolicitudEntity(Integer estado, LocalDateTime fecha) {
        return SolicitudEntity.builder()
            .id(1)
            .documentoIdentidad("12345678")
            .monto(new BigDecimal("1000000"))
            .plazo(12)
            .idTipoPrestamo(1)
            .idEstado(estado)
            .fechaSolicitud(fecha)
            .fechaActualizacion(fecha)
            .email("test@example.com")
            .build();
    }

    private Solicitud createSolicitudDomain(EstadoSolicitud estado, LocalDateTime fecha) {
        return Solicitud.builder()
            .id("1")
            .documentoIdentidad("12345678")
            .monto(new BigDecimal("1000000"))
            .plazo(12)
            .tipoPrestamoId("1")
            .estado(estado)
            .fechaCreacion(fecha)
            .fechaActualizacion(fecha)
            .build();
    }
}
