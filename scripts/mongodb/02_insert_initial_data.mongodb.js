/**
 * FIAP - MBA / Pós-Graduação em Engenharia de Software / Banco de Dados NoSQL
 * Projeto ESG - Monitoramento de Sustentabilidade e Eficiência Energética
 * 
 * Script: 02_insert_initial_data.mongodb.js
 * Descrição: Inserção de pelo menos 10 documentos completos por collection (total de 50 documentos),
 *            demonstrando riqueza de dados no tema ESG e uso de modelo flexível (polimorfismo).
 */

use('fiap_esg');

// =============================================================================
// 1. INSERÇÃO NA COLLECTION: empresas (10 DOCUMENTOS)
// Variando setores, ratings ESG e compromissos institucionais.
// =============================================================================
db.empresas.insertMany([
    {
        _id: ObjectId("660000000000000000000001"),
        nome: "EcoTech Solucoes Sustentaveis",
        cnpj: "11.111.111/0001-11",
        setor: "TECNOLOGIA",
        status_adesao_esg: "ATIVO",
        esg_rating: {
            score_geral: 92.5,
            score_ambiental: 95.0,
            score_social: 88.0,
            score_governanca: 94.5,
            classificacao: "AAA"
        },
        metas_sustentabilidade: [
            { descricao: "Neutralidade de Carbono (Net-Zero)", ano_alvo: NumberInt(2030), percentual_conclusao: 65.0 },
            { descricao: "100% de Energia Renovavel nos Datacenters", ano_alvo: NumberInt(2026), percentual_conclusao: 90.0 }
        ],
        certificacoes: ["ISO 14001", "B-Corp", "LEED Platinum"],
        contato_sustentabilidade: {
            responsavel: "Marina Silva Santos",
            email: "esg@ecotech.com.br",
            telefone: "+55 11 98888-0001"
        },
        data_cadastro: new Date("2023-01-15T09:00:00Z")
    },
    {
        _id: ObjectId("660000000000000000000002"),
        nome: "Energia Verde Renovaveis Ltda",
        cnpj: "22.222.222/0001-22",
        setor: "ENERGIA",
        status_adesao_esg: "ATIVO",
        esg_rating: {
            score_geral: 96.0,
            score_ambiental: 98.5,
            score_social: 92.0,
            score_governanca: 97.5,
            classificacao: "AAA"
        },
        metas_sustentabilidade: [
            { descricao: "Expansao de Parques Eolicos e Solares em 500MW", ano_alvo: NumberInt(2028), percentual_conclusao: 48.0 },
            { descricao: "Impacto Social Positivo nas Comunidades do Semiárido", ano_alvo: NumberInt(2027), percentual_conclusao: 75.0 }
        ],
        certificacoes: ["ISO 14001", "ISO 50001", "I-REC Standard"],
        contato_sustentabilidade: {
            responsavel: "Carlos Eduardo Luz",
            email: "sustentabilidade@energiaverde.com.br",
            telefone: "+55 81 99999-0002"
        },
        data_cadastro: new Date("2023-02-20T10:30:00Z")
    },
    {
        _id: ObjectId("660000000000000000000003"),
        nome: "Cidade Sustentavel Mobilidade e Logistica SA",
        cnpj: "33.333.333/0001-33",
        setor: "LOGISTICA",
        status_adesao_esg: "ATIVO",
        esg_rating: {
            score_geral: 84.0,
            score_ambiental: 82.0,
            score_social: 86.0,
            score_governanca: 84.0,
            classificacao: "AA"
        },
        metas_sustentabilidade: [
            { descricao: "Eletrificacao de 60% da Frota de Transporte Urbano", ano_alvo: NumberInt(2029), percentual_conclusao: 40.0 },
            { descricao: "Reducao de 45% nas Emissoes de Escopo 1", ano_alvo: NumberInt(2030), percentual_conclusao: 32.5 }
        ],
        certificacoes: ["ISO 14001", "ISO 39001"],
        contato_sustentabilidade: {
            responsavel: "Renata Faria",
            email: "esg@cidadesustentavel.com.br",
            telefone: "+55 11 97777-0003"
        },
        data_cadastro: new Date("2023-03-10T14:15:00Z")
    },
    {
        _id: ObjectId("660000000000000000000004"),
        nome: "Industria Solar BR Manufatura Verde",
        cnpj: "44.444.444/0001-44",
        setor: "INDUSTRIA",
        status_adesao_esg: "ATIVO",
        esg_rating: {
            score_geral: 78.5,
            score_ambiental: 76.0,
            score_social: 80.0,
            score_governanca: 79.5,
            classificacao: "A"
        },
        metas_sustentabilidade: [
            { descricao: "Zero Aterro para Residuos Solidos Industriais", ano_alvo: NumberInt(2026), percentual_conclusao: 82.0 },
            { descricao: "Reuso de 80% da Agua Industrial", ano_alvo: NumberInt(2027), percentual_conclusao: 55.0 }
        ],
        certificacoes: ["ISO 14001", "ISO 9001"],
        contato_sustentabilidade: {
            responsavel: "Fernando Ramos",
            email: "ambiental@industriasolarbr.com.br",
            telefone: "+55 19 96666-0004"
        },
        data_cadastro: new Date("2023-04-05T11:00:00Z")
    },
    {
        _id: ObjectId("660000000000000000000005"),
        nome: "Consumo Inteligente Comercial e Varejo ME",
        cnpj: "55.555.555/0001-55",
        setor: "VAREJO",
        status_adesao_esg: "ATIVO",
        esg_rating: {
            score_geral: 71.0,
            score_ambiental: 70.0,
            score_social: 74.0,
            score_governanca: 69.0,
            classificacao: "BBB"
        },
        metas_sustentabilidade: [
            { descricao: "Eliminacao de Plasticos de Uso Unico em Todas as Lojas", ano_alvo: NumberInt(2026), percentual_conclusao: 88.0 },
            { descricao: "Eficiencia Energetica nas Lojas com Iluminacao 100% LED", ano_alvo: NumberInt(2025), percentual_conclusao: 100.0 }
        ],
        certificacoes: ["Selo Clima Parana"],
        contato_sustentabilidade: {
            responsavel: "Patricia Lima",
            email: "patricia@consumointeligente.com.br",
            telefone: "+55 41 95555-0005"
        },
        data_cadastro: new Date("2023-05-18T16:00:00Z")
    },
    {
        _id: ObjectId("660000000000000000000006"),
        nome: "AgroBio Sustentabilidade e Alimentos SA",
        cnpj: "66.666.666/0001-66",
        setor: "AGRONEGOCIO",
        status_adesao_esg: "ATIVO",
        esg_rating: {
            score_geral: 88.0,
            score_ambiental: 89.0,
            score_social: 85.0,
            score_governanca: 90.0,
            classificacao: "AA"
        },
        metas_sustentabilidade: [
            { descricao: "Restauração de 5.000 Hectares de Mata Ciliar", ano_alvo: NumberInt(2030), percentual_conclusao: 52.0 },
            { descricao: "Adocao de Praticas Regenerativas em 100% dos Fornecedores", ano_alvo: NumberInt(2028), percentual_conclusao: 60.0 }
        ],
        certificacoes: ["Rainforest Alliance", "Organico Brasil", "ISO 14001"],
        contato_sustentabilidade: {
            responsavel: "Lucas Alcantara",
            email: "esg@agrobio.com.br",
            telefone: "+55 16 94444-0006"
        },
        data_cadastro: new Date("2023-06-12T08:30:00Z")
    },
    {
        _id: ObjectId("660000000000000000000007"),
        nome: "BioFarma Quimica e Saude Ambiental",
        cnpj: "77.777.777/0001-77",
        setor: "FARMACEUTICO",
        status_adesao_esg: "ATIVO",
        esg_rating: {
            score_geral: 81.5,
            score_ambiental: 80.0,
            score_social: 85.0,
            score_governanca: 79.5,
            classificacao: "A"
        },
        metas_sustentabilidade: [
            { descricao: "Programa de Descarte Consciente de Medicamentos", ano_alvo: NumberInt(2027), percentual_conclusao: 65.0 },
            { descricao: "Substituicao de Solventes Poluentes por Quimica Verde", ano_alvo: NumberInt(2029), percentual_conclusao: 38.0 }
        ],
        certificacoes: ["ISO 14001", "Boas Praticas ANVISA"],
        contato_sustentabilidade: {
            responsavel: "Juliana Mendez",
            email: "sustentabilidade@biofarma.com.br",
            telefone: "+55 21 93333-0007"
        },
        data_cadastro: new Date("2023-07-22T13:45:00Z")
    },
    {
        _id: ObjectId("660000000000000000000008"),
        nome: "Construcoes Verdes Engenharia Sustentavel",
        cnpj: "88.888.888/0001-88",
        setor: "CONSTRUCAO_CIVIL",
        status_adesao_esg: "EM_AVALIACAO",
        esg_rating: {
            score_geral: 68.0,
            score_ambiental: 65.0,
            score_social: 70.0,
            score_governanca: 69.0,
            classificacao: "BB"
        },
        metas_sustentabilidade: [
            { descricao: "Certificacao LEED em 100% dos Novos Canteiros", ano_alvo: NumberInt(2028), percentual_conclusao: 45.0 },
            { descricao: "Reducao da Pegada de Carbono do Concreto em 30%", ano_alvo: NumberInt(2030), percentual_conclusao: 20.0 }
        ],
        certificacoes: ["AQUA-HQE", "PBQP-H"],
        contato_sustentabilidade: {
            responsavel: "Marcos Vinicius Maia",
            email: "esg@construcoesverdes.com.br",
            telefone: "+55 31 92222-0008"
        },
        data_cadastro: new Date("2023-08-30T10:00:00Z")
    },
    {
        _id: ObjectId("660000000000000000000009"),
        nome: "Logistica Reversa Brasil Solucoes em Reciclagem",
        cnpj: "99.999.999/0001-99",
        setor: "RECICLAGEM",
        status_adesao_esg: "ATIVO",
        esg_rating: {
            score_geral: 94.0,
            score_ambiental: 97.0,
            score_social: 93.0,
            score_governanca: 92.0,
            classificacao: "AAA"
        },
        metas_sustentabilidade: [
            { descricao: "Reciclagem de 100.000 Toneladas de Eletronicos e Plasticos", ano_alvo: NumberInt(2027), percentual_conclusao: 72.0 },
            { descricao: "Inclusao e Remuneracao Digna para 30 Cooperativas de Catadores", ano_alvo: NumberInt(2026), percentual_conclusao: 85.0 }
        ],
        certificacoes: ["ISO 14001", "Selo Eureciclo", "B-Corp"],
        contato_sustentabilidade: {
            responsavel: "Beatriz Nogueira",
            email: "beatriz@logisticareversabr.com.br",
            telefone: "+55 11 91111-0009"
        },
        data_cadastro: new Date("2023-09-14T15:20:00Z")
    },
    {
        _id: ObjectId("660000000000000000000010"),
        nome: "Fintech Impacto Verde Investimentos ESG",
        cnpj: "10.000.000/0001-00",
        setor: "SERVICOS_FINANCEIROS",
        status_adesao_esg: "ATIVO",
        esg_rating: {
            score_geral: 91.0,
            score_ambiental: 88.0,
            score_social: 92.0,
            score_governanca: 93.0,
            classificacao: "AA"
        },
        metas_sustentabilidade: [
            { descricao: "Destinacao de R$ 500 Milhoes para Green Bonds e Titulos Verdes", ano_alvo: NumberInt(2027), percentual_conclusao: 62.0 },
            { descricao: "Paridade de Genero e Diversidade nos Cargos de Lideranca", ano_alvo: NumberInt(2025), percentual_conclusao: 95.0 }
        ],
        certificacoes: ["PRI (Principles for Responsible Investment)", "B-Corp"],
        contato_sustentabilidade: {
            responsavel: "Guilherme Sampaio",
            email: "greenfinance@impactoverde.com.br",
            telefone: "+55 11 90000-0010"
        },
        data_cadastro: new Date("2023-10-01T17:00:00Z")
    }
]);


