package es.convive.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearPisoSolicitud(
        @NotBlank(message = "El nombre del piso es obligatorio")
        @Size(max = 120, message = "El nombre del piso no puede superar los 120 caracteres")
        String nombre,
        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
        String direccion
) { }
