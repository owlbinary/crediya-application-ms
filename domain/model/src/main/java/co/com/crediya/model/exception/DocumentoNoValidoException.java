package co.com.crediya.model.exception;

/**
 * Excepción lanzada cuando un documento de identidad no es válido o no existe.
 */
public class DocumentoNoValidoException extends RuntimeException {
    
    public DocumentoNoValidoException(String mensaje) {
        super(mensaje);
    }
    
    public DocumentoNoValidoException(String documentoIdentidad, String mensaje) {
        super(String.format("Documento %s no es válido: %s", documentoIdentidad, mensaje));
    }
}
