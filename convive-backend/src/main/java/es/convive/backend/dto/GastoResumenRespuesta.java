package es.convive.backend.dto;

import es.convive.backend.entity.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GastoResumenRespuesta(
        Long id,
        String concepto,
        BigDecimal importe,
        LocalDate fecha,
        String categoria,
        Long pagadorUsuarioId,
        String pagadorNombre
) {
    public static GastoResumenRespuesta desde(Gasto gasto) {
        return new GastoResumenRespuesta(
                gasto.getId(), gasto.getConcepto(), gasto.getImporte(), gasto.getFecha(),
                gasto.getCategoria().name(), gasto.getPagador().getId(), gasto.getPagador().getNombre());
    }
}
