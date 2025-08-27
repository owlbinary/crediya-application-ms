package co.com.crediya.model.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InfraestructuraExceptionTest {

    @Test
    void deberiaCrearExcepcionConMensaje() {
        String mensaje = "Error de conexión a base de datos";
        
        InfraestructuraException excepcion = new InfraestructuraException(mensaje);
        
        assertThat(excepcion.getMessage()).isEqualTo("Error de conexión a base de datos");
        assertThat(excepcion.getCause()).isNull();
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
    }

    @Test
    void deberiaCrearExcepcionConMensajeYCausa() {
        String mensaje = "Error en servicio externo";
        Throwable causa = new RuntimeException("Timeout de conexión");
        
        InfraestructuraException excepcion = new InfraestructuraException(mensaje, causa);
        
        assertThat(excepcion.getMessage()).isEqualTo("Error en servicio externo");
        assertThat(excepcion.getCause()).isEqualTo(causa);
        assertThat(excepcion.getCause().getMessage()).isEqualTo("Timeout de conexión");
    }

    @Test
    void deberiaCrearExcepcionConMensajeNulo() {
        InfraestructuraException excepcion = new InfraestructuraException(null);
        
        assertThat(excepcion.getMessage()).isNull();
        assertThat(excepcion.getCause()).isNull();
    }

    @Test
    void deberiaCrearExcepcionConMensajeVacio() {
        InfraestructuraException excepcion = new InfraestructuraException("");
        
        assertThat(excepcion.getMessage()).isEmpty();
        assertThat(excepcion.getCause()).isNull();
    }

    @Test
    void deberiaCrearExcepcionConMensajeNuloYCausa() {
        Exception causa = new Exception("Error en la red");
        
        InfraestructuraException excepcion = new InfraestructuraException(null, causa);
        
        assertThat(excepcion.getMessage()).isNull();
        assertThat(excepcion.getCause()).isEqualTo(causa);
    }

    @Test
    void deberiaCrearExcepcionConMensajeYCausaNula() {
        String mensaje = "Servicio no disponible";
        
        InfraestructuraException excepcion = new InfraestructuraException(mensaje, null);
        
        assertThat(excepcion.getMessage()).isEqualTo("Servicio no disponible");
        assertThat(excepcion.getCause()).isNull();
    }

    @Test
    void deberiaPoderseLanzarLaExcepcion() {
        String mensaje = "Fallo en infraestructura";
        
        assertThatThrownBy(() -> {
            throw new InfraestructuraException(mensaje);
        })
        .isInstanceOf(InfraestructuraException.class)
        .hasMessage("Fallo en infraestructura")
        .hasNoCause();
    }

    @Test
    void deberiaPoderseLanzarLaExcepcionConCausa() {
        String mensaje = "Error en persistencia";
        Exception causa = new Exception("Disco lleno");
        
        assertThatThrownBy(() -> {
            throw new InfraestructuraException(mensaje, causa);
        })
        .isInstanceOf(InfraestructuraException.class)
        .hasMessage("Error en persistencia")
        .hasCause(causa);
    }

    @Test
    void deberiaSerSubclaseDeRuntimeException() {
        InfraestructuraException excepcion = new InfraestructuraException("test");
        
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
        assertThat(RuntimeException.class).isAssignableFrom(InfraestructuraException.class);
    }

    @Test
    void deberiaManejarCausasAnidadas() {
        Exception causaRaiz = new IllegalStateException("Estado inconsistente");
        RuntimeException causaIntermedia = new RuntimeException("Error de configuración", causaRaiz);
        
        InfraestructuraException excepcion = new InfraestructuraException("Error de infraestructura", causaIntermedia);
        
        assertThat(excepcion.getCause()).isEqualTo(causaIntermedia);
        assertThat(excepcion.getCause().getCause()).isEqualTo(causaRaiz);
    }

    @Test
    void deberiaDistinguirseDeOtrasExcepciones() {
        InfraestructuraException infraException = new InfraestructuraException("Error infraestructura");
        RuntimeException runtimeException = new RuntimeException("Error runtime");
        
        assertThat(infraException).isNotEqualTo(runtimeException);
        assertThat(infraException.getClass()).isNotEqualTo(runtimeException.getClass());
        assertThat(infraException).isInstanceOf(InfraestructuraException.class);
        assertThat(runtimeException).isNotInstanceOf(InfraestructuraException.class);
    }
}
