/**
 * FIAP ESG - Script de Inicialização Automática do MongoDB via Docker
 * Montado em /docker-entrypoint-initdb.d/01_init_fiap_esg.js
 */

db = db.getSiblingDB('fiap_esg');

print('>>> [DOCKER ENTRYPOINT] Inicializando banco de dados fiap_esg com 5 collections...');

// 1. Criação das Collections com Validação de Schema
db.createCollection('empresas', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['nome', 'cnpj', 'setor', 'status_adesao_esg'],
            properties: {
                nome: { bsonType: 'string' },
                cnpj: { bsonType: 'string' },
                setor: { bsonType: 'string' },
                status_adesao_esg: { enum: ['ATIVO', 'EM_AVALIACAO', 'SUSPENSO'] },
                esg_rating: { bsonType: 'object' },
                metas_sustentabilidade: { bsonType: 'array' },
                certificacoes: { bsonType: 'array' },
                data_cadastro: { bsonType: 'date' }
            }
        }
    }
});
db.empresas.createIndex({ cnpj: 1 }, { unique: true });
db.empresas.createIndex({ setor: 1 });

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
                localizacao: { bsonType: 'object' },
                especificacoes_tecnicas: { bsonType: 'object' },
                data_instalacao: { bsonType: 'date' }
            }
        }
    }
});
db.equipamentos.createIndex({ empresa_id: 1 });
db.equipamentos.createIndex({ categoria: 1, status: 1 });

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
                tempo_ocioso_min: { bsonType: 'int' },
                pegada_carbono_kg_co2e: { bsonType: ['double', 'int', 'decimal'] },
                telemetria_detalhada: { bsonType: 'object' }
            }
        }
    }
});
db.leituras_consumo.createIndex({ equipamento_id: 1, data_hora: -1 });
db.leituras_consumo.createIndex({ tipo_recurso: 1 });

db.createCollection('alertas_esg', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['equipamento_id', 'pilar_esg', 'tipo_alerta', 'mensagem', 'severidade', 'status'],
            properties: {
                equipamento_id: { bsonType: ['string', 'objectId'] },
                pilar_esg: { enum: ['ENVIRONMENTAL', 'SOCIAL', 'GOVERNANCE'] },
                tipo_alerta: { bsonType: 'string' },
                mensagem: { bsonType: 'string' },
                severidade: { enum: ['BAIXA', 'MEDIA', 'ALTA', 'CRITICA'] },
                status: { enum: ['ABERTO', 'EM_ANALISE', 'MITIGADO', 'RESOLVIDO'] },
                data_hora: { bsonType: 'date' },
                dados_contextuais: { bsonType: 'object' },
                historico_acoes: { bsonType: 'array' }
            }
        }
    }
});
db.alertas_esg.createIndex({ equipamento_id: 1, status: 1 });
db.alertas_esg.createIndex({ pilar_esg: 1, severidade: 1 });

db.createCollection('projetos_compensacao', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['empresa_id', 'nome_iniciativa', 'tipo_projeto', 'status', 'meta_tco2e_compensar'],
            properties: {
                empresa_id: { bsonType: ['string', 'objectId'] },
                nome_iniciativa: { bsonType: 'string' },
                tipo_projeto: { enum: ['REFLORESTAMENTO_NATIVO', 'ENERGIA_RENOVAVEL_IREC', 'ECONOMIA_CIRCULAR_RECICLAGEM', 'PRESERVACAO_BIODIVERSIDADE'] },
                status: { enum: ['PLANEJADO', 'EM_ANDAMENTO', 'CONCLUIDO', 'AUDITADO'] },
                meta_tco2e_compensar: { bsonType: ['double', 'int', 'decimal'] },
                tco2e_compensado_acumulado: { bsonType: ['double', 'int', 'decimal'] },
                investimento_brl: { bsonType: ['double', 'int', 'decimal'] },
                detalhes_execucao: { bsonType: 'object' },
                data_inicio: { bsonType: 'date' }
            }
        }
    }
});
db.projetos_compensacao.createIndex({ empresa_id: 1 });
db.projetos_compensacao.createIndex({ tipo_projeto: 1, status: 1 });

