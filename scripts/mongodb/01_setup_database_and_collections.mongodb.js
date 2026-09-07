/**
 * FIAP - MBA / Pós-Graduação em Engenharia de Software / Banco de Dados NoSQL
 * Projeto ESG - Monitoramento de Sustentabilidade e Eficiência Energética
 * 
 * Script: 01_setup_database_and_collections.mongodb.js
 * Descrição: Seleção da base de dados, criação das 5 collections com JSON Schema Validation e índices.
 * Execução: mongosh < 01_setup_database_and_collections.mongodb.js ou MongoDB Compass
 */

// Seleciona ou cria o banco de dados da aplicação
use('fiap_esg');

// -----------------------------------------------------------------------------
// 1. COLLECTION: empresas
// Representa as organizações monitoradas, com dados de governança e scores ESG.
// -----------------------------------------------------------------------------
if (db.getCollectionNames().includes('empresas')) {
    db.empresas.drop();
}

db.createCollection('empresas', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['nome', 'cnpj', 'setor', 'status_adesao_esg'],
            properties: {
                nome: {
                    bsonType: 'string',
                    description: 'Nome corporativo ou razão social da empresa - obrigatório'
                },
                cnpj: {
                    bsonType: 'string',
                    pattern: '^\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}$',
                    description: 'CNPJ formatado no padrão XX.XXX.XXX/XXXX-XX - obrigatório'
                },
                setor: {
                    bsonType: 'string',
                    description: 'Segmento de atuação da empresa (ex: TECNOLOGIA, INDUSTRIA, ENERGIA)'
                },
                status_adesao_esg: {
                    enum: ['ATIVO', 'EM_AVALIACAO', 'SUSPENSO'],
                    description: 'Status de conformidade com o programa ESG'
                },
                esg_rating: {
                    bsonType: 'object',
                    required: ['score_geral', 'classificacao'],
                    properties: {
                        score_geral: { bsonType: ['double', 'int', 'decimal'] },
                        score_ambiental: { bsonType: ['double', 'int', 'decimal'] },
                        score_social: { bsonType: ['double', 'int', 'decimal'] },
                        score_governanca: { bsonType: ['double', 'int', 'decimal'] },
                        classificacao: { enum: ['AAA', 'AA', 'A', 'BBB', 'BB', 'B', 'CCC'] }
                    }
                },
                metas_sustentabilidade: {
                    bsonType: 'array',
                    items: {
                        bsonType: 'object',
                        required: ['descricao', 'ano_alvo'],
                        properties: {
                            descricao: { bsonType: 'string' },
                            ano_alvo: { bsonType: 'int' },
                            percentual_conclusao: { bsonType: ['double', 'int', 'decimal'] }
                        }
                    }
                },
                certificacoes: {
                    bsonType: 'array',
                    items: { bsonType: 'string' }
                },
                data_cadastro: { bsonType: 'date' }
            }
        }
    }
});

// Índices da collection 'empresas'
db.empresas.createIndex({ cnpj: 1 }, { unique: true, name: 'idx_empresas_cnpj_unique' });
db.empresas.createIndex({ setor: 1 }, { name: 'idx_empresas_setor' });
db.empresas.createIndex({ 'esg_rating.classificacao': 1 }, { name: 'idx_empresas_rating' });


// -----------------------------------------------------------------------------
// 2. COLLECTION: equipamentos
// Representa os equipamentos físicos e sensores monitorados.
// DEMONSTRAÇÃO DO MODELO FLEXÍVEL: cada categoria possui especificações técnicas distintas.
// -----------------------------------------------------------------------------
if (db.getCollectionNames().includes('equipamentos')) {
    db.equipamentos.drop();
}

