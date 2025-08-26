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
    @Pattern(regexp = REGEX_DOCUMENTO, message = MENSAJE_DOCUMENTO_FORMATO)
    private String documentoIdentidad;
    
    @JsonProperty("monto")
    private BigDecimal monto;
    
    @JsonProperty("plazo")
    private Integer plazo;
    
    @JsonProperty("tipoPrestamoId")
    private String tipoPrestamoId;
}