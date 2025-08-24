package co.com.crediya.api.dto.constants;

public final class ValidationConstants {

    private ValidationConstants() {
    }

    public static final String MENSAJE_DOCUMENTO_OBLIGATORIO = "El documento de identidad es obligatorio";
    public static final String MENSAJE_DOCUMENTO_FORMATO = "El documento debe tener entre 8 y 12 dígitos numéricos";
    public static final String REGEX_DOCUMENTO = "^\\d{8,12}$";

    public static final String MENSAJE_MONTO_OBLIGATORIO = "El monto es obligatorio";
    public static final String MENSAJE_MONTO_MINIMO = "El monto mínimo es $100.000";
    public static final String MENSAJE_MONTO_MAXIMO = "El monto máximo es $100.000.000";
    public static final String MENSAJE_MONTO_DIGITOS = "El monto debe tener máximo 10 dígitos enteros y 2 decimales";
    public static final String VALOR_MONTO_MINIMO = "100000.00";
    public static final String VALOR_MONTO_MAXIMO = "100000000.00";
    public static final int DIGITOS_ENTEROS_MONTO = 10;
    public static final int DIGITOS_DECIMALES_MONTO = 2;

    public static final String MENSAJE_PLAZO_OBLIGATORIO = "El plazo es obligatorio";
    public static final String MENSAJE_PLAZO_MINIMO = "El plazo mínimo es 1 mes";
    public static final String MENSAJE_PLAZO_MAXIMO = "El plazo máximo es 120 meses (10 años)";
    public static final int VALOR_PLAZO_MINIMO = 1;
    public static final int VALOR_PLAZO_MAXIMO = 120;

    public static final String MENSAJE_TIPO_PRESTAMO_OBLIGATORIO = "El tipo de préstamo es obligatorio";
    public static final String MENSAJE_TIPO_PRESTAMO_LONGITUD = "El tipo de préstamo debe tener entre 1 y 50 caracteres";
    public static final int TIPO_PRESTAMO_LONGITUD_MINIMA = 1;
    public static final int TIPO_PRESTAMO_LONGITUD_MAXIMA = 50;

    public static final class Limites {
        private Limites() {}
        
        public static final int DOCUMENTO_MIN_DIGITOS = 8;
        public static final int DOCUMENTO_MAX_DIGITOS = 12;
        public static final long MONTO_MINIMO_PESOS = 100_000L;
        public static final long MONTO_MAXIMO_PESOS = 100_000_000L;
        public static final int PLAZO_MINIMO_MESES = 1;
        public static final int PLAZO_MAXIMO_MESES = 120;
    }
}
