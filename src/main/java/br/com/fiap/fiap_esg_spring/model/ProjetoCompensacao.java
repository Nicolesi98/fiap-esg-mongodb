package br.com.fiap.fiap_esg_spring.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
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
@Document(collection = "projetos_compensacao")
public class ProjetoCompensacao {

    @Id
    private String id;

    @NotBlank(message = "O ID da empresa e obrigatorio.")
    private String empresaId;

    @NotBlank(message = "O nome da iniciativa e obrigatorio.")
    private String nomeIniciativa;

    @NotBlank(message = "O tipo de projeto e obrigatorio.")
    private String tipoProjeto; // REFLORESTAMENTO_NATIVO, ENERGIA_RENOVAVEL_IREC, ECONOMIA_CIRCULAR_RECICLAGEM, etc.

    @Builder.Default
    private String status = "PLANEJADO"; // PLANEJADO, EM_ANDAMENTO, CONCLUIDO, AUDITADO

    @NotNull
    private BigDecimal metaTco2eCompensar;

    @Builder.Default
    private BigDecimal tco2eCompensadoAcumulado = BigDecimal.ZERO;

    private BigDecimal investimentoBrl;

    /**
     * Modelo Flexível NoSQL (Polimorfismo):
     * Estrutura contendo bioma, coordenadas, árvores plantadas, toneladas recicladas, cooperativas, etc.
     */
    @Builder.Default
    private Map<String, Object> detalhesExecucao = new HashMap<>();

    private LocalDateTime dataInicio;
}
