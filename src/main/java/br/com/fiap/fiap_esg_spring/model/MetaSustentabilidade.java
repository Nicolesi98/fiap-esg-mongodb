package br.com.fiap.fiap_esg_spring.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaSustentabilidade {
    private String descricao;
    private Integer anoAlvo;
    private Double percentualConclusao;
}
