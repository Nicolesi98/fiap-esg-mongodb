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
public class EsgScore {
    private Double scoreGeral;
    private Double scoreAmbiental;
    private Double scoreSocial;
    private Double scoreGovernanca;
    private String classificacao; // AAA, AA, A, BBB, etc.
}