// =============================================================================
// 2. INSERÇÃO NA COLLECTION: equipamentos (10 DOCUMENTOS)
// DEMONSTRAÇÃO DO MODELO FLEXÍVEL: Especificações técnicas polimórficas variando
// de acordo com a categoria (HVAC, Solar, Carregadores EV, Datacenter, Motores).
// =============================================================================
db.equipamentos.insertMany([
    // Equipamento 1: CLIMATIZACAO (HVAC / Chiller)
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
            gwp_potencial_aquecimento_global: 675, // Baixo impacto ambiental
            temperatura_alvo_celsius: 22.0,
            inverter_tecnologia: true
        },
        data_instalacao: new Date("2023-01-20T10:00:00Z")
    },
    // Equipamento 2: ENERGIA_SOLAR (Inversor Fotovoltaico)
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
            tipo_painel: "Silicio Monocristalino Bifacial",
            irradiancia_minima_wm2: 150,
            smart_grid_ready: true
        },
        data_instalacao: new Date("2023-02-10T08:00:00Z")
    },
    // Equipamento 3: TI_DATACENTER (Servidor de Alta Eficiencia)
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
            refrigeracao_liquida_direta: true,
            fonte_certificacao: "80 Plus Titanium"
        },
        data_instalacao: new Date("2023-03-01T14:00:00Z")
    },
    // Equipamento 4: MOBILIDADE_ELETRICA (Estacao de Recarga Rápida de Frota)
    {
        _id: ObjectId("661000000000000000000004"),
        empresa_id: ObjectId("660000000000000000000003"),
        nome: "Carregador Rapido DC 150kW para Caminhoes Eletricos",
        categoria: "MOBILIDADE_ELETRICA",
        status: "LIGADO",
        limite_consumo_kwh: 160.0,
        limite_ociosidade_min: NumberInt(20),
        localizacao: { bloco: "Patio Logistico", sala: "Vagas Eletricas 01-04", andar: "Externo" },
        especificacoes_tecnicas: {
            potencia_recarga_kw: 150,
            conectores: ["CCS2", "CHAdeMO"],
            quantidade_pontos_recarga: NumberInt(2),
            protocolo_comunicacao: "OCPP 2.0.1",
            balanceamento_dinamico_carga: true
        },
        data_instalacao: new Date("2023-03-25T09:30:00Z")
    },
    // Equipamento 5: MAQUINARIO_INDUSTRIAL (Motor Eletrico IE4)
    {
        _id: ObjectId("661000000000000000000005"),
        empresa_id: ObjectId("660000000000000000000004"),
        nome: "Motor Eletrico Industrial Super Premium IE4",
        categoria: "MAQUINARIO_INDUSTRIAL",
        status: "LIGADO",
        limite_consumo_kwh: 100.0,
        limite_ociosidade_min: NumberInt(20),
        localizacao: { bloco: "Fabrica 2", sala: "Linha de Extrusao 3", andar: "Térreo" },
        especificacoes_tecnicas: {
            classe_eficiencia: "IE4 Super Premium",
            potencia_cv: 75,
            rotacao_rpm: NumberInt(1800),
            inversor_frequencia_integrado: true,
            perda_termica_reduzida_pct: 35.0
        },
        data_instalacao: new Date("2023-04-12T11:20:00Z")
    },
    // Equipamento 6: CLIMATIZACAO (VRF Multi-Split Eficiente)
    {
        _id: ObjectId("661000000000000000000006"),
        empresa_id: ObjectId("660000000000000000000005"),
        nome: "Sistema VRF Climatizacao Loja Conceito",
        categoria: "CLIMATIZACAO",
        status: "LIGADO",
        limite_consumo_kwh: 35.0,
        limite_ociosidade_min: NumberInt(25),
        localizacao: { bloco: "Loja Flagship", sala: "Salo Principal", andar: "Térreo" },
        especificacoes_tecnicas: {
            potencia_btu: 180000,
            cop_coeficiente_performance: 4.85,
            gas_refrigerante: "R-410A",
            gwp_potencial_aquecimento_global: 2088,
            sensores_presenca_integrados: true
        },
        data_instalacao: new Date("2023-05-02T13:00:00Z")
    },
    // Equipamento 7: MAQUINARIO_INDUSTRIAL (Caldeira a Biomassa)
    {
        _id: ObjectId("661000000000000000000007"),
        empresa_id: ObjectId("660000000000000000000006"),
        nome: "Caldeira a Vapor com Queima de Biomassa de Bagaço",
        categoria: "MAQUINARIO_INDUSTRIAL",
        status: "LIGADO",
        limite_consumo_kwh: 150.0,
        limite_ociosidade_min: NumberInt(60),
        localizacao: { bloco: "Usinagem", sala: "Parque Termico", andar: "Subsolo" },
        especificacoes_tecnicas: {
            tipo_combustivel: "BIOMASSA_BAGASSO_CANA",
            producao_vapor_ton_h: 25.0,
            pressao_operacao_bar: 42.0,
            filtro_manga_particulados_eficiencia_pct: 99.8,
            monitoramento_continuo_cems: true
        },
        data_instalacao: new Date("2023-06-15T07:30:00Z")
    },
    // Equipamento 8: MOBILIDADE_ELETRICA (Wallbox Inteligente Sede Administrativa)
    {
        _id: ObjectId("661000000000000000000008"),
        empresa_id: ObjectId("660000000000000000000010"),
        nome: "Estacao Wallbox AC 22kW para Carros Compartilhados",
        categoria: "MOBILIDADE_ELETRICA",
        status: "LIGADO",
        limite_consumo_kwh: 25.0,
        limite_ociosidade_min: NumberInt(30),
        localizacao: { bloco: "Garagem Sede", sala: "Estacionamento Verde", andar: "G1" },
        especificacoes_tecnicas: {
            potencia_recarga_kw: 22,
            conectores: ["Type 2 (Mennekes)"],
            smart_metering_integrado: true,
            gestao_por_app: true
        },
        data_instalacao: new Date("2023-07-01T15:00:00Z")
    },
    // Equipamento 9: ENERGIA_SOLAR (Rastreador Solar Tracker Fazenda Solar)
    {
        _id: ObjectId("661000000000000000000009"),
        empresa_id: ObjectId("660000000000000000000002"),
        nome: "Sistema Tracker Solar Mono-axial 250kW",
        categoria: "ENERGIA_SOLAR",
        status: "LIGADO",
        limite_consumo_kwh: 260.0,
        limite_ociosidade_min: NumberInt(10),
        localizacao: { bloco: "Campo Solar B", sala: "Subestacao Elevadora", andar: "Externo" },
        especificacoes_tecnicas: {
            capacidade_geracao_kwp: 280.0,
            ganho_geracao_tracker_pct: 22.5,
            angulo_rotacao_graus: 60,
            sensor_vento_protecao: true
        },
        data_instalacao: new Date("2023-08-10T11:00:00Z")
    },
    // Equipamento 10: TI_DATACENTER (Storage All-Flash EcoSaver)
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
            consumo_por_terabyte_watts: 1.8, // 70% menos energia que discos mecanicos
            modo_hibernacao_inteligente: true
        },
        data_instalacao: new Date("2023-09-05T16:30:00Z")
    }
]);


