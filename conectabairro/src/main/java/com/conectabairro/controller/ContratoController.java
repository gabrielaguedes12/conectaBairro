package com.conectabairro.controller;

import com.conectabairro.model.Contrato;
import com.conectabairro.service.ContratoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/contratos")
@RequiredArgsConstructor
public class ContratoController {
    private final ContratoService contratoService;

    @GetMapping
    public ResponseEntity<List<Contrato>> findAll() {
        return ResponseEntity.ok(contratoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contrato> findById(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.findById(id));
    }

    // Rota para o Consumidor criar a solicitação
    @PostMapping
    public ResponseEntity<Void> solicitar(@RequestBody Contrato contrato) {
        contratoService.solicitarOrcamento(contrato);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Rota para o Prestador definir o valor (Ex: PUT /v1/contratos/1/orcar?valor=150.00)
    @PutMapping("/{id}/orcar")
    public ResponseEntity<Void> orcar(@PathVariable Long id, @RequestParam Double valor) {
        contratoService.orcar(id, valor);
        return ResponseEntity.noContent().build();
    }

    // Rota para o Consumidor aceitar ou rejeitar (Ex: PUT /v1/contratos/1/responder?aceito=true)
    @PutMapping("/{id}/responder")
    public ResponseEntity<Void> responder(@PathVariable Long id, @RequestParam boolean aceito) {
        contratoService.responderOrcamento(id, aceito);
        return ResponseEntity.noContent().build();
    }
}
