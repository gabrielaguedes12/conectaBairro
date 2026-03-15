package com.conectabairro.service;

import com.conectabairro.model.Contrato;
import com.conectabairro.model.Usuario;
import com.conectabairro.model.enums.StatusContrato;
import com.conectabairro.model.enums.TipoPerfil;
import com.conectabairro.repository.ContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final UsuarioService usuarioService; // Usamos o service de usuário para validar quem é quem

    public List<Contrato> findAll() {
        return contratoRepository.findAll();
    }

    public Contrato findById(Long id) {
        return contratoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado"));
    }

    // Ação 1: Consumidor pede um orçamento
    public void solicitarOrcamento(Contrato contrato) {
        // Validação 1: O Consumidor e o Prestador devem existir no banco
        Usuario consumidor = usuarioService.findById(contrato.getConsumidor().getId());
        Usuario prestador = usuarioService.findById(contrato.getPrestador().getId());

        // Validação 2: Quem pede não pode ser a mesma pessoa que faz
        if (consumidor.getId().equals(prestador.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O consumidor e o prestador não podem ser o mesmo usuário.");
        }

        // Validação 3: O alvo do pedido TEM que ter o perfil PRESTADOR
        if (prestador.getTipoPerfil() != TipoPerfil.PRESTADOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O usuário selecionado não é um prestador de serviços.");
        }

        if (contrato.getDescricaoDemanda() == null || contrato.getDescricaoDemanda().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A descrição da demanda é obrigatória.");
        }

        // Regras de estado inicial
        contrato.setConsumidor(consumidor);
        contrato.setPrestador(prestador);
        contrato.setStatusContrato(StatusContrato.SOLICITADO);
        contrato.setDataSolicitacao(LocalDate.now());
        contrato.setValorAcordado(null);

        contratoRepository.save(contrato);
    }

    // Ação 2: Prestador envia o preço
    public void orcar(Long idContrato, Double valor) {
        Contrato contrato = findById(idContrato);

        if (contrato.getStatusContrato() != StatusContrato.SOLICITADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Só é possível orçar contratos com status SOLICITADO.");
        }

        if (valor == null || valor <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor do orçamento deve ser maior que zero.");
        }

        contrato.setValorAcordado(valor);
        contrato.setStatusContrato(StatusContrato.ORCADO);
        contratoRepository.save(contrato);
    }

    // Ação 3: Consumidor aceita ou rejeita
    public void responderOrcamento(Long idContrato, boolean aceito) {
        Contrato contrato = findById(idContrato);

        if (contrato.getStatusContrato() != StatusContrato.ORCADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Só é possível responder contratos que já foram ORCADOS.");
        }

        if (aceito) {
            contrato.setStatusContrato(StatusContrato.ACEITO);
        } else {
            contrato.setStatusContrato(StatusContrato.REJEITADO);
        }

        contratoRepository.save(contrato);
    }
}
