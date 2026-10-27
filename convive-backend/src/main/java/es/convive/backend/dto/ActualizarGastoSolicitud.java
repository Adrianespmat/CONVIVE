package es.convive.backend.dto;

import es.convive.backend.entity.CategoriaGasto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ActualizarGastoSolicitud(
        String concepto,
        BigDecimal importe,
        LocalDate fecha,
        CategoriaGasto categoria,
        Long pagadorUsuarioId,
        List<Long> participantesUsuarioIds
) { }
