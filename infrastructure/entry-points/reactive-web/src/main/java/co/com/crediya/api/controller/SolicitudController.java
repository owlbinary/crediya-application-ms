package co.com.crediya.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.com.crediya.api.dto.request.SolicitudRequest;
import co.com.crediya.api.dto.request.ActualizarEstadoSolicitudRequest;
import co.com.crediya.model.PlanPagoCuota;
import co.com.crediya.api.dto.response.SolicitudConDetalleResponse;
import co.com.crediya.api.dto.response.SolicitudResponse;
import co.com.crediya.api.mapper.SolicitudConDetalleMapper;
import co.com.crediya.api.mapper.SolicitudMapper;
import co.com.crediya.api.security.JwtUserPrincipal;
import co.com.crediya.usecase.listarsolicitudes.ListarSolicitudesUseCase;
import co.com.crediya.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import co.com.crediya.usecase.actualizarestadosolicitud.ActualizarEstadoSolicitudUseCase;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
        private final ActualizarEstadoSolicitudUseCase actualizarEstadoSolicitudUseCase;

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        @Operation(summary = "Crear nueva solicitud", description = "Crea una nueva solicitud de crédito")
        @PreAuthorize("hasRole('CLIENTE')")
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
                                authorizationHeader)
                                .map(solicitudMapper::toResponse)
                                .doOnNext(respuesta -> log.info("Solicitud creada con éxito ID: {} por usuario: {}",
                                                respuesta.getId(), userPrincipal.getEmail()))
                                .doOnError(excepcion -> log.error("Fallo en creación de solicitud para usuario {}: {}",
                                                userPrincipal.getEmail(), excepcion.getMessage()));
        }

        @PatchMapping("/{id}")
        @Operation(summary = "Aprobar o rechazar solicitud de crédito", description = "Permite a un asesor aprobar o rechazar manualmente una solicitud. Envía notificación al solicitante.")
        @PreAuthorize("hasRole('ASESOR')")
        public Mono<SolicitudResponse> actualizarEstadoSolicitud(
                        @PathVariable("id") String id,
                        @Valid @RequestBody ActualizarEstadoSolicitudRequest request,
                        @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                        Authentication authentication) {
                JwtUserPrincipal userPrincipal = (JwtUserPrincipal) authentication.getPrincipal();
                log.info("Solicitud de cambio de estado para solicitud {} a '{}' por asesor {}", id,
                                request.getNuevoEstado(),
                                userPrincipal.getEmail());
                List<PlanPagoCuota> planPagoDominio = null;
                if (request.getPlanPago() != null) {
                        planPagoDominio = request.getPlanPago().stream()
                                        .map(dto -> PlanPagoCuota.builder()
                                                        .numeroCuota(dto.getNumeroCuota())
                                                        .cuota(dto.getCuota())
                                                        .abonoCapital(dto.getAbonoCapital())
                                                        .interes(dto.getInteres())
                                                        .saldoRestante(dto.getSaldoRestante())
                                                        .build())
                                        .toList();
                }
                return actualizarEstadoSolicitudUseCase
                                .ejecutar(id, request.getNuevoEstado(), request.getJustificacion(), authorizationHeader,
                                                planPagoDominio)
                                .map(solicitudMapper::toResponse)
                                .doOnNext(res -> log.info(
                                                "Estado actualizado y mensaje enviado a SQS para solicitud {}", id))
                                .doOnError(ex -> log.error("Error actualizando estado de solicitud {}: {}", id,
                                                ex.getMessage()));
        }

        @GetMapping
        @Operation(summary = "Listar solicitudes pendientes de revisión", description = "Obtiene el listado de solicitudes que requieren revisión manual. Solo para usuarios con rol Asesor.")
        @PreAuthorize("hasRole('ASESOR')")
        public Flux<SolicitudConDetalleResponse> listarSolicitudesPendientes(
                        @Parameter(description = "Número de página", example = "0") @RequestParam(value = "pagina", defaultValue = "0") int pagina,
                        @Parameter(description = "Tamaño de página", example = "10") @RequestParam(value = "tamano", defaultValue = "10") int tamano,
                        @Parameter(description = "Estado de la solicitud (opcional)") @RequestParam(value = "estado", required = false) String estado,
                        @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                        Authentication authentication) {

                JwtUserPrincipal userPrincipal = (JwtUserPrincipal) authentication.getPrincipal();
                log.info("Listando solicitudes - página: {}, tamaño: {}, estado: {} por usuario: {}",
                                pagina, tamano, estado, userPrincipal.getEmail());

                return listarSolicitudesUseCase.ejecutar(pagina, tamano, estado, authorizationHeader)
                                .map(solicitudConDetalleMapper::toResponse)
                                .doOnNext(solicitud -> log.debug("Solicitud listada: {}", solicitud.getId()))
                                .doOnError(excepcion -> log.error("Error listando solicitudes para usuario {}: {}",
                                                userPrincipal.getEmail(), excepcion.getMessage()));
        }
}
