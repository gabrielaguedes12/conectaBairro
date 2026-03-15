package com.conectabairro.model;

import jakarta.persistence.*;

@Entity
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idUsuario;
    private String motivo;
    private String descricao;

    public Denuncia() {}

}