package br.edu.tedi.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.tedi.auth.security.TokenBlacklist;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TokenBlacklistTest {

    @Test
    void revogacoesPersistemAposReiniciar(@TempDir Path diretorio) {
        TokenBlacklist primeira = new TokenBlacklist(diretorio.toString());
        primeira.revogar("token-1", Date.from(Instant.now().plusSeconds(3600)));

        TokenBlacklist segunda = new TokenBlacklist(diretorio.toString());

        assertTrue(segunda.estaRevogado("token-1"));
    }

    @Test
    void tokensExpiradosNaoSaoCarregados(@TempDir Path diretorio) {
        TokenBlacklist primeira = new TokenBlacklist(diretorio.toString());
        primeira.revogar("token-expirado", Date.from(Instant.now().minusSeconds(60)));

        TokenBlacklist segunda = new TokenBlacklist(diretorio.toString());

        assertFalse(segunda.estaRevogado("token-expirado"));
    }
}
