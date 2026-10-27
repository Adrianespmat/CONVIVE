package es.convive.backend.controller;

import es.convive.backend.dto.AutenticacionRespuesta;
import es.convive.backend.dto.InicioSesionSolicitud;
import es.convive.backend.dto.RegistroSolicitud;
import es.convive.backend.dto.UsuarioRespuesta;
import es.convive.backend.security.DetallesUsuario;
import es.convive.backend.service.ServicioAutenticacion;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/autenticacion")
public class ControladorAutenticacion {
    private final ServicioAutenticacion servicioAutenticacion;

    public ControladorAutenticacion(ServicioAutenticacion servicioAutenticacion) {
        this.servicioAutenticacion = servicioAutenticacion;
    }

    @PostMapping("/registro")
    public ResponseEntity<AutenticacionRespuesta> registrar(@Valid @RequestBody RegistroSolicitud solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioAutenticacion.registrar(solicitud));
    }

    @PostMapping("/login")
    public AutenticacionRespuesta iniciarSesion(@Valid @RequestBody InicioSesionSolicitud solicitud) {
        return servicioAutenticacion.iniciarSesion(solicitud);
    }

    @GetMapping("/yo")
    public UsuarioRespuesta usuarioAutenticado(Authentication autenticacion) {
        DetallesUsuario detalles = (DetallesUsuario) autenticacion.getPrincipal();
        return UsuarioRespuesta.desde(detalles.getUsuario());
    }
}
