package co.com.crediya.sqs.adapter;

import co.com.crediya.model.Solicitud;
import co.com.crediya.model.DetalleUsuario;
import co.com.crediya.model.gateway.NotificacionGateway;
import co.com.crediya.model.PlanPagoCuota;
import co.com.crediya.model.TipoPrestamo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import java.util.List;

@Slf4j
@Component
public class NotificacionAdapter implements NotificacionGateway {
    private final SqsAsyncClient sqsAsyncClient;
    private final String queueUrl;

    public NotificacionAdapter(
            SqsAsyncClient sqsAsyncClient,
            @Value("${aws.sqs.queueUrl}") String queueUrl) {
        this.queueUrl = queueUrl;
        this.sqsAsyncClient = sqsAsyncClient;
    }

    @Override
    public Mono<Void> enviarNotificacionEstado(Solicitud solicitud, String email, String justificacion) {
        return enviarNotificacionEstado(solicitud, email, justificacion, null);
    }

    public Mono<Void> enviarNotificacionEstado(Solicitud solicitud, String email, String justificacion, List<PlanPagoCuota> planPago) {
        String mensaje = construirMensaje(solicitud, email, justificacion, planPago);
        log.info("Enviando mensaje a SQS para solicitud {}: {}", solicitud.getId(), mensaje);
        
        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(mensaje)
                .build();
                
        return Mono.fromFuture(sqsAsyncClient.sendMessage(sendMessageRequest))
                .doOnSuccess(response -> log.info("Mensaje enviado a SQS con ID: {}", response.messageId()))
                .doOnError(e -> log.error("Error al enviar mensaje a SQS: {}", e.getMessage(), e))
                .then();
    }

    String construirMensaje(Solicitud solicitud, String email, String justificacion, List<PlanPagoCuota> planPago) {
        StringBuilder params = new StringBuilder();
        params.append(String.format("\"solicitudId\":\"%s\",\"estado\":\"%s\",\"justificacion\":\"%s\",\"email\":\"%s\",\"monto\":\"%s\",\"plazo\":\"%s\"",
            solicitud.getId(),
            solicitud.getEstado() != null ? solicitud.getEstado().name() : "",
            justificacion != null ? justificacion : "null",
            email,
            solicitud.getMonto() != null ? solicitud.getMonto().toString() : "",
            solicitud.getPlazo() != null ? solicitud.getPlazo().toString() : ""));
        if (solicitud.getEstado() != null && "APROBADO".equalsIgnoreCase(solicitud.getEstado().name()) && planPago != null && !planPago.isEmpty()) {
            params.append(",\"planPago\":");
            params.append(formatearPlanPagoParaSNS(planPago));
        }
        return String.format("{\"tipo\":\"%s\",\"params\":{%s}}", "estado_solicitud", params.toString());
    }

