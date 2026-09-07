package br.com.fiap.fiap_esg_spring.service;

import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.ConsumoEnergia;
import br.com.fiap.fiap_esg_spring.model.Equipamento;
import br.com.fiap.fiap_esg_spring.repository.ConsumoEnergiaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsumoEnergiaService {

    private final ConsumoEnergiaRepository repository;
    private final EquipamentoService equipamentoService;

    // Fator médio oficial do Sistema Interligado Nacional Brasileiro: aprox. 0.090 kg CO2 / kWh
    private static final BigDecimal FATOR_EMISSAO_CO2 = new BigDecimal("0.090");

    public List<ConsumoEnergia> listar(String equipamentoId) {
        if (equipamentoId == null || equipamentoId.isBlank()) {
            return repository.findAll();
        }
        equipamentoService.verificaEquipamento(equipamentoId);
        return repository.findByEquipamentoId(equipamentoId);
    }

    public ConsumoEnergia buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consumo de energia nao encontrado: " + id));
    }

    public ConsumoEnergia criar(ConsumoEnergia consumo) {
        String equipId = consumo.getEquipamentoId();
        if (equipId == null && consumo.getEquipamento() != null) {
            equipId = consumo.getEquipamento().getId();
        }
        Equipamento equipamento = equipamentoService.buscarPorId(equipId);
        consumo.setEquipamentoId(equipamento.getId());
        consumo.setEquipamento(equipamento);

        if (consumo.getDataHora() == null) {
            consumo.setDataHora(LocalDateTime.now());
        }

        // Cálculo automático da pegada de carbono ESG se não informado
        if (consumo.getPegadaCarbonoKgCo2e() == null && consumo.getConsumoKwh() != null) {
            consumo.setPegadaCarbonoKgCo2e(
                    consumo.getConsumoKwh().multiply(FATOR_EMISSAO_CO2).setScale(2, RoundingMode.HALF_UP)
            );
        }

        return repository.save(consumo);
    }

    public ConsumoEnergia atualizar(String id, ConsumoEnergia dados) {
        ConsumoEnergia consumo = buscarPorId(id);
        String equipId = dados.getEquipamentoId();
        if (equipId == null && dados.getEquipamento() != null) {
            equipId = dados.getEquipamento().getId();
        }
        if (equipId != null) {
            Equipamento equipamento = equipamentoService.buscarPorId(equipId);
            consumo.setEquipamentoId(equipamento.getId());
            consumo.setEquipamento(equipamento);
        }
        if (dados.getDataHora() != null) {
            consumo.setDataHora(dados.getDataHora());
        }
        consumo.setTipoRecurso(dados.getTipoRecurso());
        consumo.setConsumoKwh(dados.getConsumoKwh());
        consumo.setTempoOciosoMin(dados.getTempoOciosoMin());
        consumo.setPegadaCarbonoKgCo2e(dados.getPegadaCarbonoKgCo2e());
        if (dados.getTelemetriaDetalhada() != null) {
            consumo.setTelemetriaDetalhada(dados.getTelemetriaDetalhada());
        }
        return repository.save(consumo);
    }

    public void excluir(String id) {
        ConsumoEnergia consumo = buscarPorId(id);
        repository.delete(consumo);
    }
}
