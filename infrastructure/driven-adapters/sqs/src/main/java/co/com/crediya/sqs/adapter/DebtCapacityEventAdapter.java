package co.com.crediya.sqs.adapter;

import co.com.crediya.model.DetalleUsuario;
import co.com.crediya.model.Solicitud;
import co.com.crediya.model.TipoPrestamo;
import co.com.crediya.model.gateway.DebtCapacityEventGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor

public class DebtCapacityEventAdapter implements DebtCapacityEventGateway {

    private static final String FIELD_SOLICITUD_ID = "solicitudId";
    private static final String FIELD_DOCUMENTO_IDENTIDAD = "documentoIdentidad";
    private static final String FIELD_MONTO = "monto";
    private static final String FIELD_PLAZO = "plazo";
    private static final String FIELD_DEUDA_TOTAL_MENSUAL = "deudaTotalMensual";
    private static final String FIELD_TIPO = "tipo";
    private static final String FIELD_NOMBRE = "nombre";
    private static final String FIELD_APELLIDO = "apellido";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_SALARIO_BASE = "salarioBase";
    private static final String FIELD_TIPO_PRESTAMO = "tipoPrestamo";
    private static final String VALIDACION_AUTOMATICA = "VALIDACION_AUTOMATICA";

    private static final String FIELD_ID = "id";
    private static final String FIELD_NOMBRE_TIPO = "nombre";
    private static final String FIELD_DESCRIPCION = "descripcion";
    private static final String FIELD_MONTO_MINIMO = "montoMinimo";
    private static final String FIELD_MONTO_MAXIMO = "montoMaximo";
    private static final String FIELD_PLAZO_MINIMO_MESES = "plazoMinimoMeses";
    private static final String FIELD_PLAZO_MAXIMO_MESES = "plazoMaximoMeses";
    private static final String FIELD_TASA_INTERES = "tasaInteres";
    private static final String FIELD_ACTIVO = "activo";
    private static final String FIELD_VALIDACION_AUTOMATICA = "validacionAutomatica";

    private final SqsAsyncClient sqsAsyncClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.capacidad-endeudamiento.queueUrl}")
    private String debtCapacityEventQueueUrl;

    @Override
    public Mono<Void> enviarEvaluacionCapacidadEndeudamiento(Solicitud solicitud,
                                                            DetalleUsuario detalleUsuario,
                                                            TipoPrestamo tipoPrestamo) {
        try {
            Map<String, Object> message = construirMensaje(solicitud, detalleUsuario, tipoPrestamo);
            String messageBody = objectMapper.writeValueAsString(message);

            log.info("Enviando mensaje de evaluación de capacidad de endeudamiento para solicitud: {}", solicitud.getId());

            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(debtCapacityEventQueueUrl)
                    .messageBody(messageBody)
                    .build();

            return Mono.fromFuture(sqsAsyncClient.sendMessage(sendMessageRequest))
                    .doOnSuccess(response ->
                        log.info("Mensaje enviado exitosamente a SQS debt capacity, MessageId: {}", response.messageId()))
                    .doOnError(error ->
                        log.error("Error enviando mensaje a SQS debt capacity: {}", error.getMessage(), error))
                    .then();

        } catch (Exception e) {
            log.error("Error construyendo mensaje para SQS debt capacity: {}", e.getMessage(), e);
            return Mono.error(e);
        }
    }

    private Map<String, Object> construirMensaje(Solicitud solicitud,
                                               DetalleUsuario detalleUsuario,
                                               TipoPrestamo tipoPrestamo) {
        Map<String, Object> message = new HashMap<>();

        message.put(FIELD_SOLICITUD_ID, solicitud.getId());
        message.put(FIELD_DOCUMENTO_IDENTIDAD, solicitud.getDocumentoIdentidad());
        message.put(FIELD_MONTO, solicitud.getMonto());
        message.put(FIELD_PLAZO, solicitud.getPlazo());
        message.put(FIELD_DEUDA_TOTAL_MENSUAL, solicitud.getDeudaTotalMensual());
        message.put(FIELD_TIPO, VALIDACION_AUTOMATICA);

        if (detalleUsuario != null) {
            message.put(FIELD_NOMBRE, detalleUsuario.getNombre() != null ? detalleUsuario.getNombre() : "");
            message.put(FIELD_APELLIDO, detalleUsuario.getApellido() != null ? detalleUsuario.getApellido() : "");
            message.put(FIELD_EMAIL, detalleUsuario.getEmail() != null ? detalleUsuario.getEmail() : "");
            message.put(FIELD_SALARIO_BASE, detalleUsuario.getSalarioBase());
        } else {
            message.put(FIELD_NOMBRE, "");
            message.put(FIELD_APELLIDO, "");
            message.put(FIELD_EMAIL, solicitud.getEmail() != null ? solicitud.getEmail() : "");
            message.put(FIELD_SALARIO_BASE, null);
        }

        if (tipoPrestamo != null) {
            Map<String, Object> tipoPrestamoMap = new HashMap<>();
            tipoPrestamoMap.put(FIELD_ID, tipoPrestamo.getId());
            tipoPrestamoMap.put(FIELD_NOMBRE_TIPO, tipoPrestamo.getNombre());
            tipoPrestamoMap.put(FIELD_DESCRIPCION, tipoPrestamo.getDescripcion());
            tipoPrestamoMap.put(FIELD_MONTO_MINIMO, tipoPrestamo.getMontoMinimo());
            tipoPrestamoMap.put(FIELD_MONTO_MAXIMO, tipoPrestamo.getMontoMaximo());
            tipoPrestamoMap.put(FIELD_PLAZO_MINIMO_MESES, tipoPrestamo.getPlazoMinimoMeses());
            tipoPrestamoMap.put(FIELD_PLAZO_MAXIMO_MESES, tipoPrestamo.getPlazoMaximoMeses());
            tipoPrestamoMap.put(FIELD_TASA_INTERES, tipoPrestamo.getTasaInteres());
            tipoPrestamoMap.put(FIELD_ACTIVO, tipoPrestamo.getActivo());
            tipoPrestamoMap.put(FIELD_VALIDACION_AUTOMATICA, tipoPrestamo.getValidacionAutomatica());
            message.put(FIELD_TIPO_PRESTAMO, tipoPrestamoMap);
        } else {
            message.put(FIELD_TIPO_PRESTAMO, new HashMap<>());
        }

        return message;
    }
}
