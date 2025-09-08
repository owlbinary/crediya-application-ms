package co.com.crediya.r2dbc.repository;

import co.com.crediya.r2dbc.entity.SolicitudEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface SolicitudRepository extends ReactiveCrudRepository<SolicitudEntity, Integer> {
    
    Flux<SolicitudEntity> findByDocumentoIdentidad(String documentoIdentidad);
    
    Flux<SolicitudEntity> findByIdEstado(Integer idEstado);
    
    Mono<Long> countByDocumentoIdentidad(String documentoIdentidad);
    
    Flux<SolicitudEntity> findByIdEstadoInOrderByFechaSolicitudDesc(List<Integer> idEstados, Pageable pageable);

    Flux<SolicitudEntity> findByIdEstadoOrderByFechaSolicitudDesc(Integer idEstado, Pageable pageable);
    
    Mono<Long> countByIdEstadoIn(List<Integer> idEstados);
}
