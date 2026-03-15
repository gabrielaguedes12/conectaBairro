package com.conectabairro.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idConsumidor;
    private Long idPrestador;

    private String descricaoDemanda;

    private Double valorAcordado;

    private String status;

    private LocalDate dataSolicitacao;

    public Contrato() {}

}