package br.edu.tedi.auth.controller;

import br.edu.tedi.auth.dto.LogoutDTO;
import br.edu.tedi.auth.dto.RefreshTokenDTO;
import br.edu.tedi.auth.dto.TokenDTO;
import br.edu.tedi.auth.dto.UsuarioLoginDTO;
import br.edu.tedi.auth.security.UsuarioAutenticado;
import br.edu.tedi.auth.service.AutenticacaoService;
import br.edu.tedi.usuario.dto.UsuarioResponseDTO;
import br.edu.tedi.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AutenticacaoService autenticacaoService;
    private final UsuarioService usuarioService;

    public AuthController(AutenticacaoService autenticacaoService, UsuarioService usuarioService) {
        this.autenticacaoService = autenticacaoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public TokenDTO login(@Valid @RequestBody UsuarioLoginDTO dto) {
        return autenticacaoService.autenticar(dto);
    }

    @PostMapping("/refresh")
    public TokenDTO refresh(@Valid @RequestBody RefreshTokenDTO dto) {
        return autenticacaoService.renovarToken(dto.refreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorization,
                                       @Valid @RequestBody LogoutDTO dto,
                                       @AuthenticationPrincipal UsuarioAutenticado usuario) {
        String access = authorization.substring(7);
        autenticacaoService.encerrarSessao(access, dto.refreshToken(), usuario.id());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public UsuarioResponseDTO me(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return usuarioService.buscarUsuarioPorId(usuario.id());
    }
}
