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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PruebaPerfilPanelTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void consultaYActualizaSoloElNombreDelPerfil() throws Exception {
        String correo = "perfil+" + UUID.randomUUID() + "@example.com";
        String token = registrar("Nombre inicial", correo);

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nombre inicial"))
                .andExpect(jsonPath("$.correo").value(correo));

        mockMvc.perform(patch("/api/users/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"  Nombre actualizado  \"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nombre actualizado"))
                .andExpect(jsonPath("$.correo").value(correo));

        mockMvc.perform(patch("/api/users/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"   \"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void panelCalculaTotalUltimosGastosBalancePersonalYPeriodo() throws Exception {
        String token = registrar("Administrador panel", "panel+" + UUID.randomUUID() + "@example.com");
        String respuestaPiso = mockMvc.perform(post("/api/pisos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Piso panel\",\"direccion\":\"Calle panel 1\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String usuarioId = JsonPath.read(respuestaPiso, "$.integrantes[0].usuarioId").toString();

        crearGasto(token, usuarioId, "30.00", "2026-09-15");
        crearGasto(token, usuarioId, "20.00", "2026-10-15");

        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalGastos").value(50.00))
                .andExpect(jsonPath("$.ultimosGastos.length()").value(2))
                .andExpect(jsonPath("$.ultimosGastos[0].importe").value(20.00))
                .andExpect(jsonPath("$.ultimosGastos[1].importe").value(30.00))
                .andExpect(jsonPath("$.balancePersonal.importePagado").value(50.00))
                .andExpect(jsonPath("$.balancePersonal.importeAsumido").value(50.00))
                .andExpect(jsonPath("$.balancePersonal.balance").value(0.00));

        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", "Bearer " + token)
                        .param("desde", "2026-10-01")
                        .param("hasta", "2026-10-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalGastos").value(20.00))
                .andExpect(jsonPath("$.ultimosGastos.length()").value(1))
                .andExpect(jsonPath("$.desde").value("2026-10-01"))
                .andExpect(jsonPath("$.hasta").value("2026-10-31"));

        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", "Bearer " + token)
                        .param("desde", "2026-12-01")
                        .param("hasta", "2026-11-01"))
                .andExpect(status().isBadRequest());
    }

    private String registrar(String nombre, String correo) throws Exception {
        String respuesta = mockMvc.perform(post("/api/autenticacion/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"" + nombre + "\",\"correo\":\"" + correo
                                + "\",\"contrasena\":\"segura123\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.token");
    }

    private void crearGasto(String token, String usuarioId, String importe, String fecha) throws Exception {
        mockMvc.perform(post("/api/gastos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"concepto\":\"Gasto del panel\",\"importe\":\"" + importe
                                + "\",\"fecha\":\"" + fecha + "\",\"categoria\":\"OTROS\",\"pagadorUsuarioId\":"
                                + usuarioId + ",\"participantesUsuarioIds\":[" + usuarioId + "]}"))
                .andExpect(status().isCreated());
    }
}
