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
@Table(name = "tipo_prestamo")
public class TipoPrestamoEntity {

    @Id
    @Column("id_tipo_prestamo")
    private Integer id;

    @Column("nombre")
    private String nombre;

    @Column("monto_minimo")
    private BigDecimal montoMinimo;

    @Column("monto_maximo")
    private BigDecimal montoMaximo;

    @Column("tasa_interes")
    private BigDecimal tasaInteres;

    @Column("validacion_automatica")
    private Boolean validacionAutomatica;

    @Column("activo")
    private Boolean activo;

    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
