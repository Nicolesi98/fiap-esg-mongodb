# Guia de Testes — FIAP ESG MongoDB

Este documento descreve como executar os testes automatizados do projeto,
organizados em três camadas:

1. **Testes unitários de serviço** — validam a lógica de negócio isolada
2. **Testes de API (contrato)** — validam HTTP status codes, corpos JSON e JSON Schema para todas as APIs
3. **Testes BDD (Cucumber/Gherkin)** — validam comportamentos de ponta a ponta com linguagem natural

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Java (JDK) | 17 |
| Maven Wrapper (`./mvnw`) | incluído no projeto |
| MongoDB local *(opcional)* | 6.x — necessário apenas para testes de integração reais |

> **Nota:** os testes atuais usam `@MockBean` para simular os serviços e **não requerem** uma instância real do MongoDB.

---

## Executar todos os testes

```bash
# Linux / macOS
./mvnw test

# Windows (PowerShell)
.\mvnw.cmd test
```

Este único comando executa:
- Testes unitários de serviço (`*ServiceTest`)
- Testes de API com JSON Schema (`ApiContractTests`, `ApiIntegrationTests`)
- Cenários BDD Cucumber (`CucumberSuiteRunner` → arquivos `.feature`)

---

## Executar apenas os testes BDD (Cucumber)

```bash
# Linux / macOS
./mvnw test -Dtest=CucumberSuiteRunner

# Windows (PowerShell)
.\mvnw.cmd test -Dtest=CucumberSuiteRunner
```

---

## Executar apenas os testes de contrato de API

```bash
./mvnw test -Dtest=ApiContractTests
```

---

## Executar apenas os testes de integração existentes

```bash
./mvnw test -Dtest=ApiIntegrationTests
```

---

## Filtrar cenários BDD por tag

Você pode etiquetar cenários com `@smoke`, `@regressao`, etc. e filtrar:

```bash
# Rodar apenas cenários com a tag @smoke
./mvnw test -Dtest=CucumberSuiteRunner -Dcucumber.filter.tags="@smoke"
```

---

## Relatórios gerados

Após `./mvnw test`, os relatórios ficam disponíveis em:

| Relatório | Localização |
|---|---|
| Cobertura JaCoCo (HTML) | `target/site/jacoco/index.html` |
| Cucumber (HTML) | `target/cucumber-reports/cucumber.html` |
| Cucumber (JSON) | `target/cucumber-reports/cucumber.json` |
| Surefire JUnit (XML) | `target/surefire-reports/` |

---

## Estrutura dos testes

```
src/test/
├── java/br/com/fiap/fiap_esg_spring/
│   ├── bdd/
│   │   ├── CucumberSpringConfiguration.java  ← conecta Cucumber ao Spring
│   │   ├── CucumberSuiteRunner.java           ← entry point da suite BDD
│   │   ├── ScenarioContext.java               ← estado compartilhado entre steps
│   │   └── CommonSteps.java                   ← step definitions reutilizáveis
│   ├── service/
│   │   ├── AlertaServiceTest.java
│   │   ├── ConsumoEnergiaServiceTest.java
│   │   ├── EmpresaServiceTest.java
│   │   ├── EquipamentoServiceTest.java
│   │   └── ProjetoCompensacaoServiceTest.java
│   ├── ApiContractTests.java                  ← testes de API + JSON Schema (todos os recursos)
│   ├── ApiIntegrationTests.java               ← testes de integração existentes
│   ├── FiapEsgSpringApplicationTests.java
│   └── JsonTestUtils.java
└── resources/
    ├── features/
    │   ├── empresa.feature                    ← cenários BDD: Empresa
    │   ├── equipamento.feature                ← cenários BDD: Equipamento
    │   ├── alerta.feature                     ← cenários BDD: Alerta
    │   ├── consumo_energia.feature            ← cenários BDD: Consumo de Energia
    │   └── projeto_compensacao.feature        ← cenários BDD: Projeto de Compensação
    └── schemas/
        ├── empresa-schema.json                ← JSON Schema contrato: Empresa
        ├── empresa-lista-schema.json
        ├── equipamento-schema.json            ← JSON Schema contrato: Equipamento
        ├── alerta-schema.json                 ← JSON Schema contrato: Alerta
        ├── alerta-lista-schema.json
        ├── consumo-energia-schema.json        ← JSON Schema contrato: Consumo de Energia
        └── projeto-compensacao-schema.json    ← JSON Schema contrato: Projeto de Compensação
```

---

## Cenários BDD implementados

### `empresa.feature` — 9 cenários
| # | Tipo | Descrição |
|---|---|---|
| 1 | ✅ Positivo | Criar empresa com dados válidos |
| 2 | ✅ Positivo | Listar todas as empresas cadastradas |
| 3 | ✅ Positivo | Buscar empresa existente por ID |
| 4 | ✅ Positivo | Atualizar dados de uma empresa existente |
| 5 | ✅ Positivo | Excluir uma empresa existente |
| 6 | ❌ Negativo | Criar empresa sem o campo obrigatório "nome" |
| 7 | ❌ Negativo | Criar empresa com CNPJ excedendo tamanho máximo |
| 8 | ❌ Negativo | Buscar empresa com ID inexistente |
| 9 | ❌ Negativo | Acessar endpoint sem autenticação |

