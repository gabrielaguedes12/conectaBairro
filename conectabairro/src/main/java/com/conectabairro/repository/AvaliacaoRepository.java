package com.conectabairro.repository;

import com.conectabairro.model.Avaliacao;
import com.conectabairro.model.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

}