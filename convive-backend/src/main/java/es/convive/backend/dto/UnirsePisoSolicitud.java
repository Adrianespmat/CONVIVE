package es.convive.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UnirsePisoSolicitud(
        @NotBlank(message = "El código de invitación es obligatorio")
        @Size(max = 32, message = "El código de invitación no es válido")
        String codigoInvitacion
) { }
