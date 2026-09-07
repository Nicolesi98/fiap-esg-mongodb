# FIAP - Documentação de Modelagem NoSQL & Operações CRUD no MongoDB

## Projeto: Plataforma de Gestão e Eficiência Energética ESG (fiap-esg-mongodb)

---

## 1. Alinhamento ao Tema ESG (Environmental, Social and Governance)

Esta aplicação foi concebida para atender aos três pilares da agenda ESG no contexto corporativo e industrial moderno:

```
                      ┌──────────────────────────────────────────────┐
                      │             PLATAFORMA FIAP ESG              │
                      └──────────────────────┬───────────────────────┘
                                             │
         ┌───────────────────────────────────┼───────────────────────────────────┐
         ▼                                   ▼                                   ▼
┌───────────────────┐               ┌───────────────────┐               ┌───────────────────┐
│ E - ENVIRONMENTAL │               │    S - SOCIAL     │               │  G - GOVERNANCE   │
├───────────────────┤               ├───────────────────┤               ├───────────────────┤
│ • Telemetria de   │               │ • Monitoramento   │               │ • Rating ESG      │
│   eletricidade    │               │   de ruído e      │               │   corporativo     │
│   (kWh) e água    │               │   conforto térmico│               │   (AAA a CCC)     │
│ • Rastreamento    │               │   ocupacional     │               │ • Rastreamento de │
│   de pegada de    │               │ • Inclusão social │               │   metas Net-Zero  │
│   carbono (CO2e)  │               │   e remuneração de│               │ • Auditorias de   │
│ • Geração solar   │               │   cooperativas de │               │   conformidade    │
│   e biocombustível│               │   reciclagem      │               │   GRI / ISO 14001 │
│ • Reflorestamento │               │ • Impacto em      │               │ • Gestão de risco │
│   e créditos I-REC│               │   comunidades     │               │   e conformidade  │
└───────────────────┘               └───────────────────┘               └───────────────────┘
```

---

## 2. Adequação da Estrutura de Dados NoSQL ao Problema

Em bancos relacionais tradicionais (SQL), a necessidade de armazenar tipos de equipamentos com sensores distintos e diferentes telemetrias exigiria tabelas com dezenas de colunas nulas (Anti-Pattern: *Sparse Columns*) ou dezenas de tabelas intermediárias com múltiplos joins custosos (*Table-per-Type* / *Entity-Attribute-Value*).

No **MongoDB**, o uso de documentos BSON traz benefícios arquiteturais determinantes:
1. **Padrão Polimórfico (Polymorphic Pattern)**: Documentos dentro de uma mesma collection compartilham campos comuns de controle (ex: `empresa_id`, `status`), mas carregam especificações técnicas e telemetria altamente customizadas por tipo.
2. **Subdocumentos Embutidos (Embedding)**: Metas de sustentabilidade, scores ESG e histórico de tratativas de incidentes são armazenados dentro do próprio documento pai, garantindo leituras atômicas em uma única operação de I/O (evitando JOINs).
3. **Escalabilidade Horizontal e Time-Series**: Leituras de telemetria e sensores IoT podem ser ingeridas em alto volume sem gargalos de locks relacionais.

---

## 3. As 5 Collections e Modelo Flexível Implementado

| # | Collection | Finalidade | Demonstração do Modelo Flexível / Polimórfico |
|---|---|---|---|
| 1 | `empresas` | Cadastro corporativo, ratings ESG e compromissos institucionais. | Documentos variam entre metas de neutralidade de carbono, comitês de governança e certificações setoriais distintas (ISO 14001, B-Corp, PRI). |
| 2 | `equipamentos` | Ativos operacionais e sensores monitorados. | **Polimorfismo por Categoria**: <br>• `CLIMATIZACAO`: Gás refrigerante (GWP), BTU, zona térmica.<br>• `ENERGIA_SOLAR`: Capacidade kWp, irradiação $W/m^2$, eficiência.<br>• `MOBILIDADE_ELETRICA`: Potência kW, conectores (CCS2/Type2).<br>• `TI_DATACENTER`: PUE alvo, refrigeração líquida.<br>• `MAQUINARIO_INDUSTRIAL`: Classe IE4, pressão bar, emissões. |
| 3 | `leituras_consumo` | Telemetria temporal e medições ecológicas. | **Polimorfismo por Tipo de Recurso**: <br>• `ENERGIA_ELETRICA`: Consumo kWh, ociosidade, fator de emissão CO2.<br>• `AGUA`: Volume $m^3$, fonte de captação, percentual de reuso.<br>• `COMBUSTIVEL_FOSSIL`: Litros, emissão Scope 1, horímetro.<br>• `GERACAO_SOLAR_RENOVAVEL`: Energia gerada kWh, CO2 evitado. |
| 4 | `alertas_esg` | Incidentes ecológicos, operacionais e de governança. | Alertas categorizados nos 3 pilares ESG (`ENVIRONMENTAL`, `SOCIAL`, `GOVERNANCE`), contendo array embutido de auditoria `historico_acoes: [{ data, responsavel, acao, status }]`. |
| 5 | `projetos_compensacao` | Ações de neutralização de carbono e responsabilidade socioambiental. | **Polimorfismo de Projetos**: <br>• `REFLORESTAMENTO_NATIVO`: Bioma, hectares, mudas nativas, GeoJSON.<br>• `ENERGIA_RENOVAVEL_IREC`: Geração MWh, certificação I-REC/Gold Standard.<br>• `ECONOMIA_CIRCULAR_RECICLAGEM`: Cooperativas parceiras, toneladas recicladas, famílias beneficiadas. |

