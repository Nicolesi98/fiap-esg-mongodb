/**
 * FIAP - MBA / Pós-Graduação em Engenharia de Software / Banco de Dados NoSQL
 * Projeto ESG - Monitoramento de Sustentabilidade e Eficiência Energética
 * 
 * Script: 03_crud_operations.mongodb.js
 * Descrição: Demonstração de operações de CRUD completas (Create, Read, Update, Delete)
 *            em TODAS as 5 collections do projeto, utilizando operadores avançados,
 *            consultas em subdocumentos, arrays e pipelines de agregação.
 */

use('fiap_esg');

print('=============================================================================');
print(' 1. OPERAÇÕES CRUD - COLLECTION: empresas');
print('=============================================================================');

// 1.1 CREATE (Inserir nova empresa com rating ESG e metas)
db.empresas.insertOne({
    _id: ObjectId("660000000000000000000099"),
    nome: "EcoVanguarda Tecnologias Limpas",
    cnpj: "01.234.567/0001-89",
    setor: "TECNOLOGIA",
    status_adesao_esg: "ATIVO",
    esg_rating: {
        score_geral: 89.0,
        score_ambiental: 91.0,
        score_social: 87.0,
        score_governanca: 89.0,
        classificacao: "AA"
    },
    metas_sustentabilidade: [
        { descricao: "Eficiencia Energetica de Servidores", ano_alvo: NumberInt(2027), percentual_conclusao: 50.0 }
    ],
    certificacoes: ["ISO 14001", "Carbon Trust Standard"],
    data_cadastro: new Date()
});
print('[empresas] CREATE: Documento inserido com sucesso.');

// 1.2 READ (Consultas simples, com filtros em subdocumentos e pipeline de agregação)
// Consulta A: Buscar empresas com rating AAA ou AA no setor de TECNOLOGIA ou ENERGIA
const empresasTop = db.empresas.find(
    {
        setor: { $in: ["TECNOLOGIA", "ENERGIA"] },
        "esg_rating.classificacao": { $in: ["AAA", "AA"] }
    },
    { nome: 1, setor: 1, "esg_rating.score_geral": 1, certificacoes: 1 }
).toArray();
print('[empresas] READ (Filtro Composto):', JSON.stringify(empresasTop, null, 2));

// Consulta B: Agregação - Média de Score ESG e contagem de empresas agrupadas por setor
const mediaPorSetor = db.empresas.aggregate([
    {
        $group: {
            _id: "$setor",
            total_empresas: { $sum: 1 },
            media_score_esg: { $avg: "$esg_rating.score_geral" },
            media_ambiental: { $avg: "$esg_rating.score_ambiental" }
        }
    },
    { $sort: { media_score_esg: -1 } }
]).toArray();
print('[empresas] READ (Agregação por Setor):', JSON.stringify(mediaPorSetor, null, 2));

// 1.3 UPDATE (Atualizar com $set, $inc e $push em array)
// Adicionar uma nova meta de sustentabilidade e atualizar o score ambiental
db.empresas.updateOne(
    { _id: ObjectId("660000000000000000000099") },
    {
        $set: { "esg_rating.score_ambiental": 93.5 },
        $push: {
            metas_sustentabilidade: {
                descricao: "Reciclagem de 100% dos Laptops Corporativos",
                ano_alvo: NumberInt(2026),
                percentual_conclusao: 10.0
            }
        },
        $currentDate: { data_ultima_atualizacao: true }
    }
);
print('[empresas] UPDATE: Metas adicionadas e score atualizado.');

// 1.4 DELETE (Excluir empresa de teste)
const delEmpresa = db.empresas.deleteOne({ _id: ObjectId("660000000000000000000099") });
print('[empresas] DELETE: Documento removido. Deletados:', delEmpresa.deletedCount);


print('\n=============================================================================');
print(' 2. OPERAÇÕES CRUD - COLLECTION: equipamentos');
print('=============================================================================');

