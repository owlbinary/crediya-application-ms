package co.com.crediya.r2dbc.mapper;

import co.com.crediya.model.TipoPrestamo;
import co.com.crediya.r2dbc.entity.TipoPrestamoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TipoPrestamoEntityMapper - Mapper entre TipoPrestamoEntity y TipoPrestamo")
class TipoPrestamoEntityMapperTest {

    private TipoPrestamoEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TipoPrestamoEntityMapperImpl();
    }

    @Test
    @DisplayName("Debe convertir TipoPrestamoEntity a TipoPrestamo del dominio")
    void debeConvertirEntityATipoPrestamoDomain() {
        TipoPrestamoEntity entity = TipoPrestamoEntity.builder()
                .id(1)
                .nombre("Préstamo Personal")
                .montoMinimo(new BigDecimal("500000"))
                .montoMaximo(new BigDecimal("50000000"))
                .tasaInteres(new BigDecimal("15.5"))
                .validacionAutomatica(true)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        TipoPrestamo tipoPrestamo = mapper.toDomain(entity);

        assertThat(tipoPrestamo).isNotNull();
        assertThat(tipoPrestamo.getId()).isEqualTo("1");
        assertThat(tipoPrestamo.getDescripcion()).isEqualTo("Préstamo Personal");
        assertThat(tipoPrestamo.getMontoMinimo()).isEqualTo(new BigDecimal("500000"));
        assertThat(tipoPrestamo.getMontoMaximo()).isEqualTo(new BigDecimal("50000000"));
        assertThat(tipoPrestamo.getTasaInteres()).isEqualTo(new BigDecimal("15.5"));
        assertThat(tipoPrestamo.getActivo()).isTrue();
        assertThat(tipoPrestamo.getPlazoMinimoMeses()).isNull();
        assertThat(tipoPrestamo.getPlazoMaximoMeses()).isNull();
    }

    @Test
    @DisplayName("Debe convertir TipoPrestamo del dominio a TipoPrestamoEntity")
    void debeConvertirTipoPrestamoDomainAEntity() {
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder()
                .id("2")
                .nombre("Préstamo Hipotecario")
                .descripcion("Préstamo Hipotecario para vivienda")
                .montoMinimo(new BigDecimal("10000000"))
                .montoMaximo(new BigDecimal("500000000"))
                .plazoMinimoMeses(60)
                .plazoMaximoMeses(360)
                .tasaInteres(new BigDecimal("12.0"))
                .activo(true)
                .build();

        TipoPrestamoEntity entity = mapper.toEntity(tipoPrestamo);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(2);
        assertThat(entity.getNombre()).isEqualTo("Préstamo Hipotecario para vivienda");
        assertThat(entity.getMontoMinimo()).isEqualTo(new BigDecimal("10000000"));
        assertThat(entity.getMontoMaximo()).isEqualTo(new BigDecimal("500000000"));
        assertThat(entity.getTasaInteres()).isEqualTo(new BigDecimal("12.0"));
        assertThat(entity.getActivo()).isTrue();
        assertThat(entity.getFechaCreacion()).isNull();
        assertThat(entity.getFechaActualizacion()).isNull();
        assertThat(entity.getValidacionAutomatica()).isNull();
    }

    @Test
    @DisplayName("Debe manejar valores null en id de entity al convertir a domain")
    void debeManejarIdNullEnEntityADomain() {
        TipoPrestamoEntity entity = TipoPrestamoEntity.builder()
                .id(null)
                .nombre("Préstamo Test")
                .montoMinimo(new BigDecimal("1000000"))
                .montoMaximo(new BigDecimal("10000000"))
                .tasaInteres(new BigDecimal("18.0"))
                .activo(false)
                .build();

        TipoPrestamo tipoPrestamo = mapper.toDomain(entity);

        assertThat(tipoPrestamo).isNotNull();
        assertThat(tipoPrestamo.getId()).isNull();
        assertThat(tipoPrestamo.getDescripcion()).isEqualTo("Préstamo Test");
        assertThat(tipoPrestamo.getMontoMinimo()).isEqualTo(new BigDecimal("1000000"));
        assertThat(tipoPrestamo.getMontoMaximo()).isEqualTo(new BigDecimal("10000000"));
        assertThat(tipoPrestamo.getTasaInteres()).isEqualTo(new BigDecimal("18.0"));
        assertThat(tipoPrestamo.getActivo()).isFalse();
    }

    @Test
    @DisplayName("Debe manejar valores null en id de domain al convertir a entity")
    void debeManejarIdNullEnDomainAEntity() {
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder()
                .id(null)
                .nombre("Préstamo Test")
                .descripcion("Préstamo de prueba")
                .montoMinimo(new BigDecimal("2000000"))
                .montoMaximo(new BigDecimal("20000000"))
                .tasaInteres(new BigDecimal("16.5"))
                .activo(true)
                .build();

        TipoPrestamoEntity entity = mapper.toEntity(tipoPrestamo);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getNombre()).isEqualTo("Préstamo de prueba");
        assertThat(entity.getMontoMinimo()).isEqualTo(new BigDecimal("2000000"));
        assertThat(entity.getMontoMaximo()).isEqualTo(new BigDecimal("20000000"));
        assertThat(entity.getTasaInteres()).isEqualTo(new BigDecimal("16.5"));
        assertThat(entity.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe manejar entity null al convertir a domain")
    void debeManejarEntityNullADomain() {
        TipoPrestamo tipoPrestamo = mapper.toDomain(null);

        assertThat(tipoPrestamo).isNull();
    }

    @Test
    @DisplayName("Debe manejar domain null al convertir a entity")
    void debeManejarDomainNullAEntity() {
        TipoPrestamoEntity entity = mapper.toEntity(null);

        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Debe mapear correctamente todos los campos monetarios")
    void debeMappearCorrectamenteCamposMonetarios() {
        TipoPrestamoEntity entity = TipoPrestamoEntity.builder()
                .id(3)
                .nombre("Crédito de Libre Inversión")
                .montoMinimo(new BigDecimal("100000.50"))
                .montoMaximo(new BigDecimal("5000000.75"))
                .tasaInteres(new BigDecimal("19.99"))
                .activo(true)
                .build();

        TipoPrestamo tipoPrestamo = mapper.toDomain(entity);

        assertThat(tipoPrestamo.getMontoMinimo()).isEqualTo(new BigDecimal("100000.50"));
        assertThat(tipoPrestamo.getMontoMaximo()).isEqualTo(new BigDecimal("5000000.75"));
        assertThat(tipoPrestamo.getTasaInteres()).isEqualTo(new BigDecimal("19.99"));
    }

    @Test
    @DisplayName("Debe mapear correctamente valores booleanos")
    void debeMappearCorrectamenteValoresBooleanos() {
        TipoPrestamoEntity entityInactivo = TipoPrestamoEntity.builder()
                .id(4)
                .nombre("Préstamo Inactivo")
                .activo(false)
                .build();

        TipoPrestamo domainActivo = TipoPrestamo.builder()
                .id("5")
                .descripcion("Préstamo Activo")
                .activo(true)
                .build();

        TipoPrestamo tipoPrestamoInactivo = mapper.toDomain(entityInactivo);
        TipoPrestamoEntity entityActivo = mapper.toEntity(domainActivo);

        assertThat(tipoPrestamoInactivo.getActivo()).isFalse();
        assertThat(entityActivo.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe convertir correctamente IDs entre String y Integer")
    void debeConvertirCorrectamenteIDs() {
        TipoPrestamoEntity entity = TipoPrestamoEntity.builder()
                .id(999)
                .nombre("Test ID")
                .build();

        TipoPrestamo tipoPrestamo = TipoPrestamo.builder()
                .id("888")
                .descripcion("Test ID Domain")
                .build();

        TipoPrestamo domainResult = mapper.toDomain(entity);
        TipoPrestamoEntity entityResult = mapper.toEntity(tipoPrestamo);

        assertThat(domainResult.getId()).isEqualTo("999");
        assertThat(entityResult.getId()).isEqualTo(888);
    }

    @Test
    @DisplayName("Debe manejar valores decimales con precisión")
    void debeManejarValoresDecimalesConPrecision() {
        TipoPrestamoEntity entity = TipoPrestamoEntity.builder()
                .id(6)
                .nombre("Préstamo Precisión")
                .montoMinimo(new BigDecimal("1000000.123456"))
                .montoMaximo(new BigDecimal("50000000.987654"))
                .tasaInteres(new BigDecimal("15.123456789"))
                .activo(true)
                .build();

        TipoPrestamo tipoPrestamo = mapper.toDomain(entity);
        TipoPrestamoEntity entityMapped = mapper.toEntity(tipoPrestamo);
        assertThat(tipoPrestamo.getMontoMinimo()).isEqualTo(new BigDecimal("1000000.123456"));
        assertThat(tipoPrestamo.getMontoMaximo()).isEqualTo(new BigDecimal("50000000.987654"));
        assertThat(tipoPrestamo.getTasaInteres()).isEqualTo(new BigDecimal("15.123456789"));
        
        assertThat(entityMapped.getMontoMinimo()).isEqualTo(new BigDecimal("1000000.123456"));
        assertThat(entityMapped.getMontoMaximo()).isEqualTo(new BigDecimal("50000000.987654"));
        assertThat(entityMapped.getTasaInteres()).isEqualTo(new BigDecimal("15.123456789"));
    }
}
