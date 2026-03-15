package com.conectabairro.service;

import com.conectabairro.model.Denuncia;
import com.conectabairro.model.Usuario;
import com.conectabairro.repository.DenunciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DenunciaService {
    private final DenunciaRepository denunciaRepository;
    private final UsuarioService usuarioService;

    public List<Denuncia> findAll() {
        return denunciaRepository.findAll();
    }

    public Denuncia findById(Long id) {
        return denunciaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Denúncia não encontrada"));
    }

    public void save(Denuncia denuncia) {
        if (denuncia.getMotivo() == null || denuncia.getMotivo().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O motivo da denúncia é obrigatório.");
        }

        Usuario denunciante = usuarioService.findById(denuncia.getUsuario().getId());
        denuncia.setUsuario(denunciante);

        denunciaRepository.save(denuncia);
    }

    public void delete(Long id) {
        denunciaRepository.deleteById(id);
    }
}
