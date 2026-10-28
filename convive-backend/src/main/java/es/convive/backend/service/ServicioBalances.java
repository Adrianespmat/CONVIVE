package es.convive.backend.service;

import es.convive.backend.dto.BalanceRespuesta;
import es.convive.backend.dto.BalancesRespuesta;
import es.convive.backend.dto.LiquidacionRespuesta;
import es.convive.backend.dto.PagoLiquidacionRespuesta;
import es.convive.backend.entity.Gasto;
import es.convive.backend.entity.Membresia;
import es.convive.backend.entity.ParticipanteGasto;
import es.convive.backend.entity.Usuario;
import es.convive.backend.exception.DatosGastoException;
import es.convive.backend.repository.GastoRepository;
import es.convive.backend.repository.MembresiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServicioBalances {
    private static final BigDecimal CERO = new BigDecimal("0.00");

    private final GastoRepository gastoRepository;
    private final ServicioPisos servicioPisos;
    private final MembresiaRepository membresiaRepository;

    public ServicioBalances(GastoRepository gastoRepository, ServicioPisos servicioPisos,
                            MembresiaRepository membresiaRepository) {
        this.gastoRepository = gastoRepository;
        this.servicioPisos = servicioPisos;
        this.membresiaRepository = membresiaRepository;
    }

    @Transactional(readOnly = true)
    public BalancesRespuesta calcular(Long usuarioId, LocalDate desde, LocalDate hasta) {
        validarIntervalo(desde, hasta);
        Membresia membresia = servicioPisos.obtenerMembresiaActiva(usuarioId);
        List<Gasto> gastos = gastoRepository.buscarPorFiltros(
                membresia.getPiso().getId(), desde, hasta, null);
        return construirBalances(membresia, gastos, desde, hasta);
    }

    @Transactional(readOnly = true)
    public LiquidacionRespuesta liquidar(Long usuarioId, LocalDate desde, LocalDate hasta) {
        validarIntervalo(desde, hasta);
        Membresia membresia = servicioPisos.obtenerMembresiaActiva(usuarioId);
        List<Gasto> gastos = gastoRepository.buscarPorFiltros(
                membresia.getPiso().getId(), desde, hasta, null);
        BalancesResultado resultado = calcularValores(membresia, gastos);
        List<Pendiente> deudores = resultado.valores().entrySet().stream()
                .filter(entrada -> entrada.getValue().compareTo(CERO) < 0)
                .map(entrada -> new Pendiente(entrada.getKey(), entrada.getValue().abs()))
                .sorted(Comparator.comparingLong(p -> p.usuario().getId()))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        List<Pendiente> acreedores = resultado.valores().entrySet().stream()
                .filter(entrada -> entrada.getValue().compareTo(CERO) > 0)
                .map(entrada -> new Pendiente(entrada.getKey(), entrada.getValue()))
                .sorted(Comparator.comparingLong(p -> p.usuario().getId()))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        List<PagoLiquidacionRespuesta> pagos = new ArrayList<>();
        int indiceDeudor = 0;
        int indiceAcreedor = 0;
        while (indiceDeudor < deudores.size() && indiceAcreedor < acreedores.size()) {
            Pendiente deudor = deudores.get(indiceDeudor);
            Pendiente acreedor = acreedores.get(indiceAcreedor);
            BigDecimal importe = deudor.pendiente().min(acreedor.pendiente()).setScale(2, RoundingMode.UNNECESSARY);
            pagos.add(new PagoLiquidacionRespuesta(
                    deudor.usuario().getId(), deudor.usuario().getNombre(),
                    acreedor.usuario().getId(), acreedor.usuario().getNombre(), importe));
            deudor.restar(importe);
            acreedor.restar(importe);
            if (deudor.pendiente().compareTo(CERO) == 0) indiceDeudor++;
            if (acreedor.pendiente().compareTo(CERO) == 0) indiceAcreedor++;
        }
        return new LiquidacionRespuesta(desde, hasta, resultado.importeTotal(), pagos);
    }

    private BalancesRespuesta construirBalances(Membresia membresia, List<Gasto> gastos,
                                                LocalDate desde, LocalDate hasta) {
        BalancesResultado resultado = calcularValores(membresia, gastos);
        List<BalanceRespuesta> balances = resultado.valores().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparingLong(Usuario::getId)))
                .map(entrada -> {
                    Usuario usuario = entrada.getKey();
                    BigDecimal pagado = resultado.pagados().getOrDefault(usuario, CERO);
                    BigDecimal asumido = resultado.asumidos().getOrDefault(usuario, CERO);
                    return new BalanceRespuesta(usuario.getId(), usuario.getNombre(), pagado, asumido,
                            entrada.getValue());
                }).toList();
        return new BalancesRespuesta(desde, hasta, balances);
    }

    private BalancesResultado calcularValores(Membresia membresia, List<Gasto> gastos) {
        Map<Usuario, BigDecimal> pagados = new LinkedHashMap<>();
        Map<Usuario, BigDecimal> asumidos = new LinkedHashMap<>();
        for (Membresia integrante : membresiaRepository
                .findAllByPisoIdAndActivoTrueOrderByFechaAltaAsc(membresia.getPiso().getId())) {
            pagados.put(integrante.getUsuario(), CERO);
            asumidos.put(integrante.getUsuario(), CERO);
        }
        BigDecimal importeTotal = CERO;
        for (Gasto gasto : gastos) {
            importeTotal = importeTotal.add(gasto.getImporte()).setScale(2, RoundingMode.UNNECESSARY);
            pagados.merge(gasto.getPagador(), gasto.getImporte(), BigDecimal::add);
            for (ParticipanteGasto participante : gasto.getParticipantes()) {
                asumidos.merge(participante.getUsuario(), participante.getImporteAsignado(), BigDecimal::add);
            }
        }
        Map<Usuario, BigDecimal> valores = new LinkedHashMap<>();
        pagados.keySet().forEach(usuario -> valores.put(usuario,
                pagados.getOrDefault(usuario, CERO).subtract(asumidos.getOrDefault(usuario, CERO))
                        .setScale(2, RoundingMode.UNNECESSARY)));
        return new BalancesResultado(pagados, asumidos, valores, importeTotal);
    }

    private void validarIntervalo(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new DatosGastoException("La fecha inicial no puede ser posterior a la fecha final");
        }
    }

    private record BalancesResultado(Map<Usuario, BigDecimal> pagados, Map<Usuario, BigDecimal> asumidos,
                                     Map<Usuario, BigDecimal> valores, BigDecimal importeTotal) { }

    private static final class Pendiente {
        private final Usuario usuario;
        private BigDecimal pendiente;

        private Pendiente(Usuario usuario, BigDecimal pendiente) {
            this.usuario = usuario;
            this.pendiente = pendiente.setScale(2, RoundingMode.UNNECESSARY);
        }
        private Usuario usuario() { return usuario; }
        private BigDecimal pendiente() { return pendiente; }
        private void restar(BigDecimal importe) { pendiente = pendiente.subtract(importe).setScale(2, RoundingMode.UNNECESSARY); }
    }
}
