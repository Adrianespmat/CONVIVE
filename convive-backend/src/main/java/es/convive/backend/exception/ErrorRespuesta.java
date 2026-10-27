package es.convive.backend.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorRespuesta(Instant fecha, int estado, String error, String mensaje, String ruta, Map<String, String> campos) {
    public ErrorRespuesta(Instant fecha, int estado, String error, String mensaje, String ruta) {
        this(fecha, estado, error, mensaje, ruta, Map.of());
    }
}
