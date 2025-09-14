package co.com.crediya.sqs.listener;

import co.com.crediya.model.PlanPagoCuota;
import co.com.crediya.model.Solicitud;
import co.com.crediya.sqs.dto.ActualizarEstadoSolicitudMessage;
import co.com.crediya.usecase.actualizarestadosolicitud.ActualizarEstadoSolicitudUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActualizarEstadoSolicitudListenerTest {

    @Mock
    private ActualizarEstadoSolicitudUseCase actualizarEstadoSolicitudUseCase;

    @Mock
    private ObjectMapper objectMapper;

    private ActualizarEstadoSolicitudListener listener;

    private static final String SOLICITUD_ID = "123";
    private static final String NUEVO_ESTADO = "APROBADO";
    private static final String JUSTIFICACION = "Solicitud aprobada automáticamente";
    private static final String MESSAGE_TYPE = "actualizar_estado_solicitud";
    private static final String TEST_MESSAGE_JSON = "{\"tipo\":\"actualizar_estado_solicitud\"}";

    @BeforeEach
    void setUp() {
        listener = new ActualizarEstadoSolicitudListener(actualizarEstadoSolicitudUseCase, objectMapper);
    }

    @Test
    void shouldProcessMessageSuccessfully() throws JsonProcessingException {
        ActualizarEstadoSolicitudMessage message = crearMensajeMock();
        Solicitud solicitud = mock(Solicitud.class);

        when(objectMapper.readValue(TEST_MESSAGE_JSON, ActualizarEstadoSolicitudMessage.class))
                .thenReturn(message);
        when(actualizarEstadoSolicitudUseCase.ejecutar(anyString(), anyString(), anyString(), isNull(), any()))
                .thenReturn(Mono.just(solicitud));

        listener.procesarActualizacionEstado(TEST_MESSAGE_JSON);

        verify(objectMapper).readValue(TEST_MESSAGE_JSON, ActualizarEstadoSolicitudMessage.class);
        verify(actualizarEstadoSolicitudUseCase).ejecutar(
                eq(SOLICITUD_ID), 
                eq(NUEVO_ESTADO), 
                eq(JUSTIFICACION), 
                isNull(), 
                any(List.class)
        );
    }

    @Test
    void shouldProcessMessageWithPlanPago() throws JsonProcessingException {
        ActualizarEstadoSolicitudMessage message = crearMensajeConPlanPagoMock();
        Solicitud solicitud = mock(Solicitud.class);

        when(objectMapper.readValue(TEST_MESSAGE_JSON, ActualizarEstadoSolicitudMessage.class))
                .thenReturn(message);
        when(actualizarEstadoSolicitudUseCase.ejecutar(anyString(), anyString(), anyString(), isNull(), any()))
                .thenReturn(Mono.just(solicitud));

        listener.procesarActualizacionEstado(TEST_MESSAGE_JSON);

        verify(objectMapper).readValue(TEST_MESSAGE_JSON, ActualizarEstadoSolicitudMessage.class);
        verify(actualizarEstadoSolicitudUseCase).ejecutar(
                eq(SOLICITUD_ID), 
                eq(NUEVO_ESTADO), 
                eq(JUSTIFICACION), 
                isNull(), 
                any(List.class)
        );
    }

    @Test
    void shouldIgnoreInvalidMessageType() throws JsonProcessingException {
        String messageJson = "{\"tipo\":\"tipo_invalido\"}";
        ActualizarEstadoSolicitudMessage message = mock(ActualizarEstadoSolicitudMessage.class);
        when(message.getTipo()).thenReturn("tipo_invalido");

        when(objectMapper.readValue(messageJson, ActualizarEstadoSolicitudMessage.class))
                .thenReturn(message);

        listener.procesarActualizacionEstado(messageJson);

        verify(objectMapper).readValue(messageJson, ActualizarEstadoSolicitudMessage.class);
        verifyNoInteractions(actualizarEstadoSolicitudUseCase);
    }

    @Test
    void shouldHandleMessageWithoutParams() throws JsonProcessingException {
        ActualizarEstadoSolicitudMessage message = mock(ActualizarEstadoSolicitudMessage.class);
        when(message.getTipo()).thenReturn(MESSAGE_TYPE);
        when(message.getParams()).thenReturn(null);

        when(objectMapper.readValue(TEST_MESSAGE_JSON, ActualizarEstadoSolicitudMessage.class))
                .thenReturn(message);

        listener.procesarActualizacionEstado(TEST_MESSAGE_JSON);

        verify(objectMapper).readValue(TEST_MESSAGE_JSON, ActualizarEstadoSolicitudMessage.class);
        verifyNoInteractions(actualizarEstadoSolicitudUseCase);
    }

    @Test
    void shouldHandleJsonProcessingException() throws JsonProcessingException {
        String messageJson = "invalid json";
        when(objectMapper.readValue(messageJson, ActualizarEstadoSolicitudMessage.class))
                .thenThrow(new JsonProcessingException("Invalid JSON") {});

        listener.procesarActualizacionEstado(messageJson);

        verify(objectMapper).readValue(messageJson, ActualizarEstadoSolicitudMessage.class);
        verifyNoInteractions(actualizarEstadoSolicitudUseCase);
    }

    @Test
    void shouldHandleUseCaseError() throws JsonProcessingException {
        ActualizarEstadoSolicitudMessage message = crearMensajeMock();

        when(objectMapper.readValue(TEST_MESSAGE_JSON, ActualizarEstadoSolicitudMessage.class))
                .thenReturn(message);
        when(actualizarEstadoSolicitudUseCase.ejecutar(anyString(), anyString(), anyString(), isNull(), any()))
                .thenReturn(Mono.error(new RuntimeException("Error en use case")));

        listener.procesarActualizacionEstado(TEST_MESSAGE_JSON);

        verify(objectMapper).readValue(TEST_MESSAGE_JSON, ActualizarEstadoSolicitudMessage.class);
        verify(actualizarEstadoSolicitudUseCase).ejecutar(
                eq(SOLICITUD_ID), 
                eq(NUEVO_ESTADO), 
                eq(JUSTIFICACION), 
                isNull(), 
                any(List.class)
        );
    }

    @Test
    void shouldConvertPlanPagoCorrectly() throws JsonProcessingException {
        ActualizarEstadoSolicitudMessage message = crearMensajeConPlanPagoMock();
        Solicitud solicitud = mock(Solicitud.class);

        when(objectMapper.readValue(TEST_MESSAGE_JSON, ActualizarEstadoSolicitudMessage.class))
                .thenReturn(message);
        when(actualizarEstadoSolicitudUseCase.ejecutar(anyString(), anyString(), anyString(), isNull(), any()))
                .thenReturn(Mono.just(solicitud));

        listener.procesarActualizacionEstado(TEST_MESSAGE_JSON);

        verify(actualizarEstadoSolicitudUseCase).ejecutar(
                eq(SOLICITUD_ID), 
                eq(NUEVO_ESTADO), 
                eq(JUSTIFICACION), 
                isNull(), 
                argThat(planPago -> {
                    List<PlanPagoCuota> plan = (List<PlanPagoCuota>) planPago;
                    return plan.size() == 1 && 
                           plan.get(0).getNumeroCuota().equals(1) &&
                           plan.get(0).getCuota().equals(1000.0);
                })
        );
    }

    @Test
    void shouldHandleEmptyPlanPago() throws JsonProcessingException {
        ActualizarEstadoSolicitudMessage message = crearMensajeConPlanPagoVacioMock();
        Solicitud solicitud = mock(Solicitud.class);

        when(objectMapper.readValue(TEST_MESSAGE_JSON, ActualizarEstadoSolicitudMessage.class))
                .thenReturn(message);
        when(actualizarEstadoSolicitudUseCase.ejecutar(anyString(), anyString(), anyString(), isNull(), any()))
                .thenReturn(Mono.just(solicitud));

        listener.procesarActualizacionEstado(TEST_MESSAGE_JSON);

        verify(actualizarEstadoSolicitudUseCase).ejecutar(
                eq(SOLICITUD_ID), 
                eq(NUEVO_ESTADO), 
                eq(JUSTIFICACION), 
                isNull(), 
                argThat(planPago -> ((List<PlanPagoCuota>) planPago).isEmpty())
        );
    }

    private ActualizarEstadoSolicitudMessage crearMensajeMock() {
        ActualizarEstadoSolicitudMessage message = mock(ActualizarEstadoSolicitudMessage.class);
        ActualizarEstadoSolicitudMessage.Params params = mock(ActualizarEstadoSolicitudMessage.Params.class);
        
        when(message.getTipo()).thenReturn(MESSAGE_TYPE);
        when(message.getParams()).thenReturn(params);
        when(params.getSolicitudId()).thenReturn(SOLICITUD_ID);
        when(params.getNuevoEstado()).thenReturn(NUEVO_ESTADO);
        when(params.getJustificacion()).thenReturn(JUSTIFICACION);
        when(params.getPlanPago()).thenReturn(null);
        
        return message;
    }

    private ActualizarEstadoSolicitudMessage crearMensajeConPlanPagoMock() {
        ActualizarEstadoSolicitudMessage message = mock(ActualizarEstadoSolicitudMessage.class);
        ActualizarEstadoSolicitudMessage.Params params = mock(ActualizarEstadoSolicitudMessage.Params.class);
        
        ActualizarEstadoSolicitudMessage.PlanPagoCuotaDto cuotaDto = mock(ActualizarEstadoSolicitudMessage.PlanPagoCuotaDto.class);
        when(cuotaDto.getNumeroCuota()).thenReturn(1);
        when(cuotaDto.getCuota()).thenReturn("1000.0");
        when(cuotaDto.getAbonoCapital()).thenReturn("800.0");
        when(cuotaDto.getInteres()).thenReturn("200.0");
        when(cuotaDto.getSaldoRestante()).thenReturn("0.0");
        
        when(message.getTipo()).thenReturn(MESSAGE_TYPE);
        when(message.getParams()).thenReturn(params);
        when(params.getSolicitudId()).thenReturn(SOLICITUD_ID);
        when(params.getNuevoEstado()).thenReturn(NUEVO_ESTADO);
        when(params.getJustificacion()).thenReturn(JUSTIFICACION);
        when(params.getPlanPago()).thenReturn(Arrays.asList(cuotaDto));
        
        return message;
    }

    private ActualizarEstadoSolicitudMessage crearMensajeConPlanPagoVacioMock() {
        ActualizarEstadoSolicitudMessage message = mock(ActualizarEstadoSolicitudMessage.class);
        ActualizarEstadoSolicitudMessage.Params params = mock(ActualizarEstadoSolicitudMessage.Params.class);
        
        when(message.getTipo()).thenReturn(MESSAGE_TYPE);
        when(message.getParams()).thenReturn(params);
        when(params.getSolicitudId()).thenReturn(SOLICITUD_ID);
        when(params.getNuevoEstado()).thenReturn(NUEVO_ESTADO);
        when(params.getJustificacion()).thenReturn(JUSTIFICACION);
        when(params.getPlanPago()).thenReturn(Collections.emptyList());
        
        return message;
    }
}
