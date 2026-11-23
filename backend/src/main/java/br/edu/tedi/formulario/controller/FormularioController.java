package br.edu.tedi.formulario.controller;

import br.edu.tedi.formulario.dto.FormularioCriacaoDTO;
import br.edu.tedi.formulario.dto.FormularioDTO;
import br.edu.tedi.formulario.service.FormularioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/formularios")
public class FormularioController {

    private final FormularioService service;

    public FormularioController(FormularioService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FormularioDTO criar(@Valid @RequestBody FormularioCriacaoDTO dto) { return service.criarFormulario(dto); }

    @GetMapping
    public List<FormularioDTO> listar() { return service.listarFormularios(); }

    @GetMapping("/{id}")
    public FormularioDTO consultar(@PathVariable String id) { return service.consultarFormulario(id); }

    @PutMapping("/{id}")
    public FormularioDTO atualizar(@PathVariable String id, @Valid @RequestBody FormularioCriacaoDTO dto) {
        return service.atualizarFormulario(id, dto);
    }

    @PostMapping("/{id}/publicar")
    public FormularioDTO publicar(@PathVariable String id) { return service.publicarFormulario(id); }

    @PostMapping("/{id}/encerrar")
    public FormularioDTO encerrar(@PathVariable String id) { return service.encerrarFormulario(id); }
}
