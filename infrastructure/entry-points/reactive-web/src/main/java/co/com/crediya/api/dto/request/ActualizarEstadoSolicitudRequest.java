package co.com.crediya.api.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarEstadoSolicitudRequest {
    @NotNull
    @JsonProperty("nuevoEstado")
    private String nuevoEstado;

    @JsonProperty("justificacion")
    private String justificacion;

    @JsonProperty("planPago")
    private List<PlanPagoCuota> planPago;

    @AssertTrue(message = "planPago es obligatorio si el estado es APROBADO")
    public boolean isPlanPagoValid() {
        if ("APROBADO".equalsIgnoreCase(nuevoEstado)) {
            return planPago != null && !planPago.isEmpty();
        }
        return planPago == null || planPago.isEmpty();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlanPagoCuota {
        @JsonProperty("numero_cuota")
        @NotNull
        private Integer numeroCuota;

        @JsonProperty("cuota")
        @NotNull
        private Double cuota;

        @JsonProperty("abono_capital")
        @NotNull
        private Double abonoCapital;

        @JsonProperty("interes")
        @NotNull
        private Double interes;

        @JsonProperty("saldo_restante")
        @NotNull
        private Double saldoRestante;
    }
}
