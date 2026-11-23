package br.edu.tedi.habilidade.controller;

import br.edu.tedi.habilidade.dto.HabilidadeResponseDTO;
import br.edu.tedi.habilidade.service.HabilidadeService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/habilidades")
public class HabilidadeController {

    private final HabilidadeService service;

    public HabilidadeController(HabilidadeService service) { this.service = service; }

    @GetMapping
    public List<HabilidadeResponseDTO> listar() { return service.listarHabilidades(); }

    @GetMapping("/{id}")
    public HabilidadeResponseDTO buscar(@PathVariable Integer id) { return service.buscarHabilidadePorId(id); }
}
