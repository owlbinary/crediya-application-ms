package co.com.crediya.r2dbc.repository;

import co.com.crediya.r2dbc.entity.TipoPrestamoEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TipoPrestamoReactivoRepository extends ReactiveCrudRepository<TipoPrestamoEntity, Integer> {
    
    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM tipo_prestamo WHERE id_tipo_prestamo = $1")
    Mono<Boolean> existsByIdCustom(Integer id);
}
