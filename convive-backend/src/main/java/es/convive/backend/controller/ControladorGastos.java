package es.convive.backend.controller;

import es.convive.backend.dto.ActualizarGastoSolicitud;
import es.convive.backend.dto.CrearGastoSolicitud;
import es.convive.backend.dto.GastoRespuesta;
import es.convive.backend.entity.CategoriaGasto;
import es.convive.backend.security.DetallesUsuario;
import es.convive.backend.service.ServicioGastos;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/gastos")
public class ControladorGastos {
    private final ServicioGastos servicioGastos;

    public ControladorGastos(ServicioGastos servicioGastos) {
        this.servicioGastos = servicioGastos;
    }

    @PostMapping
    public ResponseEntity<GastoRespuesta> crear(@Valid @RequestBody CrearGastoSolicitud solicitud,
                                                 Authentication autenticacion) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicioGastos.crear(usuarioId(autenticacion), solicitud));
    }

    @GetMapping
    public List<GastoRespuesta> listar(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @RequestParam(required = false) CategoriaGasto categoria,
            Authentication autenticacion) {
        return servicioGastos.listar(usuarioId(autenticacion), desde, hasta, categoria);
    }

    @GetMapping("/{gastoId}")
    public GastoRespuesta obtener(@PathVariable Long gastoId, Authentication autenticacion) {
        return servicioGastos.obtener(usuarioId(autenticacion), gastoId);
    }

    @PutMapping("/{gastoId}")
    public GastoRespuesta actualizar(@PathVariable Long gastoId,
                                     @Valid @RequestBody ActualizarGastoSolicitud solicitud,
                                     Authentication autenticacion) {
        return servicioGastos.actualizar(usuarioId(autenticacion), gastoId, solicitud);
    }

    @DeleteMapping("/{gastoId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long gastoId, Authentication autenticacion) {
        servicioGastos.eliminar(usuarioId(autenticacion), gastoId);
        return ResponseEntity.noContent().build();
    }

    private Long usuarioId(Authentication autenticacion) {
        return ((DetallesUsuario) autenticacion.getPrincipal()).getUsuario().getId();
    }
}
