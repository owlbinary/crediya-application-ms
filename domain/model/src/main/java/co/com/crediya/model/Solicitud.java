package co.com.crediya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa una solicitud de préstamo.
 * Encapsula la información y comportamientos relacionados con las solicitudes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {
    private String id;
    private String documentoIdentidad;
    private BigDecimal monto;
    private Integer plazo;
    private String tipoPrestamoId;
    private EstadoSolicitud estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    /**
     * Factory method para crear una nueva solicitud en estado inicial.
     * 
     * @param documentoIdentidad Documento de identidad del solicitante
     * @param monto Monto solicitado
     * @param plazo Plazo en meses
     * @param tipoPrestamoId Tipo de préstamo solicitado
     * @return Nueva instancia de Solicitud
     */
    public static Solicitud crearNueva(String documentoIdentidad, BigDecimal monto, 
                                      Integer plazo, String tipoPrestamoId) {
        LocalDateTime ahora = LocalDateTime.now();
        return Solicitud.builder()
            .documentoIdentidad(documentoIdentidad)
            .monto(monto)
            .plazo(plazo)
            .tipoPrestamoId(tipoPrestamoId)
            .estado(EstadoSolicitud.PENDIENTE_REVISION)
            .fechaCreacion(ahora)
            .fechaActualizacion(ahora)
            .build();
    }
}