// Inserção das 10 empresas
db.empresas.insertMany([
    {
        _id: ObjectId("660000000000000000000001"),
        nome: "EcoTech Solucoes Sustentaveis",
        cnpj: "11.111.111/0001-11",
        setor: "TECNOLOGIA",
        status_adesao_esg: "ATIVO",
        esg_rating: { score_geral: 92.5, score_ambiental: 95.0, score_social: 88.0, score_governanca: 94.5, classificacao: "AAA" },
        metas_sustentabilidade: [
            { descricao: "Neutralidade de Carbono (Net-Zero)", ano_alvo: NumberInt(2030), percentual_conclusao: 65.0 },
            { descricao: "100% de Energia Renovavel nos Datacenters", ano_alvo: NumberInt(2026), percentual_conclusao: 90.0 }
        ],
        certificacoes: ["ISO 14001", "B-Corp", "LEED Platinum"],
        data_cadastro: new Date("2023-01-15T09:00:00Z")
    },
    {
        _id: ObjectId("660000000000000000000002"),
        nome: "Energia Verde Renovaveis Ltda",
        cnpj: "22.222.222/0001-22",
        setor: "ENERGIA",
        status_adesao_esg: "ATIVO",
        esg_rating: { score_geral: 96.0, score_ambiental: 98.5, score_social: 92.0, score_governanca: 97.5, classificacao: "AAA" },
        metas_sustentabilidade: [
            { descricao: "Expansao de Parques Eolicos e Solares em 500MW", ano_alvo: NumberInt(2028), percentual_conclusao: 48.0 }
        ],
        certificacoes: ["ISO 14001", "ISO 50001", "I-REC Standard"],
        data_cadastro: new Date("2023-02-20T10:30:00Z")
    },
    {
        _id: ObjectId("660000000000000000000003"),
        nome: "Cidade Sustentavel Mobilidade e Logistica SA",
        cnpj: "33.333.333/0001-33",
        setor: "LOGISTICA",
        status_adesao_esg: "ATIVO",
        esg_rating: { score_geral: 84.0, score_ambiental: 82.0, score_social: 86.0, score_governanca: 84.0, classificacao: "AA" },
        metas_sustentabilidade: [
            { descricao: "Eletrificacao de 60% da Frota de Transporte Urbano", ano_alvo: NumberInt(2029), percentual_conclusao: 40.0 }
        ],
        certificacoes: ["ISO 14001", "ISO 39001"],
        data_cadastro: new Date("2023-03-10T14:15:00Z")
    },
    {
        _id: ObjectId("660000000000000000000004"),
        nome: "Industria Solar BR Manufatura Verde",
        cnpj: "44.444.444/0001-44",
        setor: "INDUSTRIA",
        status_adesao_esg: "ATIVO",
        esg_rating: { score_geral: 78.5, score_ambiental: 76.0, score_social: 80.0, score_governanca: 79.5, classificacao: "A" },
        metas_sustentabilidade: [
            { descricao: "Zero Aterro para Residuos Solidos Industriais", ano_alvo: NumberInt(2026), percentual_conclusao: 82.0 }
        ],
        certificacoes: ["ISO 14001", "ISO 9001"],
        data_cadastro: new Date("2023-04-05T11:00:00Z")
    },
    {
        _id: ObjectId("660000000000000000000005"),
        nome: "Consumo Inteligente Comercial e Varejo ME",
        cnpj: "55.555.555/0001-55",
        setor: "VAREJO",
        status_adesao_esg: "ATIVO",
        esg_rating: { score_geral: 71.0, score_ambiental: 70.0, score_social: 74.0, score_governanca: 69.0, classificacao: "BBB" },
        metas_sustentabilidade: [
            { descricao: "Eliminacao de Plasticos de Uso Unico em Todas as Lojas", ano_alvo: NumberInt(2026), percentual_conclusao: 88.0 }
        ],
        certificacoes: ["Selo Clima Parana"],
        data_cadastro: new Date("2023-05-18T16:00:00Z")
    },
    {
        _id: ObjectId("660000000000000000000006"),
        nome: "AgroBio Sustentabilidade e Alimentos SA",
        cnpj: "66.666.666/0001-66",
        setor: "AGRONEGOCIO",
        status_adesao_esg: "ATIVO",
        esg_rating: { score_geral: 88.0, score_ambiental: 89.0, score_social: 85.0, score_governanca: 90.0, classificacao: "AA" },
        metas_sustentabilidade: [
            { descricao: "Restauração de 5.000 Hectares de Mata Ciliar", ano_alvo: NumberInt(2030), percentual_conclusao: 52.0 }
        ],
        certificacoes: ["Rainforest Alliance", "Organico Brasil", "ISO 14001"],
        data_cadastro: new Date("2023-06-12T08:30:00Z")
    },
    {
        _id: ObjectId("660000000000000000000007"),
        nome: "BioFarma Quimica e Saude Ambiental",
        cnpj: "77.777.777/0001-77",
        setor: "FARMACEUTICO",
        status_adesao_esg: "ATIVO",
        esg_rating: { score_geral: 81.5, score_ambiental: 80.0, score_social: 85.0, score_governanca: 79.5, classificacao: "A" },
        metas_sustentabilidade: [
            { descricao: "Programa de Descarte Consciente de Medicamentos", ano_alvo: NumberInt(2027), percentual_conclusao: 65.0 }
        ],
        certificacoes: ["ISO 14001", "Boas Praticas ANVISA"],
        data_cadastro: new Date("2023-07-22T13:45:00Z")
    },
    {
        _id: ObjectId("660000000000000000000008"),
        nome: "Construcoes Verdes Engenharia Sustentavel",
        cnpj: "88.888.888/0001-88",
        setor: "CONSTRUCAO_CIVIL",
        status_adesao_esg: "EM_AVALIACAO",
        esg_rating: { score_geral: 68.0, score_ambiental: 65.0, score_social: 70.0, score_governanca: 69.0, classificacao: "BB" },
        metas_sustentabilidade: [
            { descricao: "Certificacao LEED em 100% dos Novos Canteiros", ano_alvo: NumberInt(2028), percentual_conclusao: 45.0 }
        ],
        certificacoes: ["AQUA-HQE", "PBQP-H"],
        data_cadastro: new Date("2023-08-30T10:00:00Z")
    },
    {
        _id: ObjectId("660000000000000000000009"),
        nome: "Logistica Reversa Brasil Solucoes em Reciclagem",
        cnpj: "99.999.999/0001-99",
        setor: "RECICLAGEM",
        status_adesao_esg: "ATIVO",
        esg_rating: { score_geral: 94.0, score_ambiental: 97.0, score_social: 93.0, score_governanca: 92.0, classificacao: "AAA" },
        metas_sustentabilidade: [
            { descricao: "Reciclagem de 100.000 Toneladas de Eletronicos e Plasticos", ano_alvo: NumberInt(2027), percentual_conclusao: 72.0 }
        ],
        certificacoes: ["ISO 14001", "Selo Eureciclo", "B-Corp"],
        data_cadastro: new Date("2023-09-14T15:20:00Z")
    },
    {
        _id: ObjectId("660000000000000000000010"),
        nome: "Fintech Impacto Verde Investimentos ESG",
        cnpj: "10.000.000/0001-00",
        setor: "SERVICOS_FINANCEIROS",
        status_adesao_esg: "ATIVO",
        esg_rating: { score_geral: 91.0, score_ambiental: 88.0, score_social: 92.0, score_governanca: 93.0, classificacao: "AA" },
        metas_sustentabilidade: [
            { descricao: "Destinacao de R$ 500 Milhoes para Green Bonds e Titulos Verdes", ano_alvo: NumberInt(2027), percentual_conclusao: 62.0 }
        ],
        certificacoes: ["PRI", "B-Corp"],
        data_cadastro: new Date("2023-10-01T17:00:00Z")
    }
]);

