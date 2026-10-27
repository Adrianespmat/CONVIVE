package es.convive.backend;

import es.convive.backend.entity.Usuario;
import es.convive.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PruebaAutenticacionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder codificadorContrasenas;

    @Test
    void registraUsuarioContrasenaBCryptYProtegeIdentidad() throws Exception {
        String registro = """
                {"nombre":"Ana","correo":"Ana@Example.com","contrasena":"segura123"}
                """;

        String respuesta = mockMvc.perform(post("/api/autenticacion/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registro))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.usuario.correo").value("ana@example.com"))
                .andExpect(jsonPath("$.usuario.hashContrasena").doesNotExist())
                .andReturn().getResponse().getContentAsString();

        String token = com.jayway.jsonpath.JsonPath.read(respuesta, "$.token");
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase("ana@example.com").orElseThrow();
        assertThat(usuario.getHashContrasena()).isNotEqualTo("segura123");
        assertThat(codificadorContrasenas.matches("segura123", usuario.getHashContrasena())).isTrue();

        mockMvc.perform(get("/api/autenticacion/yo").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("ana@example.com"));
    }

    @Test
    void rechazaCorreoDuplicadoYCredencialesIncorrectas() throws Exception {
        String registro = """
                {"nombre":"Luis","correo":"luis@example.com","contrasena":"segura123"}
                """;
        mockMvc.perform(post("/api/autenticacion/registro").contentType(MediaType.APPLICATION_JSON).content(registro))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/autenticacion/registro").contentType(MediaType.APPLICATION_JSON).content(registro))
                .andExpect(status().isConflict());

        String loginIncorrecto = """
                {"correo":"luis@example.com","contrasena":"incorrecta"}
                """;
        mockMvc.perform(post("/api/autenticacion/login").contentType(MediaType.APPLICATION_JSON).content(loginIncorrecto))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rechazaAccesoProtegidoSinToken() throws Exception {
        mockMvc.perform(get("/api/autenticacion/yo"))
                .andExpect(status().isUnauthorized());
    }
}
