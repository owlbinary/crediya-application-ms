package co.com.crediya.model.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutenticacionExceptionTest {

    @Test
    void deberiaCrearExcepcionConMensaje() {
        String mensaje = "Token de autenticación inválido";
        
        AutenticacionException excepcion = new AutenticacionException(mensaje);
        
        assertThat(excepcion.getMessage()).isEqualTo("Token de autenticación inválido");
        assertThat(excepcion.getCause()).isNull();
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
    }

    @Test
    void deberiaCrearExcepcionConMensajeYCausa() {
        String mensaje = "Error de autenticación con servicio externo";
        Throwable causa = new IllegalArgumentException("Token malformado");
        
        AutenticacionException excepcion = new AutenticacionException(mensaje, causa);
        
        assertThat(excepcion.getMessage()).isEqualTo("Error de autenticación con servicio externo");
        assertThat(excepcion.getCause()).isEqualTo(causa);
        assertThat(excepcion.getCause().getMessage()).isEqualTo("Token malformado");
    }

    @Test
    void deberiaCrearExcepcionConMensajeNulo() {
        AutenticacionException excepcion = new AutenticacionException(null);
        
        assertThat(excepcion.getMessage()).isNull();
        assertThat(excepcion.getCause()).isNull();
    }

    @Test
    void deberiaCrearExcepcionConMensajeVacio() {
        AutenticacionException excepcion = new AutenticacionException("");
        
        assertThat(excepcion.getMessage()).isEmpty();
        assertThat(excepcion.getCause()).isNull();
    }

    @Test
    void deberiaCrearExcepcionConMensajeNuloYCausa() {
        RuntimeException causa = new RuntimeException("Causa original");
        
        AutenticacionException excepcion = new AutenticacionException(null, causa);
        
        assertThat(excepcion.getMessage()).isNull();
        assertThat(excepcion.getCause()).isEqualTo(causa);
    }

    @Test
    void deberiaCrearExcepcionConMensajeYCausaNula() {
        String mensaje = "Mensaje de error";
        
        AutenticacionException excepcion = new AutenticacionException(mensaje, null);
        
        assertThat(excepcion.getMessage()).isEqualTo("Mensaje de error");
        assertThat(excepcion.getCause()).isNull();
    }

    @Test
    void deberiaPoderseLanzarLaExcepcion() {
        String mensaje = "Credenciales inválidas";
        
        assertThatThrownBy(() -> {
            throw new AutenticacionException(mensaje);
        })
        .isInstanceOf(AutenticacionException.class)
        .hasMessage("Credenciales inválidas")
        .hasNoCause();
    }

    @Test
    void deberiaPoderseLanzarLaExcepcionConCausa() {
        String mensaje = "Error en autenticación";
        Exception causa = new Exception("Token expirado");
        
        assertThatThrownBy(() -> {
            throw new AutenticacionException(mensaje, causa);
        })
        .isInstanceOf(AutenticacionException.class)
        .hasMessage("Error en autenticación")
        .hasCause(causa);
    }

    @Test
    void deberiaSerSubclaseDeRuntimeException() {
        AutenticacionException excepcion = new AutenticacionException("test");
        
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
        assertThat(RuntimeException.class).isAssignableFrom(AutenticacionException.class);
    }

    @Test
    void deberiaManejarCausasAnidadas() {
        Exception causaRaiz = new IllegalStateException("Estado inválido");
        RuntimeException causaIntermedia = new RuntimeException("Error intermedio", causaRaiz);
        
        AutenticacionException excepcion = new AutenticacionException("Error de autenticación", causaIntermedia);
        
        assertThat(excepcion.getCause()).isEqualTo(causaIntermedia);
        assertThat(excepcion.getCause().getCause()).isEqualTo(causaRaiz);
    }
}
