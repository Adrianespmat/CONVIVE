package es.convive.backend.service;

import es.convive.backend.dto.BalanceRespuesta;
import es.convive.backend.dto.BalancesRespuesta;
import es.convive.backend.dto.GastoResumenRespuesta;
import es.convive.backend.dto.PanelRespuesta;
import es.convive.backend.entity.Gasto;
import es.convive.backend.entity.Membresia;
import es.convive.backend.exception.DatosGastoException;
import es.convive.backend.repository.GastoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class ServicioPanel {
    private static final int LIMITE_ULTIMOS_GASTOS = 5;

    private final GastoRepository gastoRepository;
    private final ServicioPisos servicioPisos;
    private final ServicioBalances servicioBalances;

    public ServicioPanel(GastoRepository gastoRepository, ServicioPisos servicioPisos,
                         ServicioBalances servicioBalances) {
        this.gastoRepository = gastoRepository;
        this.servicioPisos = servicioPisos;
        this.servicioBalances = servicioBalances;
    }

    @Transactional(readOnly = true)
    public PanelRespuesta obtener(Long usuarioId, LocalDate desde, LocalDate hasta) {
        validarIntervalo(desde, hasta);
        Membresia membresia = servicioPisos.obtenerMembresiaActiva(usuarioId);
        List<Gasto> gastos = gastoRepository.buscarPorFiltros(
                membresia.getPiso().getId(), desde, hasta, null);
        BigDecimal total = gastos.stream()
                .map(Gasto::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.UNNECESSARY);
        List<GastoResumenRespuesta> ultimos = gastos.stream()
                .limit(LIMITE_ULTIMOS_GASTOS)
                .map(GastoResumenRespuesta::desde)
                .toList();
        BalancesRespuesta balances = servicioBalances.calcular(usuarioId, desde, hasta);
        BalanceRespuesta personal = balances.balances().stream()
                .filter(balance -> balance.usuarioId().equals(usuarioId))
                .findFirst()
                .orElseThrow(() -> new DatosGastoException("No se pudo calcular el balance personal"));
        return new PanelRespuesta(desde, hasta, total, ultimos, personal);
    }

    private void validarIntervalo(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new DatosGastoException("La fecha inicial no puede ser posterior a la fecha final");
        }
    }
}
