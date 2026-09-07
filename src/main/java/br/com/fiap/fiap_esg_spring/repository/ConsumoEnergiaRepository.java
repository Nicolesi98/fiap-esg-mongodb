package br.com.fiap.fiap_esg_spring.repository;

import br.com.fiap.fiap_esg_spring.model.ConsumoEnergia;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConsumoEnergiaRepository extends MongoRepository<ConsumoEnergia, String> {

    List<ConsumoEnergia> findByEquipamentoId(String equipamentoId);

    List<ConsumoEnergia> findByTipoRecurso(String tipoRecurso);
}