// Inserção dos 10 equipamentos polimórficos
db.equipamentos.insertMany([
    {
        _id: ObjectId("661000000000000000000001"),
        empresa_id: ObjectId("660000000000000000000001"),
        nome: "Chiller Central EcoCooling 500TR",
        categoria: "CLIMATIZACAO",
        status: "LIGADO",
        limite_consumo_kwh: 50.0,
        limite_ociosidade_min: NumberInt(30),
        localizacao: { bloco: "A", sala: "Casa de Maquinas", andar: "Subsolo" },
        especificacoes_tecnicas: {
            potencia_btu: 6000000,
            toneladas_refrigeracao_tr: 500,
            gas_refrigerante: "R-32",
            gwp_potencial_aquecimento_global: 675,
            temperatura_alvo_celsius: 22.0
        },
        data_instalacao: new Date("2023-01-20T10:00:00Z")
    },
    {
        _id: ObjectId("661000000000000000000002"),
        empresa_id: ObjectId("660000000000000000000002"),
        nome: "Inversor Solar Central Trifasico 100kW",
        categoria: "ENERGIA_SOLAR",
        status: "LIGADO",
        limite_consumo_kwh: 120.0,
        limite_ociosidade_min: NumberInt(15),
        localizacao: { bloco: "Solar-01", sala: "Cabine Primaria", andar: "Terreo" },
        especificacoes_tecnicas: {
            capacidade_geracao_kwp: 115.5,
            eficiencia_inversor_pct: 98.6,
            tipo_painel: "Silicio Monocristalino Bifacial"
        },
        data_instalacao: new Date("2023-02-10T08:00:00Z")
    },
    {
        _id: ObjectId("661000000000000000000003"),
        empresa_id: ObjectId("660000000000000000000001"),
        nome: "Cluster de Servidores Sustentaveis Blade-X",
        categoria: "TI_DATACENTER",
        status: "LIGADO",
        limite_consumo_kwh: 80.0,
        limite_ociosidade_min: NumberInt(45),
        localizacao: { bloco: "DataCenter", sala: "Cofre Seguro 02", andar: "2" },
        especificacoes_tecnicas: {
            pue_alvo: 1.15,
            num_servidores: NumberInt(32),
            refrigeracao_liquida_direta: true
        },
        data_instalacao: new Date("2023-03-01T14:00:00Z")
    },
    {
        _id: ObjectId("661000000000000000000004"),
        empresa_id: ObjectId("660000000000000000000003"),
        nome: "Carregador Rapido DC 150kW para Caminhoes Eletricos",
        categoria: "MOBILIDADE_ELETRICA",
        status: "LIGADO",
        limite_consumo_kwh: 160.0,
        limite_ociosidade_min: NumberInt(20),
        localizacao: { bloco: "Patio Logistico", sala: "Vagas Eletricas", andar: "Externo" },
        especificacoes_tecnicas: {
            potencia_recarga_kw: 150,
            conectores: ["CCS2", "CHAdeMO"],
            quantidade_pontos_recarga: NumberInt(2)
        },
        data_instalacao: new Date("2023-03-25T09:30:00Z")
    },
    {
        _id: ObjectId("661000000000000000000005"),
        empresa_id: ObjectId("660000000000000000000004"),
        nome: "Motor Eletrico Industrial Super Premium IE4",
        categoria: "MAQUINARIO_INDUSTRIAL",
        status: "LIGADO",
        limite_consumo_kwh: 100.0,
        limite_ociosidade_min: NumberInt(20),
        localizacao: { bloco: "Fabrica 2", sala: "Linha Extrusao", andar: "Térreo" },
        especificacoes_tecnicas: {
            classe_eficiencia: "IE4 Super Premium",
            potencia_cv: 75,
            rotacao_rpm: NumberInt(1800)
        },
        data_instalacao: new Date("2023-04-12T11:20:00Z")
    },
    {
        _id: ObjectId("661000000000000000000006"),
        empresa_id: ObjectId("660000000000000000000005"),
        nome: "Sistema VRF Climatizacao Loja Conceito",
        categoria: "CLIMATIZACAO",
        status: "LIGADO",
        limite_consumo_kwh: 35.0,
        limite_ociosidade_min: NumberInt(25),
        localizacao: { bloco: "Loja Flagship", sala: "Salao", andar: "Térreo" },
        especificacoes_tecnicas: {
            potencia_btu: 180000,
            gas_refrigerante: "R-410A"
        },
        data_instalacao: new Date("2023-05-02T13:00:00Z")
    },
    {
        _id: ObjectId("661000000000000000000007"),
        empresa_id: ObjectId("660000000000000000000006"),
        nome: "Caldeira a Vapor com Queima de Biomassa",
        categoria: "MAQUINARIO_INDUSTRIAL",
        status: "LIGADO",
        limite_consumo_kwh: 150.0,
        limite_ociosidade_min: NumberInt(60),
        localizacao: { bloco: "Usinagem", sala: "Parque Termico", andar: "Subsolo" },
        especificacoes_tecnicas: {
            tipo_combustivel: "BIOMASSA_BAGASSO_CANA",
            producao_vapor_ton_h: 25.0
        },
        data_instalacao: new Date("2023-06-15T07:30:00Z")
    },
    {
        _id: ObjectId("661000000000000000000008"),
        empresa_id: ObjectId("660000000000000000000010"),
        nome: "Estacao Wallbox AC 22kW Frota Compartilhada",
        categoria: "MOBILIDADE_ELETRICA",
        status: "LIGADO",
        limite_consumo_kwh: 25.0,
        limite_ociosidade_min: NumberInt(30),
        localizacao: { bloco: "Garagem Sede", sala: "Estacionamento Verde", andar: "G1" },
        especificacoes_tecnicas: {
            potencia_recarga_kw: 22,
            conectores: ["Type 2"]
        },
        data_instalacao: new Date("2023-07-01T15:00:00Z")
    },
    {
        _id: ObjectId("661000000000000000000009"),
        empresa_id: ObjectId("660000000000000000000002"),
        nome: "Sistema Tracker Solar Mono-axial 250kW",
        categoria: "ENERGIA_SOLAR",
        status: "LIGADO",
        limite_consumo_kwh: 260.0,
        limite_ociosidade_min: NumberInt(10),
        localizacao: { bloco: "Campo Solar B", sala: "Subestacao", andar: "Externo" },
        especificacoes_tecnicas: {
            capacidade_geracao_kwp: 280.0,
            ganho_geracao_tracker_pct: 22.5
        },
        data_instalacao: new Date("2023-08-10T11:00:00Z")
    },
    {
        _id: ObjectId("661000000000000000000010"),
        empresa_id: ObjectId("660000000000000000000001"),
        nome: "Sistema de Armazenamento All-Flash NVMe Eco",
        categoria: "TI_DATACENTER",
        status: "LIGADO",
        limite_consumo_kwh: 20.0,
        limite_ociosidade_min: NumberInt(30),
        localizacao: { bloco: "DataCenter", sala: "Cofre Seguro 01", andar: "2" },
        especificacoes_tecnicas: {
            capacidade_petabytes: 1.5,
            modo_hibernacao_inteligente: true
        },
        data_instalacao: new Date("2023-09-05T16:30:00Z")
    }
]);

