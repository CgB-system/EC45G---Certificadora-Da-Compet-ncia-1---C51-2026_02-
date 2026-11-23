package br.edu.tedi.historico;

import static org.junit.jupiter.api.Assertions.*;

import br.edu.tedi.resposta.dto.StatusResposta;
import br.edu.tedi.resposta.service.RespostaStatusStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RespostaStatusStoreTest {

    @Test
    void statusEOrigemSobrevivemAoReinicio(@TempDir Path dir) {
        RespostaStatusStore a = new RespostaStatusStore(dir.toString());
        a.marcar("f1", "r1", StatusResposta.CONVERTIDA, 42);
        a.marcar("f1", "r2", StatusResposta.DESCARTADA);

        RespostaStatusStore b = new RespostaStatusStore(dir.toString());

        assertEquals(StatusResposta.CONVERTIDA, b.obter("f1", "r1"));
        assertEquals(StatusResposta.DESCARTADA, b.obter("f1", "r2"));
        assertEquals(StatusResposta.PENDENTE, b.obter("f1", "r3"));
        RespostaStatusStore.Registro origem = b.origemDoVoluntario(42).orElseThrow();
        assertEquals("f1", origem.formularioId());
        assertEquals("r1", origem.respostaId());
        assertTrue(b.origemDoVoluntario(7).isEmpty());
    }

    @Test
    void importacaoNaoSobrescreveStatusJaDefinido(@TempDir Path dir) {
        RespostaStatusStore s = new RespostaStatusStore(dir.toString());
        s.marcar("f1", "r1", StatusResposta.DESCARTADA);

        s.registrarImportada("f1", "r1");
        s.registrarImportada("f1", "r2");

        assertEquals(StatusResposta.DESCARTADA, s.obter("f1", "r1"));
        assertEquals(StatusResposta.PENDENTE, new RespostaStatusStore(dir.toString()).obter("f1", "r2"));
    }

    @Test
    void falhaDePersistenciaNaoAlteraStatusEmMemoria(@TempDir Path dir) throws IOException {
        RespostaStatusStore store = new RespostaStatusStore(dir.toString());
        Files.createDirectory(dir.resolve("respostas.tsv"));

        assertThrows(IllegalStateException.class,
                () -> store.marcar("f1", "r1", StatusResposta.DESCARTADA));
        assertEquals(StatusResposta.PENDENTE, store.obter("f1", "r1"));
    }

    @Test
    void modoMemoriaFuncionaSemArquivo() {
        RespostaStatusStore s = new RespostaStatusStore();
        s.marcar("f1", "r1", StatusResposta.CONVERTIDA, null);
        assertEquals(StatusResposta.CONVERTIDA, s.obter("f1", "r1"));
        assertTrue(s.origemDoVoluntario(null).isEmpty());
    }
}
