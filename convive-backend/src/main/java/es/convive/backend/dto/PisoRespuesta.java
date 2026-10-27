package es.convive.backend.dto;

import es.convive.backend.entity.Piso;

import java.time.Instant;

public record PisoRespuesta(Long id, String nombre, String direccion, String codigoInvitacion,
                            Instant fechaCreacion, boolean administrador) {
    public static PisoRespuesta desde(Piso piso, boolean administrador) {
        return new PisoRespuesta(
                piso.getId(),
                piso.getNombre(),
                piso.getDireccion(),
                administrador ? piso.getCodigoInvitacion() : null,
                piso.getFechaCreacion(),
                administrador);
    }
}