// Inserção das 10 leituras de consumo
db.leituras_consumo.insertMany([
    {
        _id: ObjectId("662000000000000000000001"),
        equipamento_id: ObjectId("661000000000000000000001"),
        tipo_recurso: "ENERGIA_ELETRICA",
        data_hora: new Date("2024-05-10T14:00:00Z"),
        consumo_kwh: 65.5,
        tempo_ocioso_min: NumberInt(10),
        pegada_carbono_kg_co2e: 5.89,
        telemetria_detalhada: { tensao_v: 380, delta_temperatura: 5.2 }
    },
    {
        _id: ObjectId("662000000000000000000002"),
        equipamento_id: ObjectId("661000000000000000000002"),
        tipo_recurso: "GERACAO_SOLAR_RENOVAVEL",
        data_hora: new Date("2024-05-10T12:00:00Z"),
        consumo_kwh: 0.0,
        tempo_ocioso_min: NumberInt(0),
        pegada_carbono_kg_co2e: 0.0,
        telemetria_detalhada: { energia_gerada_kwh: 98.4, co2_evitado_kg: 8.85 }
    },
    {
        _id: ObjectId("662000000000000000000003"),
        equipamento_id: ObjectId("661000000000000000000003"),
        tipo_recurso: "ENERGIA_ELETRICA",
        data_hora: new Date("2024-05-10T03:00:00Z"),
        consumo_kwh: 95.0,
        tempo_ocioso_min: NumberInt(50),
        pegada_carbono_kg_co2e: 8.55,
        telemetria_detalhada: { pue: 1.18, utilizacao_cpu_pct: 12.5 }
    },
    {
        _id: ObjectId("662000000000000000000004"),
        equipamento_id: ObjectId("661000000000000000000004"),
        tipo_recurso: "ENERGIA_ELETRICA",
        data_hora: new Date("2024-05-10T16:30:00Z"),
        consumo_kwh: 142.0,
        tempo_ocioso_min: NumberInt(5),
        pegada_carbono_kg_co2e: 12.78,
        telemetria_detalhada: { veiculo: "CAMINHAO-07", soc_pct: 82 }
    },
    {
        _id: ObjectId("662000000000000000000005"),
        equipamento_id: ObjectId("661000000000000000000005"),
        tipo_recurso: "ENERGIA_ELETRICA",
        data_hora: new Date("2024-05-10T10:00:00Z"),
        consumo_kwh: 70.0,
        tempo_ocioso_min: NumberInt(35),
        pegada_carbono_kg_co2e: 6.30,
        telemetria_detalhada: { rpm: 1750, vibracao_mm_s: 1.2 }
    },
    {
        _id: ObjectId("662000000000000000000006"),
        equipamento_id: ObjectId("661000000000000000000005"),
        tipo_recurso: "AGUA",
        data_hora: new Date("2024-05-10T11:00:00Z"),
        consumo_kwh: 12.0,
        tempo_ocioso_min: NumberInt(0),
        pegada_carbono_kg_co2e: 1.08,
        telemetria_detalhada: { volume_m3: 45.8, percentual_reuso: 85.0 }
    },
    {
        _id: ObjectId("662000000000000000000007"),
        equipamento_id: ObjectId("661000000000000000000007"),
        tipo_recurso: "COMBUSTIVEL_FOSSIL",
        data_hora: new Date("2024-05-10T08:00:00Z"),
        consumo_kwh: 135.0,
        tempo_ocioso_min: NumberInt(15),
        pegada_carbono_kg_co2e: 42.50,
        telemetria_detalhada: { combustivel: "BIODIESEL_B20", volume_litros: 18.0 }
    },
    {
        _id: ObjectId("662000000000000000000008"),
        equipamento_id: ObjectId("661000000000000000000006"),
        tipo_recurso: "ENERGIA_ELETRICA",
        data_hora: new Date("2024-05-10T15:00:00Z"),
        consumo_kwh: 28.0,
        tempo_ocioso_min: NumberInt(12),
        pegada_carbono_kg_co2e: 2.52,
        telemetria_detalhada: { temperatura_externa: 32.0 }
    },
    {
        _id: ObjectId("662000000000000000000009"),
        equipamento_id: ObjectId("661000000000000000000009"),
        tipo_recurso: "GERACAO_SOLAR_RENOVAVEL",
        data_hora: new Date("2024-05-10T13:00:00Z"),
        consumo_kwh: 0.0,
        tempo_ocioso_min: NumberInt(0),
        pegada_carbono_kg_co2e: 0.0,
        telemetria_detalhada: { energia_gerada_kwh: 245.0, co2_evitado_kg: 22.05 }
    },
    {
        _id: ObjectId("662000000000000000000010"),
        equipamento_id: ObjectId("661000000000000000000010"),
        tipo_recurso: "ENERGIA_ELETRICA",
        data_hora: new Date("2024-05-10T23:00:00Z"),
        consumo_kwh: 11.2,
        tempo_ocioso_min: NumberInt(28),
        pegada_carbono_kg_co2e: 1.01,
        telemetria_detalhada: { iops: 154000 }
    }
]);

