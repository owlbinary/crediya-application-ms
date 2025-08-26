package co.com.crediya.validacion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta del servicio de validación de documentos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidacionDocumentoResponse {
    
    @JsonProperty("documento_identidad")
    private String documentoIdentidad;

    @JsonProperty("existe")
    private Boolean existe;

    @JsonProperty("mensaje")
    private String mensaje;
}
