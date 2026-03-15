package com.conectabairro.service;

import com.conectabairro.model.Candidatura;
import com.conectabairro.model.Usuario;
import com.conectabairro.model.Vaga;
import com.conectabairro.model.enums.StatusCandidatura;
import com.conectabairro.model.enums.StatusVaga;
import com.conectabairro.model.enums.TipoPerfil;
import com.conectabairro.repository.CandidaturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidaturaService {
    private final CandidaturaRepository candidaturaRepository;
    private final VagaService vagaService;
    private final UsuarioService usuarioService;

    public List<Candidatura> findAll() {
        return candidaturaRepository.findAll();
    }

    public Candidatura findById(Long id) {
        return candidaturaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidatura não encontrada"));
    }

    public void candidatar(Candidatura candidatura) {
        Vaga vaga = vagaService.findById(candidatura.getVaga().getId());
        Usuario candidato = usuarioService.findById(candidatura.getCandidato().getId());

        if (candidato.getTipoPerfil() != TipoPerfil.CANDIDATO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Apenas usuários com perfil de CANDIDATO podem se aplicar às vagas.");
        }

        if (vaga.getStatusVaga() != StatusVaga.ABERTA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta vaga não está mais aceitando candidaturas.");
        }

        if (candidaturaRepository.existsByVagaIdAndCandidatoId(vaga.getId(), candidato.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já se candidatou a esta vaga.");
        }

        candidatura.setVaga(vaga);
        candidatura.setCandidato(candidato);
        candidatura.setDataCandidatura(LocalDateTime.now());
        candidatura.setStatusCandidatura(StatusCandidatura.PENDENTE);

        candidaturaRepository.save(candidatura);
    }

    public void avaliarCandidatura(Long idCandidatura, Long idDonoVaga, StatusCandidatura novoStatus) {
        Candidatura candidatura = findById(idCandidatura);

        if (!candidatura.getVaga().getContratante().getId().equals(idDonoVaga)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador da vaga pode avaliar as candidaturas.");
        }

        candidatura.setStatusCandidatura(novoStatus);
        candidaturaRepository.save(candidatura);
    }
}
