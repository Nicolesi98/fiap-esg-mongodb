package br.com.fiap.fiap_esg_spring.controller;

import br.com.fiap.fiap_esg_spring.model.Equipamento;
import br.com.fiap.fiap_esg_spring.service.EquipamentoService;
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
@RequestMapping("/equipamentos")
@RequiredArgsConstructor
public class EquipamentoController {

    private final EquipamentoService service;

    @GetMapping
    public List<Equipamento> listar(@RequestParam(required = false) String empresaId) {
        return service.listar(empresaId);
    }

    @GetMapping("/{id}")
    public Equipamento buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Equipamento> criar(@Valid @RequestBody Equipamento equipamento) {
        Equipamento criado = service.criar(equipamento);
        return ResponseEntity.created(URI.create("/equipamentos/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public Equipamento atualizar(@PathVariable String id, @Valid @RequestBody Equipamento equipamento) {
        return service.atualizar(id, equipamento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
