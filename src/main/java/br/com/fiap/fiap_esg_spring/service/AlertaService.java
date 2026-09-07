package br.com.fiap.fiap_esg_spring.service;

import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.Alerta;
import br.com.fiap.fiap_esg_spring.model.AlertaStatus;
import br.com.fiap.fiap_esg_spring.model.Equipamento;
import br.com.fiap.fiap_esg_spring.repository.AlertaRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository repository;
    private final EquipamentoService equipamentoService;

    public List<Alerta> listar(String equipamentoId, AlertaStatus status) {
        if (equipamentoId != null && !equipamentoId.isBlank()) {
            equipamentoService.verificaEquipamento(equipamentoId);
            return repository.findByEquipamentoId(equipamentoId);
        }
        if (status != null) {
            return repository.findByStatus(status);
        }
        return repository.findAll();
    }

    public Alerta buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta nao encontrado: " + id));
    }

    public Alerta criar(Alerta alerta) {
        String equipId = alerta.getEquipamentoId();
        if (equipId == null && alerta.getEquipamento() != null) {
            equipId = alerta.getEquipamento().getId();
        }
        Equipamento equipamento = equipamentoService.buscarPorId(equipId);
        alerta.setEquipamentoId(equipamento.getId());
        alerta.setEquipamento(equipamento);

        if (alerta.getDataHora() == null) {
            alerta.setDataHora(LocalDateTime.now());
        }
        return repository.save(alerta);
    }

    public Alerta atualizar(String id, Alerta dados) {
        Alerta alerta = buscarPorId(id);
        String equipId = dados.getEquipamentoId();
        if (equipId == null && dados.getEquipamento() != null) {
            equipId = dados.getEquipamento().getId();
        }
        if (equipId != null) {
            Equipamento equipamento = equipamentoService.buscarPorId(equipId);
            alerta.setEquipamentoId(equipamento.getId());
            alerta.setEquipamento(equipamento);
        }
        if (dados.getDataHora() != null) {
            alerta.setDataHora(dados.getDataHora());
        }
        alerta.setPilarEsg(dados.getPilarEsg());
        alerta.setTipoAlerta(dados.getTipoAlerta());
        alerta.setMensagem(dados.getMensagem());
        alerta.setSeveridade(dados.getSeveridade());
        alerta.setStatus(dados.getStatus());
        if (dados.getDadosContextuais() != null) {
            alerta.setDadosContextuais(dados.getDadosContextuais());
        }
        if (dados.getHistoricoAcoes() != null) {
            alerta.setHistoricoAcoes(dados.getHistoricoAcoes());
        }
        return repository.save(alerta);
    }

    public void excluir(String id) {
        Alerta alerta = buscarPorId(id);
        repository.delete(alerta);
    }
}
