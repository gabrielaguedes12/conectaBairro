package com.conectabairro.controller;

import com.conectabairro.model.Usuario;
import com.conectabairro.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> findById(@PathVariable Long id) {
        Usuario usuarioEncontrado = usuarioService.findById(id);
        return ResponseEntity.ok(usuarioEncontrado);
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody Usuario usuario) {
        usuarioService.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Usuario usuarioEncontrado = usuarioService.findById(id);

        usuarioService.delete(usuarioEncontrado.getId());
        return ResponseEntity.noContent().build();
    }
}
