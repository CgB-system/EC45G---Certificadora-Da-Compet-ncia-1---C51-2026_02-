package br.edu.tedi.usuario.controller;

import br.edu.tedi.common.pagination.PaginaDTO;
import br.edu.tedi.usuario.dto.AlterarStatusUsuarioDTO;
import br.edu.tedi.usuario.dto.UsuarioAtualizacaoDTO;
import br.edu.tedi.usuario.dto.UsuarioCadastroDTO;
import br.edu.tedi.usuario.dto.UsuarioResponseDTO;
import br.edu.tedi.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO cadastrar(@Valid @RequestBody UsuarioCadastroDTO dto) {
        return service.cadastrarUsuario(dto);
    }

    @GetMapping
    public PaginaDTO<UsuarioResponseDTO> listar(Pageable pageable) {
        return service.listarUsuarios(pageable);
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO buscar(@PathVariable Integer id) {
        return service.buscarUsuarioPorId(id);
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO atualizar(@PathVariable Integer id, @Valid @RequestBody UsuarioAtualizacaoDTO dto) {
        return service.atualizarUsuario(id, dto);
    }

    @PatchMapping("/{id}/status")
    public UsuarioResponseDTO alterarStatus(@PathVariable Integer id, @Valid @RequestBody AlterarStatusUsuarioDTO dto) {
        return service.alterarStatus(id, dto.ativo());
    }
}