---

## 4. Comandos de Criação das Collections com JSON Schema Validation

A integridade dos dados é garantida através de validação JSON Schema a nível de banco de dados:

```javascript
use('fiap_esg');

// 1. Collection 'empresas'
db.createCollection('empresas', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['nome', 'cnpj', 'setor', 'status_adesao_esg'],
            properties: {
                nome: { bsonType: 'string' },
                cnpj: { bsonType: 'string', pattern: '^\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}$' },
                setor: { bsonType: 'string' },
                status_adesao_esg: { enum: ['ATIVO', 'EM_AVALIACAO', 'SUSPENSO'] },
                esg_rating: { bsonType: 'object' },
                metas_sustentabilidade: { bsonType: 'array' }
            }
        }
    }
});
db.empresas.createIndex({ cnpj: 1 }, { unique: true });
db.empresas.createIndex({ setor: 1 });

// 2. Collection 'equipamentos' (Modelo Flexível)
db.createCollection('equipamentos', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['empresa_id', 'nome', 'categoria', 'status', 'limite_consumo_kwh', 'limite_ociosidade_min'],
            properties: {
                empresa_id: { bsonType: ['string', 'objectId'] },
                nome: { bsonType: 'string' },
                categoria: { enum: ['CLIMATIZACAO', 'ENERGIA_SOLAR', 'MOBILIDADE_ELETRICA', 'TI_DATACENTER', 'MAQUINARIO_INDUSTRIAL'] },
                status: { enum: ['LIGADO', 'DESLIGADO', 'ATENCAO', 'MANUTENCAO'] },
                limite_consumo_kwh: { bsonType: ['double', 'int', 'decimal'] },
                limite_ociosidade_min: { bsonType: 'int' },
                especificacoes_tecnicas: { bsonType: 'object' } // Flexível por categoria
            }
        }
    }
});
db.equipamentos.createIndex({ empresa_id: 1 });
db.equipamentos.createIndex({ categoria: 1, status: 1 });

// 3. Collection 'leituras_consumo' (Telemetria Flexível)
db.createCollection('leituras_consumo', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['equipamento_id', 'tipo_recurso', 'data_hora'],
            properties: {
                equipamento_id: { bsonType: ['string', 'objectId'] },
                tipo_recurso: { enum: ['ENERGIA_ELETRICA', 'AGUA', 'COMBUSTIVEL_FOSSIL', 'GERACAO_SOLAR_RENOVAVEL'] },
                data_hora: { bsonType: 'date' },
                consumo_kwh: { bsonType: ['double', 'int', 'decimal'] },
                pegada_carbono_kg_co2e: { bsonType: ['double', 'int', 'decimal'] },
                telemetria_detalhada: { bsonType: 'object' } // Flexível por recurso
            }
        }
    }
});
db.leituras_consumo.createIndex({ equipamento_id: 1, data_hora: -1 });

// 4. Collection 'alertas_esg' (Com Histórico Embutido)
db.createCollection('alertas_esg', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['equipamento_id', 'pilar_esg', 'tipo_alerta', 'mensagem', 'severidade', 'status'],
            properties: {
                pilar_esg: { enum: ['ENVIRONMENTAL', 'SOCIAL', 'GOVERNANCE'] },
                severidade: { enum: ['BAIXA', 'MEDIA', 'ALTA', 'CRITICA'] },
                status: { enum: ['ABERTO', 'EM_ANALISE', 'MITIGADO', 'RESOLVIDO'] },
                historico_acoes: { bsonType: 'array' }
            }
        }
    }
});
db.alertas_esg.createIndex({ equipamento_id: 1, status: 1 });

// 5. Collection 'projetos_compensacao' (Iniciativas Net-Zero)
db.createCollection('projetos_compensacao', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['empresa_id', 'nome_iniciativa', 'tipo_projeto', 'status', 'meta_tco2e_compensar'],
            properties: {
                tipo_projeto: { enum: ['REFLORESTAMENTO_NATIVO', 'ENERGIA_RENOVAVEL_IREC', 'ECONOMIA_CIRCULAR_RECICLAGEM', 'PRESERVACAO_BIODIVERSIDADE'] },
                status: { enum: ['PLANEJADO', 'EM_ANDAMENTO', 'CONCLUIDO', 'AUDITADO'] },
                detalhes_execucao: { bsonType: 'object' }
            }
        }
    }
});
db.projetos_compensacao.createIndex({ empresa_id: 1 });
db.projetos_compensacao.createIndex({ tipo_projeto: 1, status: 1 });
```

