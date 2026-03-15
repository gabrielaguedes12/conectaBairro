package com.conectabairro.service;

import com.conectabairro.model.Usuario;
import com.conectabairro.model.enums.TipoPerfil;
import com.conectabairro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService  {

    private final UsuarioRepository usuarioRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não encontrado"));
    }

    public void save(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty() ||
                usuario.getEmail() == null || usuario.getEmail().trim().isEmpty() ||
                usuario.getSenha() == null || usuario.getSenha().trim().isEmpty() ||
                usuario.getTipoPerfil() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome, e-mail, senha e tipo de perfil são obrigatórios.");
        }

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário cadastrado com este e-mail.");
        }

        if (usuario.getTipoPerfil() == TipoPerfil.PRESTADOR) {
            if (usuario.getDescricaoHabilidades() == null || usuario.getDescricaoHabilidades().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prestadores de serviço devem informar a descrição das habilidades.");
            }
        }

        usuarioRepository.save(usuario);
    }

    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }
}
