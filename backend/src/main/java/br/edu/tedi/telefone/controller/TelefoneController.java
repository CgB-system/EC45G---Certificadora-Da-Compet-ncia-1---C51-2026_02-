package br.edu.tedi.telefone.controller;

import br.edu.tedi.auth.security.UsuarioAutenticado;
import br.edu.tedi.common.exception.AcessoNegadoException;
import br.edu.tedi.telefone.dto.TelefoneDTO;
import br.edu.tedi.telefone.dto.TelefoneResponseDTO;
import br.edu.tedi.telefone.service.TelefoneService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios/{usuarioId}/telefones")
public class TelefoneController {

    private final TelefoneService service;

    public TelefoneController(TelefoneService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TelefoneResponseDTO adicionar(@PathVariable Integer usuarioId, @Valid @RequestBody TelefoneDTO dto,
                                         @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        validarPropriedade(usuarioId, autenticado);
        return service.adicionarTelefone(usuarioId, dto);
    }

    @GetMapping
    public List<TelefoneResponseDTO> listar(@PathVariable Integer usuarioId,
                                            @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        validarPropriedade(usuarioId, autenticado);
        return service.listarTelefones(usuarioId);
    }

    @PutMapping("/{telefoneId}")
    public TelefoneResponseDTO atualizar(@PathVariable Integer usuarioId, @PathVariable Integer telefoneId,
                                         @Valid @RequestBody TelefoneDTO dto,
                                         @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        validarPropriedade(usuarioId, autenticado);
        return service.atualizarTelefone(usuarioId, telefoneId, dto);
    }

    @DeleteMapping("/{telefoneId}")
    public ResponseEntity<Void> remover(@PathVariable Integer usuarioId, @PathVariable Integer telefoneId,
                                        @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        validarPropriedade(usuarioId, autenticado);
        service.removerTelefone(usuarioId, telefoneId);
        return ResponseEntity.noContent().build();
    }

    private void validarPropriedade(Integer usuarioId, UsuarioAutenticado autenticado) {
        if (autenticado == null || (!autenticado.administrador() && !autenticado.id().equals(usuarioId))) {
            throw new AcessoNegadoException("Você só pode acessar os telefones da própria conta.");
        }
    }
}
