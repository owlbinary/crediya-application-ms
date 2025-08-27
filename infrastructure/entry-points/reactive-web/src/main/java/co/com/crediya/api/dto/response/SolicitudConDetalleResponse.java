package co.com.crediya.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Respuesta de solicitud con detalles del usuario")
public class SolicitudConDetalleResponse {
    
    @Schema(description = "ID único de la solicitud", example = "1")
    private String id;
    
    @Schema(description = "Documento de identidad del solicitante", example = "12345678")
    @JsonProperty("documento_identidad")
    private String documentoIdentidad;
    
    @Schema(description = "Monto solicitado", example = "1000000")
    private BigDecimal monto;
    
    @Schema(description = "Plazo en meses", example = "12")
    private Integer plazo;
    
    @Schema(description = "ID del tipo de préstamo", example = "1")
    @JsonProperty("tipo_prestamo_id")
    private String tipoPrestamoId;
    
    @Schema(description = "Descripción del tipo de préstamo", example = "Crédito de Libre Inversión")
    @JsonProperty("descripcion_tipo_prestamo")
    private String descripcionTipoPrestamo;
    
    @Schema(description = "Tasa de interés del tipo de préstamo", example = "15.5")
    @JsonProperty("tasa_interes")
    private BigDecimal tasaInteres;
    
    @Schema(description = "Estado de la solicitud", example = "PENDIENTE_REVISION")
    private String estado;
    
    @Schema(description = "Descripción del estado", example = "Solicitud pendiente de revisión")
    @JsonProperty("estado_descripcion")
    private String estadoDescripcion;
    
    @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00")
    @JsonProperty("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última actualización", example = "2024-01-15T10:30:00")
    @JsonProperty("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @Schema(description = "Email del solicitante", example = "usuario@ejemplo.com")
    private String email;
    
    @Schema(description = "Nombre del solicitante", example = "Juan")
    private String nombre;
    
    @Schema(description = "Apellido del solicitante", example = "Pérez")
    private String apellido;
    
    @Schema(description = "Salario base mensual", example = "3000000")
    @JsonProperty("salario_base")
    private BigDecimal salarioBase;
    
    @Schema(description = "Deuda total mensual de la solicitud", example = "200000")
    @JsonProperty("deuda_total_mensual_solicitud")
    private BigDecimal deudaTotalMensualSolicitud;
}
