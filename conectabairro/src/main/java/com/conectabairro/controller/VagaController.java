package com.conectabairro.controller;

import com.conectabairro.model.Vaga;
import com.conectabairro.service.VagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/vagas")
@RequiredArgsConstructor
public class VagaController {
    private final VagaService vagaService;

    @GetMapping
    public ResponseEntity<List<Vaga>> findAll() {
        return ResponseEntity.ok(vagaService.findAll());
    }

    @GetMapping("/abertas")
    public ResponseEntity<List<Vaga>> findAbertas() {
        return ResponseEntity.ok(vagaService.findAbertas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vaga> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vagaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody Vaga vaga) {
        vagaService.save(vaga);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}/fechar")
    public ResponseEntity<Void> fecharVaga(@PathVariable Long id, @RequestParam Long idUsuario) {
        vagaService.fecharVaga(id, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long idUsuario) {
        vagaService.delete(id, idUsuario);
        return ResponseEntity.noContent().build();
    }
}
