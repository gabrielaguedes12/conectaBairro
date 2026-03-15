package com.conectabairro.service;

import com.conectabairro.model.Avaliacao;
import com.conectabairro.model.Usuario;
import com.conectabairro.repository.AvaliacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {
    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioService usuarioService;

    public List<Avaliacao> findAll() {
        return avaliacaoRepository.findAll();
    }

    public Avaliacao findById(Long id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada"));
    }

    public void save(Avaliacao avaliacao) {
        if (avaliacao.getNota() < 1 || avaliacao.getNota() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A nota deve estar entre 1 e 5 estrelas.");
        }

        Usuario autor = usuarioService.findById(avaliacao.getAutor().getId());
        Usuario avaliado = usuarioService.findById(avaliacao.getAvaliado().getId());

        if (autor.getId().equals(avaliado.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você não pode avaliar a si mesmo.");
        }

        avaliacao.setAutor(autor);
        avaliacao.setAvaliado(avaliado);

        avaliacaoRepository.save(avaliacao);
    }

    public void delete(Long id) {
        avaliacaoRepository.deleteById(id);
    }
}
