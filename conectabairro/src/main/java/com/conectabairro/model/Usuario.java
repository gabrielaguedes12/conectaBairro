package com.conectabairro.model;

import com.conectabairro.model.enums.TipoPerfil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    @Enumerated(EnumType.STRING)
    private TipoPerfil tipoPerfil;
    private String descricaoHabilidades;
}