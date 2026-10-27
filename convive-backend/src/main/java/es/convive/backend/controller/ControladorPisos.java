package es.convive.backend.controller;

import es.convive.backend.dto.ActualizarPisoSolicitud;
import es.convive.backend.dto.CrearPisoSolicitud;
import es.convive.backend.dto.PisoDetalleRespuesta;
import es.convive.backend.service.ServicioMembresias;
import es.convive.backend.service.ServicioPisos;
import es.convive.backend.security.DetallesUsuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pisos")
public class ControladorPisos {
    private final ServicioPisos servicioPisos;
    private final ServicioMembresias servicioMembresias;

    public ControladorPisos(ServicioPisos servicioPisos, ServicioMembresias servicioMembresias) {
        this.servicioPisos = servicioPisos;
        this.servicioMembresias = servicioMembresias;
    }

    @PostMapping
    public ResponseEntity<PisoDetalleRespuesta> crear(@Valid @RequestBody CrearPisoSolicitud solicitud,
                                                       Authentication autenticacion) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicioPisos.crear(usuarioId(autenticacion), solicitud));
    }

    @PostMapping("/unirse")
    public PisoDetalleRespuesta unirse(@Valid @RequestBody es.convive.backend.dto.UnirsePisoSolicitud solicitud,
                                       Authentication autenticacion) {
        return servicioMembresias.unirse(usuarioId(autenticacion), solicitud);
    }

    @GetMapping("/mio")
    public PisoDetalleRespuesta consultar(Authentication autenticacion) {
        return servicioPisos.obtenerPisoActual(usuarioId(autenticacion));
    }

    @PatchMapping("/mio")
    public PisoDetalleRespuesta actualizar(@Valid @RequestBody ActualizarPisoSolicitud solicitud,
                                           Authentication autenticacion) {
        return servicioPisos.actualizar(usuarioId(autenticacion), solicitud);
    }

    private Long usuarioId(Authentication autenticacion) {
        return ((DetallesUsuario) autenticacion.getPrincipal()).getUsuario().getId();
    }
}
