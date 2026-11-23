package br.edu.tedi.auth.service;

import br.edu.tedi.common.exception.TentativasExcessivasException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class TentativasLoginService {

    private static final int MAXIMO_FALHAS = 5;
    private static final Duration BLOQUEIO = Duration.ofMinutes(15);

    private record Estado(int falhas, Instant bloqueadoAte) {}

    private final Map<String, Estado> estados = new ConcurrentHashMap<>();

    public void verificarBloqueio(String email) {
        Estado e = estados.get(email);
        if (e != null && e.bloqueadoAte() != null) {
            if (Instant.now().isBefore(e.bloqueadoAte())) {
                throw new TentativasExcessivasException("Muitas tentativas de login. Tente novamente mais tarde.");
            }
            estados.remove(email);
        }
    }

    public void registrarFalha(String email) {
        estados.merge(email, new Estado(1, null), (atual, novo) -> {
            int falhas = atual.falhas() + 1;
            return new Estado(falhas, falhas >= MAXIMO_FALHAS ? Instant.now().plus(BLOQUEIO) : null);
        });
    }

    public void limpar(String email) { estados.remove(email); }
}
