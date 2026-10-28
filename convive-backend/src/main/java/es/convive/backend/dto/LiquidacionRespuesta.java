package es.convive.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record LiquidacionRespuesta(
        LocalDate desde,
        LocalDate hasta,
        BigDecimal importeTotal,
        List<PagoLiquidacionRespuesta> pagos
) { }
