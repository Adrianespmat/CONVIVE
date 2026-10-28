package es.convive.backend.service;

import es.convive.backend.dto.ActualizarPerfilSolicitud;
import es.convive.backend.dto.UsuarioRespuesta;
import es.convive.backend.entity.Usuario;
import es.convive.backend.exception.RecursoNoEncontradoException;
import es.convive.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioPerfil {
    private final UsuarioRepository usuarioRepository;

    public ServicioPerfil(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public UsuarioRespuesta obtener(Long usuarioId) {
        return UsuarioRespuesta.desde(obtenerUsuario(usuarioId));
    }

    @Transactional
    public UsuarioRespuesta actualizar(Long usuarioId, ActualizarPerfilSolicitud solicitud) {
        Usuario usuario = obtenerUsuario(usuarioId);
        usuario.setNombre(solicitud.nombre().trim());
        return UsuarioRespuesta.desde(usuarioRepository.save(usuario));
    }

    private Usuario obtenerUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario autenticado no existe"));
    }
}
