package co.com.crediya.r2dbc.mapper;

import co.com.crediya.model.EstadoSolicitud;
import co.com.crediya.model.Solicitud;
import co.com.crediya.r2dbc.entity.SolicitudEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SolicitudEntityMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "entityIdToDomainId")
    @Mapping(target = "estado", source = "idEstado", qualifiedByName = "entityEstadoToDomainEstado")
    @Mapping(target = "tipoPrestamoId", source = "idTipoPrestamo", qualifiedByName = "entityTipoPrestamoToDomainTipoPrestamo")
    @Mapping(target = "fechaCreacion", source = "fechaSolicitud")
    @Mapping(target = "fechaActualizacion", source = "fechaActualizacion")
    @Mapping(target = "deudaTotalMensual", source = "deudaTotalMensualSolicitudesAprobadas")
    @Mapping(target = "email", source = "email")
    Solicitud toDomain(SolicitudEntity entidad);

    @Mapping(target = "idEstado", source = "estado", qualifiedByName = "domainEstadoToEntityEstado")
    @Mapping(target = "idTipoPrestamo", source = "tipoPrestamoId", qualifiedByName = "domainTipoPrestamoToEntityTipoPrestamo")
    @Mapping(target = "fechaSolicitud", source = "fechaCreacion")
    @Mapping(target = "fechaActualizacion", source = "fechaActualizacion")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "observaciones", ignore = true)
    @Mapping(target = "deudaTotalMensualSolicitudesAprobadas", source = "deudaTotalMensual")
    SolicitudEntity toEntity(Solicitud solicitud);

    @Named("entityIdToDomainId")
    default String entityIdToDomainId(Integer entityId) {
        return entityId != null ? entityId.toString() : null;
    }

    @Named("entityTipoPrestamoToDomainTipoPrestamo")
    default String entityTipoPrestamoToDomainTipoPrestamo(Integer entityTipoPrestamoId) {
        return entityTipoPrestamoId != null ? entityTipoPrestamoId.toString() : null;
    }

    @Named("domainTipoPrestamoToEntityTipoPrestamo")
    default Integer domainTipoPrestamoToEntityTipoPrestamo(String domainTipoPrestamoId) {
        if (domainTipoPrestamoId == null || domainTipoPrestamoId.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(domainTipoPrestamoId.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Named("entityEstadoToDomainEstado")
    default EstadoSolicitud entityEstadoToDomainEstado(Integer entityEstadoId) {
        if (entityEstadoId == null) return null;
        switch (entityEstadoId) {
            case 1: return EstadoSolicitud.PENDIENTE_REVISION;
            case 2: return EstadoSolicitud.APROBADO;
            case 3: return EstadoSolicitud.RECHAZADO;
            case 4: return EstadoSolicitud.REVISION_MANUAL;
            default: return EstadoSolicitud.PENDIENTE_REVISION;
        }
    }

    @Named("domainEstadoToEntityEstado")
    default Integer domainEstadoToEntityEstado(EstadoSolicitud domainEstado) {
        if (domainEstado == null) return 1;
        switch (domainEstado) {
            case PENDIENTE_REVISION: return 1;
            case APROBADO: return 2;
            case RECHAZADO: return 3;
            case REVISION_MANUAL: return 4;
            default: return 1;
        }
    }
}
