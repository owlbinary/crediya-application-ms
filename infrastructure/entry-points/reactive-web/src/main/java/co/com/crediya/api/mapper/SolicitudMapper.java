package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.response.SolicitudResponse;
import co.com.crediya.model.Solicitud;
import co.com.crediya.model.EstadoSolicitud;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SolicitudMapper {
    
    @Mapping(target = "estado", source = "estado", qualifiedByName = "estadoToString")
    @Mapping(target = "estadoDescripcion", source = "estado", qualifiedByName = "estadoToDescripcion")
    SolicitudResponse toResponse(Solicitud solicitud);
    
    @Named("estadoToString")
    default String estadoToString(EstadoSolicitud estado) {
        return estado != null ? estado.name() : null;
    }
    
    @Named("estadoToDescripcion")
    default String estadoToDescripcion(EstadoSolicitud estado) {
        return estado != null ? estado.getDescripcion() : null;
    }
}