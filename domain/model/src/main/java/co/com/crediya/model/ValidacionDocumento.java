package co.com.crediya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de dominio que representa el resultado de la validación de un documento.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidacionDocumento {
    private String documentoIdentidad;
    private Boolean existe;
    private String mensaje;
    private DetalleUsuario detalleUsuario;
}
