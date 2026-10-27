package es.convive.backend.service;

import es.convive.backend.dto.PisoDetalleRespuesta;
import es.convive.backend.dto.UnirsePisoSolicitud;
import es.convive.backend.entity.Membresia;
import es.convive.backend.entity.Piso;
import es.convive.backend.entity.Rol;
import es.convive.backend.entity.Usuario;
import es.convive.backend.exception.AccesoDenegadoException;
import es.convive.backend.exception.ConflictoException;
import es.convive.backend.exception.RecursoNoEncontradoException;
import es.convive.backend.repository.MembresiaRepository;
import es.convive.backend.repository.PisoRepository;
import es.convive.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class ServicioMembresias {
    private final MembresiaRepository membresiaRepository;
    private final PisoRepository pisoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioPisos servicioPisos;

    public ServicioMembresias(MembresiaRepository membresiaRepository, PisoRepository pisoRepository,
                              UsuarioRepository usuarioRepository, ServicioPisos servicioPisos) {
        this.membresiaRepository = membresiaRepository;
        this.pisoRepository = pisoRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioPisos = servicioPisos;
    }

    @Transactional
    public PisoDetalleRespuesta unirse(Long usuarioId, UnirsePisoSolicitud solicitud) {
        if (membresiaRepository.existsByUsuarioIdAndActivoTrue(usuarioId)) {
            throw new ConflictoException("El usuario ya pertenece a un piso activo");
        }
        String codigo = solicitud.codigoInvitacion().trim().toUpperCase(Locale.ROOT);
        Piso piso = pisoRepository.findByCodigoInvitacionAndActivoTrue(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El código de invitación no es válido"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario autenticado no existe"));
        membresiaRepository.save(new Membresia(usuario, piso, Rol.MIEMBRO));
        return servicioPisos.obtenerPisoActual(usuarioId);
    }

    @Transactional
    public void darDeBaja(Long administradorId, Long usuarioId) {
        Membresia administrador = servicioPisos.obtenerAdministrador(administradorId);
        if (administrador.getUsuario().getId().equals(usuarioId)) {
            throw new AccesoDenegadoException("El administrador no puede darse de baja a sí mismo");
        }
        Membresia objetivo = membresiaRepository.findByUsuarioIdAndPisoIdAndActivoTrue(
                        usuarioId, administrador.getPiso().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no pertenece a este piso"));
        if (objetivo.getRol() == Rol.ADMINISTRADOR
                && membresiaRepository.countByPisoIdAndRolAndActivoTrue(administrador.getPiso().getId(), Rol.ADMINISTRADOR) <= 1) {
            throw new ConflictoException("El piso debe conservar al menos un administrador");
        }
        objetivo.setActivo(false);
        membresiaRepository.save(objetivo);
    }
}
