package br.edu.tedi.auth;

import static org.junit.jupiter.api.Assertions.*;

import br.edu.tedi.auth.security.JwtService;
import br.edu.tedi.common.exception.CredenciaisInvalidasException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private final JwtService jwt = new JwtService("segredo-de-teste-com-mais-de-32-caracteres!!", 900, 604800);

    @Test
    void geraELeTokenValido() {
        Claims claims = jwt.ler(jwt.gerarAcesso(7));
        assertEquals("7", claims.getSubject());
        assertEquals("access", claims.get("tipo", String.class));
    }

    @Test
    void tokenInvalidoLancaExcecao() {
        assertThrows(CredenciaisInvalidasException.class, () -> jwt.ler("token-qualquer"));
    }

    @Test
    void tokenAssinadoComOutraChaveEhRejeitado() {
        JwtService outro = new JwtService("outro-segredo-de-teste-com-mais-de-32-caracteres", 900, 604800);
        assertThrows(CredenciaisInvalidasException.class, () -> jwt.ler(outro.gerarAcesso(1)));
    }
}
