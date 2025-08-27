package co.com.crediya.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.com.crediya.api.dto.request.SolicitudRequest;
import co.com.crediya.api.dto.response.SolicitudResponse;
import co.com.crediya.api.mapper.SolicitudMapper;
import co.com.crediya.api.security.JwtUserPrincipal;
import co.com.crediya.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/solicitudes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Solicitudes", description = "API para gestión de solicitudes")
@SecurityRequirement(name = "bearerAuth")
public class SolicitudController {

    private final RegistrarSolicitudUseCase registrarSolicitudUseCase;
    private final SolicitudMapper solicitudMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nueva solicitud", 
               description = "Crea una nueva solicitud de crédito")
    public Mono<SolicitudResponse> crearSolicitud(
            @Valid @RequestBody SolicitudRequest solicitudRequest,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            Authentication authentication) {
        
        JwtUserPrincipal userPrincipal = (JwtUserPrincipal) authentication.getPrincipal();
        log.info("Procesando nueva solicitud para documento: {} por usuario: {}", 
                 solicitudRequest.getDocumentoIdentidad(), userPrincipal.getEmail());

        return registrarSolicitudUseCase.ejecutar(
                solicitudRequest.getDocumentoIdentidad(),
                solicitudRequest.getMonto(),
                solicitudRequest.getPlazo(),
                solicitudRequest.getTipoPrestamoId(),
                authorizationHeader
            )
            .map(solicitudMapper::toResponse)
            .doOnNext(respuesta -> log.info("Solicitud creada con éxito ID: {} por usuario: {}", 
                                           respuesta.getId(), userPrincipal.getEmail()))
            .doOnError(excepcion -> log.error("Fallo en creación de solicitud para usuario {}: {}", 
                                             userPrincipal.getEmail(), excepcion.getMessage()));
    }
}
