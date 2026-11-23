package br.edu.tedi.auth.security;

import br.edu.tedi.common.exception.CredenciaisInvalidasException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey chave;
    private final long acessoSegundos;
    private final long refreshSegundos;

    public JwtService(@Value("${tedi.jwt.secret}") String segredo,
                      @Value("${tedi.jwt.access-expiracao-segundos:900}") long acessoSegundos,
                      @Value("${tedi.jwt.refresh-expiracao-segundos:604800}") long refreshSegundos) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.acessoSegundos = acessoSegundos;
        this.refreshSegundos = refreshSegundos;
    }

    public String gerarAcesso(Integer usuarioId) { return gerar(usuarioId, "access", acessoSegundos); }

    public String gerarRefresh(Integer usuarioId) { return gerar(usuarioId, "refresh", refreshSegundos); }

    public long getAcessoSegundos() { return acessoSegundos; }

    public Claims ler(String token) {
        try {
            return Jwts.parser().verifyWith(chave).build().parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new CredenciaisInvalidasException("Token inválido ou expirado.");
        }
    }

    private String gerar(Integer usuarioId, String tipo, long segundos) {
        Date agora = new Date();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(usuarioId))
                .claim("tipo", tipo)
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + segundos * 1000))
                .signWith(chave)
                .compact();
    }
}