// 2.1 CREATE (Inserir novo equipamento polimórfico - categoria MOBILIDADE_ELETRICA)
db.equipamentos.insertOne({
    _id: ObjectId("661000000000000000000099"),
    empresa_id: ObjectId("660000000000000000000001"),
    nome: "Totem de Recarga Rapida Solar 50kW",
    categoria: "MOBILIDADE_ELETRICA",
    status: "LIGADO",
    limite_consumo_kwh: 55.0,
    limite_ociosidade_min: NumberInt(15),
    localizacao: { bloco: "Garagem Verde", sala: "Box 01", andar: "Térreo" },
    especificacoes_tecnicas: {
        potencia_recarga_kw: 50,
        conectores: ["Type 2", "CCS2"],
        alimentacao_solar_hibrida: true
    },
    data_instalacao: new Date()
});
print('[equipamentos] CREATE: Equipamento criado.');

// 2.2 READ (Consultas por categoria e especificações polimórficas)
// Consulta A: Buscar equipamentos de CLIMATIZACAO que usam gás ecológico R-32
const equipGasecologico = db.equipamentos.find({
    categoria: "CLIMATIZACAO",
    "especificacoes_tecnicas.gas_refrigerante": "R-32"
}).toArray();
print('[equipamentos] READ (Filtro em Especificação Polimórfica):', JSON.stringify(equipGasecologico, null, 2));

// Consulta B: Agregação - Total de equipamentos e média de limite de consumo por categoria
const resumoEquipamentos = db.equipamentos.aggregate([
    {
        $group: {
            _id: "$categoria",
            quantidade: { $sum: 1 },
            media_limite_kwh: { $avg: "$limite_consumo_kwh" },
            max_ociosidade_tolerada: { $max: "$limite_ociosidade_min" }
        }
    },
    { $sort: { media_limite_kwh: -1 } }
]).toArray();
print('[equipamentos] READ (Agregação por Categoria):', JSON.stringify(resumoEquipamentos, null, 2));

// 2.3 UPDATE (Alterar status e ajustar especificações)
db.equipamentos.updateOne(
    { _id: ObjectId("661000000000000000000099") },
    {
        $set: {
            status: "MANUTENCAO",
            "especificacoes_tecnicas.calibracao_concluida": true
        },
        $inc: { limite_consumo_kwh: 5.0 }
    }
);
print('[equipamentos] UPDATE: Status alterado para MANUTENCAO e limite ajustado.');

// 2.4 DELETE (Remover equipamento de teste)
const delEquip = db.equipamentos.deleteOne({ _id: ObjectId("661000000000000000000099") });
print('[equipamentos] DELETE: Equipamento removido. Deletados:', delEquip.deletedCount);


print('\n=============================================================================');
print(' 3. OPERAÇÕES CRUD - COLLECTION: leituras_consumo');
print('=============================================================================');

// 3.1 CREATE (Inserir leitura de telemetria de Água de Reuso)
db.leituras_consumo.insertOne({
    _id: ObjectId("662000000000000000000099"),
    equipamento_id: ObjectId("661000000000000000000001"),
    tipo_recurso: "AGUA",
    data_hora: new Date(),
    consumo_kwh: 5.0,
    tempo_ocioso_min: NumberInt(0),
    pegada_carbono_kg_co2e: 0.45,
    telemetria_detalhada: {
        volume_m3: 15.2,
        fonte_captacao: "AGUA_DE_CHUVA_FILTRADA",
        percentual_reuso: 100.0
    }
});
print('[leituras_consumo] CREATE: Leitura de telemetria inserida.');

// 3.2 READ (Consultas por intervalo e agregação de pegada de carbono)
// Consulta A: Leituras com pegada de carbono superior a 5 kg de CO2e
const altasEmissoes = db.leituras_consumo.find(
    { pegada_carbono_kg_co2e: { $gte: 5.0 } },
    { equipamento_id: 1, tipo_recurso: 1, consumo_kwh: 1, pegada_carbono_kg_co2e: 1, data_hora: 1 }
).sort({ pegada_carbono_kg_co2e: -1 }).toArray();
print('[leituras_consumo] READ (Emissões >= 5 kg CO2e):', JSON.stringify(altasEmissoes, null, 2));

