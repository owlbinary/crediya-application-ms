package co.com.crediya.r2dbc.config;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConfiguracionTransaccional - Configuración Transaccional R2DBC")
class ConfiguracionTransaccionalTest {

    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private ReactiveTransactionManager transactionManager;

    private ConfiguracionTransaccional configuracionTransaccional;

    @BeforeEach
    void setUp() {
        configuracionTransaccional = new ConfiguracionTransaccional();
    }

    @Test
    @DisplayName("Debe crear ReactiveTransactionManager correctamente")
    void debeCrearReactiveTransactionManagerCorrectamente() {
        ReactiveTransactionManager manager = configuracionTransaccional
                .gestorTransaccionesReactivo(connectionFactory);

        assertThat(manager).isNotNull();
    }

    @Test
    @DisplayName("Debe crear TransactionalOperator correctamente")
    void debeCrearTransactionalOperatorCorrectamente() {
        TransactionalOperator operator = configuracionTransaccional
                .operadorTransaccional(transactionManager);

        assertThat(operator).isNotNull();
    }

    @Test
    @DisplayName("Debe crear instancias diferentes de ReactiveTransactionManager")
    void debeCrearInstanciasDiferentesDeReactiveTransactionManager() {
        ReactiveTransactionManager manager1 = configuracionTransaccional
                .gestorTransaccionesReactivo(connectionFactory);
        ReactiveTransactionManager manager2 = configuracionTransaccional
                .gestorTransaccionesReactivo(connectionFactory);

        assertThat(manager1)
                .isNotNull()
                .isNotSameAs(manager2);
        assertThat(manager2).isNotNull();
    }

    @Test
    @DisplayName("Debe crear instancias diferentes de TransactionalOperator")
    void debeCrearInstanciasDiferentesDeTransactionalOperator() {
        TransactionalOperator operator1 = configuracionTransaccional
                .operadorTransaccional(transactionManager);
        TransactionalOperator operator2 = configuracionTransaccional
                .operadorTransaccional(transactionManager);

        assertThat(operator1)
                .isNotNull()
                .isNotSameAs(operator2);
        assertThat(operator2).isNotNull();
    }
}
