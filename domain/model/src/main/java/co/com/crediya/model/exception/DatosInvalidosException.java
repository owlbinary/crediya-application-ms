package co.com.crediya.model.exception;

public class DatosInvalidosException extends RuntimeException {
    public DatosInvalidosException(String mensaje) {
        super("Datos inválidos: " + mensaje);
    }
}