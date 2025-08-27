package co.com.crediya.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.com.crediya.api.dto.request.SolicitudRequest;
import co.com.crediya.api.dto.response.SolicitudConDetalleResponse;
import co.com.crediya.api.dto.response.SolicitudResponse;
import co.com.crediya.api.mapper.SolicitudConDetalleMapper;
import co.com.crediya.api.mapper.SolicitudMapper;
import co.com.crediya.api.security.JwtUserPrincipal;
import co.com.crediya.usecase.listarsolicitudes.ListarSolicitudesUseCase;
import co.com.crediya.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
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
    private final ListarSolicitudesUseCase listarSolicitudesUseCase;
    private final SolicitudMapper solicitudMapper;
    private final SolicitudConDetalleMapper solicitudConDetalleMapper;

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

    @GetMapping
    @Operation(summary = "Listar solicitudes pendientes de revisión",
               description = "Obtiene el listado de solicitudes que requieren revisión manual. Solo para usuarios con rol Asesor.")
    @PreAuthorize("hasRole('ASESOR')")
    public Flux<SolicitudConDetalleResponse> listarSolicitudesPendientes(
            @Parameter(description = "Número de página", example = "0")
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(value = "tamano", defaultValue = "10") int tamano,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            Authentication authentication) {
        
        JwtUserPrincipal userPrincipal = (JwtUserPrincipal) authentication.getPrincipal();
        log.info("Listando solicitudes pendientes - página: {}, tamaño: {} por usuario: {}", 
                 pagina, tamano, userPrincipal.getEmail());

        return listarSolicitudesUseCase.ejecutar(pagina, tamano, authorizationHeader)
            .map(solicitudConDetalleMapper::toResponse)
            .doOnNext(solicitud -> log.debug("Solicitud listada: {}", solicitud.getId()))
            .doOnError(excepcion -> log.error("Error listando solicitudes para usuario {}: {}", 
                                             userPrincipal.getEmail(), excepcion.getMessage()));
    }
}
