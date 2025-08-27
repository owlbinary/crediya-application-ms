package co.com.crediya.api.controller;

import co.com.crediya.api.dto.request.SolicitudRequest;
import co.com.crediya.api.dto.response.SolicitudResponse;
import co.com.crediya.api.mapper.SolicitudMapper;
import co.com.crediya.api.security.JwtUserPrincipal;
import co.com.crediya.model.EstadoSolicitud;
import co.com.crediya.model.Solicitud;
import co.com.crediya.model.exception.DatosInvalidosException;
import co.com.crediya.model.exception.DocumentoNoValidoException;
import co.com.crediya.model.exception.TipoPrestamoNoExisteException;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudControllerTest {

    @Mock
    private RegistrarSolicitudUseCase registrarSolicitudUseCase;

    @Mock
    private SolicitudMapper solicitudMapper;

    @InjectMocks
    private SolicitudController solicitudController;

    private SolicitudRequest solicitudRequestValida;
    private Solicitud solicitudDominio;
    private SolicitudResponse solicitudResponse;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
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

        JwtUserPrincipal principal = JwtUserPrincipal.builder()
                .email("test@ejemplo.com")
                .idUsuario("123")
                .nombre("Juan")
                .apellido("Pérez")
                .idRol("1")
                .build();

        authentication = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_1")));
    }

    @Test
    void deberiaCrearSolicitudExitosamente() {
        when(registrarSolicitudUseCase.ejecutar(anyString(), any(BigDecimal.class), 
                anyInt(), anyString(), anyString()))
            .thenReturn(Mono.just(solicitudDominio));
        when(solicitudMapper.toResponse(any(Solicitud.class)))
            .thenReturn(solicitudResponse);

        StepVerifier.create(solicitudController.crearSolicitud(solicitudRequestValida, "Bearer token", authentication))
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

        StepVerifier.create(solicitudController.crearSolicitud(solicitudRequestValida, null, authentication))
            .expectNextMatches(response -> response.getId().equals("1"))
            .verifyComplete();
    }

    @Test
    void deberiaFallarCuandoDocumentoNoValido() {
        when(registrarSolicitudUseCase.ejecutar(anyString(), any(BigDecimal.class), 
                anyInt(), anyString(), anyString()))
            .thenReturn(Mono.error(new DocumentoNoValidoException("Documento no válido")));

        StepVerifier.create(solicitudController.crearSolicitud(solicitudRequestValida, "Bearer token", authentication))
            .expectError(DocumentoNoValidoException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoTipoPrestamoNoExiste() {
        when(registrarSolicitudUseCase.ejecutar(anyString(), any(BigDecimal.class), 
                anyInt(), anyString(), anyString()))
            .thenReturn(Mono.error(new TipoPrestamoNoExisteException("Tipo de préstamo no existe")));

        StepVerifier.create(solicitudController.crearSolicitud(solicitudRequestValida, "Bearer token", authentication))
            .expectError(TipoPrestamoNoExisteException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoDatosInvalidos() {
        when(registrarSolicitudUseCase.ejecutar(anyString(), any(BigDecimal.class), 
                anyInt(), anyString(), anyString()))
            .thenReturn(Mono.error(new DatosInvalidosException("Datos inválidos")));

        StepVerifier.create(solicitudController.crearSolicitud(solicitudRequestValida, "Bearer token", authentication))
            .expectError(DatosInvalidosException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoErrorInesperado() {
        when(registrarSolicitudUseCase.ejecutar(anyString(), any(BigDecimal.class), 
                anyInt(), anyString(), anyString()))
            .thenReturn(Mono.error(new RuntimeException("Error inesperado")));

        StepVerifier.create(solicitudController.crearSolicitud(solicitudRequestValida, "Bearer token", authentication))
            .expectError(RuntimeException.class)
            .verify();
    }
}