db.createCollection('equipamentos', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['empresa_id', 'nome', 'categoria', 'status', 'limite_consumo_kwh', 'limite_ociosidade_min'],
            properties: {
                empresa_id: {
                    bsonType: ['string', 'objectId'],
                    description: 'Referência ao ID da empresa proprietária'
                },
                nome: {
                    bsonType: 'string',
                    description: 'Identificação descritiva do equipamento'
                },
                categoria: {
                    enum: [
                        'CLIMATIZACAO',
                        'ENERGIA_SOLAR',
                        'MOBILIDADE_ELETRICA',
                        'TI_DATACENTER',
                        'MAQUINARIO_INDUSTRIAL'
                    ],
                    description: 'Categoria do equipamento'
                },
                status: {
                    enum: ['LIGADO', 'DESLIGADO', 'ATENCAO', 'MANUTENCAO'],
                    description: 'Estado operacional do equipamento'
                },
                limite_consumo_kwh: {
                    bsonType: ['double', 'int', 'decimal'],
                    minimum: 0,
                    description: 'Limite máximo aceitável de consumo antes de disparar alerta'
                },
                limite_ociosidade_min: {
                    bsonType: 'int',
                    minimum: 0,
                    description: 'Tempo máximo de máquina operando em stand-by sem carga'
                },
                localizacao: {
                    bsonType: 'object',
                    properties: {
                        bloco: { bsonType: 'string' },
                        sala: { bsonType: 'string' },
                        andar: { bsonType: 'string' }
                    }
                },
                // Objeto de especificação polimórfica (flexível por categoria)
                especificacoes_tecnicas: {
                    bsonType: 'object',
                    description: 'Campos dinâmicos e flexíveis variando de acordo com a categoria'
                },
                data_instalacao: { bsonType: 'date' }
            }
        }
    }
});

// Índices da collection 'equipamentos'
db.equipamentos.createIndex({ empresa_id: 1 }, { name: 'idx_equipamentos_empresa' });
db.equipamentos.createIndex({ categoria: 1, status: 1 }, { name: 'idx_equipamentos_cat_status' });
db.equipamentos.createIndex({ 'especificacoes_tecnicas.gas_refrigerante': 1 }, { sparse: true, name: 'idx_espec_gas' });


// -----------------------------------------------------------------------------
// 3. COLLECTION: leituras_consumo
// Telemetria temporal e medições ecológicas (eletricidade, água, combustíveis e geração limpa).
// DEMONSTRAÇÃO DO MODELO FLEXÍVEL: telemetria adaptada por tipo de recurso.
// -----------------------------------------------------------------------------
if (db.getCollectionNames().includes('leituras_consumo')) {
    db.leituras_consumo.drop();
}

db.createCollection('leituras_consumo', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['equipamento_id', 'tipo_recurso', 'data_hora'],
            properties: {
                equipamento_id: {
                    bsonType: ['string', 'objectId'],
                    description: 'Identificador do equipamento de medição'
                },
                tipo_recurso: {
                    enum: [
                        'ENERGIA_ELETRICA',
                        'AGUA',
                        'COMBUSTIVEL_FOSSIL',
                        'GERACAO_SOLAR_RENOVAVEL'
                    ],
                    description: 'Tipo do recurso medido'
                },
                data_hora: {
                    bsonType: 'date',
                    description: 'Momento da medição'
                },
                consumo_kwh: {
                    bsonType: ['double', 'int', 'decimal'],
                    description: 'Consumo de energia elétrica medido em kWh'
                },
                tempo_ocioso_min: {
                    bsonType: 'int',
                    description: 'Minutos em ociosidade na janela de medição'
                },
                pegada_carbono_kg_co2e: {
                    bsonType: ['double', 'int', 'decimal'],
                    description: 'Emissão de CO2 calculada para a leitura'
                },
                telemetria_detalhada: {
                    bsonType: 'object',
                    description: 'Dados polimórficos de telemetria variando por tipo de recurso'
                }
            }
        }
    }
});

// Índices da collection 'leituras_consumo'
db.leituras_consumo.createIndex({ equipamento_id: 1, data_hora: -1 }, { name: 'idx_consumo_equip_data' });
db.leituras_consumo.createIndex({ tipo_recurso: 1, data_hora: -1 }, { name: 'idx_consumo_tipo_data' });
db.leituras_consumo.createIndex({ pegada_carbono_kg_co2e: 1 }, { name: 'idx_consumo_carbono' });


// -----------------------------------------------------------------------------
// 4. COLLECTION: alertas_esg
// Incidentes, transgressões de sustentabilidade e histórico de mitigação.
// DEMONSTRAÇÃO DO MODELO FLEXÍVEL: alertas com especificidades ambientais, sociais ou governança.
// -----------------------------------------------------------------------------
if (db.getCollectionNames().includes('alertas_esg')) {
    db.alertas_esg.drop();
}

