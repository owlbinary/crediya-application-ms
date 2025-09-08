package co.com.crediya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TipoPrestamo {
    private String id;
    private String nombre;
    private String descripcion;
    private BigDecimal montoMinimo;
    private BigDecimal montoMaximo;
    private Integer plazoMinimoMeses;
    private Integer plazoMaximoMeses;
    private BigDecimal tasaInteres;
    private Boolean activo;
    private Boolean validacionAutomatica;
}