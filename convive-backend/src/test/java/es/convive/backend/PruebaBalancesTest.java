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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PruebaBalancesTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void calculaPagadoAsumidoBalanceYLiquidacionDeterminista() throws Exception {
        String tokenAdmin = registrar("admin-balances");
        String respuestaPiso = crearPiso(tokenAdmin);
        String codigo = JsonPath.read(respuestaPiso, "$.piso.codigoInvitacion");
        String idAdmin = JsonPath.read(respuestaPiso, "$.integrantes[0].usuarioId").toString();

        String tokenUno = registrar("miembro-balances-uno");
        String respuestaUno = unirse(tokenUno, codigo);
        String idUno = JsonPath.read(respuestaUno, "$.integrantes[1].usuarioId").toString();
        String tokenDos = registrar("miembro-balances-dos");
        String respuestaDos = unirse(tokenDos, codigo);
        String idDos = JsonPath.read(respuestaDos, "$.integrantes[2].usuarioId").toString();

        crearGasto(tokenAdmin, idAdmin, idAdmin + "," + idUno + "," + idDos, "10.00", "2026-10-01");
        crearGasto(tokenUno, idUno, idAdmin + "," + idUno + "," + idDos, "4.00", "2026-10-15");

        mockMvc.perform(get("/api/balances")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.desde").doesNotExist())
                .andExpect(jsonPath("$.hasta").doesNotExist())
                .andExpect(jsonPath("$.balances.length()").value(3))
                .andExpect(jsonPath("$.balances[0].importePagado").value(10.00))
                .andExpect(jsonPath("$.balances[0].importeAsumido").value(4.68))
                .andExpect(jsonPath("$.balances[0].balance").value(5.32))
                .andExpect(jsonPath("$.balances[1].importePagado").value(4.00))
                .andExpect(jsonPath("$.balances[1].importeAsumido").value(4.66))
                .andExpect(jsonPath("$.balances[1].balance").value(-0.66))
                .andExpect(jsonPath("$.balances[2].importePagado").value(0.00))
                .andExpect(jsonPath("$.balances[2].importeAsumido").value(4.66))
                .andExpect(jsonPath("$.balances[2].balance").value(-4.66));

        mockMvc.perform(get("/api/balances/liquidacion")
                        .header("Authorization", "Bearer " + tokenUno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.importeTotal").value(14.00))
                .andExpect(jsonPath("$.pagos.length()").value(2))
                .andExpect(jsonPath("$.pagos[0].deudorUsuarioId").value(Integer.parseInt(idUno)))
                .andExpect(jsonPath("$.pagos[0].acreedorUsuarioId").value(Integer.parseInt(idAdmin)))
                .andExpect(jsonPath("$.pagos[0].importe").value(0.66))
                .andExpect(jsonPath("$.pagos[1].deudorUsuarioId").value(Integer.parseInt(idDos)))
                .andExpect(jsonPath("$.pagos[1].acreedorUsuarioId").value(Integer.parseInt(idAdmin)))
                .andExpect(jsonPath("$.pagos[1].importe").value(4.66));
    }

    @Test
    void aplicaPeriodoYRechazaIntervaloInvalido() throws Exception {
        String token = registrar("admin-periodo");
        String respuestaPiso = crearPiso(token);
        String id = JsonPath.read(respuestaPiso, "$.integrantes[0].usuarioId").toString();
        crearGasto(token, id, id, "7.00", "2026-09-01");
        crearGasto(token, id, id, "3.00", "2026-10-01");

        mockMvc.perform(get("/api/balances")
                        .header("Authorization", "Bearer " + token)
                        .param("desde", "2026-10-01")
                        .param("hasta", "2026-10-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.desde").value("2026-10-01"))
                .andExpect(jsonPath("$.hasta").value("2026-10-31"))
                .andExpect(jsonPath("$.balances[0].importePagado").value(3.00));

        mockMvc.perform(get("/api/balances/liquidacion")
                        .header("Authorization", "Bearer " + token)
                        .param("desde", "2026-12-01")
                        .param("hasta", "2026-11-01"))
                .andExpect(status().isBadRequest());
    }

    private String registrar(String prefijo) throws Exception {
        String correo = prefijo + "+" + UUID.randomUUID() + "@example.com";
        String respuesta = mockMvc.perform(post("/api/autenticacion/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"" + prefijo + "\",\"correo\":\"" + correo + "\",\"contrasena\":\"segura123\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.token");
    }

    private String crearPiso(String token) throws Exception {
        return mockMvc.perform(post("/api/pisos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Piso balances\",\"direccion\":\"Calle balances 1\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    }

    private String unirse(String token, String codigo) throws Exception {
        return mockMvc.perform(post("/api/pisos/unirse")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoInvitacion\":\"" + codigo + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    private void crearGasto(String token, String pagador, String participantes,
                            String importe, String fecha) throws Exception {
        mockMvc.perform(post("/api/gastos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"concepto\":\"Gasto balance\",\"importe\":\"" + importe
                                + "\",\"fecha\":\"" + fecha + "\",\"categoria\":\"OTROS\",\"pagadorUsuarioId\":"
                                + pagador + ",\"participantesUsuarioIds\":[" + participantes + "]}"))
                .andExpect(status().isCreated());
    }
}
