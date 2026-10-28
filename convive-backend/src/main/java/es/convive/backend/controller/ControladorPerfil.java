package es.convive.backend.controller;

import es.convive.backend.dto.ActualizarPerfilSolicitud;
import es.convive.backend.dto.UsuarioRespuesta;
import es.convive.backend.security.DetallesUsuario;
import es.convive.backend.service.ServicioPerfil;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
public class ControladorPerfil {
    private final ServicioPerfil servicioPerfil;

    public ControladorPerfil(ServicioPerfil servicioPerfil) {
        this.servicioPerfil = servicioPerfil;
    }

    @GetMapping
    public UsuarioRespuesta obtener(Authentication autenticacion) {
        return servicioPerfil.obtener(usuarioId(autenticacion));
    }

    @PatchMapping
    public UsuarioRespuesta actualizar(@Valid @RequestBody ActualizarPerfilSolicitud solicitud,
                                        Authentication autenticacion) {
        return servicioPerfil.actualizar(usuarioId(autenticacion), solicitud);
    }

    private Long usuarioId(Authentication autenticacion) {
        return ((DetallesUsuario) autenticacion.getPrincipal()).getUsuario().getId();
    }
}