db.createCollection('alertas_esg', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['equipamento_id', 'pilar_esg', 'tipo_alerta', 'mensagem', 'severidade', 'status'],
            properties: {
                equipamento_id: {
                    bsonType: ['string', 'objectId'],
                    description: 'ID do equipamento associado ao alerta'
                },
                pilar_esg: {
                    enum: ['ENVIRONMENTAL', 'SOCIAL', 'GOVERNANCE'],
                    description: 'Pilar ESG infringido ou monitorado'
                },
                tipo_alerta: {
                    bsonType: 'string',
                    description: 'Código do alerta (ex: CONSUMO_ALTO, OCIOSIDADE_CRITICA, AUDITORIA_VENCIDA)'
                },
                mensagem: {
                    bsonType: 'string',
                    description: 'Texto descritivo do incidente'
                },
                severidade: {
                    enum: ['BAIXA', 'MEDIA', 'ALTA', 'CRITICA'],
                    description: 'Nível de impacto e urgência'
                },
                status: {
                    enum: ['ABERTO', 'EM_ANALISE', 'MITIGADO', 'RESOLVIDO'],
                    description: 'Status do fluxo de tratamento do alerta'
                },
                data_hora: {
                    bsonType: 'date',
                    description: 'Timestamp do disparo do alerta'
                },
                dados_contextuais: {
                    bsonType: 'object',
                    description: 'Detalhes dinâmicos de medição e cálculo de desvio'
                },
                historico_acoes: {
                    bsonType: 'array',
                    items: {
                        bsonType: 'object',
                        required: ['data', 'responsavel', 'acao'],
                        properties: {
                            data: { bsonType: 'date' },
                            responsavel: { bsonType: 'string' },
                            acao: { bsonType: 'string' },
                            status_resultante: { bsonType: 'string' }
                        }
                    }
                }
            }
        }
    }
});

// Índices da collection 'alertas_esg'
db.alertas_esg.createIndex({ equipamento_id: 1, status: 1 }, { name: 'idx_alertas_equip_status' });
db.alertas_esg.createIndex({ pilar_esg: 1, severidade: 1 }, { name: 'idx_alertas_pilar_sev' });
db.alertas_esg.createIndex({ status: 1, data_hora: -1 }, { name: 'idx_alertas_status_data' });


// -----------------------------------------------------------------------------
// 5. COLLECTION: projetos_compensacao
// Iniciativas ativas de compensação de carbono, reflorestamento e responsabilidade social.
// DEMONSTRAÇÃO DO MODELO FLEXÍVEL: projetos florestais, usinas solares e cooperativas.
// -----------------------------------------------------------------------------
if (db.getCollectionNames().includes('projetos_compensacao')) {
    db.projetos_compensacao.drop();
}

db.createCollection('projetos_compensacao', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['empresa_id', 'nome_iniciativa', 'tipo_projeto', 'status', 'meta_tco2e_compensar'],
            properties: {
                empresa_id: {
                    bsonType: ['string', 'objectId'],
                    description: 'Empresa financiadora / patrocinadora'
                },
                nome_iniciativa: {
                    bsonType: 'string',
                    description: 'Nome fantasia do projeto ou programa ESG'
                },
                tipo_projeto: {
                    enum: [
                        'REFLORESTAMENTO_NATIVO',
                        'ENERGIA_RENOVAVEL_IREC',
                        'ECONOMIA_CIRCULAR_RECICLAGEM',
                        'PRESERVACAO_BIODIVERSIDADE'
                    ],
                    description: 'Modalidade da iniciativa ESG'
                },
                status: {
                    enum: ['PLANEJADO', 'EM_ANDAMENTO', 'CONCLUIDO', 'AUDITADO'],
                    description: 'Fase de execução do projeto'
                },
                meta_tco2e_compensar: {
                    bsonType: ['double', 'int', 'decimal'],
                    description: 'Toneladas de CO2 equivalente a neutralizar'
                },
                tco2e_compensado_acumulado: {
                    bsonType: ['double', 'int', 'decimal'],
                    description: 'Créditos ou reduções já comprovadas e emitidas'
                },
                investimento_brl: {
                    bsonType: ['double', 'int', 'decimal'],
                    description: 'Aporte financeiro alocado na iniciativa'
                },
                detalhes_execucao: {
                    bsonType: 'object',
                    description: 'Estrutura flexível contendo bioma, coordenadas, árvores ou cooperativa'
                },
                data_inicio: { bsonType: 'date' }
            }
        }
    }
});

// Índices da collection 'projetos_compensacao'
db.projetos_compensacao.createIndex({ empresa_id: 1 }, { name: 'idx_projetos_empresa' });
db.projetos_compensacao.createIndex({ tipo_projeto: 1, status: 1 }, { name: 'idx_projetos_tipo_status' });
db.projetos_compensacao.createIndex({ 'detalhes_execucao.bioma': 1 }, { sparse: true, name: 'idx_proj_bioma' });

print('>>> [SUCESSO] 5 Collections criadas com schemas validadores e índices otimizados no banco fiap_esg!');
