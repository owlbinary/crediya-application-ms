package co.com.crediya.r2dbc.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;

import co.com.crediya.model.TipoPrestamo;
import co.com.crediya.r2dbc.entity.TipoPrestamoEntity;
import co.com.crediya.r2dbc.mapper.TipoPrestamoEntityMapper;
import co.com.crediya.r2dbc.repository.TipoPrestamoRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
@DisplayName("TipoPrestamoRepositoryAdapter - Pruebas unitarias")
class TipoPrestamoRepositoryAdapterTest {

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    @Mock
    private TipoPrestamoEntityMapper entityMapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    private TipoPrestamoRepositoryAdapter adapter;
    private TipoPrestamoEntity tipoPrestamoEntity;
    private TipoPrestamo tipoPrestamoDomain;

    @BeforeEach
    void setUp() {
        adapter = new TipoPrestamoRepositoryAdapter(
            tipoPrestamoRepository, entityMapper, transactionalOperator);
        
        tipoPrestamoEntity = TipoPrestamoEntity.builder()
            .id(1)
            .nombre("Préstamo Personal")
            .montoMinimo(new BigDecimal("500000"))
            .montoMaximo(new BigDecimal("50000000"))
            .tasaInteres(new BigDecimal("15.5"))
            .validacionAutomatica(true)
            .activo(true)
            .build();

        tipoPrestamoDomain = TipoPrestamo.builder()
            .id("1")
            .nombre("Préstamo Personal")
            .descripcion("Préstamo Personal")
            .montoMinimo(new BigDecimal("500000"))
            .montoMaximo(new BigDecimal("50000000"))
            .tasaInteres(new BigDecimal("15.5"))
            .activo(true)
            .build();
    }

    @Test
    @DisplayName("Debe buscar tipo de préstamo por ID exitosamente")
    void debeBuscarTipoPrestamoPorIdExitosamente() {
        String tipoPrestamoId = "1";
        when(tipoPrestamoRepository.findById(1)).thenReturn(Mono.just(tipoPrestamoEntity));
        when(entityMapper.toDomain(tipoPrestamoEntity)).thenReturn(tipoPrestamoDomain);
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.buscarPorId(tipoPrestamoId))
            .expectNext(tipoPrestamoDomain)
            .verifyComplete();

        verify(tipoPrestamoRepository).findById(1);
        verify(entityMapper).toDomain(tipoPrestamoEntity);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    @DisplayName("Debe retornar Mono vacío cuando no se encuentra el tipo de préstamo")
    void debeRetornarMonoVacioCuandoNoSeEncuentraTipoPrestamo() {
        String tipoPrestamoId = "999";
        when(tipoPrestamoRepository.findById(999)).thenReturn(Mono.empty());
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.buscarPorId(tipoPrestamoId))
            .verifyComplete();

        verify(tipoPrestamoRepository).findById(999);
        verify(entityMapper, never()).toDomain(any());
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    @DisplayName("Debe convertir correctamente ID numérico como String")
    void debeConvertirCorrectamenteIdNumericoComoString() {
        String tipoPrestamoId = "123";
        when(tipoPrestamoRepository.findById(123)).thenReturn(Mono.just(tipoPrestamoEntity));
        when(entityMapper.toDomain(tipoPrestamoEntity)).thenReturn(tipoPrestamoDomain);
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.buscarPorId(tipoPrestamoId))
            .expectNext(tipoPrestamoDomain)
            .verifyComplete();

        verify(tipoPrestamoRepository).findById(123);
        verify(entityMapper).toDomain(tipoPrestamoEntity);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    @DisplayName("Debe manejar error en el repositorio correctamente")
    void debeManejarErrorEnRepositorioCorrectamente() {
        String tipoPrestamoId = "1";
        RuntimeException repositoryException = new RuntimeException("Error de base de datos");
        when(tipoPrestamoRepository.findById(1)).thenReturn(Mono.error(repositoryException));
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.buscarPorId(tipoPrestamoId))
            .expectError(RuntimeException.class)
            .verify();

        verify(tipoPrestamoRepository).findById(1);
        verify(entityMapper, never()).toDomain(any());
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    @DisplayName("Debe manejar error en el mapper correctamente")
    void debeManejarErrorEnMapperCorrectamente() {
        String tipoPrestamoId = "1";
        RuntimeException mapperException = new RuntimeException("Error en el mapper");
        when(tipoPrestamoRepository.findById(1)).thenReturn(Mono.just(tipoPrestamoEntity));
        when(entityMapper.toDomain(tipoPrestamoEntity)).thenThrow(mapperException);
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.buscarPorId(tipoPrestamoId))
            .expectError(RuntimeException.class)
            .verify();

        verify(tipoPrestamoRepository).findById(1);
        verify(entityMapper).toDomain(tipoPrestamoEntity);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    @DisplayName("Debe manejar números negativos como ID válido")
    void debeManejarNumerosNegativosComoIdValido() {
        String tipoPrestamoIdNegativo = "-1";
        when(tipoPrestamoRepository.findById(-1)).thenReturn(Mono.empty());
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.buscarPorId(tipoPrestamoIdNegativo))
            .verifyComplete();

        verify(tipoPrestamoRepository).findById(-1);
        verify(entityMapper, never()).toDomain(any());
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    @DisplayName("Debe manejar ID cero correctamente")
    void debeManejarIdCeroCorrectamente() {
        String tipoPrestamoIdCero = "0";
        when(tipoPrestamoRepository.findById(0)).thenReturn(Mono.empty());
        doAnswer(invocation -> invocation.getArgument(0))
            .when(transactionalOperator).transactional(any(Mono.class));

        StepVerifier.create(adapter.buscarPorId(tipoPrestamoIdCero))
            .verifyComplete();

        verify(tipoPrestamoRepository).findById(0);
        verify(entityMapper, never()).toDomain(any());
        verify(transactionalOperator).transactional(any(Mono.class));
    }
}
