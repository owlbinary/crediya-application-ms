package co.com.crediya.model.gateway;

import co.com.crediya.model.Solicitud;
import co.com.crediya.model.TipoPrestamo;
import co.com.crediya.model.DetalleUsuario;
import reactor.core.publisher.Mono;

public interface DebtCapacityEventGateway {
    
    /**
     * Envía un mensaje al SQS de debt capacity para evaluar la capacidad de endeudamiento
     * de una solicitud de préstamo.
     * 
     * @param solicitud La solicitud de préstamo
     * @param detalleUsuario Los detalles del usuario solicitante
     * @param tipoPrestamo El tipo de préstamo solicitado
     * @return Mono<Void> que indica si el mensaje fue enviado exitosamente
     */
    Mono<Void> enviarEvaluacionCapacidadEndeudamiento(Solicitud solicitud, 
                                                     DetalleUsuario detalleUsuario, 
                                                     TipoPrestamo tipoPrestamo);
}
