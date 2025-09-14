package co.com.crediya.model;

import co.com.crediya.model.exception.DatosInvalidosException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa una solicitud de préstamo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {
    private String id;
    private String documentoIdentidad;
    private String email;
    private BigDecimal monto;
    private Integer plazo;
    private String tipoPrestamoId;
    private EstadoSolicitud estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private BigDecimal deudaTotalMensual;
    
    /**
     * Factory method para crear una nueva solicitud en estado inicial.
     * 
     * @param documentoIdentidad Documento de identidad del solicitante
     * @param email Email del solicitante
     * @param monto Monto solicitado
     * @param plazo Plazo en meses
     * @param tipoPrestamoId Tipo de préstamo solicitado
     * @return Nueva instancia de Solicitud
     * @throws DatosInvalidosException si los datos no cumplen las reglas de negocio
     */
    public static Solicitud crearNueva(String documentoIdentidad, String email, BigDecimal monto, 
                                      Integer plazo, String tipoPrestamoId) {
        validarDatosNegocio(documentoIdentidad, email, monto, plazo, tipoPrestamoId);
        
        LocalDateTime ahora = LocalDateTime.now();
        return Solicitud.builder()
            .documentoIdentidad(documentoIdentidad)
            .email(email)
            .monto(monto)
            .plazo(plazo)
            .tipoPrestamoId(tipoPrestamoId)
            .estado(EstadoSolicitud.PENDIENTE_REVISION)
            .fechaCreacion(ahora)
            .fechaActualizacion(ahora)
            .build();
    }
    
    /**
     * Valida los datos para creación de solicitud sin crear la instancia.
     * Útil para validación temprana en casos de uso.
     * 
     * @param documentoIdentidad Documento de identidad del solicitante
     * @param email Email del solicitante
     * @param monto Monto solicitado
     * @param plazo Plazo en meses
     * @param tipoPrestamoId Tipo de préstamo solicitado
     * @throws DatosInvalidosException si los datos no cumplen las reglas de negocio
     */
    public static void validarDatosParaCreacion(String documentoIdentidad, String email, BigDecimal monto, 
                                               Integer plazo, String tipoPrestamoId) {
        validarDatosNegocio(documentoIdentidad, email, monto, plazo, tipoPrestamoId);
    }
    
    private static void validarDatosNegocio(String documentoIdentidad, String email, BigDecimal monto, 
                                           Integer plazo, String tipoPrestamoId) {
        validarDocumentoIdentidad(documentoIdentidad);
        validarEmail(email);
        validarMonto(monto);
        validarPlazo(plazo);
        validarTipoPrestamoId(tipoPrestamoId);
    }
    
    private static void validarDocumentoIdentidad(String documentoIdentidad) {
        if (documentoIdentidad == null || documentoIdentidad.trim().isEmpty()) {
            throw new DatosInvalidosException("Documento de identidad es obligatorio");
        }
    }
    
    private static void validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new DatosInvalidosException("Email es obligatorio");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new DatosInvalidosException("Email debe tener un formato válido");
        }
    }
    
    private static void validarMonto(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DatosInvalidosException("El monto debe ser mayor a cero");
        }
    }
    
    private static void validarPlazo(Integer plazo) {
        if (plazo == null || plazo <= 0) {
            throw new DatosInvalidosException("El plazo en meses debe ser mayor a cero");
        }
    }
    
    private static void validarTipoPrestamoId(String tipoPrestamoId) {
        if (tipoPrestamoId == null || tipoPrestamoId.trim().isEmpty()) {
            throw new DatosInvalidosException("Tipo de préstamo es obligatorio");
        }
    }
}