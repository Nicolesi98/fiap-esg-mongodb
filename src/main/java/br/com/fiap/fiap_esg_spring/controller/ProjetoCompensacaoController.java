package br.com.fiap.fiap_esg_spring.controller;

import br.com.fiap.fiap_esg_spring.model.ProjetoCompensacao;
import br.com.fiap.fiap_esg_spring.service.ProjetoCompensacaoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projetos-compensacao")
@RequiredArgsConstructor
public class ProjetoCompensacaoController {

    private final ProjetoCompensacaoService service;

    @GetMapping
    public List<ProjetoCompensacao> listar(
            @RequestParam(required = false) String empresaId,
            @RequestParam(required = false) String tipoProjeto
    ) {
        return service.listar(empresaId, tipoProjeto);
    }

    @GetMapping("/{id}")
    public ProjetoCompensacao buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<ProjetoCompensacao> criar(@Valid @RequestBody ProjetoCompensacao projeto) {
        ProjetoCompensacao criado = service.criar(projeto);
        return ResponseEntity.created(URI.create("/projetos-compensacao/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public ProjetoCompensacao atualizar(@PathVariable String id, @Valid @RequestBody ProjetoCompensacao projeto) {
        return service.atualizar(id, projeto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
