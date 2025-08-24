package co.com.crediya.model.exception;

public class TipoPrestamoNoExisteException extends RuntimeException {
    public TipoPrestamoNoExisteException(String tipoPrestamoId) {
        super("Tipo de préstamo con ID " + tipoPrestamoId + " no existe");
    }
}