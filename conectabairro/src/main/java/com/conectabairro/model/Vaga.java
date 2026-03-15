package com.conectabairro.model;

import com.conectabairro.model.enums.StatusVaga;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descricao;
    private Double valorEstimado;
    @Enumerated(EnumType.STRING)
    private StatusVaga statusVaga;
    @ManyToOne
    @JoinColumn(name = "contratante_id")
    private Usuario contratante;
}