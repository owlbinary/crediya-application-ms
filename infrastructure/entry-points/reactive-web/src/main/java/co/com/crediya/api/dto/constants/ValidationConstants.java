package co.com.crediya.api.dto.constants;

public final class ValidationConstants {

    private ValidationConstants() {
    }

    public static final String MENSAJE_DOCUMENTO_OBLIGATORIO = "El documento de identidad es obligatorio";
    public static final String MENSAJE_DOCUMENTO_FORMATO = "El documento debe tener entre 8 y 12 dígitos numéricos";
    public static final String REGEX_DOCUMENTO = "^\\d{8,12}$";
    public static final String MENSAJE_MONTO_OBLIGATORIO = "El monto es obligatorio";
    public static final String MENSAJE_PLAZO_OBLIGATORIO = "El plazo es obligatorio";
    public static final String MENSAJE_TIPO_PRESTAMO_OBLIGATORIO = "El tipo de préstamo es obligatorio";
}
