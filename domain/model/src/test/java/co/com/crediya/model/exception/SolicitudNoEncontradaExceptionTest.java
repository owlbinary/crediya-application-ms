package co.com.crediya.model.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudNoEncontradaExceptionTest {
    @Test
    void testConstructorWithMessage() {
        String message = "Solicitud no encontrada";
        SolicitudNoEncontradaException exception = new SolicitudNoEncontradaException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testIsRuntimeException() {
        SolicitudNoEncontradaException exception = new SolicitudNoEncontradaException("msg");
        assertTrue(exception instanceof RuntimeException);
    }
}
