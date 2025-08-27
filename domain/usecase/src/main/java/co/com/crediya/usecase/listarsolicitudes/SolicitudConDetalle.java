package co.com.crediya.usecase.listarsolicitudes;

import co.com.crediya.model.EstadoSolicitud;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SolicitudConDetalle {
    private String id;
    private String documentoIdentidad;
    private BigDecimal monto;
    private Integer plazo;
    private String tipoPrestamoId;
    private String descripcionTipoPrestamo;
    private BigDecimal tasaInteres;
    private EstadoSolicitud estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String email;
    private String nombre;
    private String apellido;
    private BigDecimal salarioBase;
    private BigDecimal deudaTotalMensualSolicitud;
}
