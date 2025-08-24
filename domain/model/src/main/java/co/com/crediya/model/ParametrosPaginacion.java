package co.com.crediya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Parámetros para la consulta paginada de solicitudes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametrosPaginacion {
    private Integer pagina;
    private Integer tamanio;
    private List<EstadoSolicitud> estados;
    private String orden;
    
    public static ParametrosPaginacion porDefecto() {
        return ParametrosPaginacion.builder()
            .pagina(0)
            .tamanio(20)
            .orden("fechaCreacion")
            .build();
    }
}
