package es.convive.backend.controller;

import es.convive.backend.dto.PanelRespuesta;
import es.convive.backend.security.DetallesUsuario;
import es.convive.backend.service.ServicioPanel;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
public class ControladorPanel {
    private final ServicioPanel servicioPanel;

    public ControladorPanel(ServicioPanel servicioPanel) {
        this.servicioPanel = servicioPanel;
    }

    @GetMapping
    public PanelRespuesta obtener(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            Authentication autenticacion) {
        return servicioPanel.obtener(usuarioId(autenticacion), desde, hasta);
    }

    private Long usuarioId(Authentication autenticacion) {
        return ((DetallesUsuario) autenticacion.getPrincipal()).getUsuario().getId();
    }
}
