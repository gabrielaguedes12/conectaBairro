package com.conectabairro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idVaga;
    private Long idCandidato;

    private LocalDateTime dataCandidatura;

    public Candidatura() {}

}