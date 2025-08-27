package co.com.crediya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Objeto de dominio que representa el detalle de un usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleUsuario {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String documentoIdentidad;
    private String telefono;
    private String direccion;
    private Long idRol;
    private BigDecimal salarioBase;
    private LocalDateTime fechaCreacion;
}