// =============================================================================
// 3. INSERÇÃO NA COLLECTION: leituras_consumo (10 DOCUMENTOS)
// DEMONSTRAÇÃO DO MODELO FLEXÍVEL: Telemetria adaptada para energia elétrica,
// consumo e reuso de água, queima de combustíveis e geração solar renovável.
// =============================================================================
db.leituras_consumo.insertMany([
    // Leitura 1: ENERGIA_ELETRICA (Chiller com sobreconsumo)
    {
        _id: ObjectId("662000000000000000000001"),
        equipamento_id: ObjectId("661000000000000000000001"),
        tipo_recurso: "ENERGIA_ELETRICA",
        data_hora: new Date("2024-05-10T14:00:00Z"),
        consumo_kwh: 65.5, // Ultrapassou limite de 50 kWh
        tempo_ocioso_min: NumberInt(10),
        pegada_carbono_kg_co2e: 5.89, // Fator médio SIN Brasil (0.090 kg/kWh)
        telemetria_detalhada: {
            tensao_fase_v: 380,
            corrente_amperes: 104.2,
            fator_potencia: 0.94,
            temperatura_condensador_celsius: 38.5,
            delta_temperatura_agua_gelada: 5.2
        }
    },
    // Leitura 2: GERACAO_SOLAR_RENOVAVEL (Geração fotovoltaica)
    {
        _id: ObjectId("662000000000000000000002"),
        equipamento_id: ObjectId("661000000000000000000002"),
        tipo_recurso: "GERACAO_SOLAR_RENOVAVEL",
        data_hora: new Date("2024-05-10T12:00:00Z"),
        consumo_kwh: 0.0,
        tempo_ocioso_min: NumberInt(0),
        pegada_carbono_kg_co2e: 0.0,
        telemetria_detalhada: {
            energia_gerada_kwh: 98.4,
            co2_evitado_kg: 8.85,
            irradiancia_solar_wm2: 890,
            temperatura_modulo_celsius: 47.0,
            eficiencia_sistema_pct: 97.8
        }
    },
    // Leitura 3: ENERGIA_ELETRICA (Servidor Datacenter com ociosidade)
    {
        _id: ObjectId("662000000000000000000003"),
        equipamento_id: ObjectId("661000000000000000000003"),
        tipo_recurso: "ENERGIA_ELETRICA",
        data_hora: new Date("2024-05-10T03:00:00Z"),
        consumo_kwh: 95.0, // Ultrapassou limite de 80 kWh
        tempo_ocioso_min: NumberInt(50), // Ultrapassou limite de 45 min
        pegada_carbono_kg_co2e: 8.55,
        telemetria_detalhada: {
            cpu_utilizacao_media_pct: 12.5, // Standby sem carga
            pue_instantaneo: 1.18,
            temperatura_inlet_ar_celsius: 21.0,
            vazao_fluido_refrigeracao_lpm: 45.0
        }
    },
    // Leitura 4: ENERGIA_ELETRICA (Carregador de Frota Elétrica em recarga rápida)
    {
        _id: ObjectId("662000000000000000000004"),
        equipamento_id: ObjectId("661000000000000000000004"),
        tipo_recurso: "ENERGIA_ELETRICA",
        data_hora: new Date("2024-05-10T16:30:00Z"),
        consumo_kwh: 142.0,
        tempo_ocioso_min: NumberInt(5),
        pegada_carbono_kg_co2e: 12.78,
        telemetria_detalhada: {
            veiculo_conectado_id: "CAMINHAO-ELETRICO-07",
            soc_inicial_pct: 18,
            soc_atual_pct: 82,
            potencia_instantanea_kw: 148.5,
            emissao_diesel_evitada_kg_co2e: 48.6
        }
    },
    // Leitura 5: ENERGIA_ELETRICA (Motor Industrial Operação Eficiente)
    {
        _id: ObjectId("662000000000000000000005"),
        equipamento_id: ObjectId("661000000000000000000005"),
        tipo_recurso: "ENERGIA_ELETRICA",
        data_hora: new Date("2024-05-10T10:00:00Z"),
        consumo_kwh: 70.0,
        tempo_ocioso_min: NumberInt(35), // Ultrapassou limite de 20 min
        pegada_carbono_kg_co2e: 6.30,
        telemetria_detalhada: {
            rpm_operacional: 1750,
            torque_percentual: 68.0,
            vibracao_mm_s: 1.2,
            temperatura_mancal_celsius: 52.0
        }
    },
    // Leitura 6: AGUA (Monitoramento Hídrico Industrial)
    {
        _id: ObjectId("662000000000000000000006"),
        equipamento_id: ObjectId("661000000000000000000005"),
        tipo_recurso: "AGUA",
        data_hora: new Date("2024-05-10T11:00:00Z"),
        consumo_kwh: 12.0,
        tempo_ocioso_min: NumberInt(0),
        pegada_carbono_kg_co2e: 1.08,
        telemetria_detalhada: {
            volume_m3: 45.8,
            fonte_captacao: "AGUA_DE_REUSO_TRATADA",
            percentual_reuso: 85.0,
            economia_recursos_hidricos_brl: 450.00,
            turbidez_ntu: 1.8
        }
    },
    // Leitura 7: COMBUSTIVEL_FOSSIL (Queima em Transição para Biocombustível)
    {
        _id: ObjectId("662000000000000000000007"),
        equipamento_id: ObjectId("661000000000000000000007"),
        tipo_recurso: "COMBUSTIVEL_FOSSIL",
        data_hora: new Date("2024-05-10T08:00:00Z"),
        consumo_kwh: 135.0,
        tempo_ocioso_min: NumberInt(15),
        pegada_carbono_kg_co2e: 42.50,
        telemetria_detalhada: {
            combustivel: "BIODIESEL_B20",
            volume_litros: 18.0,
            emissao_nox_ppm: 82.0,
            eficiencia_calorifica_pct: 88.4,
            temperatura_gases_chamine_celsius: 145.0
        }
    },
    // Leitura 8: ENERGIA_ELETRICA (VRF Loja Horario de Pico)
    {
        _id: ObjectId("662000000000000000000008"),
        equipamento_id: ObjectId("661000000000000000000006"),
        tipo_recurso: "ENERGIA_ELETRICA",
        data_hora: new Date("2024-05-10T15:00:00Z"),
        consumo_kwh: 28.0,
        tempo_ocioso_min: NumberInt(12),
        pegada_carbono_kg_co2e: 2.52,
        telemetria_detalhada: {
            temperatura_externa_celsius: 32.0,
            temperatura_interna_celsius: 23.5,
            tarifa_aplicada: "HORO_SAZONAL_FORA_PONTA",
            ocupacao_loja_pessoas: NumberInt(45)
        }
    },
    // Leitura 9: GERACAO_SOLAR_RENOVAVEL (Tracker em Dia Ensolarado)
    {
        _id: ObjectId("662000000000000000000009"),
        equipamento_id: ObjectId("661000000000000000000009"),
        tipo_recurso: "GERACAO_SOLAR_RENOVAVEL",
        data_hora: new Date("2024-05-10T13:00:00Z"),
        consumo_kwh: 0.0,
        tempo_ocioso_min: NumberInt(0),
        pegada_carbono_kg_co2e: 0.0,
        telemetria_detalhada: {
            energia_gerada_kwh: 245.0,
            co2_evitado_kg: 22.05,
            angulo_rastreador_graus: 15.0,
            perdas_por_sujeira_pct: 1.2
        }
    },
    // Leitura 10: ENERGIA_ELETRICA (Storage Noturno com Compressao)
    {
        _id: ObjectId("662000000000000000000010"),
        equipamento_id: ObjectId("661000000000000000000010"),
        tipo_recurso: "ENERGIA_ELETRICA",
        data_hora: new Date("2024-05-10T23:00:00Z"),
        consumo_kwh: 11.2,
        tempo_ocioso_min: NumberInt(28),
        pegada_carbono_kg_co2e: 1.01,
        telemetria_detalhada: {
            iops_medido: NumberInt(154000),
            taxa_desduplicacao: "4.8:1",
            energia_economizada_kwh: 22.4
        }
    }
]);


