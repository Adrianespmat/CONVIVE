package es.convive.backend.service;

import es.convive.backend.dto.AutenticacionRespuesta;
import es.convive.backend.dto.InicioSesionSolicitud;
import es.convive.backend.dto.RegistroSolicitud;
import es.convive.backend.dto.UsuarioRespuesta;
import es.convive.backend.entity.Usuario;
import es.convive.backend.exception.ConflictoException;
import es.convive.backend.repository.UsuarioRepository;
import es.convive.backend.security.DetallesUsuario;
import es.convive.backend.security.ServicioJwt;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class ServicioAutenticacion {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder codificadorContrasenas;
    private final AuthenticationManager gestorAutenticacion;
    private final ServicioJwt servicioJwt;

    public ServicioAutenticacion(UsuarioRepository usuarioRepository, PasswordEncoder codificadorContrasenas,
                                 AuthenticationManager gestorAutenticacion, ServicioJwt servicioJwt) {
        this.usuarioRepository = usuarioRepository;
        this.codificadorContrasenas = codificadorContrasenas;
        this.gestorAutenticacion = gestorAutenticacion;
        this.servicioJwt = servicioJwt;
    }

    @Transactional
    public AutenticacionRespuesta registrar(RegistroSolicitud solicitud) {
        String correo = normalizarCorreo(solicitud.correo());
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictoException("Ya existe un usuario con ese correo");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(solicitud.nombre().trim());
        usuario.setCorreo(correo);
        usuario.setHashContrasena(codificadorContrasenas.encode(solicitud.contrasena()));
        Usuario guardado = usuarioRepository.save(usuario);
        DetallesUsuario detalles = new DetallesUsuario(guardado);
        return new AutenticacionRespuesta(servicioJwt.generarToken(detalles), "Bearer", UsuarioRespuesta.desde(guardado));
    }

    public AutenticacionRespuesta iniciarSesion(InicioSesionSolicitud solicitud) {
        String correo = normalizarCorreo(solicitud.correo());
        Authentication autenticacion = gestorAutenticacion.authenticate(
                new UsernamePasswordAuthenticationToken(correo, solicitud.contrasena()));
        DetallesUsuario detalles = (DetallesUsuario) autenticacion.getPrincipal();
        return new AutenticacionRespuesta(servicioJwt.generarToken(detalles), "Bearer", UsuarioRespuesta.desde(detalles.getUsuario()));
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }
}
