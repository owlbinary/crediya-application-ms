package co.com.crediya.sqs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarEstadoSolicitudMessage {
    
    @JsonProperty("tipo")
    private String tipo;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("params")
    private Params params;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Params {
        
        @JsonProperty("solicitudId")
        private String solicitudId;
        
        @JsonProperty("nuevoEstado")
        private String nuevoEstado;
        
        @JsonProperty("justificacion")
        private String justificacion;
        
        @JsonProperty("planPago")
        private List<PlanPagoCuotaDto> planPago;
        
        @JsonProperty("origen")
        private String origen;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlanPagoCuotaDto {
        
        @JsonProperty("numero_cuota")
        private Integer numeroCuota;
        
        @JsonProperty("cuota")
        private String cuota;
        
        @JsonProperty("abono_capital")
        private String abonoCapital;
        
        @JsonProperty("interes")
        private String interes;
        
        @JsonProperty("saldo_restante")
        private String saldoRestante;
    }
}
