package co.com.crediya.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @JsonProperty("codigo")
    private String codigo;

    @JsonProperty("mensaje")
    private String mensaje;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("path")
    private String path;
    
    public static ErrorResponse of(String codigo, String mensaje, String path) {
        return ErrorResponse.builder()
            .codigo(codigo)
            .mensaje(mensaje)
            .timestamp(LocalDateTime.now())
            .path(path)
            .build();
    }
}
