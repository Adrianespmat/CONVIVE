package es.convive.backend.dto;

public record AutenticacionRespuesta(String token, String tipo, UsuarioRespuesta usuario) { }
