package es.convive.backend;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PruebaGastosTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void reparteCuentoExactoYDeterministaEnCentimos() throws Exception {
        String tokenAdmin = registrar("admin-reparto");
        String respuestaPiso = crearPiso(tokenAdmin);
        String codigo = JsonPath.read(respuestaPiso, "$.piso.codigoInvitacion");
        Number idAdminNumero = JsonPath.read(respuestaPiso, "$.integrantes[0].usuarioId");
        Long idAdmin = idAdminNumero.longValue();

        String tokenDos = registrar("miembro-reparto-1");
        String respuestaDos = unirse(tokenDos, codigo);
        Number idDosNumero = JsonPath.read(respuestaDos, "$.integrantes[1].usuarioId");
        Long idDos = idDosNumero.longValue();

        String tokenTres = registrar("miembro-reparto-2");
        String respuestaTres = unirse(tokenTres, codigo);
        Number idTresNumero = JsonPath.read(respuestaTres, "$.integrantes[2].usuarioId");
        Long idTres = idTresNumero.longValue();

        String respuestaGasto = mockMvc.perform(post("/api/gastos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"concepto\":\"Compra semanal\",\"importe\":\"10.00\",\"fecha\":\"2026-10-27\",\"categoria\":\"ALIMENTACION\",\"pagadorUsuarioId\":" + idAdmin + ",\"participantesUsuarioIds\":[" + idTres + "," + idAdmin + "," + idDos + "]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.importe").value(10.00))
                .andExpect(jsonPath("$.participantes.length()").value(3))
                .andReturn().getResponse().getContentAsString();

        List<?> valoresImportes = JsonPath.parse(respuestaGasto)
                .read("$.participantes[*].importeAsignado", List.class);
        List<BigDecimal> importes = valoresImportes.stream()
                .map(valor -> new BigDecimal(valor.toString()))
                .toList();
        assertThat(importes).containsExactly(new BigDecimal("3.34"), new BigDecimal("3.33"), new BigDecimal("3.33"));
        assertThat(importes.stream().reduce(BigDecimal.ZERO, BigDecimal::add))
                .isEqualByComparingTo("10.00");
    }

    @Test
    void soloElCreadorPuedeModificarYEliminarElGasto() throws Exception {
        String tokenAdmin = registrar("admin-propietario");
        String respuestaPiso = crearPiso(tokenAdmin);
        String idAdmin = JsonPath.read(respuestaPiso, "$.integrantes[0].usuarioId").toString();
        String tokenMiembro = registrar("miembro-propietario");
        String codigo = JsonPath.read(respuestaPiso, "$.piso.codigoInvitacion");
        String respuestaUnion = unirse(tokenMiembro, codigo);
        String idMiembro = JsonPath.read(respuestaUnion, "$.integrantes[1].usuarioId").toString();

        String respuestaGasto = mockMvc.perform(post("/api/gastos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gastoJson(idAdmin, idAdmin, "5.00", "2026-10-26", "AGUA")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String gastoId = JsonPath.read(respuestaGasto, "$.id").toString();

        mockMvc.perform(put("/api/gastos/" + gastoId)
                        .header("Authorization", "Bearer " + tokenMiembro)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gastoJson(idAdmin, idMiembro, "6.00", "2026-10-26", "AGUA")))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/gastos/" + gastoId)
                        .header("Authorization", "Bearer " + tokenMiembro))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/gastos/" + gastoId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gastoJson(idAdmin, idAdmin, "6.00", "2026-10-27", "AGUA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.importe").value(6.00))
                .andExpect(jsonPath("$.participantes[0].importeAsignado").value(6.00));
        mockMvc.perform(delete("/api/gastos/" + gastoId)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    @Test
    void aplicaFiltrosYRechazaParticipantesExternosEIntervalosInvalidos() throws Exception {
        String tokenAdmin = registrar("admin-filtros");
        String respuestaPiso = crearPiso(tokenAdmin);
        String idAdmin = JsonPath.read(respuestaPiso, "$.integrantes[0].usuarioId").toString();
        String respuestaGasto = mockMvc.perform(post("/api/gastos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gastoJson(idAdmin, idAdmin, "2.50", "2026-10-01", "INTERNET")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String gastoId = JsonPath.read(respuestaGasto, "$.id").toString();

        mockMvc.perform(get("/api/gastos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("desde", "2026-10-01")
                        .param("hasta", "2026-10-31")
                        .param("categoria", "INTERNET"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(Integer.parseInt(gastoId)));
        mockMvc.perform(get("/api/gastos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("desde", "2026-11-01")
                        .param("hasta", "2026-10-01"))
                .andExpect(status().isBadRequest());

        String tokenExterno = registrar("externo-filtros");
        String idExterno = JsonPath.read(mockMvc.perform(post("/api/pisos")
                                .header("Authorization", "Bearer " + tokenExterno)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"nombre\":\"Otro piso\",\"direccion\":\"Otra calle\"}"))
                        .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), "$.integrantes[0].usuarioId").toString();
        mockMvc.perform(post("/api/gastos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gastoJson(idAdmin, idExterno, "3.00", "2026-10-02", "OTROS")))
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
                        .content("{\"nombre\":\"Piso de pruebas\",\"direccion\":\"Calle de pruebas 1\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    }

    private String unirse(String token, String codigo) throws Exception {
        return mockMvc.perform(post("/api/pisos/unirse")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoInvitacion\":\"" + codigo + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    private String gastoJson(String pagadorId, String participanteId, String importe, String fecha, String categoria) {
        return "{\"concepto\":\"Gasto de prueba\",\"importe\":\"" + importe + "\",\"fecha\":\"" + fecha
                + "\",\"categoria\":\"" + categoria + "\",\"pagadorUsuarioId\":" + pagadorId
                + ",\"participantesUsuarioIds\":[" + participanteId + "]}";
    }
}
