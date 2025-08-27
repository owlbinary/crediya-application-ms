package co.com.crediya.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TipoPrestamoTest {

    @Test
    void deberiaCrearTipoPrestamoConBuilder() {
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder()
                .id("1")
                .nombre("Préstamo Personal")
                .descripcion("Préstamo personal para libre inversión")
                .montoMinimo(BigDecimal.valueOf(500000))
                .montoMaximo(BigDecimal.valueOf(50000000))
                .plazoMinimoMeses(6)
                .plazoMaximoMeses(60)
                .tasaInteres(BigDecimal.valueOf(1.5))
                .activo(true)
                .build();

        assertThat(tipoPrestamo.getId()).isEqualTo("1");
        assertThat(tipoPrestamo.getNombre()).isEqualTo("Préstamo Personal");
        assertThat(tipoPrestamo.getDescripcion()).isEqualTo("Préstamo personal para libre inversión");
        assertThat(tipoPrestamo.getMontoMinimo()).isEqualTo(BigDecimal.valueOf(500000));
        assertThat(tipoPrestamo.getMontoMaximo()).isEqualTo(BigDecimal.valueOf(50000000));
        assertThat(tipoPrestamo.getPlazoMinimoMeses()).isEqualTo(6);
        assertThat(tipoPrestamo.getPlazoMaximoMeses()).isEqualTo(60);
        assertThat(tipoPrestamo.getTasaInteres()).isEqualTo(BigDecimal.valueOf(1.5));
        assertThat(tipoPrestamo.getActivo()).isTrue();
    }

    @Test
    void deberiaCrearTipoPrestamoConConstructorCompleto() {
        TipoPrestamo tipoPrestamo = new TipoPrestamo(
                "2", "Préstamo Hipotecario", "Préstamo para compra de vivienda",
                BigDecimal.valueOf(10000000), BigDecimal.valueOf(500000000),
                120, 360, BigDecimal.valueOf(0.8), true
        );

        assertThat(tipoPrestamo)
                .extracting("id", "nombre", "descripcion", "activo")
                .containsExactly("2", "Préstamo Hipotecario", "Préstamo para compra de vivienda", true);
    }

    @Test
    void deberiaCrearTipoPrestamoVacio() {
        TipoPrestamo tipoPrestamo = new TipoPrestamo();

        assertThat(tipoPrestamo.getId()).isNull();
        assertThat(tipoPrestamo.getNombre()).isNull();
        assertThat(tipoPrestamo.getDescripcion()).isNull();
        assertThat(tipoPrestamo.getMontoMinimo()).isNull();
        assertThat(tipoPrestamo.getMontoMaximo()).isNull();
        assertThat(tipoPrestamo.getPlazoMinimoMeses()).isNull();
        assertThat(tipoPrestamo.getPlazoMaximoMeses()).isNull();
        assertThat(tipoPrestamo.getTasaInteres()).isNull();
        assertThat(tipoPrestamo.getActivo()).isNull();
    }

    @Test
    void deberiaPermitirModificacionDeAtributos() {
        TipoPrestamo tipoPrestamo = new TipoPrestamo();

        tipoPrestamo.setId("3");
        tipoPrestamo.setNombre("Préstamo Vehicular");
        tipoPrestamo.setDescripcion("Préstamo para compra de vehículos");
        tipoPrestamo.setMontoMinimo(BigDecimal.valueOf(5000000));
        tipoPrestamo.setMontoMaximo(BigDecimal.valueOf(100000000));
        tipoPrestamo.setPlazoMinimoMeses(12);
        tipoPrestamo.setPlazoMaximoMeses(72);
        tipoPrestamo.setTasaInteres(BigDecimal.valueOf(1.2));
        tipoPrestamo.setActivo(false);

        assertThat(tipoPrestamo.getId()).isEqualTo("3");
        assertThat(tipoPrestamo.getNombre()).isEqualTo("Préstamo Vehicular");
        assertThat(tipoPrestamo.getDescripcion()).isEqualTo("Préstamo para compra de vehículos");
        assertThat(tipoPrestamo.getMontoMinimo()).isEqualTo(BigDecimal.valueOf(5000000));
        assertThat(tipoPrestamo.getMontoMaximo()).isEqualTo(BigDecimal.valueOf(100000000));
        assertThat(tipoPrestamo.getPlazoMinimoMeses()).isEqualTo(12);
        assertThat(tipoPrestamo.getPlazoMaximoMeses()).isEqualTo(72);
        assertThat(tipoPrestamo.getTasaInteres()).isEqualTo(BigDecimal.valueOf(1.2));
        assertThat(tipoPrestamo.getActivo()).isFalse();
    }

    @Test
    void deberiaUtilizarToBuilderCorrectamente() {
        TipoPrestamo tipoPrestamoOriginal = TipoPrestamo.builder()
                .id("1")
                .nombre("Préstamo Personal")
                .descripcion("Descripción original")
                .montoMinimo(BigDecimal.valueOf(500000))
                .tasaInteres(BigDecimal.valueOf(1.5))
                .activo(true)
                .build();

        TipoPrestamo tipoPrestamoModificado = tipoPrestamoOriginal.toBuilder()
                .descripcion("Descripción modificada")
                .tasaInteres(BigDecimal.valueOf(1.8))
                .activo(false)
                .build();

        assertThat(tipoPrestamoModificado.getId()).isEqualTo("1");
        assertThat(tipoPrestamoModificado.getNombre()).isEqualTo("Préstamo Personal");
        assertThat(tipoPrestamoModificado.getDescripcion()).isEqualTo("Descripción modificada");
        assertThat(tipoPrestamoModificado.getMontoMinimo()).isEqualTo(BigDecimal.valueOf(500000));
        assertThat(tipoPrestamoModificado.getTasaInteres()).isEqualTo(BigDecimal.valueOf(1.8));
        assertThat(tipoPrestamoModificado.getActivo()).isFalse();
        
        assertThat(tipoPrestamoOriginal.getDescripcion()).isEqualTo("Descripción original");
        assertThat(tipoPrestamoOriginal.getTasaInteres()).isEqualTo(BigDecimal.valueOf(1.5));
        assertThat(tipoPrestamoOriginal.getActivo()).isTrue();
    }

    @Test
    void deberiaImplementarEqualsYHashCodeCorrectamente() {
        TipoPrestamo tipoPrestamo1 = TipoPrestamo.builder()
                .id("1")
                .nombre("Préstamo Personal")
                .descripcion("Descripción")
                .montoMinimo(BigDecimal.valueOf(500000))
                .tasaInteres(BigDecimal.valueOf(1.5))
                .activo(true)
                .build();

        TipoPrestamo tipoPrestamo2 = TipoPrestamo.builder()
                .id("1")
                .nombre("Préstamo Personal")
                .descripcion("Descripción")
                .montoMinimo(BigDecimal.valueOf(500000))
                .tasaInteres(BigDecimal.valueOf(1.5))
                .activo(true)
                .build();

        TipoPrestamo tipoPrestamo3 = TipoPrestamo.builder()
                .id("2")
                .nombre("Préstamo Hipotecario")
                .descripcion("Otra descripción")
                .build();

        assertThat(tipoPrestamo1)
                .isEqualTo(tipoPrestamo2)
                .isNotEqualTo(tipoPrestamo3)
                .hasSameHashCodeAs(tipoPrestamo2);
        assertThat(tipoPrestamo1.hashCode()).isNotEqualTo(tipoPrestamo3.hashCode());
    }

    @Test
    void deberiaGenerarToStringCorrectamente() {
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder()
                .id("1")
                .nombre("Préstamo Personal")
                .descripcion("Descripción del préstamo")
                .tasaInteres(BigDecimal.valueOf(1.5))
                .activo(true)
                .build();

        String toString = tipoPrestamo.toString();

        assertThat(toString)
                .contains("TipoPrestamo")
                .contains("id=1")
                .contains("nombre=Préstamo Personal")
                .contains("descripcion=Descripción del préstamo")
                .contains("tasaInteres=1.5")
                .contains("activo=true");
    }

    @Test
    void deberiaManejarValoresNulosEnBuilder() {
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder()
                .id(null)
                .nombre(null)
                .descripcion(null)
                .montoMinimo(null)
                .montoMaximo(null)
                .plazoMinimoMeses(null)
                .plazoMaximoMeses(null)
                .tasaInteres(null)
                .activo(null)
                .build();

        assertThat(tipoPrestamo.getId()).isNull();
        assertThat(tipoPrestamo.getNombre()).isNull();
        assertThat(tipoPrestamo.getDescripcion()).isNull();
        assertThat(tipoPrestamo.getMontoMinimo()).isNull();
        assertThat(tipoPrestamo.getMontoMaximo()).isNull();
        assertThat(tipoPrestamo.getPlazoMinimoMeses()).isNull();
        assertThat(tipoPrestamo.getPlazoMaximoMeses()).isNull();
        assertThat(tipoPrestamo.getTasaInteres()).isNull();
        assertThat(tipoPrestamo.getActivo()).isNull();
    }
}
