package es.convive.backend.dto;

import java.math.BigDecimal;

public record PagoLiquidacionRespuesta(
        Long deudorUsuarioId,
        String deudorNombre,
        Long acreedorUsuarioId,
        String acreedorNombre,
        BigDecimal importe
) { }
