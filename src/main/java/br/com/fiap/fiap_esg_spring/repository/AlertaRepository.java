package br.com.fiap.fiap_esg_spring.repository;

import br.com.fiap.fiap_esg_spring.model.Alerta;
import br.com.fiap.fiap_esg_spring.model.AlertaStatus;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AlertaRepository extends MongoRepository<Alerta, String> {

    List<Alerta> findByEquipamentoId(String equipamentoId);

    List<Alerta> findByStatus(AlertaStatus status);

    List<Alerta> findByPilarEsg(String pilarEsg);
}
