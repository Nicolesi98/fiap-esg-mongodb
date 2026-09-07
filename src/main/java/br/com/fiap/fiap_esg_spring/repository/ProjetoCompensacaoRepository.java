package br.com.fiap.fiap_esg_spring.repository;

import br.com.fiap.fiap_esg_spring.model.ProjetoCompensacao;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjetoCompensacaoRepository extends MongoRepository<ProjetoCompensacao, String> {

    List<ProjetoCompensacao> findByEmpresaId(String empresaId);

    List<ProjetoCompensacao> findByTipoProjeto(String tipoProjeto);

    List<ProjetoCompensacao> findByStatus(String status);
}
