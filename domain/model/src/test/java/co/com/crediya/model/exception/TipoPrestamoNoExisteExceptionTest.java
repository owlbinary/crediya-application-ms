package co.com.crediya.model.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TipoPrestamoNoExisteExceptionTest {

    @Test
    void deberiaCrearExcepcionConMensajeCorrectoPorDefecto() {
        String tipoPrestamoId = "123";
        
        TipoPrestamoNoExisteException excepcion = new TipoPrestamoNoExisteException(tipoPrestamoId);
        
        assertThat(excepcion.getMessage())
                .isEqualTo("Tipo de préstamo con ID 123 no existe");
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
    }

    @Test
    void deberiaCrearExcepcionConIdNulo() {
        TipoPrestamoNoExisteException excepcion = new TipoPrestamoNoExisteException(null);
        
        assertThat(excepcion.getMessage())
                .isEqualTo("Tipo de préstamo con ID null no existe");
    }

    @Test
    void deberiaCrearExcepcionConIdVacio() {
        TipoPrestamoNoExisteException excepcion = new TipoPrestamoNoExisteException("");
        
        assertThat(excepcion.getMessage())
                .isEqualTo("Tipo de préstamo con ID  no existe");
    }

    @Test
    void deberiaPoderseLanzarLaExcepcion() {
        String tipoPrestamoId = "999";
        
        assertThatThrownBy(() -> {
            throw new TipoPrestamoNoExisteException(tipoPrestamoId);
        })
        .isInstanceOf(TipoPrestamoNoExisteException.class)
        .hasMessage("Tipo de préstamo con ID 999 no existe");
    }

    @Test
    void deberiaSerSubclaseDeRuntimeException() {
        TipoPrestamoNoExisteException excepcion = new TipoPrestamoNoExisteException("test");
        
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
        assertThat(RuntimeException.class).isAssignableFrom(TipoPrestamoNoExisteException.class);
    }

    @Test
    void deberiaCrearExcepcionConIdEspecial() {
        String tipoPrestamoIdEspecial = "TIPO-PRESTAMO-001";
        
        TipoPrestamoNoExisteException excepcion = new TipoPrestamoNoExisteException(tipoPrestamoIdEspecial);
        
        assertThat(excepcion.getMessage())
                .isEqualTo("Tipo de préstamo con ID TIPO-PRESTAMO-001 no existe");
    }
}
