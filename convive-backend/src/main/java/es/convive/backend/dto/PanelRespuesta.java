package es.convive.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PanelRespuesta(
        LocalDate desde,
        LocalDate hasta,
        BigDecimal totalGastos,
        List<GastoResumenRespuesta> ultimosGastos,
        BalanceRespuesta balancePersonal
) { }
