package es.convive.backend.controller;

import es.convive.backend.dto.BalancesRespuesta;
import es.convive.backend.dto.LiquidacionRespuesta;
import es.convive.backend.security.DetallesUsuario;
import es.convive.backend.service.ServicioBalances;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/balances")
public class ControladorBalances {
    private final ServicioBalances servicioBalances;

    public ControladorBalances(ServicioBalances servicioBalances) {
        this.servicioBalances = servicioBalances;
    }

    @GetMapping
    public BalancesRespuesta consultar(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            Authentication autenticacion) {
        return servicioBalances.calcular(usuarioId(autenticacion), desde, hasta);
    }

    @GetMapping({"/liquidacion", "/settlement"})
    public LiquidacionRespuesta liquidar(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            Authentication autenticacion) {
        return servicioBalances.liquidar(usuarioId(autenticacion), desde, hasta);
    }

    private Long usuarioId(Authentication autenticacion) {
        return ((DetallesUsuario) autenticacion.getPrincipal()).getUsuario().getId();
    }
}
