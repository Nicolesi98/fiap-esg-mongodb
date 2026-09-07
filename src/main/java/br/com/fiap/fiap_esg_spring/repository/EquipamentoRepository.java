package br.com.fiap.fiap_esg_spring.repository;

import br.com.fiap.fiap_esg_spring.model.Equipamento;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EquipamentoRepository extends MongoRepository<Equipamento, String> {

    List<Equipamento> findByEmpresaId(String empresaId);

    List<Equipamento> findByCategoria(String categoria);
}
