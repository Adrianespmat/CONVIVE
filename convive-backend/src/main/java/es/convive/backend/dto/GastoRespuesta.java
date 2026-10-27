package es.convive.backend.dto;

import es.convive.backend.entity.Gasto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record GastoRespuesta(
        Long id,
        Long pisoId,
        Long creadoPorUsuarioId,
        Long pagadorUsuarioId,
        String pagadorNombre,
        String concepto,
        BigDecimal importe,
        LocalDate fecha,
        String categoria,
        Instant fechaCreacion,
        Instant fechaActualizacion,
        List<ParticipanteGastoRespuesta> participantes
) {
    public static GastoRespuesta desde(Gasto gasto) {
        return new GastoRespuesta(
                gasto.getId(),
                gasto.getPiso().getId(),
                gasto.getCreadoPor().getId(),
                gasto.getPagador().getId(),
                gasto.getPagador().getNombre(),
                gasto.getConcepto(),
                gasto.getImporte(),
                gasto.getFecha(),
                gasto.getCategoria().name(),
                gasto.getFechaCreacion(),
                gasto.getFechaActualizacion(),
                gasto.getParticipantes().stream().map(ParticipanteGastoRespuesta::desde).toList());
    }
}
