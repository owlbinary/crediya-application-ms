package co.com.crediya.model.exception;

/**
 * Excepción para errores relacionados con la infraestructura
 */
public class InfraestructuraException extends RuntimeException {
    
    public InfraestructuraException(String mensaje) {
        super(mensaje);
    }
    
    public InfraestructuraException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
