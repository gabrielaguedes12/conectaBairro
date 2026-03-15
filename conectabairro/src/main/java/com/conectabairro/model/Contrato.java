package com.conectabairro.model;

import com.conectabairro.model.enums.StatusContrato;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario consumidor;
    @ManyToOne
    private Usuario prestador;

    private String descricaoDemanda;
    private Double valorAcordado;
    private StatusContrato statusContrato;
    private LocalDate dataSolicitacao;
}