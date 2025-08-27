package co.com.crediya.validacion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para el detalle del usuario obtenido del servicio de validación de documentos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleUsuarioResponse {
    
    @JsonProperty("id_usuario")
    private Long idUsuario;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellido")
    private String apellido;

    @JsonProperty("email")
    private String email;

    @JsonProperty("documento_identidad")
    private String documentoIdentidad;

    @JsonProperty("telefono")
    private String telefono;

    @JsonProperty("direccion")
    private String direccion;

    @JsonProperty("id_rol")
    private Long idRol;

    @JsonProperty("salario_base")
    private BigDecimal salarioBase;

    @JsonProperty("fecha_creacion")
    private LocalDateTime fechaCreacion;
}
