package com.conectabairro.repository;

import com.conectabairro.model.Candidatura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {
    boolean existsByVagaIdAndCandidatoId(Long vagaId, Long candidatoId);
}
