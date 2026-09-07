package br.com.fiap.fiap_esg_spring.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "empresas")
public class Empresa {

    @Id
    private String id;

    @NotBlank(message = "O nome da empresa e obrigatorio.")
    @Size(max = 100)
    private String nome;

    @Indexed(unique = true)
    @Size(max = 18)
    private String cnpj;

    private String setor;

    @Builder.Default
    private String statusAdesaoEsg = "ATIVO";

    private EsgScore esgRating;

    @Builder.Default
    private List<MetaSustentabilidade> metasSustentabilidade = new ArrayList<>();

    @Builder.Default
    private List<String> certificacoes = new ArrayList<>();

    private LocalDateTime dataCadastro;
}
