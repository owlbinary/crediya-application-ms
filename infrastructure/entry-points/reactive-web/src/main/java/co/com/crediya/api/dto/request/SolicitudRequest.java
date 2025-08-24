package co.com.crediya.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

import static co.com.crediya.api.dto.constants.ValidationConstants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudRequest {
    
    @JsonProperty("documentoIdentidad")
    @NotBlank(message = MENSAJE_DOCUMENTO_OBLIGATORIO)
    @Pattern(regexp = REGEX_DOCUMENTO, message = MENSAJE_DOCUMENTO_FORMATO)
    private String documentoIdentidad;
    
    @JsonProperty("monto")
    @NotNull(message = MENSAJE_MONTO_OBLIGATORIO)
    @DecimalMin(value = VALOR_MONTO_MINIMO, message = MENSAJE_MONTO_MINIMO)
    @DecimalMax(value = VALOR_MONTO_MAXIMO, message = MENSAJE_MONTO_MAXIMO)
    @Digits(integer = DIGITOS_ENTEROS_MONTO, fraction = DIGITOS_DECIMALES_MONTO, message = MENSAJE_MONTO_DIGITOS)
    private BigDecimal monto;
    
    @JsonProperty("plazo")
    @NotNull(message = MENSAJE_PLAZO_OBLIGATORIO)
    @Min(value = VALOR_PLAZO_MINIMO, message = MENSAJE_PLAZO_MINIMO)
    @Max(value = VALOR_PLAZO_MAXIMO, message = MENSAJE_PLAZO_MAXIMO)
    private Integer plazo;
    
    @JsonProperty("tipoPrestamoId")
    @NotBlank(message = MENSAJE_TIPO_PRESTAMO_OBLIGATORIO)
    @Size(min = TIPO_PRESTAMO_LONGITUD_MINIMA, max = TIPO_PRESTAMO_LONGITUD_MAXIMA, message = MENSAJE_TIPO_PRESTAMO_LONGITUD)
    private String tipoPrestamoId;
}