package co.com.crediya.r2dbc.mapper;

import co.com.crediya.model.TipoPrestamo;
import co.com.crediya.r2dbc.entity.TipoPrestamoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para convertir entre TipoPrestamoEntity y TipoPrestamo del dominio.
 */
@Mapper(componentModel = "spring")
public interface TipoPrestamoEntityMapper {
    @Mapping(target = "id", expression = "java(entity.getId() != null ? entity.getId().toString() : null)")
    @Mapping(target = "descripcion", source = "nombre")
    @Mapping(target = "plazoMinimoMeses", ignore = true)
    @Mapping(target = "plazoMaximoMeses", ignore = true)
    TipoPrestamo toDomain(TipoPrestamoEntity entity);

    @Mapping(target = "id", expression = "java(tipoPrestamo.getId() != null ? Integer.valueOf(tipoPrestamo.getId()) : null)")
    @Mapping(target = "nombre", source = "descripcion")
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "validacionAutomatica", ignore = true)
    TipoPrestamoEntity toEntity(TipoPrestamo tipoPrestamo);
}
