package co.com.crediya.usecase.registrarsolicitud;

import co.com.crediya.model.EstadoSolicitud;
import co.com.crediya.model.Solicitud;
import co.com.crediya.model.ValidacionDocumento;
import co.com.crediya.model.exception.DatosInvalidosException;
import co.com.crediya.model.exception.DocumentoNoValidoException;
import co.com.crediya.model.exception.TipoPrestamoNoExisteException;
import co.com.crediya.model.gateway.SolicitudGateway;
import co.com.crediya.model.gateway.ValidacionDocumentoGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarSolicitudUseCaseTest {

    @Mock
    private SolicitudGateway solicitudRepository;
    
    @Mock
    private ValidacionDocumentoGateway validacionDocumentoGateway;

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
    }

    @Test
    void deberiaCrearSolicitudExitosamente() {
        Solicitud solicitudEsperada = Solicitud.crearNueva(
            documentoValido, montoValido, plazoValido, tipoPrestamoValido
        );
        
        ValidacionDocumento validacionExitosa = ValidacionDocumento.builder()
            .documentoIdentidad(documentoValido)
            .existe(true)
            .mensaje("Documento válido")
            .build();
        
        when(validacionDocumentoGateway.validarDocumento(anyString(), anyString())).thenReturn(Mono.just(validacionExitosa));
        when(solicitudRepository.existeTipoPrestamo(anyString())).thenReturn(Mono.just(true));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenReturn(Mono.just(solicitudEsperada));

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
    void deberiaFallarCuandoMontoEsCero() {
        StepVerifier.create(useCase.ejecutar(documentoValido, BigDecimal.ZERO, plazoValido, tipoPrestamoValido, "Bearer test-token"))
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
    void deberiaFallarCuandoTipoPrestamoNoExiste() {
        ValidacionDocumento validacionExitosa = ValidacionDocumento.builder()
            .documentoIdentidad(documentoValido)
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
            .documentoIdentidad(documentoValido)
            .existe(false)
            .mensaje("El documento de identidad no existe en el sistema")
            .build();
            
        when(validacionDocumentoGateway.validarDocumento(anyString(), anyString())).thenReturn(Mono.just(validacionFallida));
        when(solicitudRepository.existeTipoPrestamo(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.ejecutar(documentoValido, montoValido, plazoValido, tipoPrestamoValido, "Bearer test-token"))
            .expectError(DocumentoNoValidoException.class)
            .verify();
    }
}
