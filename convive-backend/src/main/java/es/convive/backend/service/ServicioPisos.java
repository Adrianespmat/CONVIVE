package es.convive.backend.service;

import es.convive.backend.dto.ActualizarPisoSolicitud;
import es.convive.backend.dto.CrearPisoSolicitud;
import es.convive.backend.dto.MiembroRespuesta;
import es.convive.backend.dto.PisoDetalleRespuesta;
import es.convive.backend.dto.PisoRespuesta;
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

import java.security.SecureRandom;
import java.util.List;

@Service
public class ServicioPisos {
    private static final String CARACTERES_CODIGO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LONGITUD_CODIGO = 10;
    private final SecureRandom aleatorio = new SecureRandom();

    private final PisoRepository pisoRepository;
    private final MembresiaRepository membresiaRepository;
    private final UsuarioRepository usuarioRepository;

    public ServicioPisos(PisoRepository pisoRepository, MembresiaRepository membresiaRepository,
                         UsuarioRepository usuarioRepository) {
        this.pisoRepository = pisoRepository;
        this.membresiaRepository = membresiaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public PisoDetalleRespuesta crear(Long usuarioId, CrearPisoSolicitud solicitud) {
        comprobarSinPisoActivo(usuarioId);
        Usuario usuario = obtenerUsuario(usuarioId);

        Piso piso = new Piso();
        piso.setNombre(solicitud.nombre().trim());
        piso.setDireccion(solicitud.direccion().trim());
        piso.setCodigoInvitacion(generarCodigoUnico());
        Piso guardado = pisoRepository.save(piso);
        membresiaRepository.save(new Membresia(usuario, guardado, Rol.ADMINISTRADOR));
        return detalle(guardado, true);
    }

    @Transactional(readOnly = true)
    public PisoDetalleRespuesta obtenerPisoActual(Long usuarioId) {
        Membresia membresia = obtenerMembresiaActiva(usuarioId);
        return detalle(membresia.getPiso(), membresia.getRol() == Rol.ADMINISTRADOR);
    }

    @Transactional
    public PisoDetalleRespuesta actualizar(Long usuarioId, ActualizarPisoSolicitud solicitud) {
        Membresia membresia = obtenerAdministrador(usuarioId);
        Piso piso = membresia.getPiso();
        piso.setNombre(solicitud.nombre().trim());
        piso.setDireccion(solicitud.direccion().trim());
        return detalle(piso, true);
    }

    @Transactional(readOnly = true)
    public List<MiembroRespuesta> listarIntegrantes(Long usuarioId) {
        Membresia membresia = obtenerMembresiaActiva(usuarioId);
        return membresiaRepository.findAllByPisoIdAndActivoTrueOrderByFechaAltaAsc(membresia.getPiso().getId())
                .stream().map(MiembroRespuesta::desde).toList();
    }

    public Membresia obtenerMembresiaActiva(Long usuarioId) {
        return membresiaRepository.findByUsuarioIdAndActivoTrue(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no pertenece a ningún piso activo"));
    }

    public Membresia obtenerAdministrador(Long usuarioId) {
        Membresia membresia = obtenerMembresiaActiva(usuarioId);
        if (membresia.getRol() != Rol.ADMINISTRADOR) {
            throw new AccesoDenegadoException("Solo el administrador puede realizar esta operación");
        }
        return membresia;
    }

    private void comprobarSinPisoActivo(Long usuarioId) {
        if (membresiaRepository.existsByUsuarioIdAndActivoTrue(usuarioId)) {
            throw new ConflictoException("El usuario ya pertenece a un piso activo");
        }
    }

    private Usuario obtenerUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario autenticado no existe"));
    }

    private PisoDetalleRespuesta detalle(Piso piso, boolean administrador) {
        List<MiembroRespuesta> integrantes = membresiaRepository
                .findAllByPisoIdAndActivoTrueOrderByFechaAltaAsc(piso.getId())
                .stream().map(MiembroRespuesta::desde).toList();
        return new PisoDetalleRespuesta(PisoRespuesta.desde(piso, administrador), integrantes);
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            StringBuilder resultado = new StringBuilder(LONGITUD_CODIGO);
            for (int i = 0; i < LONGITUD_CODIGO; i++) {
                resultado.append(CARACTERES_CODIGO.charAt(aleatorio.nextInt(CARACTERES_CODIGO.length())));
            }
            codigo = resultado.toString();
        } while (pisoRepository.existsByCodigoInvitacion(codigo));
        return codigo;
    }
}
