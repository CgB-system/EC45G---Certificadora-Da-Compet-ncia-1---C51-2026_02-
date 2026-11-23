package br.edu.tedi.curso.controller;

import br.edu.tedi.curso.dto.CursoResponseDTO;
import br.edu.tedi.curso.service.CursoService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cursos")
public class CursoController {

    private final CursoService service;

    public CursoController(CursoService service) { this.service = service; }

    @GetMapping
    public List<CursoResponseDTO> listar() { return service.listarCursos(); }

    @GetMapping("/{id}")
    public CursoResponseDTO buscar(@PathVariable Integer id) { return service.buscarCursoPorId(id); }
}
