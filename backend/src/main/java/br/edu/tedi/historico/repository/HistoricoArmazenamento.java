package br.edu.tedi.historico.repository;

import br.edu.tedi.common.armazenamento.ArquivoTsv;
import br.edu.tedi.historico.dto.EventoHistorico;
import br.edu.tedi.historico.dto.TipoEvento;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HistoricoArmazenamento {

    private static final Logger log = LoggerFactory.getLogger(HistoricoArmazenamento.class);

    private final Path arquivo;
    private final List<EventoHistorico> eventos = new ArrayList<>();

    public HistoricoArmazenamento(@Value("${tedi.dados.diretorio:./dados}") String diretorio) {
        this.arquivo = ArquivoTsv.prepararDiretorio(diretorio).resolve("historico.tsv");
        carregar();
    }

    public synchronized void adicionar(EventoHistorico e) {
        try {
            ArquivoTsv.anexar(arquivo, ArquivoTsv.linha(
                    e.id(), e.data().toString(), e.tipo().name(), e.entidade(),
                    texto(e.alvoId()), e.descricao(), texto(e.autorId()), e.autorEmail()));
            eventos.add(e);
        } catch (IOException ex) {
            throw new IllegalStateException("Não foi possível persistir o evento de histórico.", ex);
        }
    }

    public synchronized List<EventoHistorico> filtrar(String entidade, Integer alvoId, TipoEvento tipo) {
        List<EventoHistorico> resultado = new ArrayList<>();
        for (EventoHistorico e : eventos) {
            boolean ok = (entidade == null || entidade.isBlank() || entidade.equalsIgnoreCase(e.entidade()))
                    && (alvoId == null || alvoId.equals(e.alvoId()))
                    && (tipo == null || tipo == e.tipo());
            if (ok) {
                resultado.add(e);
            }
        }
        Collections.reverse(resultado);
        return resultado;
    }

    private void carregar() {
        try {
            for (String linha : ArquivoTsv.ler(arquivo)) {
                try {
                    String[] c = ArquivoTsv.campos(linha);
                    eventos.add(new EventoHistorico(c[0], LocalDateTime.parse(c[1]), TipoEvento.valueOf(c[2]),
                            c[3], inteiro(c[4]), c[5], inteiro(c[6]), c[7].isEmpty() ? null : c[7]));
                } catch (RuntimeException ex) {
                    log.warn("Linha de histórico ignorada (formato inválido): {}", linha);
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Não foi possível ler o histórico em " + arquivo, ex);
        }
    }

    private static String texto(Integer valor) { return valor == null ? "" : valor.toString(); }

    private static Integer inteiro(String valor) { return valor.isEmpty() ? null : Integer.valueOf(valor); }
}
