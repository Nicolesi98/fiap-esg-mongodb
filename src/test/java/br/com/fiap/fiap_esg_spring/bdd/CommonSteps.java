package br.com.fiap.fiap_esg_spring.bdd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.*;
import br.com.fiap.fiap_esg_spring.service.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Step Definitions reutilizáveis para todos os recursos da API ESG.
 *
 * Cobre:
 *  - Autenticação (Basic Auth / sem autenticação)
 *  - Requisições HTTP genéricas (GET, POST, PUT, DELETE)
 *  - Verificações de status code, campos JSON e cabeçalhos
 *  - Validação de JSON Schema (contrato de API)
 */
public class CommonSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScenarioContext ctx;

    @Autowired
    private ObjectMapper objectMapper;

    // MockBeans injetados a partir da CucumberSpringConfiguration
    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private EquipamentoService equipamentoService;

    @Autowired
    private ConsumoEnergiaService consumoEnergiaService;

    @Autowired
    private AlertaService alertaService;

    @Autowired
    private ProjetoCompensacaoService projetoCompensacaoService;

    // Credenciais correntes para o cenário
    private String currentUsername;
    private String currentPassword;
    private boolean authenticated = true;

    // IDs fixos para os mocks de pré-condição (seeds)
    private static final String EMPRESA_ID   = "660000000000000000000001";
    private static final String EQUIPAMENTO_ID = "661000000000000000000001";
    private static final String ALERTA_ID    = "663000000000000000000001";
    private static final String CONSUMO_ID   = "662000000000000000000001";
    private static final String PROJETO_ID   = "664000000000000000000001";

    // -------------------------------------------------------------------
    // Hooks de ciclo de vida
    // -------------------------------------------------------------------

    @Before
    public void beforeScenario() {
        ctx.clear();
        currentUsername = null;
        currentPassword = null;
        authenticated = true;
        Mockito.reset(empresaService, equipamentoService, consumoEnergiaService, alertaService, projetoCompensacaoService);
    }

    @After
    public void afterScenario() {
        ctx.clear();
    }

    // -------------------------------------------------------------------
    // Steps de autenticação
    // -------------------------------------------------------------------

    @Dado("que estou autenticado com usuário {string} e senha {string}")
    public void autenticado(String username, String password) {
        this.currentUsername = username;
        this.currentPassword = password;
        this.authenticated = true;
    }

    @Dado("que não estou autenticado")
    public void naoAutenticado() {
        this.authenticated = false;
        this.currentUsername = null;
        this.currentPassword = null;
    }

    // -------------------------------------------------------------------
    // Steps de pré-condição (seeds via mocks)
    // -------------------------------------------------------------------

    @Dado("que existe ao menos uma empresa cadastrada")
    public void existeEmpresaCadastrada() {
        Empresa empresa = empresa(EMPRESA_ID, "Empresa Seed BDD", "00.000.000/0001-00");
        when(empresaService.listar()).thenReturn(List.of(empresa));
        ctx.setResourceId(EMPRESA_ID);
    }

    @Dado("que existe uma empresa cadastrada com nome {string}")
    public void existeEmpresaComNome(String nome) {
        Empresa empresa = empresa(EMPRESA_ID, nome, "99.000.000/0001-01");
        when(empresaService.buscarPorId(EMPRESA_ID)).thenReturn(empresa);
        when(empresaService.criar(any())).thenReturn(empresa);
        when(empresaService.atualizar(eq(EMPRESA_ID), any())).thenReturn(empresa);
        doNothing().when(empresaService).excluir(EMPRESA_ID);
        ctx.setResourceId(EMPRESA_ID);
    }

    @Dado("que existe ao menos um equipamento cadastrado")
    public void existeEquipamentoCadastrado() {
        Equipamento eq = equipamento(EQUIPAMENTO_ID, "Equipamento Seed BDD");
        when(equipamentoService.listar(null)).thenReturn(List.of(eq));
        ctx.setResourceId(EQUIPAMENTO_ID);
        ctx.setEquipamentoId(EQUIPAMENTO_ID);
    }

    @Dado("que existe um equipamento cadastrado com nome {string}")
    public void existeEquipamentoComNome(String nome) {
        Equipamento eq = equipamento(EQUIPAMENTO_ID, nome);
        when(equipamentoService.buscarPorId(EQUIPAMENTO_ID)).thenReturn(eq);
        when(equipamentoService.criar(any())).thenReturn(eq);
        when(equipamentoService.atualizar(eq(EQUIPAMENTO_ID), any())).thenReturn(eq);
        doNothing().when(equipamentoService).excluir(EQUIPAMENTO_ID);
        ctx.setResourceId(EQUIPAMENTO_ID);
        ctx.setEquipamentoId(EQUIPAMENTO_ID);
    }

    @Dado("que existe ao menos um alerta cadastrado com status {string}")
    public void existeAlertaCadastradoComStatus(String status) {
        AlertaStatus alertaStatus = AlertaStatus.valueOf(status);
        Alerta alerta = alerta(ALERTA_ID, "CONSUMO_ALTO", "Alerta seed BDD", alertaStatus);
        when(alertaService.listar(null, alertaStatus)).thenReturn(List.of(alerta));
        ctx.setResourceId(ALERTA_ID);
    }

    @Dado("que existe um alerta cadastrado com tipo {string}")
    public void existeAlertaComTipo(String tipo) {
        Alerta alerta = alerta(ALERTA_ID, tipo, "Mensagem seed BDD", AlertaStatus.ABERTO);
        when(alertaService.buscarPorId(ALERTA_ID)).thenReturn(alerta);
        when(alertaService.criar(any())).thenReturn(alerta);
        when(alertaService.atualizar(eq(ALERTA_ID), any())).thenReturn(alerta);
        doNothing().when(alertaService).excluir(ALERTA_ID);
        ctx.setResourceId(ALERTA_ID);
    }

    @Dado("que existe um alerta vinculado ao equipamento {string}")
    public void existeAlertaVinculadoAoEquipamento(String nomeEquipamento) {
        String equipId = ctx.getEquipamentoId() != null ? ctx.getEquipamentoId() : EQUIPAMENTO_ID;
        Alerta alerta = alerta(ALERTA_ID, "CONSUMO_ALTO", "Alerta do equipamento " + nomeEquipamento, AlertaStatus.ABERTO);
        when(alertaService.listar(eq(equipId), any())).thenReturn(List.of(alerta));
        ctx.setResourceId(ALERTA_ID);
    }

    @Dado("que existe ao menos um registro de consumo cadastrado")
    public void existeRegistroDeConsumoCadastrado() {
        ConsumoEnergia consumo = consumo(CONSUMO_ID, new BigDecimal("10.0"), 5);
        when(consumoEnergiaService.listar(null)).thenReturn(List.of(consumo));
        ctx.setResourceId(CONSUMO_ID);
    }

    @Dado("que existe ao menos um projeto de compensação cadastrado")
    public void existeProjetoDeCompensacaoCadastrado() {
        ProjetoCompensacao projeto = projeto(PROJETO_ID, "Projeto Seed BDD", "REFLORESTAMENTO_NATIVO");
        when(projetoCompensacaoService.listar(null, null)).thenReturn(List.of(projeto));
        ctx.setResourceId(PROJETO_ID);
    }

    // -------------------------------------------------------------------
    // Steps de requisição HTTP
    // -------------------------------------------------------------------

    @Quando("eu envio uma requisição GET para {string}")
    public void enviarGet(String path) throws Exception {
        String resolvedPath = resolvePath(path);
        configurarMockParaGet(resolvedPath);
        MockHttpServletRequestBuilder builder = get(resolvedPath);
        executeRequest(builder);
    }

    @Quando("eu envio uma requisição POST para {string} com o corpo:")
    public void enviarPost(String path, String body) throws Exception {
        String resolvedPath = resolvePath(path);

        // Configura o mock de acordo com o endpoint
        configurarMockParaPost(resolvedPath, body);

        MockHttpServletRequestBuilder builder = post(resolvedPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
        executeRequest(builder);

        // Extrai o ID do recurso criado para uso nos passos seguintes
        if (ctx.getLastResponse().getStatus() == 201) {
            String responseBody = ctx.getLastResponse().getContentAsString(StandardCharsets.UTF_8);
            if (!responseBody.isBlank()) {
                JsonNode node = objectMapper.readTree(responseBody);
                if (node.has("id") && !node.get("id").isNull()) {
                    ctx.setResourceId(node.get("id").asText());
                }
            }
        }
    }

    @Quando("eu envio uma requisição PUT para {string} com o corpo:")
    public void enviarPut(String path, String body) throws Exception {
        String resolvedPath = resolvePath(path);
        configurarMockParaPut(resolvedPath, body);

        MockHttpServletRequestBuilder builder = put(resolvedPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
        executeRequest(builder);
    }

    @Quando("eu envio uma requisição DELETE para {string}")
    public void enviarDelete(String path) throws Exception {
        String resolvedPath = resolvePath(path);
        configurarMockParaDelete(resolvedPath);

        MockHttpServletRequestBuilder builder = delete(resolvedPath);
        executeRequest(builder);
    }

    // -------------------------------------------------------------------
    // Steps de verificação
    // -------------------------------------------------------------------

    @Então("o status da resposta deve ser {int}")
    public void verificarStatus(int expectedStatus) {
        assertThat(ctx.getLastResponse().getStatus())
                .as("HTTP status code")
                .isEqualTo(expectedStatus);
    }

    @E("o campo {string} da resposta deve ser {string}")
    public void verificarCampoString(String jsonPath, String expectedValue) throws Exception {
        String body = ctx.getLastResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode node = objectMapper.readTree(body);
        String actualValue = resolveJsonPath(node, jsonPath);
        assertThat(actualValue)
                .as("Campo JSON '%s'", jsonPath)
                .isEqualTo(expectedValue);
    }

    @E("o campo {string} da resposta deve conter {string}")
    public void verificarCampoContem(String jsonPath, String partialValue) throws Exception {
        String body = ctx.getLastResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode node = objectMapper.readTree(body);
        String actualValue = resolveJsonPath(node, jsonPath);
        assertThat(actualValue)
                .as("Campo JSON '%s' deve conter '%s'", jsonPath, partialValue)
                .contains(partialValue);
    }

    @E("o cabeçalho {string} deve conter {string}")
    public void verificarCabecalho(String headerName, String partialValue) {
        String headerValue = ctx.getLastResponse().getHeader(headerName);
        assertThat(headerValue)
                .as("Cabeçalho '%s'", headerName)
                .isNotNull()
                .contains(partialValue);
    }

    @E("a resposta deve ser uma lista")
    public void verificarLista() throws Exception {
        String body = ctx.getLastResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode node = objectMapper.readTree(body);
        assertThat(node.isArray())
                .as("A resposta deve ser um array JSON")
                .isTrue();
    }

    @E("o campo de erro {string} deve estar presente na resposta")
    public void verificarCampoDeErroPresente(String dotPath) throws Exception {
        String body = ctx.getLastResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode node = objectMapper.readTree(body);
        String[] parts = dotPath.split("\\.");
        JsonNode current = node;
        for (String part : parts) {
            assertThat(current.has(part))
                    .as("Campo '%s' deve existir em '%s'", part, dotPath)
                    .isTrue();
            current = current.get(part);
        }
        assertThat(current).isNotNull();
    }

    // -------------------------------------------------------------------
    // Validação de JSON Schema (contrato)
    // -------------------------------------------------------------------

    @E("o corpo da resposta deve estar de acordo com o JSON Schema {string}")
    public void validarJsonSchema(String schemaFileName) throws Exception {
        String body = ctx.getLastResponse().getContentAsString(StandardCharsets.UTF_8);

        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/" + schemaFileName);
        assertThat(schemaStream)
                .as("Arquivo de schema '%s' deve existir em src/test/resources/schemas/", schemaFileName)
                .isNotNull();

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema schema = factory.getSchema(schemaStream);
        JsonNode jsonNode = objectMapper.readTree(body);

        Set<ValidationMessage> errors = schema.validate(jsonNode);
        assertThat(errors)
                .as("JSON Schema '%s' violations:\n%s", schemaFileName, errors)
                .isEmpty();
    }

    // -------------------------------------------------------------------
    // Helpers de mock dinâmico
    // -------------------------------------------------------------------

    private void configurarMockParaGet(String path) {
        // Configura 404 para paths com IDs que explicitamente não existem
        if (path.startsWith("/empresas/") && isInexistentId(path, "/empresas/")) {
            String id = path.substring("/empresas/".length());
            when(empresaService.buscarPorId(id))
                    .thenThrow(new ResourceNotFoundException("Empresa nao encontrada: " + id));
        } else if (path.startsWith("/equipamentos/") && isInexistentId(path, "/equipamentos/")) {
            String id = path.substring("/equipamentos/".length());
            when(equipamentoService.buscarPorId(id))
                    .thenThrow(new ResourceNotFoundException("Equipamento nao encontrado: " + id));
        } else if (path.startsWith("/alertas/") && isInexistentId(path, "/alertas/")) {
            String id = path.substring("/alertas/".length());
            when(alertaService.buscarPorId(id))
                    .thenThrow(new ResourceNotFoundException("Alerta nao encontrado: " + id));
        } else if (path.startsWith("/consumos-energia/") && isInexistentId(path, "/consumos-energia/")) {
            String id = path.substring("/consumos-energia/".length());
            when(consumoEnergiaService.buscarPorId(id))
                    .thenThrow(new ResourceNotFoundException("ConsumoEnergia nao encontrado: " + id));
        } else if (path.startsWith("/projetos-compensacao/") && isInexistentId(path, "/projetos-compensacao/")) {
            String id = path.substring("/projetos-compensacao/".length());
            when(projetoCompensacaoService.buscarPorId(id))
                    .thenThrow(new ResourceNotFoundException("ProjetoCompensacao nao encontrado: " + id));
        }
    }

    /**
     * Heurística: considera "inexistente" qualquer ID que contenha a palavra "inexist" ou "invalido",
     * ou seja, IDs usados nos cenários negativos dos features.
     */
    private boolean isInexistentId(String path, String prefix) {
        String id = path.substring(prefix.length());
        return id.contains("inexist") || id.contains("invalido") || id.contains("nao-existe");
    }

    private void configurarMockParaPost(String path, String body) {
        if (path.equals("/empresas")) {
            Empresa e = empresa(EMPRESA_ID, extrairCampo(body, "nome"), extrairCampo(body, "cnpj"));
            when(empresaService.criar(any())).thenReturn(e);
        } else if (path.equals("/equipamentos")) {
            Equipamento eq = equipamento(EQUIPAMENTO_ID, extrairCampo(body, "nome"));
            when(equipamentoService.criar(any())).thenReturn(eq);
        } else if (path.equals("/alertas")) {
            String tipo = extrairCampo(body, "tipoAlerta");
            String msg  = extrairCampo(body, "mensagem");
            if (tipo != null && msg != null) {
                Alerta a = alerta(ALERTA_ID, tipo, msg, AlertaStatus.ABERTO);
                when(alertaService.criar(any())).thenReturn(a);
            }
        } else if (path.equals("/consumos-energia")) {
            String kwh = extrairCampo(body, "consumoKwh");
            if (kwh != null) {
                ConsumoEnergia c = consumo(CONSUMO_ID, new BigDecimal(kwh), 0);
                when(consumoEnergiaService.criar(any())).thenReturn(c);
            }
        } else if (path.equals("/projetos-compensacao")) {
            String nome = extrairCampo(body, "nomeIniciativa");
            String tipo = extrairCampo(body, "tipoProjeto");
            if (nome != null && tipo != null) {
                ProjetoCompensacao p = projeto(PROJETO_ID, nome, tipo);
                when(projetoCompensacaoService.criar(any())).thenReturn(p);
            }
        }
    }

    private void configurarMockParaPut(String path, String body) {
        if (path.startsWith("/empresas/")) {
            String id = path.substring("/empresas/".length());
            Empresa e = empresa(id, extrairCampo(body, "nome"), extrairCampo(body, "cnpj"));
            when(empresaService.atualizar(eq(id), any())).thenReturn(e);
        } else if (path.startsWith("/equipamentos/")) {
            String id = path.substring("/equipamentos/".length());
            Equipamento eq = equipamento(id, extrairCampo(body, "nome"));
            String status = extrairCampo(body, "status");
            if (status != null) {
                eq.setStatus(EquipamentoStatus.valueOf(status));
            }
            when(equipamentoService.atualizar(eq(id), any())).thenReturn(eq);
        } else if (path.startsWith("/alertas/")) {
            String id = path.substring("/alertas/".length());
            String statusStr = extrairCampo(body, "status");
            AlertaStatus status = statusStr != null ? AlertaStatus.valueOf(statusStr) : AlertaStatus.ABERTO;
            Alerta a = alerta(id, extrairCampo(body, "tipoAlerta"), extrairCampo(body, "mensagem"), status);
            when(alertaService.atualizar(eq(id), any())).thenReturn(a);
        } else if (path.startsWith("/consumos-energia/")) {
            String id = path.substring("/consumos-energia/".length());
            ConsumoEnergia c = consumo(id, new BigDecimal("25.50"), 20);
            when(consumoEnergiaService.atualizar(eq(id), any())).thenReturn(c);
        } else if (path.startsWith("/projetos-compensacao/")) {
            String id = path.substring("/projetos-compensacao/".length());
            ProjetoCompensacao p = projeto(id, extrairCampo(body, "nomeIniciativa"), extrairCampo(body, "tipoProjeto"));
            when(projetoCompensacaoService.atualizar(eq(id), any())).thenReturn(p);
        }
    }

    private void configurarMockParaDelete(String path) {
        if (path.startsWith("/empresas/")) {
            doNothing().when(empresaService).excluir(path.substring("/empresas/".length()));
        } else if (path.startsWith("/equipamentos/")) {
            doNothing().when(equipamentoService).excluir(path.substring("/equipamentos/".length()));
        } else if (path.startsWith("/alertas/")) {
            doNothing().when(alertaService).excluir(path.substring("/alertas/".length()));
        } else if (path.startsWith("/consumos-energia/")) {
            doNothing().when(consumoEnergiaService).excluir(path.substring("/consumos-energia/".length()));
        } else if (path.startsWith("/projetos-compensacao/")) {
            doNothing().when(projetoCompensacaoService).excluir(path.substring("/projetos-compensacao/".length()));
        }
    }

    // -------------------------------------------------------------------
    // Helpers de resolução de path e navegação JSON
    // -------------------------------------------------------------------

    private String resolvePath(String path) {
        String resolved = path;
        if (resolved.contains("{id}") && ctx.getResourceId() != null) {
            resolved = resolved.replace("{id}", ctx.getResourceId());
        }
        if (resolved.contains("{equipamentoId}") && ctx.getEquipamentoId() != null) {
            resolved = resolved.replace("{equipamentoId}", ctx.getEquipamentoId());
        }
        return resolved;
    }

    private void executeRequest(MockHttpServletRequestBuilder builder) throws Exception {
        if (authenticated && currentUsername != null) {
            builder = builder.with(httpBasic(currentUsername, currentPassword));
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
        ctx.setLastResponse(result.getResponse());
    }

    private String resolveJsonPath(JsonNode root, String dotPath) {
        String[] parts = dotPath.split("\\.");
        JsonNode current = root;
        for (String part : parts) {
            if (current == null || current.isNull()) return null;
            current = current.get(part);
        }
        return current != null && !current.isNull() ? current.asText() : null;
    }

    /**
     * Extrai um campo simples de um JSON inline (para configurar mocks sem deserialização completa).
     */
    private String extrairCampo(String json, String campo) {
        try {
            JsonNode node = objectMapper.readTree(json);
            JsonNode valor = node.get(campo);
            return (valor != null && !valor.isNull()) ? valor.asText() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // -------------------------------------------------------------------
    // Fábricas de objetos de domínio para os mocks
    // -------------------------------------------------------------------

    private Empresa empresa(String id, String nome, String cnpj) {
        return Empresa.builder().id(id).nome(nome != null ? nome : "Empresa Teste").cnpj(cnpj).build();
    }

    private Equipamento equipamento(String id, String nome) {
        return Equipamento.builder()
                .id(id)
                .nome(nome != null ? nome : "Equipamento Teste")
                .status(EquipamentoStatus.LIGADO)
                .limiteConsumoKwh(new BigDecimal("100.00"))
                .limiteOciosidadeMin(30)
                .build();
    }

    private Alerta alerta(String id, String tipo, String mensagem, AlertaStatus status) {
        return Alerta.builder()
                .id(id)
                .tipoAlerta(tipo != null ? tipo : "CONSUMO_ALTO")
                .mensagem(mensagem != null ? mensagem : "Mensagem padrão BDD")
                .severidade("ALTA")
                .status(status != null ? status : AlertaStatus.ABERTO)
                .pilarEsg("ENVIRONMENTAL")
                .build();
    }

    private ConsumoEnergia consumo(String id, BigDecimal kwh, int ociosoMin) {
        return ConsumoEnergia.builder()
                .id(id)
                .tipoRecurso("ENERGIA_ELETRICA")
                .consumoKwh(kwh)
                .tempoOciosoMin(ociosoMin)
                .pegadaCarbonoKgCo2e(kwh.multiply(new BigDecimal("0.09")))
                .build();
    }

    private ProjetoCompensacao projeto(String id, String nome, String tipo) {
        return ProjetoCompensacao.builder()
                .id(id)
                .empresaId(EMPRESA_ID)
                .nomeIniciativa(nome != null ? nome : "Projeto Teste")
                .tipoProjeto(tipo != null ? tipo : "REFLORESTAMENTO_NATIVO")
                .status("PLANEJADO")
                .metaTco2eCompensar(new BigDecimal("500.00"))
                .tco2eCompensadoAcumulado(BigDecimal.ZERO)
                .build();
    }
}
