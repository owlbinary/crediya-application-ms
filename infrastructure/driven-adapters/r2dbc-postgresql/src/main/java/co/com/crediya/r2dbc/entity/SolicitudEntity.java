package co.com.crediya.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "solicitud")
public class SolicitudEntity {

    @Id
    @Column("id_solicitud")
    private Integer id;

    @Column("documento_identidad")
    private String documentoIdentidad;

    @Column("monto")
    private BigDecimal monto;

    @Column("plazo")
    private Integer plazo;

    @Column("email")
    private String email;

    @Column("id_estado")
    private Integer idEstado;

    @Column("id_tipo_prestamo")
    private Integer idTipoPrestamo;

    @Column("fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @Column("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column("observaciones")
    private String observaciones;
    
    @Column("deuda_total_mensual_solicitudes_aprobadas")
    private BigDecimal deudaTotalMensualSolicitudesAprobadas;
}
