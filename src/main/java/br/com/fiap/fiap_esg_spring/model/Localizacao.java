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
public class Localizacao {
    private String bloco;
    private String sala;
    private String andar;
}
