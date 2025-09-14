package co.com.crediya.sqs.adapter;

import co.com.crediya.model.DetalleUsuario;
import co.com.crediya.model.Solicitud;
import co.com.crediya.model.TipoPrestamo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DebtCapacityEventAdapterTest {

    @Mock
    private SqsAsyncClient sqsAsyncClient;

    @Mock
    private ObjectMapper objectMapper;

    private DebtCapacityEventAdapter adapter;

    private static final String QUEUE_URL = "test-queue-url";
    private static final String MESSAGE_ID = "test-message-id";
    private static final String TEST_JSON_MESSAGE = "{\"solicitudId\":\"123\"}";

    @BeforeEach
    void setUp() {
        adapter = new DebtCapacityEventAdapter(sqsAsyncClient, objectMapper);
        ReflectionTestUtils.setField(adapter, "debtCapacityEventQueueUrl", QUEUE_URL);
    }

        @Test
        void enviarMensajeExitosamente() throws JsonProcessingException {
        Solicitud solicitud = crearSolicitudMock();
        DetalleUsuario detalleUsuario = crearDetalleUsuarioMock();
        TipoPrestamo tipoPrestamo = crearTipoPrestamoMock();

        when(objectMapper.writeValueAsString(any())).thenReturn(TEST_JSON_MESSAGE);

        SendMessageResponse response = SendMessageResponse.builder()
                .messageId(MESSAGE_ID)
                .build();
        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        Mono<Void> result = adapter.enviarEvaluacionCapacidadEndeudamiento(solicitud, detalleUsuario, tipoPrestamo);

        StepVerifier.create(result)
                .verifyComplete();

        verify(objectMapper).writeValueAsString(any());
        verify(sqsAsyncClient).sendMessage(any(SendMessageRequest.class));
    }

        @Test
        void fallaCuandoSerializacionFalla() throws JsonProcessingException {
        Solicitud solicitud = crearSolicitudMock();
        DetalleUsuario detalleUsuario = crearDetalleUsuarioMock();
        TipoPrestamo tipoPrestamo = crearTipoPrestamoMock();

        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("Error serialización") {});

        Mono<Void> result = adapter.enviarEvaluacionCapacidadEndeudamiento(solicitud, detalleUsuario, tipoPrestamo);

        StepVerifier.create(result)
                .expectError(JsonProcessingException.class)
                .verify();

        verify(objectMapper).writeValueAsString(any());
        verifyNoInteractions(sqsAsyncClient);
    }

        @Test
        void fallaCuandoSqsFalla() throws JsonProcessingException {
        Solicitud solicitud = crearSolicitudMock();
        DetalleUsuario detalleUsuario = crearDetalleUsuarioMock();
        TipoPrestamo tipoPrestamo = crearTipoPrestamoMock();

        when(objectMapper.writeValueAsString(any())).thenReturn(TEST_JSON_MESSAGE);

        CompletableFuture<SendMessageResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("SQS Error"));
        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(failedFuture);

        Mono<Void> result = adapter.enviarEvaluacionCapacidadEndeudamiento(solicitud, detalleUsuario, tipoPrestamo);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(objectMapper).writeValueAsString(any());
        verify(sqsAsyncClient).sendMessage(any(SendMessageRequest.class));
    }

        @Test
        void funcionaConDetalleUsuarioNulo() throws JsonProcessingException {
        Solicitud solicitud = crearSolicitudMock();
        TipoPrestamo tipoPrestamo = crearTipoPrestamoMock();

        when(objectMapper.writeValueAsString(any())).thenReturn(TEST_JSON_MESSAGE);

        SendMessageResponse response = SendMessageResponse.builder()
                .messageId(MESSAGE_ID)
                .build();
        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        Mono<Void> result = adapter.enviarEvaluacionCapacidadEndeudamiento(solicitud, null, tipoPrestamo);

        StepVerifier.create(result)
                .verifyComplete();

        verify(objectMapper).writeValueAsString(any());
        verify(sqsAsyncClient).sendMessage(any(SendMessageRequest.class));
    }

        @Test
        void funcionaConTipoPrestamoNulo() throws JsonProcessingException {
        Solicitud solicitud = crearSolicitudMock();
        DetalleUsuario detalleUsuario = crearDetalleUsuarioMock();

        when(objectMapper.writeValueAsString(any())).thenReturn(TEST_JSON_MESSAGE);

        SendMessageResponse response = SendMessageResponse.builder()
                .messageId(MESSAGE_ID)
                .build();
        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        Mono<Void> result = adapter.enviarEvaluacionCapacidadEndeudamiento(solicitud, detalleUsuario, null);

        StepVerifier.create(result)
                .verifyComplete();

        verify(objectMapper).writeValueAsString(any());
        verify(sqsAsyncClient).sendMessage(any(SendMessageRequest.class));
    }

    private Solicitud crearSolicitudMock() {
        Solicitud solicitud = mock(Solicitud.class);
        when(solicitud.getId()).thenReturn("123");
        when(solicitud.getDocumentoIdentidad()).thenReturn("12345678");
        when(solicitud.getMonto()).thenReturn(BigDecimal.valueOf(10000));
        when(solicitud.getPlazo()).thenReturn(12);
        when(solicitud.getDeudaTotalMensual()).thenReturn(BigDecimal.valueOf(500));
        when(solicitud.getEmail()).thenReturn("test@email.com");
        return solicitud;
    }

    private DetalleUsuario crearDetalleUsuarioMock() {
        DetalleUsuario detalleUsuario = mock(DetalleUsuario.class);
        when(detalleUsuario.getNombre()).thenReturn("Juan");
        when(detalleUsuario.getApellido()).thenReturn("Pérez");
        when(detalleUsuario.getEmail()).thenReturn("juan.perez@email.com");
        when(detalleUsuario.getSalarioBase()).thenReturn(BigDecimal.valueOf(3000000));
        return detalleUsuario;
    }

    private TipoPrestamo crearTipoPrestamoMock() {
        TipoPrestamo tipoPrestamo = mock(TipoPrestamo.class);
        when(tipoPrestamo.getId()).thenReturn("1");
        when(tipoPrestamo.getNombre()).thenReturn("Préstamo Personal");
        when(tipoPrestamo.getDescripcion()).thenReturn("Préstamo para gastos personales");
        when(tipoPrestamo.getMontoMinimo()).thenReturn(BigDecimal.valueOf(1000000));
        when(tipoPrestamo.getMontoMaximo()).thenReturn(BigDecimal.valueOf(50000000));
        when(tipoPrestamo.getPlazoMinimoMeses()).thenReturn(6);
        when(tipoPrestamo.getPlazoMaximoMeses()).thenReturn(60);
        when(tipoPrestamo.getTasaInteres()).thenReturn(BigDecimal.valueOf(0.12));
        when(tipoPrestamo.getActivo()).thenReturn(true);
        when(tipoPrestamo.getValidacionAutomatica()).thenReturn(true);
        return tipoPrestamo;
    }
}
