package es.convive.backend;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PruebaPisosMembresiasTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void creaPisoUneMiembroYAplicaPermisos() throws Exception {
        String correoAdministrador = correoUnico("admin");
        String tokenAdministrador = registrar(correoAdministrador, "Admin");
        String respuestaCreacion = mockMvc.perform(post("/api/pisos")
                        .header("Authorization", "Bearer " + tokenAdministrador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Piso Centro\",\"direccion\":\"Calle Mayor 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.piso.administrador").value(true))
                .andExpect(jsonPath("$.piso.codigoInvitacion").isString())
                .andExpect(jsonPath("$.integrantes[0].rol").value("ADMINISTRADOR"))
                .andReturn().getResponse().getContentAsString();
        String codigo = JsonPath.read(respuestaCreacion, "$.piso.codigoInvitacion");

        String tokenMiembro = registrar(correoUnico("miembro"), "Miembro");
        String respuestaUnion = mockMvc.perform(post("/api/pisos/unirse")
                        .header("Authorization", "Bearer " + tokenMiembro)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoInvitacion\":\"" + codigo.toLowerCase() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.piso.administrador").value(false))
                .andExpect(jsonPath("$.piso.codigoInvitacion").doesNotExist())
                .andExpect(jsonPath("$.integrantes.length()").value(2))
                .andReturn().getResponse().getContentAsString();
        Number idAdministradorNumero = JsonPath.read(respuestaUnion, "$.integrantes[0].usuarioId");
        Long idAdministrador = idAdministradorNumero.longValue();

        mockMvc.perform(patch("/api/pisos/mio")
                        .header("Authorization", "Bearer " + tokenMiembro)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Cambio no permitido\",\"direccion\":\"Otra\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/pisos/mio/integrantes/" + idAdministrador)
                        .header("Authorization", "Bearer " + tokenMiembro))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/pisos")
                        .header("Authorization", "Bearer " + tokenAdministrador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Segundo piso\",\"direccion\":\"Otra dirección\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void rechazaCodigoInvalidoYSoloElAdministradorPuedeDarDeBaja() throws Exception {
        String tokenAdministrador = registrar(correoUnico("admin2"), "Admin dos");
        String tokenMiembro = registrar(correoUnico("miembro2"), "Miembro dos");

        mockMvc.perform(post("/api/pisos/unirse")
                        .header("Authorization", "Bearer " + tokenMiembro)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoInvitacion\":\"NO-EXISTE\"}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/pisos")
                        .header("Authorization", "Bearer " + tokenAdministrador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Piso Dos\",\"direccion\":\"Calle Dos\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void usuarioSinPisoNoPuedeConsultarIntegrantes() throws Exception {
        String token = registrar(correoUnico("sinpiso"), "Sin piso");
        mockMvc.perform(get("/api/pisos/mio/integrantes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    private String registrar(String correo, String nombre) throws Exception {
        String respuesta = mockMvc.perform(post("/api/autenticacion/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"" + nombre + "\",\"correo\":\"" + correo + "\",\"contrasena\":\"segura123\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.token");
    }

    private String correoUnico(String prefijo) {
        return prefijo + "+" + UUID.randomUUID() + "@example.com";
    }
}
