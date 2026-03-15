package com.conectabairro.model;

import com.conectabairro.model.enums.StatusCandidatura;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vaga_id")
    private Vaga vaga;

    @ManyToOne
    @JoinColumn(name = "candidato_id")
    private Usuario candidato;

    private LocalDateTime dataCandidatura;

    @Enumerated(EnumType.STRING)
    private StatusCandidatura statusCandidatura;

}