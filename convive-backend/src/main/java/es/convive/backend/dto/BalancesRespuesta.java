package es.convive.backend.dto;

import java.time.LocalDate;
import java.util.List;

public record BalancesRespuesta(
        LocalDate desde,
        LocalDate hasta,
        List<BalanceRespuesta> balances
) { }
