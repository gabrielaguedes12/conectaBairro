package com.conectabairro.model;

import jakarta.persistence.*;

@Entity
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idAutor;
    private Long idAvaliado;

    private int nota;
    private String comentario;

    public Avaliacao() {}

}