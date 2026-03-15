package com.conectabairro.controller;

import com.conectabairro.model.Denuncia;
import com.conectabairro.service.DenunciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/denuncias")
@RequiredArgsConstructor
public class DenunciaController {
    private final DenunciaService denunciaService;

    @GetMapping
    public ResponseEntity<List<Denuncia>> findAll() {
        return ResponseEntity.ok(denunciaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Denuncia> findById(@PathVariable Long id) {
        return ResponseEntity.ok(denunciaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody Denuncia denuncia) {
        denunciaService.save(denuncia);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        denunciaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
