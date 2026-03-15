package com.conectabairro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Usuario autor;
    @ManyToOne
    @JoinColumn(name = "avaliado_id")
    private Usuario avaliado;
    private int nota;
    private String comentario;
}