// Inserção dos 10 alertas ESG
db.alertas_esg.insertMany([
    {
        _id: ObjectId("663000000000000000000001"),
        equipamento_id: ObjectId("661000000000000000000001"),
        pilar_esg: "ENVIRONMENTAL",
        tipo_alerta: "CONSUMO_EXCESSIVO",
        mensagem: "Chiller operando 31% acima do limite sustentavel (65.5 vs 50.0 kWh).",
        severidade: "ALTA",
        status: "ABERTO",
        data_hora: new Date("2024-05-10T14:05:00Z"),
        dados_contextuais: { consumo_medido_kwh: 65.5, limite_kwh: 50.0 },
        historico_acoes: [
            { data: new Date("2024-05-10T14:05:00Z"), responsavel: "Sistema IoT", acao: "Disparo automatico", status_resultante: "ABERTO" }
        ]
    },
    {
        _id: ObjectId("663000000000000000000002"),
        equipamento_id: ObjectId("661000000000000000000003"),
        pilar_esg: "ENVIRONMENTAL",
        tipo_alerta: "OCIOSIDADE_ALTA",
        mensagem: "Servidores em standby ocioso consumindo 95 kWh sem alocacao de carga.",
        severidade: "MEDIA",
        status: "EM_ANALISE",
        data_hora: new Date("2024-05-10T03:15:00Z"),
        dados_contextuais: { tempo_ocioso_min: NumberInt(50), limite_ociosidade_min: NumberInt(45) },
        historico_acoes: [
            { data: new Date("2024-05-10T03:15:00Z"), responsavel: "Monitor TI", acao: "Alerta gerado", status_resultante: "ABERTO" }
        ]
    },
    {
        _id: ObjectId("663000000000000000000003"),
        equipamento_id: ObjectId("661000000000000000000007"),
        pilar_esg: "GOVERNANCE",
        tipo_alerta: "CONFORMIDADE_AMBIENTAL",
        mensagem: "Caldeira requer auditoria de emissao de gases antes da renovacao da licenca CETESB.",
        severidade: "CRITICA",
        status: "ABERTO",
        data_hora: new Date("2024-05-08T09:00:00Z"),
        dados_contextuais: { orgao: "CETESB", licenca: "LO-2021-987654" },
        historico_acoes: [
            { data: new Date("2024-05-08T09:00:00Z"), responsavel: "Compliance ESG", acao: "Prazo detectado", status_resultante: "ABERTO" }
        ]
    },
    {
        _id: ObjectId("663000000000000000000004"),
        equipamento_id: ObjectId("661000000000000000000001"),
        pilar_esg: "SOCIAL",
        tipo_alerta: "CONFORTO_TERMICO_SEGURANCA",
        mensagem: "Temperatura na sala de operadores ultrapassou 28C por falha no insuflamento.",
        severidade: "ALTA",
        status: "RESOLVIDO",
        data_hora: new Date("2024-05-05T13:20:00Z"),
        dados_contextuais: { temperatura_celsius: 28.5, norma: "NR-17" },
        historico_acoes: [
            { data: new Date("2024-05-05T13:20:00Z"), responsavel: "Sensor Termico", acao: "Transgressao detectada", status_resultante: "ABERTO" },
            { data: new Date("2024-05-05T14:00:00Z"), responsavel: "Manutencao Predial", acao: "Troca de valvula", status_resultante: "RESOLVIDO" }
        ]
    },
    {
        _id: ObjectId("663000000000000000000005"),
        equipamento_id: ObjectId("661000000000000000000005"),
        pilar_esg: "ENVIRONMENTAL",
        tipo_alerta: "OCIOSIDADE_ALTA",
        mensagem: "Motor operando sem esteira carregada por 35 minutos continuos.",
        severidade: "MEDIA",
        status: "RESOLVIDO",
        data_hora: new Date("2024-05-09T10:35:00Z"),
        dados_contextuais: { tempo_ocioso_min: NumberInt(35) },
        historico_acoes: [
            { data: new Date("2024-05-09T10:35:00Z"), responsavel: "Trigger IoT", acao: "Incidente gerado", status_resultante: "ABERTO" },
            { data: new Date("2024-05-09T10:45:00Z"), responsavel: "Supervisao", acao: "Desligamento remoto", status_resultante: "RESOLVIDO" }
        ]
    },
    {
        _id: ObjectId("663000000000000000000006"),
        equipamento_id: ObjectId("661000000000000000000004"),
        pilar_esg: "GOVERNANCE",
        tipo_alerta: "REPORTE_GRI_ATRASO",
        mensagem: "Auditoria apontou atraso no consolidado de emissoes da frota eletrica.",
        severidade: "MEDIA",
        status: "EM_ANALISE",
        data_hora: new Date("2024-05-07T11:00:00Z"),
        dados_contextuais: { modulo_gri: "GRI 302" },
        historico_acoes: [
            { data: new Date("2024-05-07T11:00:00Z"), responsavel: "Auditoria Interna", acao: "Apontamento registrado", status_resultante: "ABERTO" }
        ]
    },
    {
        _id: ObjectId("663000000000000000000007"),
        equipamento_id: ObjectId("661000000000000000000002"),
        pilar_esg: "ENVIRONMENTAL",
        tipo_alerta: "EFICIENCIA_REDUZIDA",
        mensagem: "Inversor registrando geracao 18% abaixo do modelo preditivo.",
        severidade: "BAIXA",
        status: "ABERTO",
        data_hora: new Date("2024-05-10T13:00:00Z"),
        dados_contextuais: { desvio_pct: 18.0 },
        historico_acoes: [
            { data: new Date("2024-05-10T13:00:00Z"), responsavel: "Algoritmo ML Solar", acao: "Desvio sinalizado", status_resultante: "ABERTO" }
        ]
    },
    {
        _id: ObjectId("663000000000000000000008"),
        equipamento_id: ObjectId("661000000000000000000005"),
        pilar_esg: "SOCIAL",
        tipo_alerta: "SAUDE_OCUPACIONAL_RUIDO",
        mensagem: "Sensor acustico registrou 89 dBA continuo na vizinhanca de trabalho.",
        severidade: "ALTA",
        status: "MITIGADO",
        data_hora: new Date("2024-05-06T15:30:00Z"),
        dados_contextuais: { medicao_dba: 89.4, norma: "NR-15" },
        historico_acoes: [
            { data: new Date("2024-05-06T15:30:00Z"), responsavel: "Sensor Acustico", acao: "Notificacao emitida", status_resultante: "ABERTO" },
            { data: new Date("2024-05-06T16:15:00Z"), responsavel: "Tecnico Seguranca", acao: "Biombo acustico instalado", status_resultante: "MITIGADO" }
        ]
    },
    {
        _id: ObjectId("663000000000000000000009"),
        equipamento_id: ObjectId("661000000000000000000006"),
        pilar_esg: "ENVIRONMENTAL",
        tipo_alerta: "VAZAMENTO_GAS_EFEITO_ESTUFA",
        mensagem: "Sensor acusou queda subita de pressao de fluido refrigerante R-410A.",
        severidade: "CRITICA",
        status: "ABERTO",
        data_hora: new Date("2024-05-10T15:45:00Z"),
        dados_contextuais: { fluido: "R-410A", gwp: 2088 },
        historico_acoes: [
            { data: new Date("2024-05-10T15:45:00Z"), responsavel: "Sensor Pressao", acao: "Queda de pressao detectada", status_resultante: "ABERTO" }
        ]
    },
    {
        _id: ObjectId("663000000000000000000010"),
        equipamento_id: ObjectId("661000000000000000000009"),
        pilar_esg: "GOVERNANCE",
        tipo_alerta: "AUDITORIA_CALIBRACAO",
        mensagem: "Medidor fiscal I-REC necessita de calibracao RBC anual.",
        severidade: "BAIXA",
        status: "RESOLVIDO",
        data_hora: new Date("2024-05-02T10:00:00Z"),
        dados_contextuais: { padrao: "I-REC Standard" },
        historico_acoes: [
            { data: new Date("2024-05-02T10:00:00Z"), responsavel: "Sistema Metrologico", acao: "Calibracao agendada", status_resultante: "ABERTO" },
            { data: new Date("2024-05-04T16:00:00Z"), responsavel: "Laboratorio", acao: "Laudo RBC emitido", status_resultante: "RESOLVIDO" }
        ]
    }
]);

