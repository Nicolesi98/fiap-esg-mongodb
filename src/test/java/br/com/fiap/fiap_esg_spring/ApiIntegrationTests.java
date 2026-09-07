package br.com.fiap.fiap_esg_spring;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.Alerta;
import br.com.fiap.fiap_esg_spring.model.AlertaStatus;
import br.com.fiap.fiap_esg_spring.model.ConsumoEnergia;
import br.com.fiap.fiap_esg_spring.model.Empresa;
import br.com.fiap.fiap_esg_spring.model.Equipamento;
import br.com.fiap.fiap_esg_spring.model.EquipamentoStatus;
import br.com.fiap.fiap_esg_spring.service.AlertaService;
import br.com.fiap.fiap_esg_spring.service.ConsumoEnergiaService;
import br.com.fiap.fiap_esg_spring.service.EmpresaService;
import br.com.fiap.fiap_esg_spring.service.EquipamentoService;
import br.com.fiap.fiap_esg_spring.service.ProjetoCompensacaoService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTests {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin123";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpresaService empresaService;

    @MockBean
    private EquipamentoService equipamentoService;

    @MockBean
    private ConsumoEnergiaService consumoEnergiaService;

    @MockBean
    private AlertaService alertaService;

    @MockBean
    private ProjetoCompensacaoService projetoCompensacaoService;

    @Test
    void deveExigirAutenticacaoNosEndpointsDaApi() throws Exception {
        mockMvc.perform(get("/empresas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirSwaggerSemAutenticacaoEDeclararBasicAuth() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.basicAuth.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.basicAuth.scheme").value("basic"))
                .andExpect(jsonPath("$.security[0].basicAuth").exists());
    }

    @Test
    void deveListarEmpresasComBasicAuth() throws Exception {
        Empresa e1 = Empresa.builder().id("660000000000000000000001").nome("EcoTech Solucoes").cnpj("11.111.111/0001-11").build();
        when(empresaService.listar()).thenReturn(List.of(e1));

        mockMvc.perform(get("/empresas").with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("EcoTech Solucoes"));
    }

    @Test
    void deveCriarAtualizarEExcluirEmpresa() throws Exception {
        Empresa criada = Empresa.builder()
                .id("660000000000000000000004")
                .nome("Empresa Teste")
                .cnpj("44.444.444/0001-44")
                .build();
        when(empresaService.criar(any(Empresa.class))).thenReturn(criada);

        String createResponse = mockMvc.perform(post("/empresas")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Empresa Teste",
                                  "cnpj": "44.444.444/0001-44"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/empresas/660000000000000000000004")))
                .andExpect(jsonPath("$.nome").value("Empresa Teste"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = JsonTestUtils.idFrom(createResponse);

        Empresa atualizada = Empresa.builder()
                .id(id)
                .nome("Empresa Atualizada")
                .cnpj("55.555.555/0001-55")
                .build();
        when(empresaService.atualizar(eq(id), any(Empresa.class))).thenReturn(atualizada);

        mockMvc.perform(put("/empresas/{id}", id)
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Empresa Atualizada",
                                  "cnpj": "55.555.555/0001-55"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Empresa Atualizada"));

        doNothing().when(empresaService).excluir(id);
        mockMvc.perform(delete("/empresas/{id}", id).with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isNoContent());

        when(empresaService.buscarPorId(id)).thenThrow(new ResourceNotFoundException("Empresa nao encontrada: " + id));
        mockMvc.perform(get("/empresas/{id}", id).with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Empresa nao encontrada: " + id));
    }

    @Test
    void deveValidarPayloadInvalido() throws Exception {
        mockMvc.perform(post("/empresas")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cnpj": "1234567890123456789"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados invalidos."))
                .andExpect(jsonPath("$.fields.nome").exists())
                .andExpect(jsonPath("$.fields.cnpj").exists());
    }

    @Test
    void deveFiltrarEquipamentosConsumosEAlertas() throws Exception {
        Equipamento eq = Equipamento.builder()
                .id("661000000000000000000001")
                .nome("Ar Condicionado")
                .status(EquipamentoStatus.LIGADO)
                .limiteConsumoKwh(new BigDecimal("50.00"))
                .limiteOciosidadeMin(30)
                .build();
        when(equipamentoService.listar("660000000000000000000001")).thenReturn(List.of(eq));

        ConsumoEnergia c = ConsumoEnergia.builder()
                .id("662000000000000000000002")
                .tempoOciosoMin(25)
                .consumoKwh(new BigDecimal("12.00"))
                .build();
        when(consumoEnergiaService.listar("661000000000000000000002")).thenReturn(List.of(c));

        Alerta a = Alerta.builder()
                .id("663000000000000000000001")
                .tipoAlerta("CONSUMO_ALTO")
                .mensagem("Consumo acima do limite")
                .status(AlertaStatus.ABERTO)
                .build();
        when(alertaService.listar(null, AlertaStatus.ABERTO)).thenReturn(List.of(a));

        mockMvc.perform(get("/equipamentos?empresaId=660000000000000000000001").with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Ar Condicionado"));

        mockMvc.perform(get("/consumos-energia?equipamentoId=661000000000000000000002").with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tempoOciosoMin").value(25));

        mockMvc.perform(get("/alertas?status=ABERTO").with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipoAlerta").value("CONSUMO_ALTO"));
    }
}
