package br.edu.tedi.gerenciador.controller;

import br.edu.tedi.common.pagination.PaginaDTO;
import br.edu.tedi.auth.security.UsuarioAutenticado;
import br.edu.tedi.common.exception.AcessoNegadoException;
import br.edu.tedi.gerenciador.dto.AtribuirVoluntarioDTO;
import br.edu.tedi.gerenciador.dto.FinalizarGerenciamentoDTO;
import br.edu.tedi.gerenciador.dto.GerenciamentoResponseDTO;
import br.edu.tedi.gerenciador.service.GerenciadorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/gerenciamentos")
public class GerenciadorController {

    private final GerenciadorService service;

    public GerenciadorController(GerenciadorService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GerenciamentoResponseDTO atribuir(@Valid @RequestBody AtribuirVoluntarioDTO dto,
                                             @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        if (autenticado == null || !autenticado.administrador()
                || !autenticado.id().equals(dto.administradorId())) {
            throw new AcessoNegadoException("O administrador informado deve corresponder ao usuário autenticado.");
        }
        return service.atribuirVoluntario(dto);
    }

    @GetMapping
    public PaginaDTO<GerenciamentoResponseDTO> listar(@RequestParam(required = false) Integer administradorId,
                                                      @RequestParam(required = false) Integer voluntarioId,
                                                      Pageable pageable) {
        return service.consultarGerenciamentos(administradorId, voluntarioId, pageable);
    }

    @GetMapping("/{administradorId}/{voluntarioId}")
    public GerenciamentoResponseDTO buscar(@PathVariable Integer administradorId, @PathVariable Integer voluntarioId) {
        return service.buscarGerenciamento(administradorId, voluntarioId);
    }

    @PatchMapping("/{administradorId}/{voluntarioId}/finalizar")
    public GerenciamentoResponseDTO finalizar(@PathVariable Integer administradorId, @PathVariable Integer voluntarioId,
                                              @RequestBody(required = false) FinalizarGerenciamentoDTO dto,
                                              @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        if (autenticado == null || !autenticado.administrador()
                || !autenticado.id().equals(administradorId)) {
            throw new AcessoNegadoException("Somente o administrador responsável pode encerrar este gerenciamento.");
        }
        return service.finalizarGerenciamento(administradorId, voluntarioId, dto == null ? null : dto.dataFim());
    }
}
