package co.com.crediya.usecase.registrarsolicitud;

import co.com.crediya.model.EstadoSolicitud;
import co.com.crediya.model.Solicitud;
import co.com.crediya.model.ValidacionDocumento;
import co.com.crediya.model.exception.DatosInvalidosException;
import co.com.crediya.model.exception.DocumentoNoValidoException;
import co.com.crediya.model.exception.TipoPrestamoNoExisteException;
import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.model.gateway.ValidacionDocumentoGateway;
import co.com.crediya.model.gateway.TipoPrestamoGateway;
import co.com.crediya.model.gateway.NotificacionGateway;
import co.com.crediya.model.TipoPrestamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class RegistrarSolicitudUseCaseTest {

    @Mock
    private SolicitudGateway solicitudRepository;
    
    @Mock
    private ValidacionDocumentoGateway validacionDocumentoGateway;


    @Mock
    private TipoPrestamoGateway tipoPrestamoGateway;

    @Mock
    private NotificacionGateway notificacionGateway;

    @InjectMocks
    private RegistrarSolicitudUseCase useCase;

    private String documentoValido;
    private BigDecimal montoValido;
    private Integer plazoValido;
    private String tipoPrestamoValido;

    @BeforeEach
    void setUp() {
        documentoValido = "12345678";
        montoValido = BigDecimal.valueOf(1000000);
        plazoValido = 12;
        tipoPrestamoValido = "1";
    lenient().when(solicitudRepository.findByDocumentoIdentidadAndEstado(anyString(), any())).thenReturn(reactor.core.publisher.Flux.empty());
    }

    @Test
    void deberiaCrearSolicitudExitosamente() {
        Solicitud solicitudEsperada = Solicitud.crearNueva(
            documentoValido, montoValido, plazoValido, tipoPrestamoValido
        );
        ValidacionDocumento validacionExitosa = ValidacionDocumento.builder()
            .existe(true)
            .mensaje("Documento válido")
            .build();
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder()
            .id(tipoPrestamoValido)
            .validacionAutomatica(false)
            .build();
        when(validacionDocumentoGateway.validarDocumento(anyString(), anyString())).thenReturn(Mono.just(validacionExitosa));
        when(solicitudRepository.existeTipoPrestamo(anyString())).thenReturn(Mono.just(true));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenReturn(Mono.just(solicitudEsperada));
        when(tipoPrestamoGateway.buscarPorId(anyString())).thenReturn(Mono.just(tipoPrestamo));

        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectNextMatches(solicitud -> 
                solicitud.getDocumentoIdentidad().equals(documentoValido) &&
                solicitud.getMonto().equals(montoValido) &&
                solicitud.getPlazo().equals(plazoValido) &&
                solicitud.getTipoPrestamoId().equals(tipoPrestamoValido) &&
                solicitud.getEstado() == EstadoSolicitud.PENDIENTE_REVISION
            )
            .verifyComplete();
    }

    @Test
    void deberiaFallarCuandoDocumentoEsNulo() {
        StepVerifier.create(useCase.ejecutar(null, montoValido, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectError(DatosInvalidosException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoDocumentoEsVacio() {
        StepVerifier.create(useCase.ejecutar("", montoValido, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectError(DatosInvalidosException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoDocumentoSoloTieneEspacios() {
        StepVerifier.create(useCase.ejecutar("   ", montoValido, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectError(DatosInvalidosException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoMontoEsNulo() {
        StepVerifier.create(useCase.ejecutar(documentoValido, null, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectError(DatosInvalidosException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoMontoEsCero() {
        StepVerifier.create(useCase.ejecutar(documentoValido, BigDecimal.ZERO, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectError(DatosInvalidosException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoMontoEsNegativo() {
        BigDecimal montoNegativo = BigDecimal.valueOf(-1000);
        
        StepVerifier.create(useCase.ejecutar(documentoValido, montoNegativo, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectError(DatosInvalidosException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoPlazoEsNulo() {
        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, null, tipoPrestamoValido, "Bearer test-token"))
            .expectError(DatosInvalidosException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoPlazoEsCero() {
        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, 0, tipoPrestamoValido, "Bearer test-token"))
            .expectError(DatosInvalidosException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoPlazoEsNegativo() {
        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, -1, tipoPrestamoValido, "Bearer test-token"))
            .expectError(DatosInvalidosException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoTipoPrestamoEsNulo() {
        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, null, "Bearer test-token"))
            .expectError(DatosInvalidosException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoTipoPrestamoEsVacio() {
        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, "", "Bearer test-token"))
            .expectError(DatosInvalidosException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoTipoPrestamoSoloTieneEspacios() {
        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, "   ", "Bearer test-token"))
            .expectError(DatosInvalidosException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoTipoPrestamoNoExiste() {
        ValidacionDocumento validacionExitosa = ValidacionDocumento.builder()
            .existe(true)
            .mensaje("Documento válido")
            .build();
            
        when(validacionDocumentoGateway.validarDocumento(anyString(), anyString())).thenReturn(Mono.just(validacionExitosa));
        when(solicitudRepository.existeTipoPrestamo(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectError(TipoPrestamoNoExisteException.class)
            .verify();
    }
    
    @Test
    void deberiaFallarCuandoDocumentoNoExiste() {
        ValidacionDocumento validacionFallida = ValidacionDocumento.builder()
            .existe(false)
            .mensaje("El documento de identidad no existe en el sistema")
            .build();
            
        when(validacionDocumentoGateway.validarDocumento(anyString(), anyString())).thenReturn(Mono.just(validacionFallida));
        when(solicitudRepository.existeTipoPrestamo(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectError(DocumentoNoValidoException.class)
            .verify();
    }

    @Test
    void deberiaCrearSolicitudSinTokenAutorizacion() {
        Solicitud solicitudEsperada = Solicitud.crearNueva(
            documentoValido, montoValido, plazoValido, tipoPrestamoValido
        );
        ValidacionDocumento validacionExitosa = ValidacionDocumento.builder()
            .existe(true)
            .mensaje("Documento válido")
            .build();
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder()
            .id(tipoPrestamoValido)
            .validacionAutomatica(false)
            .build();
        when(validacionDocumentoGateway.validarDocumento(documentoValido, null)).thenReturn(Mono.just(validacionExitosa));
        when(solicitudRepository.existeTipoPrestamo(anyString())).thenReturn(Mono.just(true));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenReturn(Mono.just(solicitudEsperada));
        when(tipoPrestamoGateway.buscarPorId(anyString())).thenReturn(Mono.just(tipoPrestamo));

        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, tipoPrestamoValido, null))
            .expectNextMatches(solicitud -> 
                solicitud.getDocumentoIdentidad().equals(documentoValido) &&
                solicitud.getMonto().equals(montoValido) &&
                solicitud.getPlazo().equals(plazoValido) &&
                solicitud.getTipoPrestamoId().equals(tipoPrestamoValido) &&
                solicitud.getEstado() == EstadoSolicitud.PENDIENTE_REVISION
            )
            .verifyComplete();
    }

    @Test
    void deberiaCrearSolicitudConTokenVacio() {
        Solicitud solicitudEsperada = Solicitud.crearNueva(
            documentoValido, montoValido, plazoValido, tipoPrestamoValido
        );
        ValidacionDocumento validacionExitosa = ValidacionDocumento.builder()
            .existe(true)
            .mensaje("Documento válido")
            .build();
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder()
            .id(tipoPrestamoValido)
            .validacionAutomatica(false)
            .build();
        when(validacionDocumentoGateway.validarDocumento(documentoValido, "")).thenReturn(Mono.just(validacionExitosa));
        when(solicitudRepository.existeTipoPrestamo(anyString())).thenReturn(Mono.just(true));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenReturn(Mono.just(solicitudEsperada));
        when(tipoPrestamoGateway.buscarPorId(anyString())).thenReturn(Mono.just(tipoPrestamo));

        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, tipoPrestamoValido, ""))
            .expectNextMatches(solicitud -> 
                solicitud.getDocumentoIdentidad().equals(documentoValido) &&
                solicitud.getMonto().equals(montoValido) &&
                solicitud.getPlazo().equals(plazoValido) &&
                solicitud.getTipoPrestamoId().equals(tipoPrestamoValido) &&
                solicitud.getEstado() == EstadoSolicitud.PENDIENTE_REVISION
            )
            .verifyComplete();
    }

    @Test
    void deberiaFallarCuandoValidacionDocumentoFalla() {
        RuntimeException errorValidacion = new RuntimeException("Error de conexión con servicio de validación");
        
        when(validacionDocumentoGateway.validarDocumento(anyString(), anyString())).thenReturn(Mono.error(errorValidacion));
        when(solicitudRepository.existeTipoPrestamo(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    void deberiaFallarCuandoGuardadoFalla() {
        ValidacionDocumento validacionExitosa = ValidacionDocumento.builder()
            .existe(true)
            .mensaje("Documento válido")
            .build();
        RuntimeException errorGuardado = new RuntimeException("Error al guardar en base de datos");
        
        when(validacionDocumentoGateway.validarDocumento(anyString(), anyString())).thenReturn(Mono.just(validacionExitosa));
        when(solicitudRepository.existeTipoPrestamo(anyString())).thenReturn(Mono.just(true));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenReturn(Mono.error(errorGuardado));

        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    void deberiaCrearSolicitudYEnviarNotificacionAutomaticaSiValidacionAutomaticaEsTrue() {
        Solicitud solicitudEsperada = Solicitud.crearNueva(documentoValido, montoValido, plazoValido, tipoPrestamoValido);
        ValidacionDocumento validacionExitosa = ValidacionDocumento.builder().existe(true).mensaje("Documento válido").build();
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder().id(tipoPrestamoValido).validacionAutomatica(true).tasaInteres(BigDecimal.valueOf(0.02)).build();
        co.com.crediya.model.DetalleUsuario detalleUsuario = co.com.crediya.model.DetalleUsuario.builder().email("test@mail.com").build();
        when(validacionDocumentoGateway.validarDocumento(anyString(), anyString())).thenReturn(Mono.just(validacionExitosa));
        when(solicitudRepository.existeTipoPrestamo(anyString())).thenReturn(Mono.just(true));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenReturn(Mono.just(solicitudEsperada));
        when(tipoPrestamoGateway.buscarPorId(anyString())).thenReturn(Mono.just(tipoPrestamo));
        when(validacionDocumentoGateway.obtenerDetalleUsuario(anyString(), anyString())).thenReturn(Mono.just(detalleUsuario));
        when(notificacionGateway.enviarValidacionAutomaticaSolicitud(any(), any(), any(), anyString())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectNextMatches(solicitud -> solicitud.getDocumentoIdentidad().equals(documentoValido))
            .verifyComplete();
    }

    @Test
    void deberiaSumarDeudaTotalMensualDeSolicitudesAprobadas() {
        Solicitud solicitudAprobada1 = Solicitud.crearNueva(documentoValido, BigDecimal.valueOf(1000), 10, tipoPrestamoValido);
        solicitudAprobada1.setEstado(EstadoSolicitud.APROBADO);
        Solicitud solicitudAprobada2 = Solicitud.crearNueva(documentoValido, BigDecimal.valueOf(2000), 20, tipoPrestamoValido);
        solicitudAprobada2.setEstado(EstadoSolicitud.APROBADO);
        ValidacionDocumento validacionExitosa = ValidacionDocumento.builder().existe(true).mensaje("Documento válido").build();
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder().id(tipoPrestamoValido).validacionAutomatica(false).tasaInteres(BigDecimal.valueOf(0.01)).build();
        when(validacionDocumentoGateway.validarDocumento(anyString(), anyString())).thenReturn(Mono.just(validacionExitosa));
        when(solicitudRepository.existeTipoPrestamo(anyString())).thenReturn(Mono.just(true));
        when(solicitudRepository.findByDocumentoIdentidadAndEstado(anyString(), any())).thenReturn(reactor.core.publisher.Flux.just(solicitudAprobada1, solicitudAprobada2));
        when(tipoPrestamoGateway.buscarPorId(anyString())).thenReturn(Mono.just(tipoPrestamo));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenAnswer(invocation -> {
            Solicitud s = invocation.getArgument(0);
            assertNotNull(s.getDeudaTotalMensual());
            return Mono.just(s);
        });
        when(tipoPrestamoGateway.buscarPorId(anyString())).thenReturn(Mono.just(tipoPrestamo));

        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectNextMatches(solicitud -> solicitud.getDocumentoIdentidad().equals(documentoValido) && solicitud.getDeudaTotalMensual() != null)
            .verifyComplete();
    }

    @Test
    void deberiaRetornarCeroSiMontoPlazoTipoPrestamoIdEsNuloEnCalculoCuota() {
        Solicitud solicitudAprobada = Solicitud.builder().id("1").monto(null).plazo(null).tipoPrestamoId(null).build();
        Mono<BigDecimal> cuota = useCase.calcularCuotaMensualConTipoPrestamo(solicitudAprobada);
        StepVerifier.create(cuota).expectNext(BigDecimal.ZERO).verifyComplete();
    }

    @Test
    void deberiaRetornarCeroSiTasaInteresEsNulaOCero() {
        Solicitud solicitudAprobada = Solicitud.crearNueva(documentoValido, BigDecimal.valueOf(1000), 10, tipoPrestamoValido);
        solicitudAprobada.setEstado(EstadoSolicitud.APROBADO);
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder().id(tipoPrestamoValido).tasaInteres(null).build();
        when(tipoPrestamoGateway.buscarPorId(anyString())).thenReturn(Mono.just(tipoPrestamo));
        Mono<BigDecimal> cuota = useCase.calcularCuotaMensualConTipoPrestamo(solicitudAprobada);
        StepVerifier.create(cuota).expectNext(BigDecimal.ZERO).verifyComplete();

        tipoPrestamo = TipoPrestamo.builder().id(tipoPrestamoValido).tasaInteres(BigDecimal.ZERO).build();
        when(tipoPrestamoGateway.buscarPorId(anyString())).thenReturn(Mono.just(tipoPrestamo));
        cuota = useCase.calcularCuotaMensualConTipoPrestamo(solicitudAprobada);
        StepVerifier.create(cuota).expectNext(BigDecimal.ZERO).verifyComplete();
    }

    @Test
    void deberiaRetornarCeroSiDenominadorEsCero() {
        Solicitud solicitudAprobada = Solicitud.crearNueva(documentoValido, BigDecimal.valueOf(1000), 1, tipoPrestamoValido);
        solicitudAprobada.setEstado(EstadoSolicitud.APROBADO);
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder().id(tipoPrestamoValido).tasaInteres(BigDecimal.valueOf(-1)).build();
        when(tipoPrestamoGateway.buscarPorId(anyString())).thenReturn(Mono.just(tipoPrestamo));
        Mono<BigDecimal> cuota = useCase.calcularCuotaMensualConTipoPrestamo(solicitudAprobada);
        StepVerifier.create(cuota)
            .expectNextMatches(val -> val.compareTo(BigDecimal.ZERO) == 0)
            .verifyComplete();
    }

    @Test
    void deberiaNoEnviarNotificacionSiValidacionAutomaticaEsNull() {
        Solicitud solicitudEsperada = Solicitud.crearNueva(documentoValido, montoValido, plazoValido, tipoPrestamoValido);
        ValidacionDocumento validacionExitosa = ValidacionDocumento.builder().existe(true).mensaje("Documento válido").build();
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder().id(tipoPrestamoValido).validacionAutomatica(null).tasaInteres(BigDecimal.valueOf(0.02)).build();
        when(validacionDocumentoGateway.validarDocumento(anyString(), anyString())).thenReturn(Mono.just(validacionExitosa));
        when(solicitudRepository.existeTipoPrestamo(anyString())).thenReturn(Mono.just(true));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenReturn(Mono.just(solicitudEsperada));
        when(tipoPrestamoGateway.buscarPorId(anyString())).thenReturn(Mono.just(tipoPrestamo));
        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectNextMatches(solicitud -> solicitud.getDocumentoIdentidad().equals(documentoValido))
            .verifyComplete();
    }

    @Test
    void deberiaFallarSiObtenerDetalleUsuarioRetornaError() {
        Solicitud solicitudEsperada = Solicitud.crearNueva(documentoValido, montoValido, plazoValido, tipoPrestamoValido);
        ValidacionDocumento validacionExitosa = ValidacionDocumento.builder().existe(true).mensaje("Documento válido").build();
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder().id(tipoPrestamoValido).validacionAutomatica(true).tasaInteres(BigDecimal.valueOf(0.02)).build();
        when(validacionDocumentoGateway.validarDocumento(anyString(), anyString())).thenReturn(Mono.just(validacionExitosa));
        when(solicitudRepository.existeTipoPrestamo(anyString())).thenReturn(Mono.just(true));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenReturn(Mono.just(solicitudEsperada));
        when(tipoPrestamoGateway.buscarPorId(anyString())).thenReturn(Mono.just(tipoPrestamo));
        when(validacionDocumentoGateway.obtenerDetalleUsuario(anyString(), anyString())).thenReturn(Mono.error(new RuntimeException("Error detalle usuario")));
        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    void deberiaFallarSiNotificacionAutomaticaRetornaError() {
        Solicitud solicitudEsperada = Solicitud.crearNueva(documentoValido, montoValido, plazoValido, tipoPrestamoValido);
        ValidacionDocumento validacionExitosa = ValidacionDocumento.builder().existe(true).mensaje("Documento válido").build();
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder().id(tipoPrestamoValido).validacionAutomatica(true).tasaInteres(BigDecimal.valueOf(0.02)).build();
        co.com.crediya.model.DetalleUsuario detalleUsuario = co.com.crediya.model.DetalleUsuario.builder().email("test@mail.com").build();
        when(validacionDocumentoGateway.validarDocumento(anyString(), anyString())).thenReturn(Mono.just(validacionExitosa));
        when(solicitudRepository.existeTipoPrestamo(anyString())).thenReturn(Mono.just(true));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenReturn(Mono.just(solicitudEsperada));
        when(tipoPrestamoGateway.buscarPorId(anyString())).thenReturn(Mono.just(tipoPrestamo));
        when(validacionDocumentoGateway.obtenerDetalleUsuario(anyString(), anyString())).thenReturn(Mono.just(detalleUsuario));
        when(notificacionGateway.enviarValidacionAutomaticaSolicitud(any(), any(), any(), anyString())).thenReturn(Mono.error(new RuntimeException("Error notificacion")));
        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectError(RuntimeException.class)
            .verify();
    }
}