---

## 5. Inserção Inicial de Dados (10 Documentos por Collection = 50 Documentos)

Os 50 documentos completos estão implementados e parametrizados no script:
`scripts/mongodb/02_insert_initial_data.mongodb.js`

### Exemplos Representativos de Documentos Polimórficos:

#### Exemplo 1: Equipamento com Especificação de Climatização (Gás Ecológico e GWP)
```json
{
  "_id": ObjectId("661000000000000000000001"),
  "empresa_id": ObjectId("660000000000000000000001"),
  "nome": "Chiller Central EcoCooling 500TR",
  "categoria": "CLIMATIZACAO",
  "status": "LIGADO",
  "limite_consumo_kwh": 50.0,
  "limite_ociosidade_min": 30,
  "especificacoes_tecnicas": {
    "potencia_btu": 6000000,
    "toneladas_refrigeracao_tr": 500,
    "gas_refrigerante": "R-32",
    "gwp_potencial_aquecimento_global": 675,
    "temperatura_alvo_celsius": 22.0
  }
}
```

#### Exemplo 2: Equipamento de Mobilidade Elétrica (Pontos de Carga e Conectores)
```json
{
  "_id": ObjectId("661000000000000000000004"),
  "empresa_id": ObjectId("660000000000000000000003"),
  "nome": "Carregador Rapido DC 150kW Frota",
  "categoria": "MOBILIDADE_ELETRICA",
  "status": "LIGADO",
  "limite_consumo_kwh": 160.0,
  "limite_ociosidade_min": 20,
  "especificacoes_tecnicas": {
    "potencia_recarga_kw": 150,
    "conectores": ["CCS2", "CHAdeMO"],
    "quantidade_pontos_recarga": 2
  }
}
```

#### Exemplo 3: Alerta ESG com Histórico Embutido de Mitigação
```json
{
  "_id": ObjectId("663000000000000000000004"),
  "equipamento_id": ObjectId("661000000000000000000001"),
  "pilar_esg": "SOCIAL",
  "tipo_alerta": "CONFORTO_TERMICO_SEGURANCA",
  "mensagem": "Temperatura na sala de operadores ultrapassou 28C.",
  "severidade": "ALTA",
  "status": "RESOLVIDO",
  "historico_acoes": [
    {
      "data": ISODate("2024-05-05T13:20:00Z"),
      "responsavel": "Sensor IoT",
      "acao": "Deteccao de transgressao da NR-17",
      "status_resultante": "ABERTO"
    },
    {
      "data": ISODate("2024-05-05T14:00:00Z"),
      "responsavel": "Equipe de Manutencao",
      "acao": "Substituicao de valvula de expansao",
      "status_resultante": "RESOLVIDO"
    }
  ]
}
```

---

## 6. Demonstração Completa das Operações CRUD (C, R, U, D)

Todas as operações abaixo estão contidas no script executável `scripts/mongodb/03_crud_operations.mongodb.js`:

### 6.1 Collection: `empresas`
* **CREATE**:
  ```javascript
  db.empresas.insertOne({
    nome: "EcoVanguarda Tecnologias Limpas",
    cnpj: "01.234.567/0001-89",
    setor: "TECNOLOGIA",
    status_adesao_esg: "ATIVO",
    esg_rating: { score_geral: 89.0, classificacao: "AA" }
  });
  ```
