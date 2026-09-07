package br.com.fiap.fiap_esg_spring.service;

import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.Empresa;
import br.com.fiap.fiap_esg_spring.model.ProjetoCompensacao;
import br.com.fiap.fiap_esg_spring.repository.ProjetoCompensacaoRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjetoCompensacaoService {

    private final ProjetoCompensacaoRepository repository;
    private final EmpresaService empresaService;

    public List<ProjetoCompensacao> listar(String empresaId, String tipoProjeto) {
        if (empresaId != null && !empresaId.isBlank()) {
            empresaService.verificaEmpresa(empresaId);
            return repository.findByEmpresaId(empresaId);
        }
        if (tipoProjeto != null && !tipoProjeto.isBlank()) {
            return repository.findByTipoProjeto(tipoProjeto);
        }
        return repository.findAll();
    }

    public ProjetoCompensacao buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto de compensacao nao encontrado: " + id));
    }

    public ProjetoCompensacao criar(ProjetoCompensacao projeto) {
        Empresa empresa = empresaService.buscarPorId(projeto.getEmpresaId());
        projeto.setEmpresaId(empresa.getId());

        if (projeto.getDataInicio() == null) {
            projeto.setDataInicio(LocalDateTime.now());
        }
        return repository.save(projeto);
    }

    public ProjetoCompensacao atualizar(String id, ProjetoCompensacao dados) {
        ProjetoCompensacao projeto = buscarPorId(id);
        if (dados.getEmpresaId() != null) {
            Empresa empresa = empresaService.buscarPorId(dados.getEmpresaId());
            projeto.setEmpresaId(empresa.getId());
        }
        projeto.setNomeIniciativa(dados.getNomeIniciativa());
        projeto.setTipoProjeto(dados.getTipoProjeto());
        projeto.setStatus(dados.getStatus());
        projeto.setMetaTco2eCompensar(dados.getMetaTco2eCompensar());
        projeto.setTco2eCompensadoAcumulado(dados.getTco2eCompensadoAcumulado());
        projeto.setInvestimentoBrl(dados.getInvestimentoBrl());
        if (dados.getDetalhesExecucao() != null) {
            projeto.setDetalhesExecucao(dados.getDetalhesExecucao());
        }
        return repository.save(projeto);
    }

    public void excluir(String id) {
        ProjetoCompensacao projeto = buscarPorId(id);
        repository.delete(projeto);
    }
}
