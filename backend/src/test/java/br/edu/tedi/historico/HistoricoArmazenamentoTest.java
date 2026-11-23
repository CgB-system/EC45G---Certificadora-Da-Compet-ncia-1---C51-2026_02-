package br.edu.tedi.historico;

import static org.junit.jupiter.api.Assertions.*;

import br.edu.tedi.historico.dto.EventoHistorico;
import br.edu.tedi.historico.dto.TipoEvento;
import br.edu.tedi.historico.repository.HistoricoArmazenamento;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class HistoricoArmazenamentoTest {

    private EventoHistorico evento(String id, TipoEvento tipo, Integer alvo, String descricao) {
        return new EventoHistorico(id, LocalDateTime.of(2026, 11, 20, 10, 30, 15), tipo, "VOLUNTARIO", alvo,
                descricao, 1, "credencial");
    }

    @Test
    void historicoSobreviveAoReinicioEPreservaCaracteresEspeciais(@TempDir Path dir) {
        HistoricoArmazenamento a = new HistoricoArmazenamento(dir.toString());
        a.adicionar(evento("e1", TipoEvento.CADASTRO, 5, "linha 1\nlinha 2\tcom tab e \\ barra"));
        a.adicionar(evento("e2", TipoEvento.ALTERACAO_STATUS, 5, "Status alterado para INATIVO."));

        HistoricoArmazenamento b = new HistoricoArmazenamento(dir.toString());
        List<EventoHistorico> lista = b.filtrar(null, 5, null);

        assertEquals(2, lista.size());
        assertEquals("e2", lista.get(0).id());
        assertEquals("linha 1\nlinha 2\tcom tab e \\ barra", lista.get(1).descricao());
        assertEquals("credencial", lista.get(1).autorEmail());
        assertEquals(LocalDateTime.of(2026, 11, 20, 10, 30, 15), lista.get(1).data());
    }

    @Test
    void filtraPorAlvoETipo(@TempDir Path dir) {
        HistoricoArmazenamento a = new HistoricoArmazenamento(dir.toString());
        a.adicionar(evento("e1", TipoEvento.CADASTRO, 5, "x"));
        a.adicionar(evento("e2", TipoEvento.EDICAO, 6, "y"));
        a.adicionar(evento("e3", TipoEvento.EDICAO, 5, "z"));

        assertEquals(2, a.filtrar(null, 5, null).size());
        assertEquals(1, a.filtrar(null, 5, TipoEvento.EDICAO).size());
        assertEquals(3, a.filtrar("voluntario", null, null).size());
        assertTrue(a.filtrar(null, 99, null).isEmpty());
    }

    @Test
    void camposOpcionaisNulosSaoPreservados(@TempDir Path dir) {
        HistoricoArmazenamento a = new HistoricoArmazenamento(dir.toString());
        a.adicionar(new EventoHistorico("e1", LocalDateTime.of(2026, 1, 1, 0, 0), TipoEvento.CADASTRO,
                "USUARIO", null, "sem autor", null, null));

        EventoHistorico e = new HistoricoArmazenamento(dir.toString()).filtrar(null, null, null).get(0);

        assertNull(e.alvoId());
        assertNull(e.autorId());
        assertNull(e.autorEmail());
    }
}