// Consulta B: Agregação - Total de consumo kWh e emissões de carbono agrupadas por tipo de recurso
const emissoesPorRecurso = db.leituras_consumo.aggregate([
    {
        $group: {
            _id: "$tipo_recurso",
            total_leituras: { $sum: 1 },
            consumo_total_kwh: { $sum: "$consumo_kwh" },
            carbono_total_kg: { $sum: "$pegada_carbono_kg_co2e" },
            media_tempo_ocioso: { $avg: "$tempo_ocioso_min" }
        }
    }
]).toArray();
print('[leituras_consumo] READ (Agregação de Recursos e Pegada de Carbono):', JSON.stringify(emissoesPorRecurso, null, 2));

// 3.3 UPDATE (Atualizar leitura com recálculo ou correção de ociosidade)
db.leituras_consumo.updateOne(
    { _id: ObjectId("662000000000000000000099") },
    {
        $set: { "telemetria_detalhada.volume_m3": 18.0 },
        $inc: { consumo_kwh: 1.5, pegada_carbono_kg_co2e: 0.15 }
    }
);
print('[leituras_consumo] UPDATE: Volume e consumo recalculados.');

// 3.4 DELETE (Remover leitura de teste)
const delLeitura = db.leituras_consumo.deleteOne({ _id: ObjectId("662000000000000000000099") });
print('[leituras_consumo] DELETE: Leitura removida. Deletados:', delLeitura.deletedCount);


print('\n=============================================================================');
print(' 4. OPERAÇÕES CRUD - COLLECTION: alertas_esg');
print('=============================================================================');

// 4.1 CREATE (Inserir alerta do pilar GOVERNANCE)
db.alertas_esg.insertOne({
    _id: ObjectId("663000000000000000000099"),
    equipamento_id: ObjectId("661000000000000000000003"),
    pilar_esg: "GOVERNANCE",
    tipo_alerta: "AUDITORIA_SEGURANCA_DADOS",
    mensagem: "Datacenter requer auditoria de conformidade de descarte seguro de componentes.",
    severidade: "MEDIA",
    status: "ABERTO",
    data_hora: new Date(),
    dados_contextuais: { norma: "ISO 27001 / WEEE Directive" },
    historico_acoes: [
        {
            data: new Date(),
            responsavel: "Auditor de TI",
            acao: "Abertura de pendencia de governanca",
            status_resultante: "ABERTO"
        }
    ]
});
print('[alertas_esg] CREATE: Alerta de governança registrado.');

// 4.2 READ (Consultas por status e agregação de severidade por pilar)
// Consulta A: Buscar alertas ABERTOS com severidade ALTA ou CRITICA
const alertasCriticos = db.alertas_esg.find({
    status: "ABERTO",
    severidade: { $in: ["ALTA", "CRITICA"] }
}).toArray();
print('[alertas_esg] READ (Alertas Críticos Abertos):', JSON.stringify(alertasCriticos, null, 2));

// Consulta B: Agregação - Contagem de alertas agrupados por Pilar ESG e Status
const alertasPorPilar = db.alertas_esg.aggregate([
    {
        $group: {
            _id: { pilar: "$pilar_esg", status: "$status" },
            quantidade: { $sum: 1 }
        }
    },
    { $sort: { "_id.pilar": 1, quantidade: -1 } }
]).toArray();
print('[alertas_esg] READ (Agregação de Incidentes por Pilar ESG):', JSON.stringify(alertasPorPilar, null, 2));

