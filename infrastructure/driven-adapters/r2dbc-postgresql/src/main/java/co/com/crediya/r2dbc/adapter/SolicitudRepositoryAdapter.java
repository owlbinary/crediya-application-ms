package co.com.crediya.r2dbc.adapter;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;

import co.com.crediya.model.Solicitud;
import co.com.crediya.model.exception.InfraestructuraException;
import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.r2dbc.mapper.SolicitudEntityMapper;
import co.com.crediya.r2dbc.repository.SolicitudRepository;
import co.com.crediya.r2dbc.repository.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SolicitudRepositoryAdapter implements SolicitudGateway {

    private static final String MENSAJE_ID_NUMERICO_REQUERIDO = "ID debe ser numérico: {}";
    private static final String MENSAJE_ID_TIPO_PRESTAMO_NUMERICO = "ID de tipo de préstamo debe ser numérico: ";
    private static final String MENSAJE_INICIANDO_TRANSACCION_GUARDAR = "Iniciando guardar solicitud: {}";
    private static final String MENSAJE_PERSISTIENDO_ENTIDAD = "Persistiendo entidad en base de datos: {}";
    private static final String MENSAJE_SOLICITUD_GUARDADA = "Solicitud guardada exitosamente: {}";
    private static final String MENSAJE_ERROR_GUARDAR = "Error al guardar solicitud: {}";
    private static final String MENSAJE_CONSULTANDO_POR_ID = "Consultando solicitud por ID de solo lectura: {}";
    private static final String MENSAJE_SOLICITUD_RECUPERADA = "Solicitud recuperada de base de datos: {}";
    private static final String MENSAJE_ERROR_CONSULTA_ID = "Error en consulta por ID {}: {}";
    private static final String MENSAJE_VERIFICANDO_TIPO_PRESTAMO = "Verificando existencia de tipo de préstamo: {}";
    private static final String MENSAJE_TIPO_PRESTAMO_DISPONIBLE = "disponible";
    private static final String MENSAJE_TIPO_PRESTAMO_NO_DISPONIBLE = "no disponible";
    private static final String MENSAJE_ERROR_VERIFICACION_TIPO = "Error verificando tipo de préstamo {}: {}";
    private static final String MENSAJE_ERROR_CONSULTA_PRINCIPAL = "Error en consulta principal de tipo de préstamo: {}";

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final SolicitudEntityMapper entityMapper;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<Solicitud> guardar(Solicitud solicitud) {
        log.debug(MENSAJE_INICIANDO_TRANSACCION_GUARDAR, solicitud.getId());
        
        return Mono.just(solicitud)
            .map(entityMapper::toEntity)
            .flatMap(entidad -> {
                log.debug(MENSAJE_PERSISTIENDO_ENTIDAD, entidad.getId());
                return solicitudRepository.save(entidad);
            })
            .map(entityMapper::toDomain)
            .as(transactionalOperator::transactional)
            .doOnSuccess(resultado -> 
                log.info(MENSAJE_SOLICITUD_GUARDADA, resultado.getId()))
            .doOnError(excepcion -> 
                log.error(MENSAJE_ERROR_GUARDAR, excepcion.getMessage(), excepcion));
    }

        @Override
    public Mono<Solicitud> buscarPorId(String id) {
        log.debug(MENSAJE_CONSULTANDO_POR_ID, id);
        
        return convertirStringAEntero(id)
            .flatMap(this::buscarSolicitudPorIdInterno)
            .as(transactionalOperator::transactional)
            .onErrorResume(NumberFormatException.class, e -> {
                log.error(MENSAJE_ID_NUMERICO_REQUERIDO, id);
                return Mono.empty();
            });
    }
    
    private Mono<Integer> convertirStringAEntero(String id) {
        try {
            return Mono.just(Integer.valueOf(id));
        } catch (NumberFormatException e) {
            return Mono.error(e);
        }
    }
    
    private Mono<Solicitud> buscarSolicitudPorIdInterno(Integer id) {
        return solicitudRepository.findById(id)
            .map(entityMapper::toDomain)
            .doOnNext(solicitud -> log.debug(MENSAJE_SOLICITUD_RECUPERADA, solicitud.getId()))
            .doOnError(excepcion -> 
                log.error(MENSAJE_ERROR_CONSULTA_ID, id, excepcion.getMessage()));
    }

    @Override
    public Mono<Boolean> existeTipoPrestamo(String tipoPrestamoId) {
        log.debug(MENSAJE_VERIFICANDO_TIPO_PRESTAMO, tipoPrestamoId);
        
        return convertirIdATipoValido(tipoPrestamoId)
            .flatMap(tipoPrestamoRepository::existsByIdCustom)
            .onErrorResume(this::manejarErrorConsultaTipoPrestamo)
            .as(transactionalOperator::transactional)
            .doOnNext(existe -> logResultadoExistenciaTipoPrestamo(tipoPrestamoId, existe))
            .doOnError(excepcion -> 
                log.error(MENSAJE_ERROR_VERIFICACION_TIPO, 
                    tipoPrestamoId, excepcion.getMessage()));
    }
    
    private void logResultadoExistenciaTipoPrestamo(String tipoPrestamoId, Boolean existe) {
        String estado = Boolean.TRUE.equals(existe) ? MENSAJE_TIPO_PRESTAMO_DISPONIBLE : MENSAJE_TIPO_PRESTAMO_NO_DISPONIBLE;
        log.debug("Tipo de préstamo {}: {}", tipoPrestamoId, estado);
    }
    
    private Mono<Integer> convertirIdATipoValido(String tipoPrestamoId) {
        try {
            return Mono.just(Integer.valueOf(tipoPrestamoId));
        } catch (NumberFormatException e) {
            log.warn(MENSAJE_ID_NUMERICO_REQUERIDO, tipoPrestamoId);
            return Mono.error(new InfraestructuraException(
                MENSAJE_ID_TIPO_PRESTAMO_NUMERICO + tipoPrestamoId));
        }
    }
    
    private Mono<Boolean> manejarErrorConsultaTipoPrestamo(Throwable error) {
        log.warn(MENSAJE_ERROR_CONSULTA_PRINCIPAL, error.getMessage());
        return Mono.just(false);
    }
}