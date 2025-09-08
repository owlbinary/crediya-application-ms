package co.com.crediya.model.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificacionEstadoExceptionTest {
    @Test
    void testConstructorWithMessage() {
        String message = "Error en notificación de estado";
        NotificacionEstadoException exception = new NotificacionEstadoException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testIsRuntimeException() {
        NotificacionEstadoException exception = new NotificacionEstadoException("msg");
        assertTrue(exception instanceof RuntimeException);
    }
}
