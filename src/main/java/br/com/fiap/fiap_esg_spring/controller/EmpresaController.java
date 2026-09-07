package br.com.fiap.fiap_esg_spring.controller;

import br.com.fiap.fiap_esg_spring.model.Empresa;
import br.com.fiap.fiap_esg_spring.service.EmpresaService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService service;

    @GetMapping
    public List<Empresa> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Empresa buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Empresa> criar(@Valid @RequestBody Empresa empresa) {
        Empresa criada = service.criar(empresa);
        return ResponseEntity.created(URI.create("/empresas/" + criada.getId())).body(criada);
    }

    @PutMapping("/{id}")
    public Empresa atualizar(@PathVariable String id, @Valid @RequestBody Empresa empresa) {
        return service.atualizar(id, empresa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
