package co.com.crediya.model.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstadoSolicitudNoValidoExceptionTest {
    @Test
    void testConstructorWithMessage() {
        String message = "Estado de solicitud no válido";
        EstadoSolicitudNoValidoException exception = new EstadoSolicitudNoValidoException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testIsRuntimeException() {
        EstadoSolicitudNoValidoException exception = new EstadoSolicitudNoValidoException("msg");
        assertTrue(exception instanceof RuntimeException);
    }
}
