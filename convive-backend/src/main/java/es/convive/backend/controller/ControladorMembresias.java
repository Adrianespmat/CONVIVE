package es.convive.backend.controller;

import es.convive.backend.dto.MiembroRespuesta;
import es.convive.backend.security.DetallesUsuario;
import es.convive.backend.service.ServicioMembresias;
import es.convive.backend.service.ServicioPisos;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pisos/mio/integrantes")
public class ControladorMembresias {
    private final ServicioPisos servicioPisos;
    private final ServicioMembresias servicioMembresias;

    public ControladorMembresias(ServicioPisos servicioPisos, ServicioMembresias servicioMembresias) {
        this.servicioPisos = servicioPisos;
        this.servicioMembresias = servicioMembresias;
    }

    @GetMapping
    public List<MiembroRespuesta> listar(Authentication autenticacion) {
        return servicioPisos.listarIntegrantes(usuarioId(autenticacion));
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> darDeBaja(@PathVariable Long usuarioId, Authentication autenticacion) {
        servicioMembresias.darDeBaja(usuarioId(autenticacion), usuarioId);
        return ResponseEntity.noContent().build();
    }

    private Long usuarioId(Authentication autenticacion) {
        return ((DetallesUsuario) autenticacion.getPrincipal()).getUsuario().getId();
    }
}
