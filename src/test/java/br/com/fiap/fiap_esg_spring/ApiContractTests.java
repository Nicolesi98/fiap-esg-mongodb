package br.com.fiap.fiap_esg_spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.*;
import br.com.fiap.fiap_esg_spring.service.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Testes de API — validação completa de:
 *  1. Status codes HTTP
 *  2. Corpo de resposta JSON (campos e valores)
 *  3. Contrato via JSON Schema (networknt json-schema-validator)
 *  4. Segurança (endpoints exigem autenticação)
 *
 * Estratégia: @MockBean nos serviços para isolar a camada HTTP.
 * Os testes rodam sem MongoDB real (perfil "test").
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes de Contrato e API — FIAP ESG")
class ApiContractTests {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private EmpresaService empresaService;
    @MockBean private EquipamentoService equipamentoService;
    @MockBean private ConsumoEnergiaService consumoEnergiaService;
    @MockBean private AlertaService alertaService;
    @MockBean private ProjetoCompensacaoService projetoCompensacaoService;

    // -----------------------------------------------------------------------
    // Utilitário de JSON Schema
    // -----------------------------------------------------------------------

    private Set<ValidationMessage> validateSchema(String json, String schemaFile) throws Exception {
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/" + schemaFile);
        assertThat(schemaStream)
                .as("Schema file '%s' must exist in src/test/resources/schemas/", schemaFile)
                .isNotNull();
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema schema = factory.getSchema(schemaStream);
        JsonNode node = objectMapper.readTree(json);
        return schema.validate(node);
    }

    // -----------------------------------------------------------------------
    // SEGURANÇA
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Segurança — Autenticação")
    class SegurancaTests {

