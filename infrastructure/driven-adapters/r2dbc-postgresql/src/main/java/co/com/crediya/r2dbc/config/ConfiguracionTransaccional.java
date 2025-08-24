package co.com.crediya.r2dbc.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
@EnableR2dbcRepositories(basePackages = "co.com.crediya.r2dbc.repository")
@EnableR2dbcAuditing
@EnableTransactionManagement
public class ConfiguracionTransaccional {

    @Bean
    public ReactiveTransactionManager gestorTransaccionesReactivo(ConnectionFactory fabricaConexion) {
        return new R2dbcTransactionManager(fabricaConexion);
    }

    @Bean
    public TransactionalOperator operadorTransaccional(ReactiveTransactionManager gestorTransacciones) {
        return TransactionalOperator.create(gestorTransacciones);
    }
}