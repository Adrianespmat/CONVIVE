package es.convive.backend.dto;

import java.util.List;

public record PisoDetalleRespuesta(PisoRespuesta piso, List<MiembroRespuesta> integrantes) { }
