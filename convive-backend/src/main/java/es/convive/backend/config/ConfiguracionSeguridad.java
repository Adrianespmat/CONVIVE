package es.convive.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.convive.backend.security.FiltroJwt;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
public class ConfiguracionSeguridad {
    private final FiltroJwt filtroJwt;
    private final ObjectMapper objectMapper;

    public ConfiguracionSeguridad(FiltroJwt filtroJwt, ObjectMapper objectMapper) {
        this.filtroJwt = filtroJwt;
        this.objectMapper = objectMapper;
    }

    @Bean
    PasswordEncoder codificadorContrasenas() { return new BCryptPasswordEncoder(); }

    @Bean
    AuthenticationManager gestorAutenticacion(AuthenticationConfiguration configuracion) throws Exception {
        return configuracion.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain cadenaSeguridad(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> { })
                .sessionManagement(sesiones -> sesiones.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(autorizacion -> autorizacion
                        .requestMatchers("/api/autenticacion/registro", "/api/autenticacion/login", "/error").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint((solicitud, respuesta, ex) -> escribirError(respuesta, 401, "No autenticado"))
                        .accessDeniedHandler((solicitud, respuesta, ex) -> escribirError(respuesta, 403, "No tienes permisos para esta operación")))
                .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private void escribirError(HttpServletResponse respuesta, int estado, String mensaje) throws java.io.IOException {
        respuesta.setStatus(estado);
        respuesta.setCharacterEncoding(StandardCharsets.UTF_8.name());
        respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        respuesta.getWriter().write(objectMapper.writeValueAsString(Map.of("estado", estado, "mensaje", mensaje)));
    }
}
