package co.com.crediya.model.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenInvalidoExceptionTest {
    @Test
    void testConstructorWithMessage() {
        String message = "Token inválido";
        TokenInvalidoException exception = new TokenInvalidoException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testIsRuntimeException() {
        TokenInvalidoException exception = new TokenInvalidoException("msg");
        assertTrue(exception instanceof RuntimeException);
    }
}
