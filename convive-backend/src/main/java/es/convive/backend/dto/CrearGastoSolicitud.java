package es.convive.backend.dto;

import es.convive.backend.entity.CategoriaGasto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CrearGastoSolicitud(
        @NotBlank(message = "El concepto es obligatorio")
        @Size(max = 180, message = "El concepto no puede superar los 180 caracteres")
        String concepto,
        @NotNull(message = "El importe es obligatorio")
        @DecimalMin(value = "0.01", message = "El importe debe ser mayor que cero")
        @Digits(integer = 10, fraction = 2, message = "El importe debe tener como máximo dos decimales")
        BigDecimal importe,
        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,
        @NotNull(message = "La categoría es obligatoria")
        CategoriaGasto categoria,
        @NotNull(message = "El pagador es obligatorio")
        Long pagadorUsuarioId,
        @NotEmpty(message = "Debe existir al menos un participante")
        List<Long> participantesUsuarioIds
) { }
