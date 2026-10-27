package es.convive.backend.dto;

import es.convive.backend.entity.Usuario;

public record UsuarioRespuesta(Long id, String nombre, String correo) {

    public static UsuarioRespuesta desde(Usuario usuario) {
        return new UsuarioRespuesta(usuario.getId(), usuario.getNombre(), usuario.getCorreo());
    }
}
