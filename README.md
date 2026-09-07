# FIAP ESG - Monitoramento de Sustentabilidade e Eficiência Energética (MongoDB)

[![Java 17+](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.2.4](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB 7.0](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://www.docker.com/)

Repositório da aplicação de gestão e monitoramento **ESG (Environmental, Social and Governance)**, migrada da arquitetura relacional para o banco de dados orientado a documentos **MongoDB**, com suporte a **5 collections**, **modelo flexível/polimórfico**, telemetria ecológica e orquestração completa via **Docker Compose**.

---

## 1. Visão Geral e Alinhamento ESG

A plataforma monitora ativos corporativos e industriais com foco em metas de descarbonização, eficiência energética e responsabilidade socioambiental:

* **E (Environmental)**: Monitoramento de consumo de energia ($kWh$), consumo e reuso de água ($m^3$), queima de combustíveis e emissões de carbono ($kg\ CO_2e$ e $tCO_2e$), medição de geração fotovoltaica e projetos de reflorestamento nativo.
* **S (Social)**: Alertas de conforto térmico (NR-17) e ruído ocupacional (NR-15) em linhas de produção; projetos de inclusão de cooperativas de reciclagem e catadores.
* **G (Governance)**: Ratings e scores de compliance ESG corporativo (AAA a CCC), metas auditadas de neutralidade (Net-Zero), gestão de licenciamento ambiental (CETESB/IBAMA) e conformidade com padrões GRI e ISO 14001.

---

## 2. Estrutura das 5 Collections no MongoDB

A modelagem aproveita o **Padrão Polimórfico (Polymorphic Pattern)** e subdocumentos embutidos:

```
┌─────────────────┐       1:N       ┌──────────────────┐       1:N       ┌──────────────────────┐
│    empresas     ├────────────────►│   equipamentos   ├────────────────►│   leituras_consumo   │
│                 │                 │ (Modelo Flexível)│                 │ (Telemetria Flexível)│
└────────┬────────┘                 └────────┬─────────┘                 └──────────────────────┘
         │                                   │
         │ 1:N                               │ 1:N
         ▼                                   ▼
┌─────────────────────────┐         ┌──────────────────┐
│  projetos_compensacao   │         │   alertas_esg    │
│ (Iniciativas Net-Zero)  │         │(Histórico Imerso)│
└─────────────────────────┘         └──────────────────┘
```

1. **`empresas`**: Dados cadastrais, setor, status de adesão, rating ESG detalhado (`esg_rating`), metas de sustentabilidade (`metas_sustentabilidade`) e certificações internacionais.
2. **`equipamentos`**: Ativos cadastrados com **esquema flexível por categoria**:
   * `CLIMATIZACAO`: Gás refrigerante (GWP), BTU, temperatura alvo.
   * `ENERGIA_SOLAR`: Capacidade kWp, irradiação $W/m^2$, eficiência do painel.
   * `MOBILIDADE_ELETRICA`: Potência de recarga (kW), tipos de conectores (CCS2, Type 2).
   * `TI_DATACENTER`: PUE alvo, servidores por rack, refrigeração líquida.
   * `MAQUINARIO_INDUSTRIAL`: Classe de eficiência IE4, pressão e combustíveis.
3. **`leituras_consumo`**: Telemetria temporal e medições ecológicas:
   * Eletricidade ($kWh$, ociosidade, fator SIN de emissão de carbono).
   * Água ($m^3$, fonte de captação e índice de reuso).
   * Combustíveis (biodiesel, diesel, gás natural e emissões Scope 1).
   * Geração renovável (energia gerada e $CO_2$ evitado).
4. **`alertas_esg`**: Incidentes classificados por Pilar ESG (`ENVIRONMENTAL`, `SOCIAL`, `GOVERNANCE`), severidade (`BAIXA` a `CRITICA`), status e array de auditoria `historico_acoes: [{ data, responsavel, acao, status_resultante }]`.
5. **`projetos_compensacao`**: Projetos de compensação ativa de emissões:
   * Reflorestamento nativo com biomas (Mata Atlântica, Cerrado, Amazônia) e coordenadas GeoJSON.
   * Geração limpa e emissão de créditos I-REC / Gold Standard.
   * Economia circular com cooperativas parceiras e toneladas de resíduos reciclados.

---

## 3. Subindo o MongoDB via Docker Compose

A infraestrutura é configurada para inicialização imediata:

```bash
docker compose up -d
```

### O que o Docker Compose executa:
1. **Container `fiap-esg-mongodb` (MongoDB 7.0)**:
   * Porta: `27017:27017`
   * Banco: `fiap_esg`
   * Persistência: volume nomeado `mongodb_data`
   * **Inicialização Automática**: Executa automaticamente o script `scripts/docker-init/01_init_fiap_esg.js`, criando as 5 collections com validação JSON Schema e populando **50 documentos iniciais (10 por collection)**.
2. **Container `fiap-esg-mongo-express` (Interface Gráfica Web)**:
   * Porta: `8081:8081`
   * Acesso imediato no navegador: **`http://localhost:8081`** para visualização e gerenciamento visual das collections e documentos.

### Para parar os containers:
```bash
docker compose down
```

---

## 4. Scripts MongoDB (mongosh / Compass)

Os scripts estão organizados na pasta [`scripts/mongodb/`](scripts/mongodb):

| Arquivo | Descrição |
|---|---|
| [`01_setup_database_and_collections.mongodb.js`](scripts/mongodb/01_setup_database_and_collections.mongodb.js) | Criação do banco `fiap_esg`, definição das 5 collections com JSON Schema Validation e índices. |
| [`02_insert_initial_data.mongodb.js`](scripts/mongodb/02_insert_initial_data.mongodb.js) | Inserção dos 50 documentos (10 por collection) com dados realistas e esquemas polimórficos. |
| [`03_crud_operations.mongodb.js`](scripts/mongodb/03_crud_operations.mongodb.js) | Demonstração detalhada de operações Create, Read (filtros e agregação), Update e Delete para todas as 5 collections. |

### Execução manual via mongosh:
```bash
# Criar coleções e índices
mongosh mongodb://localhost:27017/fiap_esg scripts/mongodb/01_setup_database_and_collections.mongodb.js

# Inserir os 50 documentos
mongosh mongodb://localhost:27017/fiap_esg scripts/mongodb/02_insert_initial_data.mongodb.js

# Executar todas as operações CRUD
mongosh mongodb://localhost:27017/fiap_esg scripts/mongodb/03_crud_operations.mongodb.js
```

> Para a documentação completa de todas as consultas, operadores e agregações utilizadas, consulte [`MONGODB_CRUD_OPERATIONS.md`](MONGODB_CRUD_OPERATIONS.md).

---

## 5. Executando a Aplicação Spring Boot

Com o MongoDB ativo (via Docker Compose na porta 27017):

```bash
# Compilar e executar os testes unitários
./mvnw.cmd clean test

# Iniciar o servidor Spring Boot
./mvnw.cmd spring-boot:run
```

A aplicação subirá na porta `8080` com as credenciais padrão:
* **Usuário**: `admin`
* **Senha**: `admin123`

### Swagger / Documentação OpenAPI
Acesse a documentação interativa da API em:
```
http://localhost:8080/swagger-ui/index.html
```

---

## 6. Endpoints REST da Aplicação

| Método | Endpoint | Descrição |
|---|---|---|
| `GET / POST` | `/empresas` | Listagem e cadastro de empresas e scores ESG |
| `GET / PUT / DELETE` | `/empresas/{id}` | Busca, atualização e exclusão de empresa |
| `GET / POST` | `/equipamentos` | Listagem (com filtro `?empresaId=...`) e cadastro |
| `GET / PUT / DELETE` | `/equipamentos/{id}` | Busca, atualização e exclusão de equipamento |
| `GET / POST` | `/consumos-energia` | Listagem (com filtro `?equipamentoId=...`) e telemetria |
| `GET / PUT / DELETE` | `/consumos-energia/{id}` | Busca, atualização e exclusão de leitura |
| `GET / POST` | `/alertas` | Listagem (com filtros `?equipamentoId=...&status=...`) |
| `GET / PUT / DELETE` | `/alertas/{id}` | Busca, atualização e exclusão de alerta |
| `GET / POST` | `/projetos-compensacao` | Listagem (com filtros `?empresaId=...&tipoProjeto=...`) |
| `GET / PUT / DELETE` | `/projetos-compensacao/{id}` | Busca, atualização e exclusão de projeto |
