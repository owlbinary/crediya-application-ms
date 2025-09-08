package co.com.crediya.sqs.adapter;
import co.com.crediya.model.DetalleUsuario;
import java.util.function.Function;

import java.math.BigDecimal;

public class NotificacionUtils {
    private NotificacionUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String getUsuarioCampo(DetalleUsuario usuario, Function<DetalleUsuario, String> getter) {
        return usuario != null && getter.apply(usuario) != null ? getter.apply(usuario) : "";
    }

    public static String getUsuarioCampoBigDecimal(DetalleUsuario usuario, Function<DetalleUsuario, java.math.BigDecimal> getter) {
        return usuario != null && getter.apply(usuario) != null ? getter.apply(usuario).toPlainString() : "null";
    }

    public static String safeString(Object value) {
        return value != null ? value.toString() : "";
    }

    public static String safeBigDecimal(BigDecimal value) {
        return value != null ? value.toPlainString() : "null";
    }

    public static String safeInteger(Number value) {
        return value != null ? value.toString() : "null";
    }

    public static String safeBoolean(Boolean value) {
        return value != null ? value.toString() : "null";
    }

    public static String safeEnum(Enum<?> value) {
        return value != null ? value.name() : "";
    }
}
