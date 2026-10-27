package es.convive.backend.dto;

import es.convive.backend.entity.ParticipanteGasto;

import java.math.BigDecimal;

public record ParticipanteGastoRespuesta(Long usuarioId, String nombre, BigDecimal importeAsignado) {
    public static ParticipanteGastoRespuesta desde(ParticipanteGasto participante) {
        return new ParticipanteGastoRespuesta(
                participante.getUsuario().getId(),
                participante.getUsuario().getNombre(),
                participante.getImporteAsignado());
    }
}
