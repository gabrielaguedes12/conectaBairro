package com.conectabairro.model;

import jakarta.persistence.*;

@Entity
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;
    private Double valorEstimado;
    private String status;

    public Vaga() {}

}