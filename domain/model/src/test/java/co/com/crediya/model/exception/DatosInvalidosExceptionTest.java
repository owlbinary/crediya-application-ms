package co.com.crediya.model.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatosInvalidosExceptionTest {

    @Test
    void deberiaCrearExcepcionConMensajePrefijado() {
        String mensaje = "El documento de identidad es obligatorio";
        
        DatosInvalidosException excepcion = new DatosInvalidosException(mensaje);
        
        assertThat(excepcion.getMessage())
                .isEqualTo("Datos inválidos: El documento de identidad es obligatorio");
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
    }

    @Test
    void deberiaCrearExcepcionConMensajeNulo() {
        DatosInvalidosException excepcion = new DatosInvalidosException(null);
        
        assertThat(excepcion.getMessage())
                .isEqualTo("Datos inválidos: null");
    }

    @Test
    void deberiaCrearExcepcionConMensajeVacio() {
        DatosInvalidosException excepcion = new DatosInvalidosException("");
        
        assertThat(excepcion.getMessage())
                .isEqualTo("Datos inválidos: ");
    }

    @Test
    void deberiaPoderseLanzarLaExcepcion() {
        String mensaje = "El monto debe ser mayor a cero";
        
        assertThatThrownBy(() -> {
            throw new DatosInvalidosException(mensaje);
        })
        .isInstanceOf(DatosInvalidosException.class)
        .hasMessage("Datos inválidos: El monto debe ser mayor a cero");
    }

    @Test
    void deberiaSerSubclaseDeRuntimeException() {
        DatosInvalidosException excepcion = new DatosInvalidosException("test");
        
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
        assertThat(RuntimeException.class).isAssignableFrom(DatosInvalidosException.class);
    }

    @Test
    void deberiaCrearExcepcionConMensajesVariados() {
        DatosInvalidosException excepcionMonto = new DatosInvalidosException("El monto debe ser mayor a cero");
        DatosInvalidosException excepcionPlazo = new DatosInvalidosException("El plazo en meses debe ser mayor a cero");
        DatosInvalidosException excepcionTipo = new DatosInvalidosException("Tipo de préstamo es obligatorio");
        
        assertThat(excepcionMonto.getMessage())
                .isEqualTo("Datos inválidos: El monto debe ser mayor a cero");
        assertThat(excepcionPlazo.getMessage())
                .isEqualTo("Datos inválidos: El plazo en meses debe ser mayor a cero");
        assertThat(excepcionTipo.getMessage())
                .isEqualTo("Datos inválidos: Tipo de préstamo es obligatorio");
    }

    @Test
    void deberiaCrearExcepcionConMensajeConEspacios() {
        String mensajeConEspacios = "   Mensaje con espacios   ";
        
        DatosInvalidosException excepcion = new DatosInvalidosException(mensajeConEspacios);
        
        assertThat(excepcion.getMessage())
                .isEqualTo("Datos inválidos:    Mensaje con espacios   ");
    }
}
