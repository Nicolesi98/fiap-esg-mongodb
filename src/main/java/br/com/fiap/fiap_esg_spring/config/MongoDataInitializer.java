package br.com.fiap.fiap_esg_spring.config;

import br.com.fiap.fiap_esg_spring.model.AcaoAlerta;
import br.com.fiap.fiap_esg_spring.model.Alerta;
import br.com.fiap.fiap_esg_spring.model.AlertaStatus;
import br.com.fiap.fiap_esg_spring.model.ConsumoEnergia;
import br.com.fiap.fiap_esg_spring.model.Empresa;
import br.com.fiap.fiap_esg_spring.model.Equipamento;
import br.com.fiap.fiap_esg_spring.model.EquipamentoStatus;
import br.com.fiap.fiap_esg_spring.model.EsgScore;
import br.com.fiap.fiap_esg_spring.model.Localizacao;
import br.com.fiap.fiap_esg_spring.model.MetaSustentabilidade;
import br.com.fiap.fiap_esg_spring.model.ProjetoCompensacao;
import br.com.fiap.fiap_esg_spring.repository.AlertaRepository;
import br.com.fiap.fiap_esg_spring.repository.ConsumoEnergiaRepository;
import br.com.fiap.fiap_esg_spring.repository.EmpresaRepository;
import br.com.fiap.fiap_esg_spring.repository.EquipamentoRepository;
import br.com.fiap.fiap_esg_spring.repository.ProjetoCompensacaoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class MongoDataInitializer implements CommandLineRunner {

    private final EmpresaRepository empresaRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final ConsumoEnergiaRepository consumoEnergiaRepository;
    private final AlertaRepository alertaRepository;
    private final ProjetoCompensacaoRepository projetoCompensacaoRepository;

    @Override
    public void run(String... args) {
        if (empresaRepository.count() > 0) {
            log.info(">>> Banco MongoDB ja inicializado com dados. Pulando seeding inicial.");
            return;
        }

        log.info(">>> Inicializando dados ESG nas 5 collections do MongoDB...");

        // 1. Inserção de 10 Empresas
        List<Empresa> empresas = List.of(
                Empresa.builder().id("660000000000000000000001").nome("EcoTech Solucoes").cnpj("11.111.111/0001-11").setor("TECNOLOGIA")
                        .statusAdesaoEsg("ATIVO").esgRating(new EsgScore(92.5, 95.0, 88.0, 94.5, "AAA"))
                        .metasSustentabilidade(List.of(new MetaSustentabilidade("Net-Zero", 2030, 65.0)))
                        .certificacoes(List.of("ISO 14001", "B-Corp")).build(),
                Empresa.builder().id("660000000000000000000002").nome("Energia Verde Renovaveis").cnpj("22.222.222/0001-22").setor("ENERGIA")
                        .statusAdesaoEsg("ATIVO").esgRating(new EsgScore(96.0, 98.5, 92.0, 97.5, "AAA"))
                        .certificacoes(List.of("ISO 14001", "I-REC")).build(),
                Empresa.builder().id("660000000000000000000003").nome("Cidade Sustentavel Mobilidade").cnpj("33.333.333/0001-33").setor("LOGISTICA")
                        .statusAdesaoEsg("ATIVO").esgRating(new EsgScore(84.0, 82.0, 86.0, 84.0, "AA")).build(),
                Empresa.builder().id("660000000000000000000004").nome("Industria Solar BR").cnpj("44.444.444/0001-44").setor("INDUSTRIA")
                        .statusAdesaoEsg("ATIVO").esgRating(new EsgScore(78.5, 76.0, 80.0, 79.5, "A")).build(),
                Empresa.builder().id("660000000000000000000005").nome("Consumo Inteligente Varejo").cnpj("55.555.555/0001-55").setor("VAREJO")
                        .statusAdesaoEsg("ATIVO").esgRating(new EsgScore(71.0, 70.0, 74.0, 69.0, "BBB")).build(),
                Empresa.builder().id("660000000000000000000006").nome("AgroBio Sustentabilidade").cnpj("66.666.666/0001-66").setor("AGRONEGOCIO")
                        .statusAdesaoEsg("ATIVO").esgRating(new EsgScore(88.0, 89.0, 85.0, 90.0, "AA")).build(),
                Empresa.builder().id("660000000000000000000007").nome("BioFarma Saude Ambiental").cnpj("77.777.777/0001-77").setor("FARMACEUTICO")
                        .statusAdesaoEsg("ATIVO").esgRating(new EsgScore(81.5, 80.0, 85.0, 79.5, "A")).build(),
                Empresa.builder().id("660000000000000000000008").nome("Construcoes Verdes Eng").cnpj("88.888.888/0001-88").setor("CONSTRUCAO_CIVIL")
                        .statusAdesaoEsg("EM_AVALIACAO").esgRating(new EsgScore(68.0, 65.0, 70.0, 69.0, "BB")).build(),
                Empresa.builder().id("660000000000000000000009").nome("Logistica Reversa Brasil").cnpj("99.999.999/0001-99").setor("RECICLAGEM")
                        .statusAdesaoEsg("ATIVO").esgRating(new EsgScore(94.0, 97.0, 93.0, 92.0, "AAA")).build(),
                Empresa.builder().id("660000000000000000000010").nome("Fintech Impacto Verde").cnpj("10.000.000/0001-00").setor("FINANCAS")
                        .statusAdesaoEsg("ATIVO").esgRating(new EsgScore(91.0, 88.0, 92.0, 93.0, "AA")).build()
        );
        empresaRepository.saveAll(empresas);

        // 2. Inserção de 10 Equipamentos (Demonstrando modelo flexível / polimorfismo)
        List<Equipamento> equipamentos = List.of(
                Equipamento.builder().id("661000000000000000000001").empresaId("660000000000000000000001").nome("Chiller Central EcoCooling")
                        .categoria("CLIMATIZACAO").tipo("CLIMATIZACAO").status(EquipamentoStatus.LIGADO)
                        .limiteConsumoKwh(new BigDecimal("50.0")).limiteOciosidadeMin(30)
                        .localizacao(new Localizacao("A", "Maquinas", "Subsolo"))
                        .especificacoesTecnicas(Map.of("gasRefrigerante", "R-32", "gwp", 675, "potenciaBtu", 6000000)).build(),
                Equipamento.builder().id("661000000000000000000002").empresaId("660000000000000000000002").nome("Inversor Solar Central 100kW")
                        .categoria("ENERGIA_SOLAR").tipo("SOLAR").status(EquipamentoStatus.LIGADO)
                        .limiteConsumoKwh(new BigDecimal("120.0")).limiteOciosidadeMin(15)
                        .especificacoesTecnicas(Map.of("capacidadeKwp", 115.5, "eficienciaPct", 98.6)).build(),
                Equipamento.builder().id("661000000000000000000003").empresaId("660000000000000000000001").nome("Cluster Servidores Blade-X")
                        .categoria("TI_DATACENTER").tipo("TI").status(EquipamentoStatus.LIGADO)
                        .limiteConsumoKwh(new BigDecimal("80.0")).limiteOciosidadeMin(45)
                        .especificacoesTecnicas(Map.of("pueAlvo", 1.15, "refrigeracaoLiquida", true)).build(),
                Equipamento.builder().id("661000000000000000000004").empresaId("660000000000000000000003").nome("Carregador DC 150kW Frota")
                        .categoria("MOBILIDADE_ELETRICA").tipo("MOBILIDADE").status(EquipamentoStatus.LIGADO)
                        .limiteConsumoKwh(new BigDecimal("160.0")).limiteOciosidadeMin(20)
                        .especificacoesTecnicas(Map.of("potenciaKw", 150, "conectores", List.of("CCS2", "CHAdeMO"))).build(),
                Equipamento.builder().id("661000000000000000000005").empresaId("660000000000000000000004").nome("Motor Industrial IE4")
                        .categoria("MAQUINARIO_INDUSTRIAL").tipo("PRODUCAO").status(EquipamentoStatus.LIGADO)
                        .limiteConsumoKwh(new BigDecimal("100.0")).limiteOciosidadeMin(20)
                        .especificacoesTecnicas(Map.of("classeEficiencia", "IE4 Super Premium", "potenciaCv", 75)).build(),
                Equipamento.builder().id("661000000000000000000006").empresaId("660000000000000000000005").nome("Sistema VRF Loja Conceito")
                        .categoria("CLIMATIZACAO").tipo("CLIMATIZACAO").status(EquipamentoStatus.LIGADO)
                        .limiteConsumoKwh(new BigDecimal("35.0")).limiteOciosidadeMin(25)
                        .especificacoesTecnicas(Map.of("potenciaBtu", 180000, "gasRefrigerante", "R-410A")).build(),
                Equipamento.builder().id("661000000000000000000007").empresaId("660000000000000000000006").nome("Caldeira Queima Biomassa")
                        .categoria("MAQUINARIO_INDUSTRIAL").tipo("TERMICO").status(EquipamentoStatus.LIGADO)
                        .limiteConsumoKwh(new BigDecimal("150.0")).limiteOciosidadeMin(60)
                        .especificacoesTecnicas(Map.of("combustivel", "BIOMASSA", "pressaoBar", 42.0)).build(),
                Equipamento.builder().id("661000000000000000000008").empresaId("660000000000000000000010").nome("Wallbox AC 22kW Sede")
                        .categoria("MOBILIDADE_ELETRICA").tipo("MOBILIDADE").status(EquipamentoStatus.LIGADO)
                        .limiteConsumoKwh(new BigDecimal("25.0")).limiteOciosidadeMin(30)
                        .especificacoesTecnicas(Map.of("potenciaKw", 22, "conectores", List.of("Type 2"))).build(),
                Equipamento.builder().id("661000000000000000000009").empresaId("660000000000000000000002").nome("Tracker Solar Monoaxial")
                        .categoria("ENERGIA_SOLAR").tipo("SOLAR").status(EquipamentoStatus.LIGADO)
                        .limiteConsumoKwh(new BigDecimal("260.0")).limiteOciosidadeMin(10)
                        .especificacoesTecnicas(Map.of("capacidadeKwp", 280.0, "ganhoTrackerPct", 22.5)).build(),
                Equipamento.builder().id("661000000000000000000010").empresaId("660000000000000000000001").nome("Storage NVMe EcoSaver")
                        .categoria("TI_DATACENTER").tipo("TI").status(EquipamentoStatus.LIGADO)
                        .limiteConsumoKwh(new BigDecimal("20.0")).limiteOciosidadeMin(30)
                        .especificacoesTecnicas(Map.of("capacidadePetabytes", 1.5, "hibernacao", true)).build()
        );
        equipamentoRepository.saveAll(equipamentos);

        // 3. Inserção de 10 Leituras de Consumo / Telemetria
        List<ConsumoEnergia> leituras = List.of(
                ConsumoEnergia.builder().id("662000000000000000000001").equipamentoId("661000000000000000000001")
                        .tipoRecurso("ENERGIA_ELETRICA").dataHora(LocalDateTime.now().minusHours(2))
                        .consumoKwh(new BigDecimal("65.50")).tempoOciosoMin(10).pegadaCarbonoKgCo2e(new BigDecimal("5.89")).build(),
                ConsumoEnergia.builder().id("662000000000000000000002").equipamentoId("661000000000000000000002")
                        .tipoRecurso("GERACAO_SOLAR_RENOVAVEL").dataHora(LocalDateTime.now().minusHours(4))
                        .consumoKwh(BigDecimal.ZERO).tempoOciosoMin(0).pegadaCarbonoKgCo2e(BigDecimal.ZERO)
                        .telemetriaDetalhada(Map.of("energiaGeradaKwh", 98.4, "co2EvitadoKg", 8.85)).build(),
                ConsumoEnergia.builder().id("662000000000000000000003").equipamentoId("661000000000000000000003")
                        .tipoRecurso("ENERGIA_ELETRICA").dataHora(LocalDateTime.now().minusHours(12))
                        .consumoKwh(new BigDecimal("95.00")).tempoOciosoMin(50).pegadaCarbonoKgCo2e(new BigDecimal("8.55")).build(),
                ConsumoEnergia.builder().id("662000000000000000000004").equipamentoId("661000000000000000000004")
                        .tipoRecurso("ENERGIA_ELETRICA").dataHora(LocalDateTime.now().minusHours(1))
                        .consumoKwh(new BigDecimal("142.00")).tempoOciosoMin(5).pegadaCarbonoKgCo2e(new BigDecimal("12.78")).build(),
                ConsumoEnergia.builder().id("662000000000000000000005").equipamentoId("661000000000000000000005")
                        .tipoRecurso("ENERGIA_ELETRICA").dataHora(LocalDateTime.now().minusHours(6))
                        .consumoKwh(new BigDecimal("70.00")).tempoOciosoMin(35).pegadaCarbonoKgCo2e(new BigDecimal("6.30")).build(),
                ConsumoEnergia.builder().id("662000000000000000000006").equipamentoId("661000000000000000000005")
                        .tipoRecurso("AGUA").dataHora(LocalDateTime.now().minusHours(5))
                        .consumoKwh(new BigDecimal("12.00")).tempoOciosoMin(0).pegadaCarbonoKgCo2e(new BigDecimal("1.08"))
                        .telemetriaDetalhada(Map.of("volumeM3", 45.8, "percentualReuso", 85.0)).build(),
                ConsumoEnergia.builder().id("662000000000000000000007").equipamentoId("661000000000000000000007")
                        .tipoRecurso("COMBUSTIVEL_FOSSIL").dataHora(LocalDateTime.now().minusHours(8))
                        .consumoKwh(new BigDecimal("135.00")).tempoOciosoMin(15).pegadaCarbonoKgCo2e(new BigDecimal("42.50")).build(),
                ConsumoEnergia.builder().id("662000000000000000000008").equipamentoId("661000000000000000000006")
                        .tipoRecurso("ENERGIA_ELETRICA").dataHora(LocalDateTime.now().minusHours(3))
                        .consumoKwh(new BigDecimal("28.00")).tempoOciosoMin(12).pegadaCarbonoKgCo2e(new BigDecimal("2.52")).build(),
                ConsumoEnergia.builder().id("662000000000000000000009").equipamentoId("661000000000000000000009")
                        .tipoRecurso("GERACAO_SOLAR_RENOVAVEL").dataHora(LocalDateTime.now().minusHours(7))
                        .consumoKwh(BigDecimal.ZERO).tempoOciosoMin(0).pegadaCarbonoKgCo2e(BigDecimal.ZERO)
                        .telemetriaDetalhada(Map.of("energiaGeradaKwh", 245.0, "co2EvitadoKg", 22.05)).build(),
                ConsumoEnergia.builder().id("662000000000000000000010").equipamentoId("661000000000000000000010")
                        .tipoRecurso("ENERGIA_ELETRICA").dataHora(LocalDateTime.now().minusHours(10))
                        .consumoKwh(new BigDecimal("11.20")).tempoOciosoMin(28).pegadaCarbonoKgCo2e(new BigDecimal("1.01")).build()
        );
        consumoEnergiaRepository.saveAll(leituras);

        // 4. Inserção de 10 Alertas ESG
        List<Alerta> alertas = List.of(
                Alerta.builder().id("663000000000000000000001").equipamentoId("661000000000000000000001")
                        .pilarEsg("ENVIRONMENTAL").tipoAlerta("CONSUMO_EXCESSIVO").mensagem("Chiller consumindo 65.5 kWh acima do limite.")
                        .severidade("ALTA").status(AlertaStatus.ABERTO).dataHora(LocalDateTime.now().minusMinutes(40))
                        .historicoAcoes(List.of(new AcaoAlerta(LocalDateTime.now().minusMinutes(40), "IoT", "Disparo", "ABERTO"))).build(),
                Alerta.builder().id("663000000000000000000002").equipamentoId("661000000000000000000003")
                        .pilarEsg("ENVIRONMENTAL").tipoAlerta("OCIOSIDADE_ALTA").mensagem("Servidor ocioso por 50 min.")
                        .severidade("MEDIA").status(AlertaStatus.ABERTO).dataHora(LocalDateTime.now().minusHours(2)).build(),
                Alerta.builder().id("663000000000000000000003").equipamentoId("661000000000000000000007")
                        .pilarEsg("GOVERNANCE").tipoAlerta("CONFORMIDADE_AMBIENTAL").mensagem("Caldeira requer auditoria de emissoes.")
                        .severidade("CRITICA").status(AlertaStatus.ABERTO).dataHora(LocalDateTime.now().minusDays(1)).build(),
                Alerta.builder().id("663000000000000000000004").equipamentoId("661000000000000000000001")
                        .pilarEsg("SOCIAL").tipoAlerta("CONFORTO_TERMICO").mensagem("Temperatura ambiente acima de 28C.")
                        .severidade("ALTA").status(AlertaStatus.RESOLVIDO).dataHora(LocalDateTime.now().minusDays(2)).build(),
                Alerta.builder().id("663000000000000000000005").equipamentoId("661000000000000000000005")
                        .pilarEsg("ENVIRONMENTAL").tipoAlerta("OCIOSIDADE_ALTA").mensagem("Motor ocioso por 35 minutos.")
                        .severidade("MEDIA").status(AlertaStatus.RESOLVIDO).dataHora(LocalDateTime.now().minusDays(3)).build(),
                Alerta.builder().id("663000000000000000000006").equipamentoId("661000000000000000000004")
                        .pilarEsg("GOVERNANCE").tipoAlerta("REPORTE_GRI_ATRASO").mensagem("Atraso no consolidado GRI 302.")
                        .severidade("MEDIA").status(AlertaStatus.ABERTO).dataHora(LocalDateTime.now().minusDays(3)).build(),
                Alerta.builder().id("663000000000000000000007").equipamentoId("661000000000000000000002")
                        .pilarEsg("ENVIRONMENTAL").tipoAlerta("EFICIENCIA_REDUZIDA").mensagem("Inversor 18% abaixo do esperado.")
                        .severidade("BAIXA").status(AlertaStatus.ABERTO).dataHora(LocalDateTime.now().minusHours(5)).build(),
                Alerta.builder().id("663000000000000000000008").equipamentoId("661000000000000000000005")
                        .pilarEsg("SOCIAL").tipoAlerta("RUIDO_OCUPACIONAL").mensagem("Ruido continuo de 89 dBA na linha.")
                        .severidade("ALTA").status(AlertaStatus.RESOLVIDO).dataHora(LocalDateTime.now().minusDays(4)).build(),
                Alerta.builder().id("663000000000000000000009").equipamentoId("661000000000000000000006")
                        .pilarEsg("ENVIRONMENTAL").tipoAlerta("VAZAMENTO_GAS_ESTUFA").mensagem("Queda de pressao do fluido R-410A.")
                        .severidade("CRITICA").status(AlertaStatus.ABERTO).dataHora(LocalDateTime.now().minusHours(1)).build(),
                Alerta.builder().id("663000000000000000000010").equipamentoId("661000000000000000000009")
                        .pilarEsg("GOVERNANCE").tipoAlerta("CALIBRACAO_IREC").mensagem("Calibracao do medidor I-REC realizada.")
                        .severidade("BAIXA").status(AlertaStatus.RESOLVIDO).dataHora(LocalDateTime.now().minusDays(5)).build()
        );
        alertaRepository.saveAll(alertas);

        // 5. Inserção de 10 Projetos de Compensação
        List<ProjetoCompensacao> projetos = List.of(
                ProjetoCompensacao.builder().id("664000000000000000000001").empresaId("660000000000000000000001")
                        .nomeIniciativa("Corredor Ecologico Serra do Mar").tipoProjeto("REFLORESTAMENTO_NATIVO")
                        .status("EM_ANDAMENTO").metaTco2eCompensar(new BigDecimal("2500.0"))
                        .tco2eCompensadoAcumulado(new BigDecimal("1420.0")).investimentoBrl(new BigDecimal("480000.00"))
                        .detalhesExecucao(Map.of("bioma", "Mata Atlantica", "arvores", 42000)).build(),
                ProjetoCompensacao.builder().id("664000000000000000000002").empresaId("660000000000000000000002")
                        .nomeIniciativa("Parque Solar Sertao").tipoProjeto("ENERGIA_RENOVAVEL_IREC")
                        .status("CONCLUIDO").metaTco2eCompensar(new BigDecimal("5000.0"))
                        .tco2eCompensadoAcumulado(new BigDecimal("5240.0")).investimentoBrl(new BigDecimal("1200000.00")).build(),
                ProjetoCompensacao.builder().id("664000000000000000000003").empresaId("660000000000000000000009")
                        .nomeIniciativa("Circulo Verde Logistica Reversa").tipoProjeto("ECONOMIA_CIRCULAR_RECICLAGEM")
                        .status("EM_ANDAMENTO").metaTco2eCompensar(new BigDecimal("1800.0"))
                        .tco2eCompensadoAcumulado(new BigDecimal("1250.0")).investimentoBrl(new BigDecimal("350000.00")).build(),
                ProjetoCompensacao.builder().id("664000000000000000000004").empresaId("660000000000000000000006")
                        .nomeIniciativa("Amazonia Viva REDD+").tipoProjeto("PRESERVACAO_BIODIVERSIDADE")
                        .status("EM_ANDAMENTO").metaTco2eCompensar(new BigDecimal("10000.0"))
                        .tco2eCompensadoAcumulado(new BigDecimal("6800.0")).investimentoBrl(new BigDecimal("2500000.00")).build(),
                ProjetoCompensacao.builder().id("664000000000000000000005").empresaId("660000000000000000000004")
                        .nomeIniciativa("Nascentes do Cerrado").tipoProjeto("REFLORESTAMENTO_NATIVO")
                        .status("EM_ANDAMENTO").metaTco2eCompensar(new BigDecimal("1500.0"))
                        .tco2eCompensadoAcumulado(new BigDecimal("890.0")).investimentoBrl(new BigDecimal("290000.00")).build(),
                ProjetoCompensacao.builder().id("664000000000000000000006").empresaId("660000000000000000000007")
                        .nomeIniciativa("Usina de Biogas Efluentes").tipoProjeto("ENERGIA_RENOVAVEL_IREC")
                        .status("CONCLUIDO").metaTco2eCompensar(new BigDecimal("3200.0"))
                        .tco2eCompensadoAcumulado(new BigDecimal("3350.0")).investimentoBrl(new BigDecimal("850000.00")).build(),
                ProjetoCompensacao.builder().id("664000000000000000000007").empresaId("660000000000000000000001")
                        .nomeIniciativa("Mineracao Urbana e-Lixo").tipoProjeto("ECONOMIA_CIRCULAR_RECICLAGEM")
                        .status("EM_ANDAMENTO").metaTco2eCompensar(new BigDecimal("900.0"))
                        .tco2eCompensadoAcumulado(new BigDecimal("580.0")).investimentoBrl(new BigDecimal("180000.00")).build(),
                ProjetoCompensacao.builder().id("664000000000000000000008").empresaId("660000000000000000000003")
                        .nomeIniciativa("Carbono Azul Manguezais").tipoProjeto("PRESERVACAO_BIODIVERSIDADE")
                        .status("EM_ANDAMENTO").metaTco2eCompensar(new BigDecimal("4000.0"))
                        .tco2eCompensadoAcumulado(new BigDecimal("2100.0")).investimentoBrl(new BigDecimal("950000.00")).build(),
                ProjetoCompensacao.builder().id("664000000000000000000009").empresaId("660000000000000000000010")
                        .nomeIniciativa("Escolas Solares").tipoProjeto("ENERGIA_RENOVAVEL_IREC")
                        .status("CONCLUIDO").metaTco2eCompensar(new BigDecimal("600.0"))
                        .tco2eCompensadoAcumulado(new BigDecimal("640.0")).investimentoBrl(new BigDecimal("220000.00")).build(),
                ProjetoCompensacao.builder().id("664000000000000000000010").empresaId("660000000000000000000006")
                        .nomeIniciativa("Agrofloresta Sintropica").tipoProjeto("REFLORESTAMENTO_NATIVO")
                        .status("EM_ANDAMENTO").metaTco2eCompensar(new BigDecimal("2100.0"))
                        .tco2eCompensadoAcumulado(new BigDecimal("1100.0")).investimentoBrl(new BigDecimal("380000.00")).build()
        );
        projetoCompensacaoRepository.saveAll(projetos);

        log.info(">>> [SUCESSO] Inicializacao concluida: 5 collections populadas com 10 documentos cada (total 50)!");
    }
}
