package co.com.crediya.model.exception;

/**
 * Excepción lanzada cuando hay problemas de autenticación en servicios externos.
 */
public class AutenticacionException extends RuntimeException {
    
    public AutenticacionException(String mensaje) {
        super(mensaje);
    }
    
    public AutenticacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