* **READ (Filtro e Agregação)**:
  ```javascript
  // Filtro por subdocumento e operador $in
  db.empresas.find({
    setor: { $in: ["TECNOLOGIA", "ENERGIA"] },
    "esg_rating.classificacao": { $in: ["AAA", "AA"] }
  });

  // Agregação: Média de Score ESG por Setor
  db.empresas.aggregate([
    {
      $group: {
        _id: "$setor",
        total_empresas: { $sum: 1 },
        media_score_esg: { $avg: "$esg_rating.score_geral" }
      }
    },
    { $sort: { media_score_esg: -1 } }
  ]);
  ```
* **UPDATE**:
  ```javascript
  db.empresas.updateOne(
    { cnpj: "01.234.567/0001-89" },
    {
      $set: { "esg_rating.score_ambiental": 93.5 },
      $push: {
        metas_sustentabilidade: {
          descricao: "Reciclagem de 100% dos Laptops Corporativos",
          ano_alvo: NumberInt(2026),
          percentual_conclusao: 10.0
        }
      }
    }
  );
  ```
* **DELETE**:
  ```javascript
  db.empresas.deleteOne({ cnpj: "01.234.567/0001-89" });
  ```

---

### 6.2 Collection: `equipamentos`
* **CREATE**:
  ```javascript
  db.equipamentos.insertOne({
    empresa_id: ObjectId("660000000000000000000001"),
    nome: "Totem de Recarga Rapida Solar 50kW",
    categoria: "MOBILIDADE_ELETRICA",
    status: "LIGADO",
    limite_consumo_kwh: 55.0,
    limite_ociosidade_min: NumberInt(15),
    especificacoes_tecnicas: {
      potencia_recarga_kw: 50,
      conectores: ["Type 2", "CCS2"]
    }
  });
  ```
* **READ (Filtro em Atributo Polimórfico e Agregação)**:
  ```javascript
  // Buscar apenas climatizadores que usam o gás ecológico R-32
  db.equipamentos.find({
    categoria: "CLIMATIZACAO",
    "especificacoes_tecnicas.gas_refrigerante": "R-32"
  });

  // Agregação: Média de limite de consumo por categoria de equipamento
  db.equipamentos.aggregate([
    {
      $group: {
        _id: "$categoria",
        quantidade: { $sum: 1 },
        media_limite_kwh: { $avg: "$limite_consumo_kwh" }
      }
    }
  ]);
  ```
* **UPDATE**:
  ```javascript
  db.equipamentos.updateOne(
    { nome: "Totem de Recarga Rapida Solar 50kW" },
    {
      $set: { status: "MANUTENCAO" },
      $inc: { limite_consumo_kwh: 5.0 }
    }
  );
  ```
* **DELETE**:
  ```javascript
  db.equipamentos.deleteOne({ nome: "Totem de Recarga Rapida Solar 50kW" });
  ```

---

### 6.3 Collection: `leituras_consumo`
* **CREATE**:
  ```javascript
  db.leituras_consumo.insertOne({
    equipamento_id: ObjectId("661000000000000000000001"),
    tipo_recurso: "AGUA",
    data_hora: new Date(),
    consumo_kwh: 5.0,
    pegada_carbono_kg_co2e: 0.45,
    telemetria_detalhada: {
      volume_m3: 15.2,
      fonte_captacao: "AGUA_DE_CHUVA_FILTRADA",
      percentual_reuso: 100.0
    }
  });
  ```
* **READ (Filtros de Intervalo e Totalizadores)**:
  ```javascript
  // Filtrar medições críticas com emissão de carbono >= 5 kg CO2e
  db.leituras_consumo.find({ pegada_carbono_kg_co2e: { $gte: 5.0 } });

  // Agregação: Total de Consumo kWh e Emissões por Recurso
  db.leituras_consumo.aggregate([
    {
      $group: {
        _id: "$tipo_recurso",
        total_leituras: { $sum: 1 },
        consumo_total_kwh: { $sum: "$consumo_kwh" },
        carbono_total_kg: { $sum: "$pegada_carbono_kg_co2e" }
      }
    }
  ]);
  ```
* **UPDATE**:
  ```javascript
  db.leituras_consumo.updateOne(
    { _id: ObjectId("662000000000000000000099") },
    {
      $set: { "telemetria_detalhada.volume_m3": 18.0 },
      $inc: { pegada_carbono_kg_co2e: 0.15 }
    }
  );
  ```
* **DELETE**:
  ```javascript
  db.leituras_consumo.deleteOne({ _id: ObjectId("662000000000000000000099") });
  ```

