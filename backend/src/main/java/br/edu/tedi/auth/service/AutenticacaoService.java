package br.edu.tedi.auth.service;

import br.edu.tedi.auth.dto.TokenDTO;
import br.edu.tedi.auth.dto.UsuarioLoginDTO;
import br.edu.tedi.auth.security.JwtService;
import br.edu.tedi.auth.security.TokenBlacklist;
import br.edu.tedi.common.exception.CredenciaisInvalidasException;
import br.edu.tedi.common.exception.AcessoNegadoException;
import br.edu.tedi.usuario.entity.Usuario;
import br.edu.tedi.usuario.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService {

    private static final String MSG_CREDENCIAIS = "Credenciais inválidas.";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final TokenBlacklist blacklist;
    private final TentativasLoginService tentativas;

    public AutenticacaoService(UsuarioRepository usuarioRepository, PasswordEncoder encoder, JwtService jwtService,
                               TokenBlacklist blacklist, TentativasLoginService tentativas) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.blacklist = blacklist;
        this.tentativas = tentativas;
    }

    public TokenDTO autenticar(UsuarioLoginDTO dto) {
        String email = dto.email().trim().toLowerCase();
        tentativas.verificarBloqueio(email);
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email).orElse(null);
        if (usuario == null || !Boolean.TRUE.equals(usuario.getAtivo())
                || !encoder.matches(dto.senha(), usuario.getSenha())) {
            tentativas.registrarFalha(email);
            throw new CredenciaisInvalidasException(MSG_CREDENCIAIS);
        }
        tentativas.limpar(email);
        return gerarToken(usuario.getId());
    }

    public TokenDTO renovarToken(String refreshToken) {
        Claims claims = jwtService.ler(refreshToken);
        if (!"refresh".equals(claims.get("tipo", String.class)) || blacklist.estaRevogado(claims.getId())) {
            throw new CredenciaisInvalidasException("Token inválido ou expirado.");
        }
        Integer id = Integer.valueOf(claims.getSubject());
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null || !Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new CredenciaisInvalidasException("Token inválido ou expirado.");
        }
        blacklist.revogar(claims.getId(), claims.getExpiration());
        return gerarToken(id);
    }

    public void encerrarSessao(String accessToken, String refreshToken, Integer usuarioAutenticadoId) {
        revogarSeValido(accessToken, usuarioAutenticadoId, "access");
        if (refreshToken != null && !refreshToken.isBlank()) {
            revogarSeValido(refreshToken, usuarioAutenticadoId, "refresh");
        }
    }

    public TokenDTO gerarToken(Integer usuarioId) {
        return new TokenDTO(jwtService.gerarAcesso(usuarioId), jwtService.gerarRefresh(usuarioId),
                "Bearer", jwtService.getAcessoSegundos());
    }

    private void revogarSeValido(String token, Integer usuarioAutenticadoId, String tipoEsperado) {
        try {
            Claims claims = jwtService.ler(token);
            Integer donoToken = Integer.valueOf(claims.getSubject());
            String tipo = claims.get("tipo", String.class);
            if (!usuarioAutenticadoId.equals(donoToken) || !tipoEsperado.equals(tipo)) {
                throw new AcessoNegadoException("O token informado não pertence à sessão autenticada.");
            }
            blacklist.revogar(claims.getId(), claims.getExpiration());
        } catch (CredenciaisInvalidasException e) {
        }
    }
}
