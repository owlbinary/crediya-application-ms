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

    @JsonProperty("existe")
    private Boolean existe;

    @JsonProperty("mensaje")
    private String mensaje;
    
    @JsonProperty("usuario")
    private DetalleUsuarioResponse detalleUsuario;
}
