package br.com.fiap.fiap_esg_spring.controller;

import br.com.fiap.fiap_esg_spring.model.ConsumoEnergia;
import br.com.fiap.fiap_esg_spring.service.ConsumoEnergiaService;
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
@RequestMapping("/consumos-energia")
@RequiredArgsConstructor
public class ConsumoEnergiaController {

    private final ConsumoEnergiaService service;

    @GetMapping
    public List<ConsumoEnergia> listar(@RequestParam(required = false) String equipamentoId) {
        return service.listar(equipamentoId);
    }

    @GetMapping("/{id}")
    public ConsumoEnergia buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<ConsumoEnergia> criar(@Valid @RequestBody ConsumoEnergia consumo) {
        ConsumoEnergia criado = service.criar(consumo);
        return ResponseEntity.created(URI.create("/consumos-energia/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public ConsumoEnergia atualizar(@PathVariable String id, @Valid @RequestBody ConsumoEnergia consumo) {
        return service.atualizar(id, consumo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
