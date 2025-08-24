package co.com.crediya.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudResponse {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("documentoIdentidad")
    private String documentoIdentidad;
    
    @JsonProperty("monto")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal monto;
    
    @JsonProperty("plazo")
    private Integer plazo;
    
    @JsonProperty("tipoPrestamoId")
    private String tipoPrestamoId;
    
    @JsonProperty("estado")
    private String estado;
    
    @JsonProperty("estadoDescripcion")
    private String estadoDescripcion;
    
    @JsonProperty("fechaCreacion")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCreacion;
    
    @JsonProperty("fechaActualizacion")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaActualizacion;
}