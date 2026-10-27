package es.convive.backend.dto;

import es.convive.backend.entity.Membresia;

import java.time.LocalDate;

public record MiembroRespuesta(Long usuarioId, String nombre, String correo, String rol, LocalDate fechaAlta) {
    public static MiembroRespuesta desde(Membresia membresia) {
        return new MiembroRespuesta(
                membresia.getUsuario().getId(),
                membresia.getUsuario().getNombre(),
                membresia.getUsuario().getCorreo(),
                membresia.getRol().name(),
                membresia.getFechaAlta());
    }
}
