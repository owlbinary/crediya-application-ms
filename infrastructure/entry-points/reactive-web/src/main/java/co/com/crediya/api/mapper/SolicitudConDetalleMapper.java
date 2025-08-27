package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.response.SolicitudConDetalleResponse;
import co.com.crediya.model.EstadoSolicitud;
import co.com.crediya.usecase.listarsolicitudes.SolicitudConDetalle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SolicitudConDetalleMapper {
    
    @Mapping(target = "estado", source = "estado", qualifiedByName = "estadoToString")
    @Mapping(target = "estadoDescripcion", source = "estado", qualifiedByName = "estadoToDescripcion")
    @Mapping(target = "deudaTotalMensualSolicitud", source = "deudaTotalMensualSolicitud")
    SolicitudConDetalleResponse toResponse(SolicitudConDetalle solicitud);
    
    @Named("estadoToString")
    default String estadoToString(EstadoSolicitud estado) {
        return estado != null ? estado.name() : null;
    }
    
    @Named("estadoToDescripcion")
    default String estadoToDescripcion(EstadoSolicitud estado) {
        return estado != null ? estado.getDescripcion() : null;
    }
}
