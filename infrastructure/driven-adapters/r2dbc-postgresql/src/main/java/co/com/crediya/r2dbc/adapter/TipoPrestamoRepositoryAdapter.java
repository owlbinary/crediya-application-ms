package co.com.crediya.r2dbc.adapter;

import co.com.crediya.model.TipoPrestamo;
import co.com.crediya.model.exception.InfraestructuraException;
import co.com.crediya.model.gateway.TipoPrestamoGateway;
import co.com.crediya.r2dbc.mapper.TipoPrestamoEntityMapper;
import co.com.crediya.r2dbc.repository.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

/**
 * Adapter para operaciones con tipos de préstamo.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class TipoPrestamoRepositoryAdapter implements TipoPrestamoGateway {

    private static final String MENSAJE_CONSULTANDO_TIPO_PRESTAMO = "Consultando tipo de préstamo por ID: {}";
    private static final String MENSAJE_TIPO_PRESTAMO_ENCONTRADO = "Tipo de préstamo encontrado: {}";
    private static final String MENSAJE_ID_TIPO_PRESTAMO_NUMERICO = "ID de tipo de préstamo debe ser numérico: ";

    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final TipoPrestamoEntityMapper entityMapper;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<TipoPrestamo> buscarPorId(String tipoPrestamoId) {
        log.debug(MENSAJE_CONSULTANDO_TIPO_PRESTAMO, tipoPrestamoId);
        
        return convertirIdATipoValido(tipoPrestamoId)
            .flatMap(tipoPrestamoRepository::findById)
            .map(entityMapper::toDomain)
            .as(transactionalOperator::transactional)
            .doOnNext(tipoPrestamo -> 
                log.debug(MENSAJE_TIPO_PRESTAMO_ENCONTRADO, tipoPrestamo.getId()));
    }

    private Mono<Integer> convertirIdATipoValido(String tipoPrestamoId) {
        try {
            return Mono.just(Integer.valueOf(tipoPrestamoId));
        } catch (NumberFormatException e) {
            log.warn("ID debe ser numérico: {}", tipoPrestamoId);
            return Mono.error(new InfraestructuraException(
                MENSAJE_ID_TIPO_PRESTAMO_NUMERICO + tipoPrestamoId));
        }
    }
}
