package co.com.crediya.sqs.listener;

import co.com.crediya.model.PlanPagoCuota;
import co.com.crediya.sqs.dto.ActualizarEstadoSolicitudMessage;
import co.com.crediya.usecase.actualizarestadosolicitud.ActualizarEstadoSolicitudUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActualizarEstadoSolicitudListener {

    private final ActualizarEstadoSolicitudUseCase actualizarEstadoSolicitudUseCase;
    private final ObjectMapper objectMapper;

    @SqsListener("${aws.sqs.estado-solicitud.queueUrl}")
    public void procesarActualizacionEstado(String message) {
        try {
            log.info("Mensaje recibido de SQS para actualización de estado: {}", message);
            
            ActualizarEstadoSolicitudMessage messageDto = objectMapper.readValue(message, ActualizarEstadoSolicitudMessage.class);
            
            if (!"actualizar_estado_solicitud".equals(messageDto.getTipo())) {
                log.warn("Tipo de mensaje no reconocido: {}", messageDto.getTipo());
                return;
            }
            
            ActualizarEstadoSolicitudMessage.Params params = messageDto.getParams();
            if (params == null) {
                log.error("Mensaje sin parámetros: {}", message);
                return;
            }

            List<PlanPagoCuota> planPagoDominio = convertirPlanPago(params.getPlanPago());

            actualizarEstadoSolicitudUseCase.ejecutar(
                params.getSolicitudId(),
                params.getNuevoEstado(), 
                params.getJustificacion(),
                null,
                planPagoDominio
            )
            .doOnSuccess(solicitud -> 
                log.info("Estado actualizado exitosamente para solicitud {} a estado {}", 
                    params.getSolicitudId(), params.getNuevoEstado()))
            .doOnError(error -> 
                log.error("Error actualizando estado para solicitud {}: {}", 
                    params.getSolicitudId(), error.getMessage(), error))
            .onErrorResume(error -> {
                log.error("Error procesando mensaje SQS: {}", error.getMessage());
                return Mono.empty();
            })
            .subscribe();
            
        } catch (Exception e) {
            log.error("Error deserializando mensaje SQS: {}", e.getMessage(), e);
        }
    }
    
    private List<PlanPagoCuota> convertirPlanPago(List<ActualizarEstadoSolicitudMessage.PlanPagoCuotaDto> planPagoDto) {
        if (planPagoDto == null || planPagoDto.isEmpty()) {
            return Collections.emptyList();
        }
        
        return planPagoDto.stream()
            .map(cuotaDto -> PlanPagoCuota.builder()
                .numeroCuota(cuotaDto.getNumeroCuota())
                .cuota(Double.parseDouble(cuotaDto.getCuota()))
                .abonoCapital(Double.parseDouble(cuotaDto.getAbonoCapital()))
                .interes(Double.parseDouble(cuotaDto.getInteres()))
                .saldoRestante(Double.parseDouble(cuotaDto.getSaldoRestante()))
                .build())
            .toList();
    }
}
