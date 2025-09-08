package co.com.crediya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanPagoCuota {
    private Integer numeroCuota;
    private Double cuota;
    private Double abonoCapital;
    private Double interes;
    private Double saldoRestante;
}