        @Test
        @DisplayName("GET /empresas sem credenciais deve retornar 401")
        void semAutenticacaoRetorna401() throws Exception {
            mockMvc.perform(get("/empresas"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /equipamentos sem credenciais deve retornar 401")
        void equipamentosSemAutenticacaoRetorna401() throws Exception {
            mockMvc.perform(get("/equipamentos"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /alertas sem credenciais deve retornar 401")
        void alertasSemAutenticacaoRetorna401() throws Exception {
            mockMvc.perform(get("/alertas"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /consumos-energia sem credenciais deve retornar 401")
        void consumosSemAutenticacaoRetorna401() throws Exception {
            mockMvc.perform(get("/consumos-energia"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /projetos-compensacao sem credenciais deve retornar 401")
        void projetosSemAutenticacaoRetorna401() throws Exception {
            mockMvc.perform(get("/projetos-compensacao"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Swagger e OpenAPI docs devem ser acessíveis sem autenticação")
        void swaggerPublicoEDeclaraBasicAuth() throws Exception {
            mockMvc.perform(get("/v3/api-docs"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.components.securitySchemes.basicAuth.type").value("http"))
                    .andExpect(jsonPath("$.components.securitySchemes.basicAuth.scheme").value("basic"));
        }
    }

    // -----------------------------------------------------------------------
    // EMPRESA
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("API /empresas")
    class EmpresaApiTests {

        private final Empresa empresa = Empresa.builder()
                .id("660000000000000000000001")
                .nome("EcoTech Solutions")
                .cnpj("11.111.111/0001-11")
                .setor("Energia Renovável")
                .statusAdesaoEsg("ATIVO")
                .build();

        @BeforeEach
        void setup() {
            when(empresaService.listar()).thenReturn(List.of(empresa));
            when(empresaService.buscarPorId("660000000000000000000001")).thenReturn(empresa);
            when(empresaService.criar(any())).thenReturn(empresa);
            when(empresaService.atualizar(eq("660000000000000000000001"), any())).thenReturn(empresa);
            doNothing().when(empresaService).excluir("660000000000000000000001");
        }

        @Test
        @DisplayName("GET /empresas — status 200 e lista de empresas")
        void listarEmpresas_status200_lista() throws Exception {
            mockMvc.perform(get("/empresas").with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].nome").value("EcoTech Solutions"))
                    .andExpect(jsonPath("$[0].cnpj").value("11.111.111/0001-11"));
        }

        @Test
        @DisplayName("GET /empresas — contrato JSON Schema lista")
        void listarEmpresas_jsonSchemaContrato() throws Exception {
            MvcResult result = mockMvc.perform(get("/empresas").with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andReturn();
            Set<ValidationMessage> errors = validateSchema(result.getResponse().getContentAsString(), "empresa-lista-schema.json");
            assertThat(errors).as("JSON Schema violations").isEmpty();
        }

        @Test
        @DisplayName("GET /empresas/{id} — status 200 e corpo correto")
        void buscarEmpresaPorId_status200() throws Exception {
            mockMvc.perform(get("/empresas/660000000000000000000001").with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("660000000000000000000001"))
                    .andExpect(jsonPath("$.nome").value("EcoTech Solutions"));
        }

        @Test
        @DisplayName("GET /empresas/{id} — contrato JSON Schema objeto")
        void buscarEmpresaPorId_jsonSchemaContrato() throws Exception {
            MvcResult result = mockMvc.perform(get("/empresas/660000000000000000000001").with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andReturn();
            Set<ValidationMessage> errors = validateSchema(result.getResponse().getContentAsString(), "empresa-schema.json");
            assertThat(errors).as("JSON Schema violations").isEmpty();
        }

        @Test
        @DisplayName("POST /empresas — status 201, Location header e corpo criado")
        void criarEmpresa_status201_comLocation() throws Exception {
            mockMvc.perform(post("/empresas")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nome": "EcoTech Solutions", "cnpj": "11.111.111/0001-11"}
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/empresas/660000000000000000000001")))
                    .andExpect(jsonPath("$.nome").value("EcoTech Solutions"))
                    .andExpect(jsonPath("$.id").isNotEmpty());
        }

        @Test
        @DisplayName("POST /empresas — contrato JSON Schema do objeto criado")
        void criarEmpresa_jsonSchemaContrato() throws Exception {
            MvcResult result = mockMvc.perform(post("/empresas")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nome": "EcoTech Solutions", "cnpj": "11.111.111/0001-11"}
                                    """))
                    .andExpect(status().isCreated())
                    .andReturn();
            Set<ValidationMessage> errors = validateSchema(result.getResponse().getContentAsString(), "empresa-schema.json");
            assertThat(errors).as("JSON Schema violations").isEmpty();
        }

        @Test
        @DisplayName("PUT /empresas/{id} — status 200 e dados atualizados")
        void atualizarEmpresa_status200() throws Exception {
            mockMvc.perform(put("/empresas/660000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nome": "EcoTech Solutions", "cnpj": "11.111.111/0001-11"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("EcoTech Solutions"));
        }

        @Test
        @DisplayName("DELETE /empresas/{id} — status 204 sem corpo")
        void excluirEmpresa_status204() throws Exception {
            mockMvc.perform(delete("/empresas/660000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("GET /empresas/{id} inexistente — status 404 com mensagem de erro")
        void buscarEmpresaInexistente_status404() throws Exception {
            when(empresaService.buscarPorId("id-invalido"))
                    .thenThrow(new ResourceNotFoundException("Empresa nao encontrada: id-invalido"));

            mockMvc.perform(get("/empresas/id-invalido").with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value(containsString("Empresa nao encontrada")));
        }

        @Test
        @DisplayName("POST /empresas — payload sem 'nome' deve retornar 400 com campo de erro")
        void criarEmpresaSemNome_status400_comFieldError() throws Exception {
            mockMvc.perform(post("/empresas")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"cnpj": "11.111.111/0001-11"}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Dados invalidos."))
                    .andExpect(jsonPath("$.fields.nome").exists());
        }

        @Test
        @DisplayName("POST /empresas — CNPJ com mais de 18 chars deve retornar 400")
        void criarEmpresaComCnpjInvalido_status400() throws Exception {
            mockMvc.perform(post("/empresas")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nome": "Empresa X", "cnpj": "00.000.000/0000-00.INVALIDO.EXTRA"}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Dados invalidos."))
                    .andExpect(jsonPath("$.fields.cnpj").exists());
        }
    }

    // -----------------------------------------------------------------------
    // EQUIPAMENTO
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("API /equipamentos")
    class EquipamentoApiTests {

        private final Equipamento equipamento = Equipamento.builder()
                .id("661000000000000000000001")
                .nome("Ar Condicionado Central")
                .tipo("HVAC")
                .status(EquipamentoStatus.LIGADO)
                .limiteConsumoKwh(new BigDecimal("150.00"))
                .limiteOciosidadeMin(60)
                .build();

        @BeforeEach
        void setup() {
            when(equipamentoService.listar(null)).thenReturn(List.of(equipamento));
            when(equipamentoService.listar("660000000000000000000001")).thenReturn(List.of(equipamento));
            when(equipamentoService.buscarPorId("661000000000000000000001")).thenReturn(equipamento);
            when(equipamentoService.criar(any())).thenReturn(equipamento);
            when(equipamentoService.atualizar(eq("661000000000000000000001"), any())).thenReturn(equipamento);
            doNothing().when(equipamentoService).excluir("661000000000000000000001");
        }

        @Test
        @DisplayName("GET /equipamentos — status 200 e lista")
        void listarEquipamentos_status200() throws Exception {
            mockMvc.perform(get("/equipamentos").with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].nome").value("Ar Condicionado Central"))
                    .andExpect(jsonPath("$[0].status").value("LIGADO"));
        }

        @Test
        @DisplayName("GET /equipamentos?empresaId — filtra por empresa")
        void listarEquipamentosPorEmpresa_status200() throws Exception {
            mockMvc.perform(get("/equipamentos?empresaId=660000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("GET /equipamentos/{id} — status 200 e contrato JSON Schema")
        void buscarEquipamentoPorId_status200_comSchema() throws Exception {
            MvcResult result = mockMvc.perform(get("/equipamentos/661000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("Ar Condicionado Central"))
                    .andReturn();

            Set<ValidationMessage> errors = validateSchema(result.getResponse().getContentAsString(), "equipamento-schema.json");
            assertThat(errors).as("JSON Schema violations").isEmpty();
        }

        @Test
        @DisplayName("POST /equipamentos — status 201 e contrato JSON Schema")
        void criarEquipamento_status201_comSchema() throws Exception {
            MvcResult result = mockMvc.perform(post("/equipamentos")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "nome": "Ar Condicionado Central",
                                      "status": "LIGADO",
                                      "limiteConsumoKwh": 150.00,
                                      "limiteOciosidadeMin": 60
                                    }
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/equipamentos/")))
                    .andReturn();

            Set<ValidationMessage> errors = validateSchema(result.getResponse().getContentAsString(), "equipamento-schema.json");
            assertThat(errors).as("JSON Schema violations").isEmpty();
        }

        @Test
        @DisplayName("DELETE /equipamentos/{id} — status 204")
        void excluirEquipamento_status204() throws Exception {
            mockMvc.perform(delete("/equipamentos/661000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("POST /equipamentos — payload sem 'nome' deve retornar 400")
        void criarEquipamentoSemNome_status400() throws Exception {
            mockMvc.perform(post("/equipamentos")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"status": "LIGADO", "limiteConsumoKwh": 100.0, "limiteOciosidadeMin": 30}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Dados invalidos."))
                    .andExpect(jsonPath("$.fields.nome").exists());
        }

        @Test
        @DisplayName("GET /equipamentos/{id} inexistente — status 404")
        void buscarEquipamentoInexistente_status404() throws Exception {
            when(equipamentoService.buscarPorId("id-inexistente"))
                    .thenThrow(new ResourceNotFoundException("Equipamento nao encontrado: id-inexistente"));

            mockMvc.perform(get("/equipamentos/id-inexistente")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("Equipamento nao encontrado")));
        }
    }

    // -----------------------------------------------------------------------
    // CONSUMO DE ENERGIA
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("API /consumos-energia")
    class ConsumoEnergiaApiTests {

        private final ConsumoEnergia consumo = ConsumoEnergia.builder()
                .id("662000000000000000000001")
                .tipoRecurso("ENERGIA_ELETRICA")
                .consumoKwh(new BigDecimal("25.50"))
                .tempoOciosoMin(15)
                .pegadaCarbonoKgCo2e(new BigDecimal("2.295"))
                .build();

        @BeforeEach
        void setup() {
            when(consumoEnergiaService.listar(null)).thenReturn(List.of(consumo));
            when(consumoEnergiaService.listar("661000000000000000000001")).thenReturn(List.of(consumo));
            when(consumoEnergiaService.buscarPorId("662000000000000000000001")).thenReturn(consumo);
            when(consumoEnergiaService.criar(any())).thenReturn(consumo);
            when(consumoEnergiaService.atualizar(eq("662000000000000000000001"), any())).thenReturn(consumo);
            doNothing().when(consumoEnergiaService).excluir("662000000000000000000001");
        }

        @Test
        @DisplayName("GET /consumos-energia — status 200 e lista")
        void listarConsumos_status200() throws Exception {
            mockMvc.perform(get("/consumos-energia").with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].consumoKwh").value(25.50))
                    .andExpect(jsonPath("$[0].tempoOciosoMin").value(15));
        }

        @Test
        @DisplayName("GET /consumos-energia?equipamentoId — filtra por equipamento")
        void listarConsumosPorEquipamento_status200() throws Exception {
            mockMvc.perform(get("/consumos-energia?equipamentoId=661000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].consumoKwh").value(25.50));
        }

        @Test
        @DisplayName("GET /consumos-energia/{id} — status 200 e contrato JSON Schema")
        void buscarConsumoPorId_status200_comSchema() throws Exception {
            MvcResult result = mockMvc.perform(get("/consumos-energia/662000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andReturn();

            Set<ValidationMessage> errors = validateSchema(result.getResponse().getContentAsString(), "consumo-energia-schema.json");
            assertThat(errors).as("JSON Schema violations").isEmpty();
        }

        @Test
        @DisplayName("POST /consumos-energia — status 201, Location e contrato JSON Schema")
        void criarConsumo_status201_comSchema() throws Exception {
            MvcResult result = mockMvc.perform(post("/consumos-energia")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"tipoRecurso": "ENERGIA_ELETRICA", "consumoKwh": 25.50, "tempoOciosoMin": 15}
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/consumos-energia/")))
                    .andExpect(jsonPath("$.consumoKwh").value(25.50))
                    .andReturn();

            Set<ValidationMessage> errors = validateSchema(result.getResponse().getContentAsString(), "consumo-energia-schema.json");
            assertThat(errors).as("JSON Schema violations").isEmpty();
        }

        @Test
        @DisplayName("PUT /consumos-energia/{id} — status 200")
        void atualizarConsumo_status200() throws Exception {
            mockMvc.perform(put("/consumos-energia/662000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"consumoKwh": 25.50, "tempoOciosoMin": 20}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.consumoKwh").value(25.50));
        }

        @Test
        @DisplayName("DELETE /consumos-energia/{id} — status 204")
        void excluirConsumo_status204() throws Exception {
            mockMvc.perform(delete("/consumos-energia/662000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("POST /consumos-energia — sem consumoKwh deve retornar 400")
        void criarConsumoSemKwh_status400() throws Exception {
            mockMvc.perform(post("/consumos-energia")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"tipoRecurso": "ENERGIA_ELETRICA", "tempoOciosoMin": 10}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Dados invalidos."))
                    .andExpect(jsonPath("$.fields.consumoKwh").exists());
        }

        @Test
        @DisplayName("POST /consumos-energia — consumoKwh negativo deve retornar 400")
        void criarConsumoComKwhNegativo_status400() throws Exception {
            mockMvc.perform(post("/consumos-energia")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"consumoKwh": -5.00, "tempoOciosoMin": 0}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Dados invalidos."))
                    .andExpect(jsonPath("$.fields.consumoKwh").exists());
        }
    }

    // -----------------------------------------------------------------------
    // ALERTA
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("API /alertas")
    class AlertaApiTests {

        private final Alerta alerta = Alerta.builder()
                .id("663000000000000000000001")
                .tipoAlerta("CONSUMO_ALTO")
                .mensagem("Consumo 40% acima do limite no turno noturno")
                .severidade("ALTA")
                .status(AlertaStatus.ABERTO)
                .pilarEsg("ENVIRONMENTAL")
                .build();

        @BeforeEach
        void setup() {
            when(alertaService.listar(null, null)).thenReturn(List.of(alerta));
            when(alertaService.listar(null, AlertaStatus.ABERTO)).thenReturn(List.of(alerta));
            when(alertaService.listar("661000000000000000000001", null)).thenReturn(List.of(alerta));
            when(alertaService.buscarPorId("663000000000000000000001")).thenReturn(alerta);
            when(alertaService.criar(any())).thenReturn(alerta);
            when(alertaService.atualizar(eq("663000000000000000000001"), any())).thenReturn(alerta);
            doNothing().when(alertaService).excluir("663000000000000000000001");
        }

        @Test
        @DisplayName("GET /alertas — status 200 e lista de alertas")
        void listarAlertas_status200() throws Exception {
            mockMvc.perform(get("/alertas").with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].tipoAlerta").value("CONSUMO_ALTO"))
                    .andExpect(jsonPath("$[0].status").value("ABERTO"));
        }

        @Test
        @DisplayName("GET /alertas?status=ABERTO — filtra por status")
        void listarAlertasPorStatus_status200() throws Exception {
            mockMvc.perform(get("/alertas?status=ABERTO").with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status").value("ABERTO"));
        }

        @Test
        @DisplayName("GET /alertas?equipamentoId — filtra por equipamento")
        void listarAlertasPorEquipamento_status200() throws Exception {
            mockMvc.perform(get("/alertas?equipamentoId=661000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("GET /alertas/{id} — status 200 e contrato JSON Schema")
        void buscarAlertaPorId_status200_comSchema() throws Exception {
            MvcResult result = mockMvc.perform(get("/alertas/663000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tipoAlerta").value("CONSUMO_ALTO"))
                    .andReturn();

            Set<ValidationMessage> errors = validateSchema(result.getResponse().getContentAsString(), "alerta-schema.json");
            assertThat(errors).as("JSON Schema violations").isEmpty();
        }

        @Test
        @DisplayName("POST /alertas — status 201, Location e contrato JSON Schema")
        void criarAlerta_status201_comSchema() throws Exception {
            MvcResult result = mockMvc.perform(post("/alertas")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "tipoAlerta": "CONSUMO_ALTO",
                                      "mensagem": "Consumo 40% acima do limite no turno noturno",
                                      "severidade": "ALTA",
                                      "status": "ABERTO"
                                    }
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/alertas/")))
                    .andExpect(jsonPath("$.tipoAlerta").value("CONSUMO_ALTO"))
                    .andReturn();

            Set<ValidationMessage> errors = validateSchema(result.getResponse().getContentAsString(), "alerta-schema.json");
            assertThat(errors).as("JSON Schema violations").isEmpty();
        }

        @Test
        @DisplayName("PUT /alertas/{id} — status 200 e dados atualizados")
        void atualizarAlerta_status200() throws Exception {
            mockMvc.perform(put("/alertas/663000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "tipoAlerta": "CONSUMO_ALTO",
                                      "mensagem": "Consumo normalizado",
                                      "severidade": "BAIXA",
                                      "status": "ABERTO"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tipoAlerta").value("CONSUMO_ALTO"));
        }

        @Test
        @DisplayName("DELETE /alertas/{id} — status 204")
        void excluirAlerta_status204() throws Exception {
            mockMvc.perform(delete("/alertas/663000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("POST /alertas — sem tipoAlerta e mensagem deve retornar 400 com campos de erro")
        void criarAlertaSemCamposObrigatorios_status400() throws Exception {
            mockMvc.perform(post("/alertas")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"severidade": "ALTA"}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Dados invalidos."))
                    .andExpect(jsonPath("$.fields.tipoAlerta").exists())
                    .andExpect(jsonPath("$.fields.mensagem").exists());
        }

        @Test
        @DisplayName("GET /alertas/{id} inexistente — status 404")
        void buscarAlertaInexistente_status404() throws Exception {
            when(alertaService.buscarPorId("alerta-inexistente"))
                    .thenThrow(new ResourceNotFoundException("Alerta nao encontrado: alerta-inexistente"));

            mockMvc.perform(get("/alertas/alerta-inexistente")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("Alerta nao encontrado")));
        }

        @Test
        @DisplayName("GET /alertas — contrato JSON Schema da lista")
        void listarAlertas_jsonSchemaContrato() throws Exception {
            MvcResult result = mockMvc.perform(get("/alertas").with(httpBasic(USERNAME, PASSWORD)))
                    .andReturn();

            Set<ValidationMessage> errors = validateSchema(result.getResponse().getContentAsString(), "alerta-lista-schema.json");
            assertThat(errors).as("JSON Schema violations").isEmpty();
        }
    }

    // -----------------------------------------------------------------------
    // PROJETO DE COMPENSAÇÃO
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("API /projetos-compensacao")
    class ProjetoCompensacaoApiTests {

        private final ProjetoCompensacao projeto = ProjetoCompensacao.builder()
                .id("664000000000000000000001")
                .empresaId("660000000000000000000001")
                .nomeIniciativa("Reflorestamento Mata Atlântica 2026")
                .tipoProjeto("REFLORESTAMENTO_NATIVO")
                .status("PLANEJADO")
                .metaTco2eCompensar(new BigDecimal("500.00"))
                .tco2eCompensadoAcumulado(BigDecimal.ZERO)
                .build();

        @BeforeEach
        void setup() {
            when(projetoCompensacaoService.listar(null, null)).thenReturn(List.of(projeto));
            when(projetoCompensacaoService.listar("660000000000000000000001", null)).thenReturn(List.of(projeto));
            when(projetoCompensacaoService.listar(null, "REFLORESTAMENTO_NATIVO")).thenReturn(List.of(projeto));
            when(projetoCompensacaoService.buscarPorId("664000000000000000000001")).thenReturn(projeto);
            when(projetoCompensacaoService.criar(any())).thenReturn(projeto);
            when(projetoCompensacaoService.atualizar(eq("664000000000000000000001"), any())).thenReturn(projeto);
            doNothing().when(projetoCompensacaoService).excluir("664000000000000000000001");
        }

        @Test
        @DisplayName("GET /projetos-compensacao — status 200 e lista")
        void listarProjetos_status200() throws Exception {
            mockMvc.perform(get("/projetos-compensacao").with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].nomeIniciativa").value("Reflorestamento Mata Atlântica 2026"))
                    .andExpect(jsonPath("$[0].tipoProjeto").value("REFLORESTAMENTO_NATIVO"));
        }

        @Test
        @DisplayName("GET /projetos-compensacao?empresaId — filtra por empresa")
        void listarProjetosPorEmpresa_status200() throws Exception {
            mockMvc.perform(get("/projetos-compensacao?empresaId=660000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("GET /projetos-compensacao?tipoProjeto — filtra por tipo")
        void listarProjetosPorTipo_status200() throws Exception {
            mockMvc.perform(get("/projetos-compensacao?tipoProjeto=REFLORESTAMENTO_NATIVO")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].tipoProjeto").value("REFLORESTAMENTO_NATIVO"));
        }

        @Test
        @DisplayName("GET /projetos-compensacao/{id} — status 200 e contrato JSON Schema")
        void buscarProjetoPorId_status200_comSchema() throws Exception {
            MvcResult result = mockMvc.perform(get("/projetos-compensacao/664000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isOk())
                    .andReturn();

            Set<ValidationMessage> errors = validateSchema(result.getResponse().getContentAsString(), "projeto-compensacao-schema.json");
            assertThat(errors).as("JSON Schema violations").isEmpty();
        }

        @Test
        @DisplayName("POST /projetos-compensacao — status 201 e contrato JSON Schema")
        void criarProjeto_status201_comSchema() throws Exception {
            MvcResult result = mockMvc.perform(post("/projetos-compensacao")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "empresaId": "660000000000000000000001",
                                      "nomeIniciativa": "Reflorestamento Mata Atlântica 2026",
                                      "tipoProjeto": "REFLORESTAMENTO_NATIVO",
                                      "metaTco2eCompensar": 500.00
                                    }
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/projetos-compensacao/")))
                    .andReturn();

            Set<ValidationMessage> errors = validateSchema(result.getResponse().getContentAsString(), "projeto-compensacao-schema.json");
            assertThat(errors).as("JSON Schema violations").isEmpty();
        }

        @Test
        @DisplayName("PUT /projetos-compensacao/{id} — status 200")
        void atualizarProjeto_status200() throws Exception {
            mockMvc.perform(put("/projetos-compensacao/664000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "empresaId": "660000000000000000000001",
                                      "nomeIniciativa": "Reflorestamento Atualizado",
                                      "tipoProjeto": "REFLORESTAMENTO_NATIVO",
                                      "status": "EM_ANDAMENTO",
                                      "metaTco2eCompensar": 500.00
                                    }
                                    """))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("DELETE /projetos-compensacao/{id} — status 204")
        void excluirProjeto_status204() throws Exception {
            mockMvc.perform(delete("/projetos-compensacao/664000000000000000000001")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("POST /projetos-compensacao — sem empresaId deve retornar 400")
        void criarProjetoSemEmpresaId_status400() throws Exception {
            mockMvc.perform(post("/projetos-compensacao")
                            .with(httpBasic(USERNAME, PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "nomeIniciativa": "Projeto sem empresa",
                                      "tipoProjeto": "ENERGIA_RENOVAVEL_IREC",
                                      "metaTco2eCompensar": 100.00
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Dados invalidos."))
                    .andExpect(jsonPath("$.fields.empresaId").exists());
        }

        @Test
        @DisplayName("GET /projetos-compensacao/{id} inexistente — status 404")
        void buscarProjetoInexistente_status404() throws Exception {
            when(projetoCompensacaoService.buscarPorId("proj-inexistente"))
                    .thenThrow(new ResourceNotFoundException("ProjetoCompensacao nao encontrado: proj-inexistente"));

            mockMvc.perform(get("/projetos-compensacao/proj-inexistente")
                            .with(httpBasic(USERNAME, PASSWORD)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("ProjetoCompensacao nao encontrado")));
        }
    }
}
