package com.conectabairro.controller;

import com.conectabairro.model.Candidatura;
import com.conectabairro.model.enums.StatusCandidatura;
import com.conectabairro.service.CandidaturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/candidaturas")
@RequiredArgsConstructor
public class CandidaturaController {
    private final CandidaturaService candidaturaService;

    @GetMapping
    public ResponseEntity<List<Candidatura>> findAll() {
        return ResponseEntity.ok(candidaturaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidatura> findById(@PathVariable Long id) {
        return ResponseEntity.ok(candidaturaService.findById(id));
    }

    // Rota para o candidato enviar o currículo
    @PostMapping
    public ResponseEntity<Void> candidatar(@RequestBody Candidatura candidatura) {
        candidaturaService.candidatar(candidatura);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Rota para o contratante aprovar/reprovar (Ex: PUT /v1/candidaturas/1/avaliar?idDonoVaga=3&status=ACEITA)
    @PutMapping("/{id}/avaliar")
    public ResponseEntity<Void> avaliarCandidatura(
            @PathVariable Long id,
            @RequestParam Long idDonoVaga,
            @RequestParam StatusCandidatura status) {

        candidaturaService.avaliarCandidatura(id, idDonoVaga, status);
        return ResponseEntity.noContent().build();
    }
}
