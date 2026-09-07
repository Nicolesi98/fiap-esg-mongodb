package br.com.fiap.fiap_esg_spring.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "alertas_esg")
public class Alerta {

    @Id
    private String id;

    private String equipamentoId;

    private Equipamento equipamento;

    @Builder.Default
    private String pilarEsg = "ENVIRONMENTAL"; // ENVIRONMENTAL, SOCIAL, GOVERNANCE

    private LocalDateTime dataHora;

    @NotBlank
    @Size(max = 50)
    private String tipoAlerta;

    @NotBlank
    @Size(max = 250)
    private String mensagem;

    @Builder.Default
    private String severidade = "ALTA"; // BAIXA, MEDIA, ALTA, CRITICA

    @NotNull
    @Builder.Default
    private AlertaStatus status = AlertaStatus.ABERTO;

    @Builder.Default
    private Map<String, Object> dadosContextuais = new HashMap<>();

    @Builder.Default
    private List<AcaoAlerta> historicoAcoes = new ArrayList<>();
}
