package co.com.crediya.model.gateway;

import co.com.crediya.model.TipoPrestamo;
import reactor.core.publisher.Mono;

/**
 * Gateway para operaciones con tipos de préstamo.
 */
public interface TipoPrestamoGateway {
    
    /**
     * Busca un tipo de préstamo por su ID.
     * 
     * @param tipoPrestamoId ID del tipo de préstamo
     * @return Mono con el tipo de préstamo encontrado o vacío si no existe
     */
    Mono<TipoPrestamo> buscarPorId(String tipoPrestamoId);
}
