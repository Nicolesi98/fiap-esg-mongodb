package br.com.fiap.fiap_esg_spring.service;

import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.Empresa;
import br.com.fiap.fiap_esg_spring.model.Equipamento;
import br.com.fiap.fiap_esg_spring.repository.EquipamentoRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EquipamentoService {

    private final EquipamentoRepository repository;
    private final EmpresaService empresaService;

    public List<Equipamento> listar(String empresaId) {
        if (empresaId == null || empresaId.isBlank()) {
            return repository.findAll();
        }
        empresaService.verificaEmpresa(empresaId);
        return repository.findByEmpresaId(empresaId);
    }

    public Equipamento buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipamento nao encontrado: " + id));
    }

    public void verificaEquipamento(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Equipamento nao encontrado: " + id);
        }
    }

    public Equipamento criar(Equipamento equipamento) {
        String empId = equipamento.getEmpresaId();
        if (empId == null && equipamento.getEmpresa() != null) {
            empId = equipamento.getEmpresa().getId();
        }
        Empresa empresa = empresaService.buscarPorId(empId);
        equipamento.setEmpresaId(empresa.getId());
        equipamento.setEmpresa(empresa);
        if (equipamento.getDataInstalacao() == null) {
            equipamento.setDataInstalacao(LocalDateTime.now());
        }
        return repository.save(equipamento);
    }

    public Equipamento atualizar(String id, Equipamento dados) {
        Equipamento equipamento = buscarPorId(id);
        String empId = dados.getEmpresaId();
        if (empId == null && dados.getEmpresa() != null) {
            empId = dados.getEmpresa().getId();
        }
        if (empId != null) {
            Empresa empresa = empresaService.buscarPorId(empId);
            equipamento.setEmpresaId(empresa.getId());
            equipamento.setEmpresa(empresa);
        }
        equipamento.setNome(dados.getNome());
        equipamento.setTipo(dados.getTipo());
        equipamento.setCategoria(dados.getCategoria());
        equipamento.setStatus(dados.getStatus());
        equipamento.setLimiteConsumoKwh(dados.getLimiteConsumoKwh());
        equipamento.setLimiteOciosidadeMin(dados.getLimiteOciosidadeMin());
        if (dados.getLocalizacao() != null) {
            equipamento.setLocalizacao(dados.getLocalizacao());
        }
        if (dados.getEspecificacoesTecnicas() != null) {
            equipamento.setEspecificacoesTecnicas(dados.getEspecificacoesTecnicas());
        }
        return repository.save(equipamento);
    }

    public void excluir(String id) {
        Equipamento equipamento = buscarPorId(id);
        repository.delete(equipamento);
    }
}
