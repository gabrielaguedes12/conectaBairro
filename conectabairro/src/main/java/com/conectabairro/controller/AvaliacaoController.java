package com.conectabairro.controller;

import com.conectabairro.model.Avaliacao;
import com.conectabairro.service.AvaliacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {
    private final AvaliacaoService avaliacaoService;

    @GetMapping
    public ResponseEntity<List<Avaliacao>> findAll() {
        return ResponseEntity.ok(avaliacaoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> findById(@PathVariable Long id) {
        return ResponseEntity.ok(avaliacaoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody Avaliacao avaliacao) {
        avaliacaoService.save(avaliacao);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        avaliacaoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
