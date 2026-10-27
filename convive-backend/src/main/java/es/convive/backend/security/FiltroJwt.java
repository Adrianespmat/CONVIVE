package es.convive.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FiltroJwt extends OncePerRequestFilter {
    private final ServicioJwt servicioJwt;
    private final ServicioDetallesUsuario servicioDetallesUsuario;

    public FiltroJwt(ServicioJwt servicioJwt, ServicioDetallesUsuario servicioDetallesUsuario) {
        this.servicioJwt = servicioJwt;
        this.servicioDetallesUsuario = servicioDetallesUsuario;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest solicitud,
                                    @NonNull HttpServletResponse respuesta,
                                    @NonNull FilterChain cadena) throws ServletException, IOException {
        String cabecera = solicitud.getHeader("Authorization");
        if (cabecera == null || !cabecera.startsWith("Bearer ")) {
            cadena.doFilter(solicitud, respuesta);
            return;
        }
        String token = cabecera.substring(7);
        try {
            String correo = servicioJwt.extraerCorreo(token);
            if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails detalles = servicioDetallesUsuario.loadUserByUsername(correo);
                if (servicioJwt.esValido(token, detalles)) {
                    UsernamePasswordAuthenticationToken autenticacion = new UsernamePasswordAuthenticationToken(detalles, null, detalles.getAuthorities());
                    autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(solicitud));
                    SecurityContextHolder.getContext().setAuthentication(autenticacion);
                }
            }
        } catch (RuntimeException ignored) {
            // El endpoint protegido devolverá 401; no se exponen detalles del token.
        }
        cadena.doFilter(solicitud, respuesta);
    }
}
