package br.com.fiap.fiap_esg_spring.service;

import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.Empresa;
import br.com.fiap.fiap_esg_spring.repository.EmpresaRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository repository;

    public List<Empresa> listar() {
        return repository.findAll();
    }

    public Empresa buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada: " + id));
    }

    public void verificaEmpresa(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Empresa nao encontrada: " + id);
        }
    }

    public Empresa criar(Empresa empresa) {
        if (empresa.getDataCadastro() == null) {
            empresa.setDataCadastro(LocalDateTime.now());
        }
        return repository.save(empresa);
    }

    public Empresa atualizar(String id, Empresa dados) {
        Empresa empresa = buscarPorId(id);
        empresa.setNome(dados.getNome());
        empresa.setCnpj(dados.getCnpj());
        empresa.setSetor(dados.getSetor());
        empresa.setStatusAdesaoEsg(dados.getStatusAdesaoEsg());
        if (dados.getEsgRating() != null) {
            empresa.setEsgRating(dados.getEsgRating());
        }
        if (dados.getMetasSustentabilidade() != null) {
            empresa.setMetasSustentabilidade(dados.getMetasSustentabilidade());
        }
        if (dados.getCertificacoes() != null) {
            empresa.setCertificacoes(dados.getCertificacoes());
        }
        return repository.save(empresa);
    }

    public void excluir(String id) {
        Empresa empresa = buscarPorId(id);
        repository.delete(empresa);
    }
}
