package br.edu.tedi.voluntario.controller;

import br.edu.tedi.common.pagination.PaginaDTO;
import br.edu.tedi.gerenciador.dto.GerenciamentoResponseDTO;
import br.edu.tedi.gerenciador.service.GerenciadorService;
import br.edu.tedi.voluntario.dto.*;
import br.edu.tedi.voluntario.service.VoluntarioConsultaService;
import br.edu.tedi.voluntario.service.VoluntarioService;
import br.edu.tedi.voluntario.service.VoluntarioStatusService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/voluntarios")
public class VoluntarioController {

    private final VoluntarioService voluntarioService;
    private final VoluntarioConsultaService consultaService;
    private final VoluntarioStatusService statusService;
    private final GerenciadorService gerenciadorService;

    public VoluntarioController(VoluntarioService voluntarioService, VoluntarioConsultaService consultaService,
                                VoluntarioStatusService statusService, GerenciadorService gerenciadorService) {
        this.voluntarioService = voluntarioService;
        this.consultaService = consultaService;
        this.statusService = statusService;
        this.gerenciadorService = gerenciadorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VoluntarioResponseDTO cadastrar(@Valid @RequestBody VoluntarioCadastroDTO dto) {
        return voluntarioService.cadastrarVoluntario(dto);
    }

    @GetMapping
    public PaginaDTO<VoluntarioResponseDTO> listar(VoluntarioFiltroDTO filtro, Pageable pageable) {
        return consultaService.filtrarVoluntarios(filtro, pageable);
    }

    @GetMapping("/{id}")
    public VoluntarioResponseDTO buscar(@PathVariable Integer id) {
        return consultaService.buscarVoluntarioPorId(id);
    }

    @PutMapping("/{id}")
    public VoluntarioResponseDTO atualizar(@PathVariable Integer id, @Valid @RequestBody VoluntarioAtualizacaoDTO dto) {
        return voluntarioService.atualizarVoluntario(id, dto);
    }

    @PatchMapping("/{id}/status")
    public VoluntarioResponseDTO alterarStatus(@PathVariable Integer id, @Valid @RequestBody AlterarStatusDTO dto) {
        return statusService.alterarStatus(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Integer id) {
        voluntarioService.desativarVoluntario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/gerenciamentos")
    public List<GerenciamentoResponseDTO> gerenciamentos(@PathVariable Integer id) {
        return gerenciadorService.consultarPeriodosDoVoluntario(id);
    }
}
