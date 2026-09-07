package br.com.fiap.fiap_esg_spring.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
@Document(collection = "leituras_consumo")
public class ConsumoEnergia {

    @Id
    private String id;

    private String equipamentoId;

    private Equipamento equipamento;

    @Builder.Default
    private String tipoRecurso = "ENERGIA_ELETRICA";

    private LocalDateTime dataHora;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal consumoKwh;

    @NotNull
    @Min(0)
    @Builder.Default
    private Integer tempoOciosoMin = 0;

    private BigDecimal pegadaCarbonoKgCo2e;

    /**
     * Modelo Flexível NoSQL (Polimorfismo):
     * Dados dinâmicos de telemetria variando se a leitura é elétrica, hídrica, geração renovável ou combustível fóssil.
     */
    @Builder.Default
    private Map<String, Object> telemetriaDetalhada = new HashMap<>();
}
