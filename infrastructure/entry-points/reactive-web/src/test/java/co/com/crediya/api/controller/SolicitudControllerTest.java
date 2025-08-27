package co.com.crediya.api.controller;

import co.com.crediya.api.dto.request.SolicitudRequest;
import co.com.crediya.api.dto.response.SolicitudConDetalleResponse;
import co.com.crediya.api.dto.response.SolicitudResponse;
import co.com.crediya.api.mapper.SolicitudConDetalleMapper;
import co.com.crediya.api.mapper.SolicitudMapper;
import co.com.crediya.api.security.JwtUserPrincipal;
import co.com.crediya.model.EstadoSolicitud;
import co.com.crediya.model.Solicitud;
import co.com.crediya.model.exception.DatosInvalidosException;
import co.com.crediya.model.exception.DocumentoNoValidoException;
import co.com.crediya.model.exception.InfraestructuraException;
import co.com.crediya.model.exception.TipoPrestamoNoExisteException;
import co.com.crediya.usecase.listarsolicitudes.ListarSolicitudesUseCase;
import co.com.crediya.usecase.listarsolicitudes.SolicitudConDetalle;
import co.com.crediya.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudControllerTest {

    @Mock
    private RegistrarSolicitudUseCase registrarSolicitudUseCase;

    @Mock
    private ListarSolicitudesUseCase listarSolicitudesUseCase;

    @Mock
    private SolicitudMapper solicitudMapper;

    @Mock
    private SolicitudConDetalleMapper solicitudConDetalleMapper;

    @InjectMocks
    private SolicitudController solicitudController;

    private SolicitudRequest solicitudRequestValida;
    private Solicitud solicitudDominio;
    private SolicitudResponse solicitudResponse;
    private SolicitudConDetalle solicitudConDetalle;
    private SolicitudConDetalleResponse solicitudConDetalleResponse;
    private Authentication mockAuthentication;

    @BeforeEach
    void setUp() {
        JwtUserPrincipal userPrincipal = JwtUserPrincipal.builder()
                .idUsuario("123456789")
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@email.com")
                .idRol("1")
                .build();
        mockAuthentication = new UsernamePasswordAuthenticationToken(
            userPrincipal, 
            null, 
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        solicitudRequestValida = SolicitudRequest.builder()
            .documentoIdentidad("12345678")
            .monto(new BigDecimal("1000000"))
            .plazo(12)
            .tipoPrestamoId("1")
            .build();

        solicitudDominio = Solicitud.builder()
            .id("1")
            .documentoIdentidad("12345678")
            .monto(new BigDecimal("1000000"))
            .plazo(12)
            .tipoPrestamoId("1")
            .estado(EstadoSolicitud.PENDIENTE_REVISION)
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .build();

        solicitudResponse = SolicitudResponse.builder()
            .id("1")
            .documentoIdentidad("12345678")
            .monto(new BigDecimal("1000000"))
            .plazo(12)
            .tipoPrestamoId("1")
            .estado("PENDIENTE_REVISION")
            .estadoDescripcion("Pendiente de revisión")
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .build();

        solicitudConDetalle = SolicitudConDetalle.builder()
            .id("1")
            .documentoIdentidad("12345678")
            .monto(new BigDecimal("1000000"))
            .plazo(12)
            .tipoPrestamoId("1")
            .descripcionTipoPrestamo("Préstamo Personal")
            .tasaInteres(new BigDecimal("15.5"))
            .estado(EstadoSolicitud.PENDIENTE_REVISION)
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .email("test@test.com")
            .nombre("Juan")
            .apellido("Pérez")
            .salarioBase(new BigDecimal("3000000"))
            .deudaTotalMensualSolicitud(new BigDecimal("200000"))
            .build();

        solicitudConDetalleResponse = SolicitudConDetalleResponse.builder()
            .id("1")
            .documentoIdentidad("12345678")
            .monto(new BigDecimal("1000000"))
            .plazo(12)
            .tipoPrestamoId("1")
            .descripcionTipoPrestamo("Préstamo Personal")
            .tasaInteres(new BigDecimal("15.5"))
            .estado("PENDIENTE_REVISION")
            .estadoDescripcion("Pendiente de revisión")
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .email("test@test.com")
            .nombre("Juan")
            .apellido("Pérez")
            .salarioBase(new BigDecimal("3000000"))
            .deudaTotalMensualSolicitud(new BigDecimal("200000"))
            .build();
    }

    @Test
    void deberiaCrearSolicitudExitosamente() {
        when(registrarSolicitudUseCase.ejecutar(anyString(), any(BigDecimal.class), 
                anyInt(), anyString(), anyString()))
            .thenReturn(Mono.just(solicitudDominio));
        when(solicitudMapper.toResponse(any(Solicitud.class)))
            .thenReturn(solicitudResponse);

        StepVerifier.create(solicitudController.crearSolicitud(solicitudRequestValida, "Bearer token", mockAuthentication))
            .expectNextMatches(response -> 
                response.getId().equals("1") &&
                response.getDocumentoIdentidad().equals("12345678") &&
                response.getMonto().equals(new BigDecimal("1000000")) &&
                response.getPlazo().equals(12) &&
                response.getTipoPrestamoId().equals("1") &&
                response.getEstado().equals("PENDIENTE_REVISION")
            )
            .verifyComplete();
    }

    @Test
    void deberiaCrearSolicitudSinTokenAutorizacion() {
        when(registrarSolicitudUseCase.ejecutar(anyString(), any(BigDecimal.class), 
                anyInt(), anyString(), isNull()))
            .thenReturn(Mono.just(solicitudDominio));
        when(solicitudMapper.toResponse(any(Solicitud.class)))
            .thenReturn(solicitudResponse);

        StepVerifier.create(solicitudController.crearSolicitud(solicitudRequestValida, null, mockAuthentication))
            .expectNextMatches(response -> response.getId().equals("1"))
            .verifyComplete();
    }

    @Test
    void deberiaFallarCuandoDocumentoNoValido() {
        when(registrarSolicitudUseCase.ejecutar(anyString(), any(BigDecimal.class), 
                anyInt(), anyString(), anyString()))
            .thenReturn(Mono.error(new DocumentoNoValidoException("Documento no válido")));

        StepVerifier.create(solicitudController.crearSolicitud(solicitudRequestValida, "Bearer token", mockAuthentication))
            .expectError(DocumentoNoValidoException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoTipoPrestamoNoExiste() {
        when(registrarSolicitudUseCase.ejecutar(anyString(), any(BigDecimal.class), 
                anyInt(), anyString(), anyString()))
            .thenReturn(Mono.error(new TipoPrestamoNoExisteException("Tipo de préstamo no existe")));

        StepVerifier.create(solicitudController.crearSolicitud(solicitudRequestValida, "Bearer token", mockAuthentication))
            .expectError(TipoPrestamoNoExisteException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoDatosInvalidos() {
        when(registrarSolicitudUseCase.ejecutar(anyString(), any(BigDecimal.class), 
                anyInt(), anyString(), anyString()))
            .thenReturn(Mono.error(new DatosInvalidosException("Datos inválidos")));

        StepVerifier.create(solicitudController.crearSolicitud(solicitudRequestValida, "Bearer token", mockAuthentication))
            .expectError(DatosInvalidosException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoErrorInesperado() {
        when(registrarSolicitudUseCase.ejecutar(anyString(), any(BigDecimal.class), 
                anyInt(), anyString(), anyString()))
            .thenReturn(Mono.error(new RuntimeException("Error inesperado")));

        StepVerifier.create(solicitudController.crearSolicitud(solicitudRequestValida, "Bearer token", mockAuthentication))
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    void deberiaListarSolicitudesPendientesExitosamente() {
        when(listarSolicitudesUseCase.ejecutar(anyInt(), anyInt(), anyString()))
            .thenReturn(Flux.just(solicitudConDetalle));
        when(solicitudConDetalleMapper.toResponse(any(SolicitudConDetalle.class)))
            .thenReturn(solicitudConDetalleResponse);

        StepVerifier.create(solicitudController.listarSolicitudesPendientes(0, 10, "Bearer token", mockAuthentication))
            .expectNextMatches(response -> 
                response.getId().equals("1") &&
                response.getDocumentoIdentidad().equals("12345678") &&
                response.getMonto().equals(new BigDecimal("1000000")) &&
                response.getPlazo().equals(12) &&
                response.getTipoPrestamoId().equals("1") &&
                response.getEstado().equals("PENDIENTE_REVISION") &&
                response.getEmail().equals("test@test.com") &&
                response.getNombre().equals("Juan") &&
                response.getApellido().equals("Pérez")
            )
            .verifyComplete();
    }

    @Test
    void deberiaListarSolicitudesPendientesSinToken() {
        when(listarSolicitudesUseCase.ejecutar(anyInt(), anyInt(), isNull()))
            .thenReturn(Flux.just(solicitudConDetalle));
        when(solicitudConDetalleMapper.toResponse(any(SolicitudConDetalle.class)))
            .thenReturn(solicitudConDetalleResponse);

        StepVerifier.create(solicitudController.listarSolicitudesPendientes(0, 10, null, mockAuthentication))
            .expectNextMatches(response -> response.getId().equals("1"))
            .verifyComplete();
    }

    @Test
    void deberiaListarSolicitudesPendientesConParametrosPersonalizados() {
        when(listarSolicitudesUseCase.ejecutar(eq(2), eq(5), anyString()))
            .thenReturn(Flux.just(solicitudConDetalle));
        when(solicitudConDetalleMapper.toResponse(any(SolicitudConDetalle.class)))
            .thenReturn(solicitudConDetalleResponse);

        StepVerifier.create(solicitudController.listarSolicitudesPendientes(2, 5, "Bearer token", mockAuthentication))
            .expectNextMatches(response -> response.getId().equals("1"))
            .verifyComplete();
    }

    @Test
    void deberiaListarSolicitudesPendientesVacia() {
        when(listarSolicitudesUseCase.ejecutar(anyInt(), anyInt(), anyString()))
            .thenReturn(Flux.empty());

        StepVerifier.create(solicitudController.listarSolicitudesPendientes(0, 10, "Bearer token", mockAuthentication))
            .verifyComplete();
    }

    @Test
    void deberiaFallarCuandoErrorEnListarSolicitudes() {
        when(listarSolicitudesUseCase.ejecutar(anyInt(), anyInt(), anyString()))
            .thenReturn(Flux.error(new InfraestructuraException("Error de infraestructura")));

        StepVerifier.create(solicitudController.listarSolicitudesPendientes(0, 10, "Bearer token", mockAuthentication))
            .expectError(InfraestructuraException.class)
            .verify();
    }

    @Test
    void deberiaListarMultiplesSolicitudesPendientes() {
        SolicitudConDetalle solicitud2 = SolicitudConDetalle.builder()
            .id("2")
            .documentoIdentidad("87654321")
            .monto(new BigDecimal("2000000"))
            .plazo(24)
            .tipoPrestamoId("2")
            .descripcionTipoPrestamo("Préstamo Hipotecario")
            .tasaInteres(new BigDecimal("12.5"))
            .estado(EstadoSolicitud.PENDIENTE_REVISION)
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .email("maria@test.com")
            .nombre("María")
            .apellido("García")
            .salarioBase(new BigDecimal("4000000"))
            .deudaTotalMensualSolicitud(new BigDecimal("300000"))
            .build();

        SolicitudConDetalleResponse response2 = SolicitudConDetalleResponse.builder()
            .id("2")
            .documentoIdentidad("87654321")
            .monto(new BigDecimal("2000000"))
            .plazo(24)
            .tipoPrestamoId("2")
            .descripcionTipoPrestamo("Préstamo Hipotecario")
            .tasaInteres(new BigDecimal("12.5"))
            .estado("PENDIENTE_REVISION")
            .estadoDescripcion("Pendiente de revisión")
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .email("maria@test.com")
            .nombre("María")
            .apellido("García")
            .salarioBase(new BigDecimal("4000000"))
            .deudaTotalMensualSolicitud(new BigDecimal("300000"))
            .build();

        when(listarSolicitudesUseCase.ejecutar(anyInt(), anyInt(), anyString()))
            .thenReturn(Flux.just(solicitudConDetalle, solicitud2));
        when(solicitudConDetalleMapper.toResponse(solicitudConDetalle))
            .thenReturn(solicitudConDetalleResponse);
        when(solicitudConDetalleMapper.toResponse(solicitud2))
            .thenReturn(response2);

        StepVerifier.create(solicitudController.listarSolicitudesPendientes(0, 10, "Bearer token", mockAuthentication))
            .expectNextMatches(response -> response.getId().equals("1"))
            .expectNextMatches(response -> response.getId().equals("2"))
            .verifyComplete();
    }
}