// Inserção dos 10 projetos de compensação
db.projetos_compensacao.insertMany([
    {
        _id: ObjectId("664000000000000000000001"),
        empresa_id: ObjectId("660000000000000000000001"),
        nome_iniciativa: "Corredor Ecologico da Serra do Mar",
        tipo_projeto: "REFLORESTAMENTO_NATIVO",
        status: "EM_ANDAMENTO",
        meta_tco2e_compensar: 2500.0,
        tco2e_compensado_acumulado: 1420.0,
        investimento_brl: 480000.00,
        detalhes_execucao: { bioma: "Mata Atlantica", arvores_plantadas: NumberInt(42000), certificadora: "Verra VCS" },
        data_inicio: new Date("2023-03-01T00:00:00Z")
    },
    {
        _id: ObjectId("664000000000000000000002"),
        empresa_id: ObjectId("660000000000000000000002"),
        nome_iniciativa: "Parque Solar Comunitario do Sertao",
        tipo_projeto: "ENERGIA_RENOVAVEL_IREC",
        status: "CONCLUIDO",
        meta_tco2e_compensar: 5000.0,
        tco2e_compensado_acumulado: 5240.0,
        investimento_brl: 1200000.00,
        detalhes_execucao: { capacidade_mwp: 3.5, certificados_irec: NumberInt(6800) },
        data_inicio: new Date("2022-08-15T00:00:00Z")
    },
    {
        _id: ObjectId("664000000000000000000003"),
        empresa_id: ObjectId("660000000000000000000009"),
        nome_iniciativa: "Circulo Verde - Logistica Reversa de Embalagens",
        tipo_projeto: "ECONOMIA_CIRCULAR_RECICLAGEM",
        status: "EM_ANDAMENTO",
        meta_tco2e_compensar: 1800.0,
        tco2e_compensado_acumulado: 1250.0,
        investimento_brl: 350000.00,
        detalhes_execucao: { catadores_beneficiados: NumberInt(120), toneladas_recicladas: 840.5 },
        data_inicio: new Date("2023-05-10T00:00:00Z")
    },
    {
        _id: ObjectId("664000000000000000000004"),
        empresa_id: ObjectId("660000000000000000000006"),
        nome_iniciativa: "Amazonia Viva - Preservacao Florestal REDD+",
        tipo_projeto: "PRESERVACAO_BIODIVERSIDADE",
        status: "EM_ANDAMENTO",
        meta_tco2e_compensar: 10000.0,
        tco2e_compensado_acumulado: 6800.0,
        investimento_brl: 2500000.00,
        detalhes_execucao: { bioma: "Amazonia", hectares: 12500.0, metodologia: "VCS VM0015" },
        data_inicio: new Date("2023-01-10T00:00:00Z")
    },
    {
        _id: ObjectId("664000000000000000000005"),
        empresa_id: ObjectId("660000000000000000000004"),
        nome_iniciativa: "Nascentes Vivas do Cerrado",
        tipo_projeto: "REFLORESTAMENTO_NATIVO",
        status: "EM_ANDAMENTO",
        meta_tco2e_compensar: 1500.0,
        tco2e_compensado_acumulado: 890.0,
        investimento_brl: 290000.00,
        detalhes_execucao: { bioma: "Cerrado", nascentes_recuperadas: NumberInt(12), arvores: NumberInt(18500) },
        data_inicio: new Date("2023-06-20T00:00:00Z")
    },
    {
        _id: ObjectId("664000000000000000000006"),
        empresa_id: ObjectId("660000000000000000000007"),
        nome_iniciativa: "Usina de Biogas e Biodigestao Anaerobia",
        tipo_projeto: "ENERGIA_RENOVAVEL_IREC",
        status: "CONCLUIDO",
        meta_tco2e_compensar: 3200.0,
        tco2e_compensado_acumulado: 3350.0,
        investimento_brl: 850000.00,
        detalhes_execucao: { geracao_kwh: 890000.0, padrao: "Gold Standard" },
        data_inicio: new Date("2022-11-01T00:00:00Z")
    },
    {
        _id: ObjectId("664000000000000000000007"),
        empresa_id: ObjectId("660000000000000000000001"),
        nome_iniciativa: "Mineracao Urbana e Reciclagem de Eletronicos",
        tipo_projeto: "ECONOMIA_CIRCULAR_RECICLAGEM",
        status: "EM_ANDAMENTO",
        meta_tco2e_compensar: 900.0,
        tco2e_compensado_acumulado: 580.0,
        investimento_brl: 180000.00,
        detalhes_execucao: { itens_recolhidos: NumberInt(15400), computadores_doados: NumberInt(210) },
        data_inicio: new Date("2023-04-18T00:00:00Z")
    },
    {
        _id: ObjectId("664000000000000000000008"),
        empresa_id: ObjectId("660000000000000000000003"),
        nome_iniciativa: "Carbono Azul - Restauracao de Manguezais",
        tipo_projeto: "PRESERVACAO_BIODIVERSIDADE",
        status: "EM_ANDAMENTO",
        meta_tco2e_compensar: 4000.0,
        tco2e_compensado_acumulado: 2100.0,
        investimento_brl: 950000.00,
        detalhes_execucao: { bioma: "Manguezal", hectares: 48.0, familias_apoiadas: NumberInt(65) },
        data_inicio: new Date("2023-07-15T00:00:00Z")
    },
    {
        _id: ObjectId("664000000000000000000009"),
        empresa_id: ObjectId("660000000000000000000010"),
        nome_iniciativa: "Escolas Solares - Energia Limpa Comunitaria",
        tipo_projeto: "ENERGIA_RENOVAVEL_IREC",
        status: "CONCLUIDO",
        meta_tco2e_compensar: 600.0,
        tco2e_compensado_acumulado: 640.0,
        investimento_brl: 220000.00,
        detalhes_execucao: { escolas_beneficiadas: NumberInt(15), alunos: NumberInt(6200) },
        data_inicio: new Date("2023-02-01T00:00:00Z")
    },
    {
        _id: ObjectId("664000000000000000000010"),
        empresa_id: ObjectId("660000000000000000000006"),
        nome_iniciativa: "Agrofloresta Sintropica Vale Fertil",
        tipo_projeto: "REFLORESTAMENTO_NATIVO",
        status: "EM_ANDAMENTO",
        meta_tco2e_compensar: 2100.0,
        tco2e_compensado_acumulado: 1100.0,
        investimento_brl: 380000.00,
        detalhes_execucao: { modelo: "Agricultura Sintropica", hectares: 25.0, produtores: NumberInt(40) },
        data_inicio: new Date("2023-05-01T00:00:00Z")
    }
]);

print('>>> [DOCKER ENTRYPOINT SUCESSO] Banco fiap_esg inicializado com 5 collections e 50 documentos!');
