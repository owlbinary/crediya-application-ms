package co.com.crediya.r2dbc.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;

import co.com.crediya.model.EstadoSolicitud;
import co.com.crediya.model.Solicitud;
import co.com.crediya.r2dbc.entity.SolicitudEntity;
import co.com.crediya.r2dbc.mapper.SolicitudEntityMapper;
import co.com.crediya.r2dbc.repository.SolicitudRepository;
import co.com.crediya.r2dbc.repository.TipoPrestamoRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class SolicitudRepositoryAdapterTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    @Mock
    private SolicitudEntityMapper entityMapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    private SolicitudRepositoryAdapter adapter;
    private Solicitud solicitudDominio;
    private SolicitudEntity solicitudEntity;

    @BeforeEach
    void setUp() {
        adapter = new SolicitudRepositoryAdapter(
            solicitudRepository, tipoPrestamoRepository, entityMapper, transactionalOperator);
        
        LocalDateTime now = LocalDateTime.now();
        
        solicitudDominio = Solicitud.builder()
            .id("1")
            .documentoIdentidad("12345678")
            .monto(new BigDecimal("1000000"))
            .plazo(12)
            .tipoPrestamoId("1")
            .estado(EstadoSolicitud.PENDIENTE_REVISION)
            .fechaCreacion(now)
            .fechaActualizacion(now)
            .build();

        solicitudEntity = SolicitudEntity.builder()
            .id(1)
            .documentoIdentidad("12345678")
            .monto(new BigDecimal("1000000"))
            .plazo(12)
            .idTipoPrestamo(1)
            .idEstado(1)
            .fechaSolicitud(now)
            .fechaActualizacion(now)
            .email("temp@example.com")
            .build();
    }

    @Test
    void deberiaVerificarExistenciaTipoPrestamoExitosamente() {
        when(tipoPrestamoRepository.existsByIdCustom(1)).thenReturn(Mono.just(true));
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.existeTipoPrestamo("1"))
            .expectNext(true)
            .verifyComplete();

        verify(tipoPrestamoRepository).existsByIdCustom(1);
    }

    @Test
    void deberiaRetornarFalsoCuandoTipoPrestamoNoExiste() {
        when(tipoPrestamoRepository.existsByIdCustom(999)).thenReturn(Mono.just(false));
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.existeTipoPrestamo("999"))
            .expectNext(false)
            .verifyComplete();

        verify(tipoPrestamoRepository).existsByIdCustom(999);
    }

    @Test
    void deberiaRetornarFalsoCuandoIdTipoPrestamoNoEsNumerico() {
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.existeTipoPrestamo("abc"))
            .expectNextMatches(existe -> !existe)
            .verifyComplete();

        verify(tipoPrestamoRepository, never()).existsByIdCustom(anyInt());
    }

    @Test
    void deberiaManejarErrorEnConsultaTipoPrestamo() {
        when(tipoPrestamoRepository.existsByIdCustom(1))
            .thenReturn(Mono.error(new RuntimeException("Error de BD")));
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.existeTipoPrestamo("1"))
            .expectNext(false)
            .verifyComplete();

        verify(tipoPrestamoRepository).existsByIdCustom(1);
    }

    @Test
    void deberiaBuscarPorIdConIdValido() {
        when(solicitudRepository.findById(1)).thenReturn(Mono.just(solicitudEntity));
        when(entityMapper.toDomain(solicitudEntity)).thenReturn(solicitudDominio);
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.buscarPorId("1"))
            .expectNextMatches(resultado -> 
                resultado.getId().equals("1") &&
                resultado.getDocumentoIdentidad().equals("12345678")
            )
            .verifyComplete();

        verify(solicitudRepository).findById(1);
        verify(entityMapper).toDomain(solicitudEntity);
    }

    @Test
    void deberiaRetornarVacioCuandoIdNoEsNumerico() {
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.buscarPorId("abc"))
            .verifyComplete();

        verify(solicitudRepository, never()).findById(anyInt());
    }

    @Test
    void deberiaRetornarVacioCuandoNoEncuentraSolicitud() {
        when(solicitudRepository.findById(999)).thenReturn(Mono.empty());
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.buscarPorId("999"))
            .verifyComplete();

        verify(solicitudRepository).findById(999);
    }

    @Test
    void deberiaGuardarSolicitudExitosamente() {
        when(entityMapper.toEntity(solicitudDominio)).thenReturn(solicitudEntity);
        when(solicitudRepository.save(solicitudEntity)).thenReturn(Mono.just(solicitudEntity));
        when(entityMapper.toDomain(solicitudEntity)).thenReturn(solicitudDominio);
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.guardar(solicitudDominio))
            .expectNextMatches(resultado -> 
                resultado.getId().equals("1") &&
                resultado.getDocumentoIdentidad().equals("12345678") &&
                resultado.getMonto().equals(new BigDecimal("1000000"))
            )
            .verifyComplete();

        verify(entityMapper).toEntity(solicitudDominio);
        verify(solicitudRepository).save(solicitudEntity);
        verify(entityMapper).toDomain(solicitudEntity);
    }

    @Test
    void deberiaFallarAlGuardarCuandoHayError() {
        when(entityMapper.toEntity(solicitudDominio)).thenReturn(solicitudEntity);
        when(solicitudRepository.save(solicitudEntity))
            .thenReturn(Mono.error(new RuntimeException("Error de BD")));
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.guardar(solicitudDominio))
            .expectError(RuntimeException.class)
            .verify();
    }
}
