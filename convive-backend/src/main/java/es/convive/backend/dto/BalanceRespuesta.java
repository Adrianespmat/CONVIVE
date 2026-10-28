package es.convive.backend.dto;

import java.math.BigDecimal;

public record BalanceRespuesta(
        Long usuarioId,
        String nombre,
        BigDecimal importePagado,
        BigDecimal importeAsumido,
        BigDecimal balance
) { }
