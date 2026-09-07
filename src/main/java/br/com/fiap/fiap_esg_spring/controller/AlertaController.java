package br.com.fiap.fiap_esg_spring.controller;

import br.com.fiap.fiap_esg_spring.model.Alerta;
import br.com.fiap.fiap_esg_spring.model.AlertaStatus;
import br.com.fiap.fiap_esg_spring.service.AlertaService;
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
@RequestMapping("/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService service;

    @GetMapping
    public List<Alerta> listar(
            @RequestParam(required = false) String equipamentoId,
            @RequestParam(required = false) AlertaStatus status
    ) {
        return service.listar(equipamentoId, status);
    }

    @GetMapping("/{id}")
    public Alerta buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Alerta> criar(@Valid @RequestBody Alerta alerta) {
        Alerta criado = service.criar(alerta);
        return ResponseEntity.created(URI.create("/alertas/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public Alerta atualizar(@PathVariable String id, @Valid @RequestBody Alerta alerta) {
        return service.atualizar(id, alerta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
