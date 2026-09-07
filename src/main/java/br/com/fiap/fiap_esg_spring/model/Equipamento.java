package br.com.fiap.fiap_esg_spring.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Document(collection = "equipamentos")
public class Equipamento {

    @Id
    private String id;

    private String empresaId;

    private Empresa empresa;

    @NotBlank(message = "O nome do equipamento e obrigatorio.")
    @Size(max = 100)
    private String nome;

    @Size(max = 50)
    private String tipo;

    private String categoria;

    @NotNull
    @Builder.Default
    private EquipamentoStatus status = EquipamentoStatus.LIGADO;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal limiteConsumoKwh;

    @NotNull
    @Min(0)
    private Integer limiteOciosidadeMin;

    private Localizacao localizacao;

    /**
     * Modelo Flexível NoSQL (Polimorfismo):
     * Permite armazenar especificações distintas dependendo da categoria
     * (ex: BTU e gás para HVAC; kWp e irradiação para Solar; conectores para EV, etc.).
     */
    @Builder.Default
    private Map<String, Object> especificacoesTecnicas = new HashMap<>();

    private LocalDateTime dataInstalacao;
}
