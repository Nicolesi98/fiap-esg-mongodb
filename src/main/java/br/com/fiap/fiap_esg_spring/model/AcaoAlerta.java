package br.com.fiap.fiap_esg_spring.model;

import java.time.LocalDateTime;
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
public class AcaoAlerta {
    private LocalDateTime data;
    private String responsavel;
    private String acao;
    private String statusResultante;
}