---

### 6.4 Collection: `alertas_esg`
* **CREATE**:
  ```javascript
  db.alertas_esg.insertOne({
    equipamento_id: ObjectId("661000000000000000000003"),
    pilar_esg: "GOVERNANCE",
    tipo_alerta: "AUDITORIA_SEGURANCA_DADOS",
    mensagem: "Requer auditoria de conformidade de descarte.",
    severidade: "MEDIA",
    status: "ABERTO",
    historico_acoes: [
      { data: new Date(), responsavel: "Auditor TI", acao: "Abertura de pendencia", status_resultante: "ABERTO" }
    ]
  });
  ```
* **READ**:
  ```javascript
  // Alertas críticos em aberto
  db.alertas_esg.find({ status: "ABERTO", severidade: { $in: ["ALTA", "CRITICA"] } });

  // Agregação por Pilar ESG
  db.alertas_esg.aggregate([
    {
      $group: {
        _id: { pilar: "$pilar_esg", status: "$status" },
        quantidade: { $sum: 1 }
      }
    }
  ]);
  ```
* **UPDATE (Modificação Atômica e Adição ao Histórico)**:
  ```javascript
  db.alertas_esg.updateOne(
    { tipo_alerta: "AUDITORIA_SEGURANCA_DADOS" },
    {
      $set: { status: "RESOLVIDO" },
      $push: {
        historico_acoes: {
          data: new Date(),
          responsavel: "Coordenador ESG",
          acao: "Certificado de descarte anexado",
          status_resultante: "RESOLVIDO"
        }
      }
    }
  );
  ```
* **DELETE**:
  ```javascript
  db.alertas_esg.deleteOne({ tipo_alerta: "AUDITORIA_SEGURANCA_DADOS" });
  ```

---

### 6.5 Collection: `projetos_compensacao`
* **CREATE**:
  ```javascript
  db.projetos_compensacao.insertOne({
    empresa_id: ObjectId("660000000000000000000001"),
    nome_iniciativa: "Plantando o Futuro - Restauração Fluvial",
    tipo_projeto: "REFLORESTAMENTO_NATIVO",
    status: "PLANEJADO",
    meta_tco2e_compensar: 1200.0,
    investimento_brl: 150000.00
  });
  ```
* **READ**:
  ```javascript
  // Projetos com grande porte (meta >= 2000 tCO2e)
  db.projetos_compensacao.find({ meta_tco2e_compensar: { $gte: 2000.0 } });

  // Agregação: Total Investido e Carbono Compensado por Tipo de Projeto
  db.projetos_compensacao.aggregate([
    {
      $group: {
        _id: "$tipo_projeto",
        total_projetos: { $sum: 1 },
        investimento_total_brl: { $sum: "$investimento_brl" },
        carbono_neutralizado_acumulado_tco2e: { $sum: "$tco2e_compensado_acumulado" }
      }
    },
    { $sort: { carbono_neutralizado_acumulado_tco2e: -1 } }
  ]);
  ```
* **UPDATE**:
  ```javascript
  db.projetos_compensacao.updateOne(
    { nome_iniciativa: "Plantando o Futuro - Restauração Fluvial" },
    {
      $set: { status: "EM_ANDAMENTO" },
      $inc: { tco2e_compensado_acumulado: 350.0 }
    }
  );
  ```
* **DELETE**:
  ```javascript
  db.projetos_compensacao.deleteOne({ nome_iniciativa: "Plantando o Futuro - Restauração Fluvial" });
  ```

---

## 7. Como Executar via Docker Compose & mongosh

### 1. Subindo a infraestrutura com Docker Compose
```bash
docker compose up -d
```
O MongoDB 7.0 inicializará automaticamente o banco `fiap_esg` executando o script `01_init_fiap_esg.js`, criando as 5 collections e inserindo os 50 documentos.

### 2. Acessando a Interface Gráfica Web (Mongo Express)
Acesse no navegador:
```
http://localhost:8081
```

### 3. Executando os Scripts no mongosh ou MongoDB Compass
```bash
# Executando criação e validações:
mongosh mongodb://localhost:27017/fiap_esg scripts/mongodb/01_setup_database_and_collections.mongodb.js

# Inserindo dados iniciais (50 documentos):
mongosh mongodb://localhost:27017/fiap_esg scripts/mongodb/02_insert_initial_data.mongodb.js

# Executando operações CRUD completas:
mongosh mongodb://localhost:27017/fiap_esg scripts/mongodb/03_crud_operations.mongodb.js
```