    String formatearPlanPagoParaSNS(List<PlanPagoCuota> planPago) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < planPago.size(); i++) {
            PlanPagoCuota cuota = planPago.get(i);
            sb.append(String.format("{\"numero_cuota\":%d,\"cuota\":%.2f,\"abono_capital\":%.2f,\"interes\":%.2f,\"saldo_restante\":%.2f}",
                cuota.getNumeroCuota(),
                cuota.getCuota(),
                cuota.getAbonoCapital(),
                cuota.getInteres(),
                cuota.getSaldoRestante()
            ));
            if (i < planPago.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Mono<Void> enviarValidacionAutomaticaSolicitud(Solicitud solicitud, DetalleUsuario detalleUsuario, TipoPrestamo tipoPrestamo, String authorizationToken) {
        String mensaje = construirMensajeValidacionAutomatica(solicitud, detalleUsuario, tipoPrestamo, authorizationToken);
        log.info("Enviando mensaje de validación automática a SQS para solicitud {}: {}", solicitud.getId(), mensaje);
        
        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(mensaje)
                .build();
                
        return Mono.fromFuture(sqsAsyncClient.sendMessage(sendMessageRequest))
                .doOnSuccess(response -> log.info("Mensaje de validación automática enviado a SQS con ID: {}", response.messageId()))
                .doOnError(e -> log.error("Error al enviar mensaje de validación automática a SQS: {}", e.getMessage(), e))
                .then();
    }

    private String construirMensajeValidacionAutomatica(Solicitud solicitud, DetalleUsuario usuario, TipoPrestamo tipoPrestamo, String authorizationToken) {
        String tipoPrestamoJson = construirTipoPrestamoJson(tipoPrestamo);
        String nombre = NotificacionUtils.getUsuarioCampo(usuario, DetalleUsuario::getNombre);
        String apellido = NotificacionUtils.getUsuarioCampo(usuario, DetalleUsuario::getApellido);
        String email = NotificacionUtils.getUsuarioCampo(usuario, DetalleUsuario::getEmail);
        String salarioBase = NotificacionUtils.getUsuarioCampoBigDecimal(usuario, DetalleUsuario::getSalarioBase);

    String tokenLimpio = authorizationToken == null ? "" : authorizationToken.replace("Bearer", "").replaceAll("\\s", "");
    return String.format("{" +
            "\"token\":\"%s\"," +
            "\"solicitudId\":\"%s\"," +
            "\"documentoIdentidad\":\"%s\"," +
            "\"monto\":%s," +
            "\"plazo\":%s," +
            "\"deudaTotalMensual\":%s," +
            "\"nombre\":\"%s\"," +
            "\"apellido\":\"%s\"," +
            "\"email\":\"%s\"," +
            "\"salarioBase\":%s," +
            "\"tipo\":\"VALIDACION_AUTOMATICA\"," +
            "\"tipoPrestamo\": %s" +
            "}",
        NotificacionUtils.safeString(tokenLimpio),
        NotificacionUtils.safeString(solicitud.getId()),
        NotificacionUtils.safeString(solicitud.getDocumentoIdentidad()),
        NotificacionUtils.safeBigDecimal(solicitud.getMonto()),
        NotificacionUtils.safeInteger(solicitud.getPlazo()),
        NotificacionUtils.safeBigDecimal(solicitud.getDeudaTotalMensual()),
        nombre,
        apellido,
        email,
        salarioBase,
        tipoPrestamoJson
    );
    }

    private String construirTipoPrestamoJson(TipoPrestamo tipoPrestamo) {
        if (tipoPrestamo == null) {
            return "{}";
        }
        return String.format("{" +
                        "\"id\":\"%s\"," +
                        "\"nombre\":\"%s\"," +
                        "\"descripcion\":\"%s\"," +
                        "\"montoMinimo\":%s," +
                        "\"montoMaximo\":%s," +
                        "\"plazoMinimoMeses\":%s," +
                        "\"plazoMaximoMeses\":%s," +
                        "\"tasaInteres\":%s," +
                        "\"activo\":%s," +
                        "\"validacionAutomatica\":%s" +
                        "}",
                NotificacionUtils.safeString(tipoPrestamo.getId()),
                NotificacionUtils.safeString(tipoPrestamo.getNombre()),
                NotificacionUtils.safeString(tipoPrestamo.getDescripcion()),
                NotificacionUtils.safeBigDecimal(tipoPrestamo.getMontoMinimo()),
                NotificacionUtils.safeBigDecimal(tipoPrestamo.getMontoMaximo()),
                NotificacionUtils.safeInteger(tipoPrestamo.getPlazoMinimoMeses()),
                NotificacionUtils.safeInteger(tipoPrestamo.getPlazoMaximoMeses()),
                NotificacionUtils.safeBigDecimal(tipoPrestamo.getTasaInteres()),
                NotificacionUtils.safeBoolean(tipoPrestamo.getActivo()),
                NotificacionUtils.safeBoolean(tipoPrestamo.getValidacionAutomatica())
        );
    }



}