### `equipamento.feature` — 7 cenários
| # | Tipo | Descrição |
|---|---|---|
| 1 | ✅ Positivo | Criar equipamento com dados válidos |
| 2 | ✅ Positivo | Listar equipamentos filtrando por empresa |
| 3 | ✅ Positivo | Buscar equipamento por ID |
| 4 | ✅ Positivo | Atualizar status de um equipamento |
| 5 | ✅ Positivo | Excluir um equipamento |
| 6 | ❌ Negativo | Criar equipamento sem campos obrigatórios |
| 7 | ❌ Negativo | Buscar equipamento com ID inexistente |

### `alerta.feature` — 9 cenários
| # | Tipo | Descrição |
|---|---|---|
| 1 | ✅ Positivo | Criar alerta com dados válidos |
| 2 | ✅ Positivo | Listar alertas com status ABERTO |
| 3 | ✅ Positivo | Buscar alerta por ID |
| 4 | ✅ Positivo | Atualizar status de alerta para RESOLVIDO |
| 5 | ✅ Positivo | Excluir alerta existente |
| 6 | ✅ Positivo | Filtrar alertas por equipamento |
| 7 | ❌ Negativo | Criar alerta sem campos obrigatórios |
| 8 | ❌ Negativo | Criar alerta com mensagem > 250 caracteres |
| 9 | ❌ Negativo | Buscar alerta com ID inexistente |

### `consumo_energia.feature` — 4 cenários
| # | Tipo | Descrição |
|---|---|---|
| 1 | ✅ Positivo | Registrar leitura de consumo com dados válidos |
| 2 | ✅ Positivo | Listar registros de consumo |
| 3 | ❌ Negativo | Registrar consumo sem campo obrigatório "consumoKwh" |
| 4 | ❌ Negativo | Registrar consumo com valor negativo |

### `projeto_compensacao.feature` — 4 cenários
| # | Tipo | Descrição |
|---|---|---|
| 1 | ✅ Positivo | Criar projeto de reflorestamento com dados válidos |
| 2 | ✅ Positivo | Listar projetos de compensação |
| 3 | ❌ Negativo | Criar projeto sem campo obrigatório "empresaId" |
| 4 | ❌ Negativo | Buscar projeto com ID inexistente |

**Total: 33 cenários BDD**

---

## Cobertura dos testes de API (ApiContractTests)

Para cada recurso, são validados:

| Verificação | Empresa | Equipamento | Consumo | Alerta | Projeto |
|---|:---:|:---:|:---:|:---:|:---:|
| GET lista — status 200 | ✅ | ✅ | ✅ | ✅ | ✅ |
| GET lista — JSON Schema | ✅ | — | — | ✅ | — |
| GET por ID — status 200 | ✅ | ✅ | ✅ | ✅ | ✅ |
| GET por ID — JSON Schema | ✅ | ✅ | ✅ | ✅ | ✅ |
| POST — status 201 | ✅ | ✅ | ✅ | ✅ | ✅ |
| POST — Location header | ✅ | ✅ | ✅ | ✅ | ✅ |
| POST — JSON Schema | ✅ | ✅ | ✅ | ✅ | ✅ |
| PUT — status 200 | ✅ | — | ✅ | ✅ | ✅ |
| DELETE — status 204 | ✅ | ✅ | ✅ | ✅ | ✅ |
| Filtro por query param | — | ✅ | ✅ | ✅ | ✅ |
| Validação 400 (campos) | ✅ | ✅ | ✅ | ✅ | ✅ |
| 404 ID inexistente | ✅ | ✅ | — | ✅ | ✅ |
| 401 sem autenticação | ✅ | ✅ | ✅ | ✅ | ✅ |

---

## CI/CD — GitHub Actions

O pipeline `.github/workflows/ci-tests.yml` executa automaticamente em:
- **Push** para qualquer branch
- **Pull Request** para `main` ou `staging`

### Jobs do pipeline

| Job | Serviço | Java | O que roda |
|---|---|---|---|
| `tests-esg-api` | ESG Spring (MongoDB) | 17 | Todos os testes + JaCoCo + Cucumber HTML |
| `tests-entregas-api` | Entregas (JPA/H2) | 21 | Testes do microserviço de entregas |

### Artefatos publicados por execução

- `jacoco-report` — cobertura de código (HTML)
- `cucumber-report` — relatório BDD Cucumber (HTML + JSON)
- `surefire-report` — resultados JUnit (XML)
- `surefire-report-entregas` — resultados do microserviço de entregas

---

## Configuração de segurança nos testes

A API usa HTTP Basic Authentication. Nos testes, as credenciais são:

```
Username: admin
Password: admin123
```

Configuradas em `src/main/resources/application-test.yaml` e passadas
nos testes via `SecurityMockMvcRequestPostProcessors.httpBasic(USERNAME, PASSWORD)`.

---

## Tecnologias utilizadas nos testes

| Biblioteca | Versão | Finalidade |
|---|---|---|
| JUnit 5 | (via Spring Boot 3.2.4) | Framework de testes |
| Mockito | (via Spring Boot 3.2.4) | Mocks e stubs |
| Spring MockMvc | (via Spring Boot 3.2.4) | Testes de camada HTTP |
| Spring Security Test | (via Spring Boot 3.2.4) | Suporte a Basic Auth em testes |
| AssertJ | (via Spring Boot 3.2.4) | Asserções fluentes |
| Cucumber Java | 7.18.0 | BDD step definitions |
| Cucumber Spring | 7.18.0 | Integração Cucumber + Spring |
| Cucumber JUnit Platform | 7.18.0 | Runner JUnit 5 para Cucumber |
| JUnit Platform Suite | (via Spring Boot 3.2.4) | Suite runner |
| networknt json-schema-validator | 1.4.3 | Validação de JSON Schema (contrato) |
| JaCoCo | 0.8.12 | Cobertura de código |