// =============================================================================
// 4. INSERÇÃO NA COLLECTION: alertas_esg (10 DOCUMENTOS)
// DEMONSTRAÇÃO DO MODELO FLEXÍVEL: Alertas distribuídos nos 3 pilares ESG
// (Environmental, Social, Governance) com histórico de ações embutido.
// =============================================================================
db.alertas_esg.insertMany([
    // Alerta 1: ENVIRONMENTAL (Consumo excessivo de eletricidade)
    {
        _id: ObjectId("663000000000000000000001"),
        equipamento_id: ObjectId("661000000000000000000001"),
        pilar_esg: "ENVIRONMENTAL",
        tipo_alerta: "CONSUMO_EXCESSIVO",
        mensagem: "Chiller operando 31% acima do limite de consumo sustentavel (65.5 kWh vs 50.0 kWh).",
        severidade: "ALTA",
        status: "ABERTO",
        data_hora: new Date("2024-05-10T14:05:00Z"),
        dados_contextuais: {
            consumo_medido_kwh: 65.5,
            limite_kwh: 50.0,
            impacto_excedente_co2_kg: 1.39,
            custo_extra_estimado_brl: 45.80
        },
        historico_acoes: [
            {
                data: new Date("2024-05-10T14:05:00Z"),
                responsavel: "Sistema Automatico IoT",
                acao: "Disparo de telemetria de alerta e notificacao via webhook",
                status_resultante: "ABERTO"
            }
        ]
    },
    // Alerta 2: ENVIRONMENTAL (Ociosidade e desperdício de energia)
    {
        _id: ObjectId("663000000000000000000002"),
        equipamento_id: ObjectId("661000000000000000000003"),
        pilar_esg: "ENVIRONMENTAL",
        tipo_alerta: "OCIOSIDADE_ALTA",
        mensagem: "Servidores em standby ocioso consumindo 95 kWh sem alocacao de carga de trabalho.",
        severidade: "MEDIA",
        status: "EM_ANALISE",
        data_hora: new Date("2024-05-10T03:15:00Z"),
        dados_contextuais: {
            tempo_ocioso_min: NumberInt(50),
            limite_ociosidade_min: NumberInt(45),
            desperdicio_kwh: 35.0
        },
        historico_acoes: [
            {
                data: new Date("2024-05-10T03:15:00Z"),
                responsavel: "Monitor de Eficiencia TI",
                acao: "Alerta gerado automaticamente por sensor de carga",
                status_resultante: "ABERTO"
            },
            {
                data: new Date("2024-05-10T04:00:00Z"),
                responsavel: "Andre Neves (DevOps)",
                acao: "Iniciando rebalanceamento de VMs e desligamento de nodes ociosos",
                status_resultante: "EM_ANALISE"
            }
        ]
    },
    // Alerta 3: GOVERNANCE (Renovação de Licença de Operação Ambiental)
    {
        _id: ObjectId("663000000000000000000003"),
        equipamento_id: ObjectId("661000000000000000000007"),
        pilar_esg: "GOVERNANCE",
        tipo_alerta: "CONFORMIDADE_AMBIENTAL",
        mensagem: "Caldeira requer auditoria de conformidade de emissao de gases antes da renovacao da licenca CETESB.",
        severidade: "CRITICA",
        status: "ABERTO",
        data_hora: new Date("2024-05-08T09:00:00Z"),
        dados_contextuais: {
            dias_para_vencimento: NumberInt(20),
            orgao_fiscalizador: "CETESB",
            protocolo_licenca: "LO-2021-987654"
        },
        historico_acoes: [
            {
                data: new Date("2024-05-08T09:00:00Z"),
                responsavel: "Agente de Compliance ESG",
                acao: "Identificacao de prazo limite em relatorio de governanca",
                status_resultante: "ABERTO"
            }
        ]
    },
    // Alerta 4: SOCIAL (Segurança no Trabalho e Climatização de Sala de Operadores)
    {
        _id: ObjectId("663000000000000000000004"),
        equipamento_id: ObjectId("661000000000000000000001"),
        pilar_esg: "SOCIAL",
        tipo_alerta: "CONFORTO_TERMICO_SEGURANCA",
        mensagem: "Temperatura ambiente na sala de operadores ultrapassou 28°C devido a falha no insuflamento.",
        severidade: "ALTA",
        status: "RESOLVIDO",
        data_hora: new Date("2024-05-05T13:20:00Z"),
        dados_contextuais: {
            temperatura_registrada_celsius: 28.5,
            norma_regulamentadora: "NR-17 Ergonomia e Conforto",
            colaboradores_afetados: NumberInt(18)
        },
        historico_acoes: [
            {
                data: new Date("2024-05-05T13:20:00Z"),
                responsavel: "Sensor IoT Termico",
                acao: "Detecção de transgressao da NR-17",
                status_resultante: "ABERTO"
            },
            {
                data: new Date("2024-05-05T14:00:00Z"),
                responsavel: "Equipe de Manutencao Predial",
                acao: "Substituicao de valvula de expansao e rearme do chiller",
                status_resultante: "RESOLVIDO"
            }
        ]
    },
    // Alerta 5: ENVIRONMENTAL (Ociosidade Prolongada de Motor Industrial)
    {
        _id: ObjectId("663000000000000000000005"),
        equipamento_id: ObjectId("661000000000000000000005"),
        pilar_esg: "ENVIRONMENTAL",
        tipo_alerta: "OCIOSIDADE_ALTA",
        mensagem: "Motor operando sem esteira carregada por 35 minutos continuos.",
        severidade: "MEDIA",
        status: "RESOLVIDO",
        data_hora: new Date("2024-05-09T10:35:00Z"),
        dados_contextuais: {
            tempo_ocioso_min: NumberInt(35),
            limite_ociosidade_min: NumberInt(20),
            energia_desperdicada_kwh: 18.2
        },
        historico_acoes: [
            {
                data: new Date("2024-05-09T10:35:00Z"),
                responsavel: "Trigger IoT Consumo",
                acao: "Geracao de incidente por tempo sem carga",
                status_resultante: "ABERTO"
            },
            {
                data: new Date("2024-05-09T10:45:00Z"),
                responsavel: "Supervisao de Producao",
                acao: "Desligamento remoto via supervisorio CLP",
                status_resultante: "RESOLVIDO"
            }
        ]
    },
    // Alerta 6: GOVERNANCE (Relatório de Pegada de Carbono Atrasado)
    {
        _id: ObjectId("663000000000000000000006"),
        equipamento_id: ObjectId("661000000000000000000004"),
        pilar_esg: "GOVERNANCE",
        tipo_alerta: "REPORTE_GRI_ATRASO",
        mensagem: "Auditoria interna apontou ausencia de consolidacao dos dados de telemetria da frota de recarga.",
        severidade: "MEDIA",
        status: "EM_ANALISE",
        data_hora: new Date("2024-05-07T11:00:00Z"),
        dados_contextuais: {
            modulo_gri: "GRI 302: Energia e Emissoes",
            periodo: "Q1-2024"
        },
        historico_acoes: [
            {
                data: new Date("2024-05-07T11:00:00Z"),
                responsavel: "Auditoria Interna ESG",
                acao: "Abertura de apontamento de conformidade",
                status_resultante: "ABERTO"
            }
        ]
    },
    // Alerta 7: ENVIRONMENTAL (Queda de Rendimento Fotovoltaico por Sujidade)
    {
        _id: ObjectId("663000000000000000000007"),
        equipamento_id: ObjectId("661000000000000000000002"),
        pilar_esg: "ENVIRONMENTAL",
        tipo_alerta: "EFICIENCIA_REDUZIDA",
        mensagem: "Inversor registrando geracao 18% abaixo do modelo preditivo de irradiancia.",
        severidade: "BAIXA",
        status: "ABERTO",
        data_hora: new Date("2024-05-10T13:00:00Z"),
        dados_contextuais: {
            geracao_esperada_kwh: 120.0,
            geracao_real_kwh: 98.4,
            causa_provavel: "Acumulo de poeira nas placas solares"
        },
        historico_acoes: [
            {
                data: new Date("2024-05-10T13:00:00Z"),
                responsavel: "Algoritmo de Machine Learning Solar",
                acao: "Sinalizacao de desvio de performance",
                status_resultante: "ABERTO"
            }
        ]
    },
    // Alerta 8: SOCIAL (Vibração Excessiva em Área com Operadores)
    {
        _id: ObjectId("663000000000000000000008"),
        equipamento_id: ObjectId("661000000000000000000005"),
        pilar_esg: "SOCIAL",
        tipo_alerta: "SAUDE_OCUPACIONAL_RUIDO",
        mensagem: "Sensor acustico registrou 89 dBA continuo na vizinhanca do posto de trabalho.",
        severidade: "ALTA",
        status: "MITIGADO",
        data_hora: new Date("2024-05-06T15:30:00Z"),
        dados_contextuais: {
            limite_norma_dba: 85.0,
            medicao_dba: 89.4,
            norma_referencia: "NR-15 Anexo 1"
        },
        historico_acoes: [
            {
                data: new Date("2024-05-06T15:30:00Z"),
                responsavel: "Sensor IoT Ruido Fabril",
                acao: "Notificacao de seguranca ocupacional",
                status_resultante: "ABERTO"
            },
            {
                data: new Date("2024-05-06T16:15:00Z"),
                responsavel: "Tecnico de Seguranca do Trabalho",
                acao: "Instalacao temporaria de biombo acustico e remanejamento dos operadores",
                status_resultante: "MITIGADO"
            }
        ]
    },
    // Alerta 9: ENVIRONMENTAL (Vazamento Potencial de Gás Refrigerante)
    {
        _id: ObjectId("663000000000000000000009"),
        equipamento_id: ObjectId("661000000000000000000006"),
        pilar_esg: "ENVIRONMENTAL",
        tipo_alerta: "VAZAMENTO_GAS_EFEITO_ESTUFA",
        mensagem: "Sensor de pressao de linha frigorifica VRF acusou queda subita de pressao de fluido R-410A.",
        severidade: "CRITICA",
        status: "ABERTO",
        data_hora: new Date("2024-05-10T15:45:00Z"),
        dados_contextuais: {
            fluido: "R-410A",
            gwp_equivalente: 2088,
            pressao_queda_psi: 45.0
        },
        historico_acoes: [
            {
                data: new Date("2024-05-10T15:45:00Z"),
                responsavel: "Sensor de Pressao Frigorifica",
                acao: "Alerta critico de vazamento de fluido halogenado",
                status_resultante: "ABERTO"
            }
        ]
    },
    // Alerta 10: GOVERNANCE (Vencimento de Calibracao de Medidor I-REC)
    {
        _id: ObjectId("663000000000000000000010"),
        equipamento_id: ObjectId("661000000000000000000009"),
        tipo_recurso: "GOVERNANCE",
        tipo_alerta: "AUDITORIA_CALIBRACAO",
        pilar_esg: "GOVERNANCE",
        mensagem: "Medidor fiscal de geracao fotovoltaica para creditos I-REC necessita de calibracao RBC anual.",
        severidade: "BAIXA",
        status: "RESOLVIDO",
        data_hora: new Date("2024-05-02T10:00:00Z"),
        dados_contextuais: {
            organismo_acreditador: "INMETRO / RBC",
            padrao_credito: "I-REC Standard"
        },
        historico_acoes: [
            {
                data: new Date("2024-05-02T10:00:00Z"),
                responsavel: "Sistema de Gestao Metrologica",
                acao: "Notificacao de calibracao preventiva",
                status_resultante: "ABERTO"
            },
            {
                data: new Date("2024-05-04T16:00:00Z"),
                responsavel: "Laboratorio CalibraTech",
                acao: "Laudo emitido com sucesso e registrado no portal I-REC",
                status_resultante: "RESOLVIDO"
            }
        ]
    }
]);