// 4.3 UPDATE (Adicionar ação no histórico e resolver o alerta com $push e $set)
db.alertas_esg.updateOne(
    { _id: ObjectId("663000000000000000000099") },
    {
        $set: { status: "RESOLVIDO" },
        $push: {
            historico_acoes: {
                data: new Date(),
                responsavel: "Coordenador de TI Verde",
                acao: "Comprovante de descarte certificado anexado",
                status_resultante: "RESOLVIDO"
            }
        }
    }
);
print('[alertas_esg] UPDATE: Alerta resolvido com registro no array historico_acoes.');

// 4.4 DELETE (Remover alerta de teste)
const delAlerta = db.alertas_esg.deleteOne({ _id: ObjectId("663000000000000000000099") });
print('[alertas_esg] DELETE: Alerta removido. Deletados:', delAlerta.deletedCount);


print('\n=============================================================================');
print(' 5. OPERAÇÕES CRUD - COLLECTION: projetos_compensacao');
print('=============================================================================');

// 5.1 CREATE (Inserir novo projeto de compensação de carbono)
db.projetos_compensacao.insertOne({
    _id: ObjectId("664000000000000000000099"),
    empresa_id: ObjectId("660000000000000000000001"),
    nome_iniciativa: "Plantando o Futuro - Restauração Fluvial",
    tipo_projeto: "REFLORESTAMENTO_NATIVO",
    status: "PLANEJADO",
    meta_tco2e_compensar: 1200.0,
    tco2e_compensado_acumulado: 0.0,
    investimento_brl: 150000.00,
    detalhes_execucao: {
        bioma: "Pantanal",
        arvores_planejadas: NumberInt(15000),
        comunidades_envolvidas: NumberInt(3)
    },
    data_inicio: new Date()
});
print('[projetos_compensacao] CREATE: Projeto de compensação cadastrado.');

// 5.2 READ (Consultas por tipo de projeto e agregação financeira de impacto)
// Consulta A: Buscar projetos com meta superior a 2.000 tCO2e
const megaprojetos = db.projetos_compensacao.find(
    { meta_tco2e_compensar: { $gte: 2000.0 } },
    { nome_iniciativa: 1, tipo_projeto: 1, meta_tco2e_compensar: 1, tco2e_compensado_acumulado: 1, status: 1 }
).toArray();
print('[projetos_compensacao] READ (Metas >= 2.000 tCO2e):', JSON.stringify(megaprojetos, null, 2));

// Consulta B: Agregação - Total investido (R$) e carbono compensado por modalidade de projeto
const balancoCompensacao = db.projetos_compensacao.aggregate([
    {
        $group: {
            _id: "$tipo_projeto",
            total_projetos: { $sum: 1 },
            investimento_total_brl: { $sum: "$investimento_brl" },
            carbono_neutralizado_acumulado_tco2e: { $sum: "$tco2e_compensado_acumulado" },
            meta_total_tco2e: { $sum: "$meta_tco2e_compensar" }
        }
    },
    { $sort: { carbono_neutralizado_acumulado_tco2e: -1 } }
]).toArray();
print('[projetos_compensacao] READ (Agregação de Impacto Ambiental e Investimentos):', JSON.stringify(balancoCompensacao, null, 2));

// 5.3 UPDATE (Incrementar carbono compensado e avançar status para EM_ANDAMENTO)
db.projetos_compensacao.updateOne(
    { _id: ObjectId("664000000000000000000099") },
    {
        $set: { status: "EM_ANDAMENTO" },
        $inc: { tco2e_compensado_acumulado: 350.0 }
    }
);
print('[projetos_compensacao] UPDATE: Status alterado para EM_ANDAMENTO e tCO2e compensado incrementado.');

// 5.4 DELETE (Remover projeto de teste)
const delProjeto = db.projetos_compensacao.deleteOne({ _id: ObjectId("664000000000000000000099") });
print('[projetos_compensacao] DELETE: Projeto removido. Deletados:', delProjeto.deletedCount);

print('\n=============================================================================');
print('>>> [SUCESSO] Operações de CRUD executadas com êxito em todas as 5 collections!');
print('=============================================================================');
