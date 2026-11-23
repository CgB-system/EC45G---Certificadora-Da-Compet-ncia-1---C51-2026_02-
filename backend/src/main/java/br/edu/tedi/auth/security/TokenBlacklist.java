package br.edu.tedi.auth.security;

import br.edu.tedi.common.armazenamento.ArquivoTsv;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenBlacklist {

    private final Map<String, Long> revogados = new ConcurrentHashMap<>();
    private final Path arquivo;

    public TokenBlacklist(@Value("${tedi.dados.diretorio:./dados}") String diretorio) {
        this.arquivo = ArquivoTsv.prepararDiretorio(diretorio).resolve("tokens-revogados.tsv");
        carregar();
    }

    public synchronized void revogar(String jti, Date expiracao) {
        long agora = System.currentTimeMillis();
        Map<String, Long> atualizados = new HashMap<>(revogados);
        atualizados.entrySet().removeIf(item -> item.getValue() <= agora);
        if (expiracao.getTime() > agora) {
            atualizados.put(jti, expiracao.getTime());
        }
        persistir(atualizados);
        revogados.entrySet().removeIf(item -> item.getValue() <= agora);
        if (expiracao.getTime() > agora) {
            revogados.put(jti, expiracao.getTime());
        } else {
            revogados.remove(jti);
        }
    }

    public boolean estaRevogado(String jti) {
        Long expiracao = revogados.get(jti);
        if (expiracao == null) {
            return false;
        }
        if (expiracao <= System.currentTimeMillis()) {
            revogados.remove(jti, expiracao);
            return false;
        }
        return true;
    }

    private void carregar() {
        try {
            long agora = System.currentTimeMillis();
            for (String linha : ArquivoTsv.ler(arquivo)) {
                try {
                    String[] campos = ArquivoTsv.campos(linha);
                    if (campos.length != 2) {
                        continue;
                    }
                    long expiracao = Long.parseLong(campos[1]);
                    if (expiracao > agora && !campos[0].isBlank()) {
                        revogados.put(campos[0], expiracao);
                    }
                } catch (RuntimeException ex) {
                    continue;
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Não foi possível carregar a lista de tokens revogados.", ex);
        }
    }

    private void persistir(Map<String, Long> estado) {
        Path temporario = arquivo.resolveSibling(arquivo.getFileName() + ".tmp");
        StringBuilder conteudo = new StringBuilder();
        estado.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(item -> conteudo.append(ArquivoTsv.linha(item.getKey(), item.getValue().toString()))
                        .append('\n'));

        try {
            Files.writeString(temporario, conteudo.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            try {
                Files.move(temporario, arquivo, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            try {
                Files.deleteIfExists(temporario);
            } catch (IOException ignored) {
            }
            throw new IllegalStateException("Não foi possível persistir a lista de tokens revogados.", ex);
        }
    }
}