// =============================================================================
// 5. INSERÇÃO NA COLLECTION: projetos_compensacao (10 DOCUMENTOS)
// DEMONSTRAÇÃO DO MODELO FLEXÍVEL: Variedade de iniciativas de mitigação
// (Reflorestamento nativo, Créditos I-REC, Cooperativas de Reciclagem e Biodiversidade).
// =============================================================================
db.projetos_compensacao.insertMany([
    // Projeto 1: REFLORESTAMENTO_NATIVO (Mata Atlântica)
    {
        _id: ObjectId("664000000000000000000001"),
        empresa_id: ObjectId("660000000000000000000001"),
        nome_iniciativa: "Corredor Ecologico da Serra do Mar",
        tipo_projeto: "REFLORESTAMENTO_NATIVO",
        status: "EM_ANDAMENTO",
        meta_tco2e_compensar: 2500.0,
        tco2e_compensado_acumulado: 1420.0,
        investimento_brl: 480000.00,
        detalhes_execucao: {
            bioma: "Mata Atlantica",
            area_restaurada_hectares: 35.5,
            arvores_plantadas: NumberInt(42000),
            especies_nativas: ["Pau-Brasil", "Ipe-Amarelo", "Jequitiba-Rosa", "Embauba"],
            geolocalizacao: {
                tipo: "Point",
                coordinates: [-45.352, -23.589]
            },
            certificadora_carbono: "Verra (VCS Standard)"
        },
        data_inicio: new Date("2023-03-01T00:00:00Z")
    },
    // Projeto 2: ENERGIA_RENOVAVEL_IREC (Parque Solar Sertão)
    {
        _id: ObjectId("664000000000000000000002"),
        empresa_id: ObjectId("660000000000000000000002"),
        nome_iniciativa: "Parque Solar Comunitario do Sertao",
        tipo_projeto: "ENERGIA_RENOVAVEL_IREC",
        status: "CONCLUIDO",
        meta_tco2e_compensar: 5000.0,
        tco2e_compensado_acumulado: 5240.0,
        investimento_brl: 1200000.00,
        detalhes_execucao: {
            capacidade_instalada_mwp: 3.5,
            geracao_anual_mwh: 6800.0,
            certificados_irec_emitidos: NumberInt(6800),
            comunidades_rurais_eletrificadas: NumberInt(450),
            auditor_independente: "TUV Rheinland"
        },
        data_inicio: new Date("2022-08-15T00:00:00Z")
    },
    // Projeto 3: ECONOMIA_CIRCULAR_RECICLAGEM (Cooperativas e Inclusão Social)
    {
        _id: ObjectId("664000000000000000000003"),
        empresa_id: ObjectId("660000000000000000000009"),
        nome_iniciativa: "Circulo Verde - Logistica Reversa de Embalagens",
        tipo_projeto: "ECONOMIA_CIRCULAR_RECICLAGEM",
        status: "EM_ANDAMENTO",
        meta_tco2e_compensar: 1800.0,
        tco2e_compensado_acumulado: 1250.0,
        investimento_brl: 350000.00,
        detalhes_execucao: {
            cooperativas_parceiras: ["CoopRecicla SP", "Cooperativa Esperanca Viva"],
            catadores_beneficiados: NumberInt(120),
            residuos_reciclados_toneladas: 840.5,
            materiais: ["PET", "Aluminio", "Papelao Ondulado", "PEAD"],
            creditos_reciclagem_emitidos: "Selo Eureciclo"
        },
        data_inicio: new Date("2023-05-10T00:00:00Z")
    },
    // Projeto 4: PRESERVACAO_BIODIVERSIDADE (Amazonia em Pé)
    {
        _id: ObjectId("664000000000000000000004"),
        empresa_id: ObjectId("660000000000000000000006"),
        nome_iniciativa: "Amazonia Viva - Preservacao Florestal REDD+",
        tipo_projeto: "PRESERVACAO_BIODIVERSIDADE",
        status: "EM_ANDAMENTO",
        meta_tco2e_compensar: 10000.0,
        tco2e_compensado_acumulado: 6800.0,
        investimento_brl: 2500000.00,
        detalhes_execucao: {
            bioma: "Amazonia",
            area_protegida_hectares: 12500.0,
            monitoramento_satelite: "DETER/PRODES e Constelacao Planet",
            comunidades_ribeirinhas_parceiras: NumberInt(8),
            especies_ameacadas_protegidas: ["Onca-Pintada", "Gaviao-Real", "Peixe-Boi-da-Amazonia"],
            metodologia_redd_plus: "VCS VM0015"
        },
        data_inicio: new Date("2023-01-10T00:00:00Z")
    },
    // Projeto 5: REFLORESTAMENTO_NATIVO (Cerrado e Nascentes)
    {
        _id: ObjectId("664000000000000000000005"),
        empresa_id: ObjectId("660000000000000000000004"),
        nome_iniciativa: "Nascentes Vivas do Cerrado",
        tipo_projeto: "REFLORESTAMENTO_NATIVO",
        status: "EM_ANDAMENTO",
        meta_tco2e_compensar: 1500.0,
        tco2e_compensado_acumulado: 890.0,
        investimento_brl: 290000.00,
        detalhes_execucao: {
            bioma: "Cerrado",
            nascentes_recuperadas: NumberInt(12),
            arvores_plantadas: NumberInt(18500),
            especies_nativas: ["Pequi", "Buriti", "Copaiba", "Baru"],
            geolocalizacao: {
                tipo: "Point",
                coordinates: [-48.215, -15.780]
            }
        },
        data_inicio: new Date("2023-06-20T00:00:00Z")
    },
    // Projeto 6: ENERGIA_RENOVAVEL_IREC (Biogas de Efluentes Industriais)
    {
        _id: ObjectId("664000000000000000000006"),
        empresa_id: ObjectId("660000000000000000000007"),
        nome_iniciativa: "Usina de Biogas e Biodigestao Anaerobia",
        tipo_projeto: "ENERGIA_RENOVAVEL_IREC",
        status: "CONCLUIDO",
        meta_tco2e_compensar: 3200.0,
        tco2e_compensado_acumulado: 3350.0,
        investimento_brl: 850000.00,
        detalhes_execucao: {
            metano_queimado_e_convertido_m3: 420000.0,
            geracao_eletrica_kwh: 890000.0,
            fertilizante_organico_gerado_ton: 310.0,
            padrao_certificacao: "Gold Standard"
        },
        data_inicio: new Date("2022-11-01T00:00:00Z")
    },
    // Projeto 7: ECONOMIA_CIRCULAR_RECICLAGEM (Descarte de E-lixo e Mineracao Urbana)
    {
        _id: ObjectId("664000000000000000000007"),
        empresa_id: ObjectId("660000000000000000000001"),
        nome_iniciativa: "Mineracao Urbana e Reciclagem de Eletronicos",
        tipo_projeto: "ECONOMIA_CIRCULAR_RECICLAGEM",
        status: "EM_ANDAMENTO",
        meta_tco2e_compensar: 900.0,
        tco2e_compensado_acumulado: 580.0,
        investimento_brl: 180000.00,
        detalhes_execucao: {
            equipamentos_eletronicos_recolhidos: NumberInt(15400),
            cobre_recuperado_kg: 2400.0,
            ouro_prata_recuperados_g: 650.0,
            computadores_recondicionados_doados_escolas: NumberInt(210)
        },
        data_inicio: new Date("2023-04-18T00:00:00Z")
    },
    // Projeto 8: PRESERVACAO_BIODIVERSIDADE (Manguezais e Carbono Azul)
    {
        _id: ObjectId("664000000000000000000008"),
        empresa_id: ObjectId("660000000000000000000003"),
        nome_iniciativa: "Carbono Azul - Restauracao de Manguezais",
        tipo_projeto: "PRESERVACAO_BIODIVERSIDADE",
        status: "EM_ANDAMENTO",
        meta_tco2e_compensar: 4000.0,
        tco2e_compensado_acumulado: 2100.0,
        investimento_brl: 950000.00,
        detalhes_execucao: {
            bioma: "Costeiro Marinho (Manguezal)",
            area_recuperada_hectares: 48.0,
            capacidade_retencao_carbono_x_floresta: "4x maior que floresta tropical",
            familias_marisqueiras_apoiadas: NumberInt(65)
        },
        data_inicio: new Date("2023-07-15T00:00:00Z")
    },
    // Projeto 9: ENERGIA_RENOVAVEL_IREC (Microgeracao Fotovoltaica para Escolas Publicas)
    {
        _id: ObjectId("664000000000000000000009"),
        empresa_id: ObjectId("660000000000000000000010"),
        nome_iniciativa: "Escolas Solares - Energia Limpa Comunitária",
        tipo_projeto: "ENERGIA_RENOVAVEL_IREC",
        status: "CONCLUIDO",
        meta_tco2e_compensar: 600.0,
        tco2e_compensado_acumulado: 640.0,
        investimento_brl: 220000.00,
        detalhes_execucao: {
            escolas_publicas_beneficiadas: NumberInt(15),
            capacidade_total_kwp: 180.0,
            economia_energia_revertida_em_educacao_brl: 145000.00,
            alunos_impactados: NumberInt(6200)
        },
        data_inicio: new Date("2023-02-01T00:00:00Z")
    },
    // Projeto 10: REFLORESTAMENTO_NATIVO (Agrofloresta Sintrópica)
    {
        _id: ObjectId("664000000000000000000010"),
        empresa_id: ObjectId("660000000000000000000006"),
        nome_iniciativa: "Agrofloresta Sintropica Vale Fertil",
        tipo_projeto: "REFLORESTAMENTO_NATIVO",
        status: "EM_ANDAMENTO",
        meta_tco2e_compensar: 2100.0,
        tco2e_compensado_acumulado: 1100.0,
        investimento_brl: 380000.00,
        detalhes_execucao: {
            modelo: "Agricultura Sintropica Regenerativa",
            area_hectares: 25.0,
            alimentos_organicos_produzidos_ton: 85.0,
            agricultores_familiares_capacitados: NumberInt(40),
            solo_carbono_organico_incremento_pct: 35.0
        },
        data_inicio: new Date("2023-05-01T00:00:00Z")
    }
]);

print('>>> [SUCESSO] 50 Documentos inseridos com sucesso! (10 documentos por collection demonstrando modelo flexivel/polimorfico).');
