package co.com.crediya.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidacionDocumentoTest {

    @Test
    void deberiaCrearValidacionDocumentoConBuilder() {
        DetalleUsuario detalleUsuario = DetalleUsuario.builder()
                .idUsuario(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .build();

        ValidacionDocumento validacion = ValidacionDocumento.builder()
                .existe(true)
                .mensaje("Documento válido")
                .detalleUsuario(detalleUsuario)
                .build();

        assertThat(validacion.getExiste()).isTrue();
        assertThat(validacion.getMensaje()).isEqualTo("Documento válido");
        assertThat(validacion.getDetalleUsuario()).isEqualTo(detalleUsuario);
    }

    @Test
    void deberiaCrearValidacionDocumentoConConstructorCompleto() {
        DetalleUsuario detalleUsuario = new DetalleUsuario();
        
        ValidacionDocumento validacion = new ValidacionDocumento(false, "Documento no válido", detalleUsuario);

        assertThat(validacion.getExiste()).isFalse();
        assertThat(validacion.getMensaje()).isEqualTo("Documento no válido");
        assertThat(validacion.getDetalleUsuario()).isEqualTo(detalleUsuario);
    }

    @Test
    void deberiaCrearValidacionDocumentoVacia() {
        ValidacionDocumento validacion = new ValidacionDocumento();

        assertThat(validacion.getExiste()).isNull();
        assertThat(validacion.getMensaje()).isNull();
        assertThat(validacion.getDetalleUsuario()).isNull();
    }

    @Test
    void deberiaPermitirModificacionDeAtributos() {
        ValidacionDocumento validacion = new ValidacionDocumento();
        DetalleUsuario detalleUsuario = new DetalleUsuario();

        validacion.setExiste(true);
        validacion.setMensaje("Documento encontrado");
        validacion.setDetalleUsuario(detalleUsuario);

        assertThat(validacion.getExiste()).isTrue();
        assertThat(validacion.getMensaje()).isEqualTo("Documento encontrado");
        assertThat(validacion.getDetalleUsuario()).isEqualTo(detalleUsuario);
    }

    @Test
    void deberiaImplementarEqualsYHashCodeCorrectamente() {
        DetalleUsuario detalleUsuario = DetalleUsuario.builder()
                .nombre("Juan")
                .email("juan@test.com")
                .build();

        ValidacionDocumento validacion1 = ValidacionDocumento.builder()
                .existe(true)
                .mensaje("Válido")
                .detalleUsuario(detalleUsuario)
                .build();

        ValidacionDocumento validacion2 = ValidacionDocumento.builder()
                .existe(true)
                .mensaje("Válido")
                .detalleUsuario(detalleUsuario)
                .build();

        ValidacionDocumento validacion3 = ValidacionDocumento.builder()
                .existe(false)
                .mensaje("Inválido")
                .detalleUsuario(detalleUsuario)
                .build();

        assertThat(validacion1)
                .isEqualTo(validacion2)
                .isNotEqualTo(validacion3)
                .hasSameHashCodeAs(validacion2);
        assertThat(validacion1.hashCode()).isNotEqualTo(validacion3.hashCode());
    }

    @Test
    void deberiaGenerarToStringCorrectamente() {
        ValidacionDocumento validacion = ValidacionDocumento.builder()
                .existe(true)
                .mensaje("Documento válido")
                .build();

        String toString = validacion.toString();

        assertThat(toString)
                .contains("ValidacionDocumento")
                .contains("existe=true")
                .contains("mensaje=Documento válido");
    }
}
