package co.com.crediya.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DetalleUsuarioTest {

    @Test
    void deberiaCrearDetalleUsuarioConBuilder() {
        LocalDateTime fechaCreacion = LocalDateTime.now();
        
        DetalleUsuario detalleUsuario = DetalleUsuario.builder()
                .idUsuario(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .documentoIdentidad("12345678")
                .telefono("3001234567")
                .direccion("Calle 123 #45-67")
                .idRol(2L)
                .salarioBase(BigDecimal.valueOf(5000000))
                .fechaCreacion(fechaCreacion)
                .build();

        assertThat(detalleUsuario.getIdUsuario()).isEqualTo(1L);
        assertThat(detalleUsuario.getNombre()).isEqualTo("Juan");
        assertThat(detalleUsuario.getApellido()).isEqualTo("Pérez");
        assertThat(detalleUsuario.getEmail()).isEqualTo("juan@test.com");
        assertThat(detalleUsuario.getDocumentoIdentidad()).isEqualTo("12345678");
        assertThat(detalleUsuario.getTelefono()).isEqualTo("3001234567");
        assertThat(detalleUsuario.getDireccion()).isEqualTo("Calle 123 #45-67");
        assertThat(detalleUsuario.getIdRol()).isEqualTo(2L);
        assertThat(detalleUsuario.getSalarioBase()).isEqualTo(BigDecimal.valueOf(5000000));
        assertThat(detalleUsuario.getFechaCreacion()).isEqualTo(fechaCreacion);
    }

    @Test
    void deberiaCrearDetalleUsuarioConConstructorCompleto() {
        LocalDateTime fechaCreacion = LocalDateTime.now();
        
        DetalleUsuario detalleUsuario = new DetalleUsuario(
                1L, "María", "García", "maria@test.com", "87654321",
                "3007654321", "Carrera 456 #78-90", 3L,
                BigDecimal.valueOf(6000000), fechaCreacion
        );

        assertThat(detalleUsuario)
                .extracting("idUsuario", "nombre", "apellido", "email")
                .containsExactly(1L, "María", "García", "maria@test.com");
    }

    @Test
    void deberiaCrearDetalleUsuarioVacio() {
        DetalleUsuario detalleUsuario = new DetalleUsuario();

        assertThat(detalleUsuario.getIdUsuario()).isNull();
        assertThat(detalleUsuario.getNombre()).isNull();
        assertThat(detalleUsuario.getApellido()).isNull();
        assertThat(detalleUsuario.getEmail()).isNull();
        assertThat(detalleUsuario.getDocumentoIdentidad()).isNull();
        assertThat(detalleUsuario.getTelefono()).isNull();
        assertThat(detalleUsuario.getDireccion()).isNull();
        assertThat(detalleUsuario.getIdRol()).isNull();
        assertThat(detalleUsuario.getSalarioBase()).isNull();
        assertThat(detalleUsuario.getFechaCreacion()).isNull();
    }

    @Test
    void deberiaPermitirModificacionDeAtributos() {
        DetalleUsuario detalleUsuario = new DetalleUsuario();
        LocalDateTime fechaCreacion = LocalDateTime.now();

        detalleUsuario.setIdUsuario(5L);
        detalleUsuario.setNombre("Carlos");
        detalleUsuario.setApellido("López");
        detalleUsuario.setEmail("carlos@test.com");
        detalleUsuario.setDocumentoIdentidad("11223344");
        detalleUsuario.setTelefono("3001122334");
        detalleUsuario.setDireccion("Avenida 789 #12-34");
        detalleUsuario.setIdRol(1L);
        detalleUsuario.setSalarioBase(BigDecimal.valueOf(4500000));
        detalleUsuario.setFechaCreacion(fechaCreacion);

        assertThat(detalleUsuario.getIdUsuario()).isEqualTo(5L);
        assertThat(detalleUsuario.getNombre()).isEqualTo("Carlos");
        assertThat(detalleUsuario.getApellido()).isEqualTo("López");
        assertThat(detalleUsuario.getEmail()).isEqualTo("carlos@test.com");
        assertThat(detalleUsuario.getDocumentoIdentidad()).isEqualTo("11223344");
        assertThat(detalleUsuario.getTelefono()).isEqualTo("3001122334");
        assertThat(detalleUsuario.getDireccion()).isEqualTo("Avenida 789 #12-34");
        assertThat(detalleUsuario.getIdRol()).isEqualTo(1L);
        assertThat(detalleUsuario.getSalarioBase()).isEqualTo(BigDecimal.valueOf(4500000));
        assertThat(detalleUsuario.getFechaCreacion()).isEqualTo(fechaCreacion);
    }

    @Test
    void deberiaImplementarEqualsYHashCodeCorrectamente() {
        LocalDateTime fechaCreacion = LocalDateTime.now();

        DetalleUsuario detalleUsuario1 = DetalleUsuario.builder()
                .idUsuario(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .salarioBase(BigDecimal.valueOf(5000000))
                .fechaCreacion(fechaCreacion)
                .build();

        DetalleUsuario detalleUsuario2 = DetalleUsuario.builder()
                .idUsuario(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .salarioBase(BigDecimal.valueOf(5000000))
                .fechaCreacion(fechaCreacion)
                .build();

        DetalleUsuario detalleUsuario3 = DetalleUsuario.builder()
                .idUsuario(2L)
                .nombre("María")
                .apellido("García")
                .email("maria@test.com")
                .build();

        assertThat(detalleUsuario1)
                .isEqualTo(detalleUsuario2)
                .isNotEqualTo(detalleUsuario3)
                .hasSameHashCodeAs(detalleUsuario2);
        assertThat(detalleUsuario1.hashCode()).isNotEqualTo(detalleUsuario3.hashCode());
    }

    @Test
    void deberiaGenerarToStringCorrectamente() {
        DetalleUsuario detalleUsuario = DetalleUsuario.builder()
                .idUsuario(1L)
                .nombre("Juan")
                .email("juan@test.com")
                .salarioBase(BigDecimal.valueOf(5000000))
                .build();

        String toString = detalleUsuario.toString();

        assertThat(toString)
                .contains("DetalleUsuario")
                .contains("idUsuario=1")
                .contains("nombre=Juan")
                .contains("email=juan@test.com");
    }

    @Test
    void deberiaManejarValoresNulosEnBuilder() {
        DetalleUsuario detalleUsuario = DetalleUsuario.builder()
                .nombre(null)
                .email(null)
                .salarioBase(null)
                .build();

        assertThat(detalleUsuario.getNombre()).isNull();
        assertThat(detalleUsuario.getEmail()).isNull();
        assertThat(detalleUsuario.getSalarioBase()).isNull();
    }
}
