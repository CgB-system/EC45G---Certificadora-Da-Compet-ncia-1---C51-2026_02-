package br.edu.tedi.resposta.service;

import br.edu.tedi.common.armazenamento.ArquivoTsv;
import br.edu.tedi.resposta.dto.StatusResposta;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RespostaStatusStore {

    private static final Logger log = LoggerFactory.getLogger(RespostaStatusStore.class);

    public record Registro(String formularioId, String respostaId, StatusResposta status,
                           Integer voluntarioId, LocalDateTime em) {}

    private final Map<String, Registro> registros = new ConcurrentHashMap<>();
    private final Path arquivo;

    public RespostaStatusStore() { this.arquivo = null; }

    @Autowired
    public RespostaStatusStore(@Value("${tedi.dados.diretorio:./dados}") String diretorio) {
        this.arquivo = ArquivoTsv.prepararDiretorio(diretorio).resolve("respostas.tsv");
        carregar();
    }

    public StatusResposta obter(String formularioId, String respostaId) {
        Registro r = registros.get(chave(formularioId, respostaId));
        return r == null ? StatusResposta.PENDENTE : r.status();
    }

    public void marcar(String formularioId, String respostaId, StatusResposta novo) {
        marcar(formularioId, respostaId, novo, null);
    }

    public synchronized void marcar(String formularioId, String respostaId, StatusResposta novo, Integer voluntarioId) {
        gravar(new Registro(formularioId, respostaId, novo, voluntarioId, LocalDateTime.now()));
    }

    public synchronized void registrarImportada(String formularioId, String respostaId) {
        if (!registros.containsKey(chave(formularioId, respostaId))) {
            gravar(new Registro(formularioId, respostaId, StatusResposta.PENDENTE, null, LocalDateTime.now()));
        }
    }

    public Optional<Registro> origemDoVoluntario(Integer voluntarioId) {
        if (voluntarioId == null) {
            return Optional.empty();
        }
        return registros.values().stream()
                .filter(r -> r.status() == StatusResposta.CONVERTIDA && voluntarioId.equals(r.voluntarioId()))
                .findFirst();
    }

    private void gravar(Registro r) {
        if (arquivo != null) {
            try {
                ArquivoTsv.anexar(arquivo, ArquivoTsv.linha(r.formularioId(), r.respostaId(), r.status().name(),
                        r.voluntarioId() == null ? "" : r.voluntarioId().toString(), r.em().toString()));
            } catch (IOException e) {
                throw new IllegalStateException("Não foi possível salvar o status da resposta.", e);
            }
        }
        registros.put(chave(r.formularioId(), r.respostaId()), r);
    }

    private void carregar() {
        try {
            for (String linha : ArquivoTsv.ler(arquivo)) {
                try {
                    String[] c = ArquivoTsv.campos(linha);
                    Registro r = new Registro(c[0], c[1], StatusResposta.valueOf(c[2]),
                            c[3].isEmpty() ? null : Integer.valueOf(c[3]), LocalDateTime.parse(c[4]));
                    registros.put(chave(r.formularioId(), r.respostaId()), r);
                } catch (RuntimeException e) {
                    log.warn("Linha de respostas ignorada (formato inválido): {}", linha);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler o registro de respostas em " + arquivo, e);
        }
    }

    private String chave(String formularioId, String respostaId) { return formularioId + ":" + respostaId; }
}
