package com.conectabairro.repository;

import com.conectabairro.model.Vaga;
import com.conectabairro.model.enums.StatusVaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VagaRepository extends JpaRepository<Vaga, Long> {
    List<Vaga> findByStatusVaga(StatusVaga status);
}