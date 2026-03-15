package com.conectabairro.service;

import com.conectabairro.model.Usuario;
import com.conectabairro.model.Vaga;
import com.conectabairro.model.enums.StatusVaga;
import com.conectabairro.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VagaService {
    private final VagaRepository vagaRepository;
    private final UsuarioService usuarioService;

    public List<Vaga> findAll() {
        return vagaRepository.findAll();
    }

    public List<Vaga> findAbertas() {
        return vagaRepository.findByStatusVaga(StatusVaga.ABERTA);
    }

    public Vaga findById(Long id) {
        return vagaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaga não encontrada"));
    }

    public void save(Vaga vaga) {
        if (vaga.getTitulo() == null || vaga.getTitulo().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O título da vaga é obrigatório.");
        }
        if (vaga.getDescricao() == null || vaga.getDescricao().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A descrição da vaga é obrigatória.");
        }

        Usuario contratante = usuarioService.findById(vaga.getContratante().getId());
        vaga.setContratante(contratante);

        if (vaga.getId() == null) {
            vaga.setStatusVaga(StatusVaga.ABERTA);
        }

        vagaRepository.save(vaga);
    }

    public void fecharVaga(Long idVaga, Long idUsuarioSolicitante) {
        Vaga vaga = findById(idVaga);

        if (!vaga.getContratante().getId().equals(idUsuarioSolicitante)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador da vaga pode fechá-la.");
        }

        vaga.setStatusVaga(StatusVaga.FECHADA);
        vagaRepository.save(vaga);
    }

    public void delete(Long idVaga, Long idUsuarioSolicitante) {
        Vaga vaga = findById(idVaga);

        if (!vaga.getContratante().getId().equals(idUsuarioSolicitante)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador da vaga pode excluí-la.");
        }

        vagaRepository.deleteById(idVaga);
    }
}
