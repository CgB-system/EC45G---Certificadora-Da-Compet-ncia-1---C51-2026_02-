package br.edu.tedi.resposta;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.edu.tedi.curso.service.CursoService;
import br.edu.tedi.formulario.dto.RespostaFormulario;
import br.edu.tedi.formulario.integration.PerguntasPadrao;
import br.edu.tedi.habilidade.service.HabilidadeService;
import br.edu.tedi.resposta.service.RespostaAnalisador;
import br.edu.tedi.voluntario.service.VoluntarioService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RespostaAnalisadorTest {
    @Test
    void rejeitaPeriodoQueContemCaracteresNaoNumericosEmVezDeAlterarSeuValor() {
        CursoService cursoService = mock(CursoService.class);
        HabilidadeService habilidadeService = mock(HabilidadeService.class);
        VoluntarioService voluntarioService = mock(VoluntarioService.class);
        when(cursoService.buscarIdPorNome("Engenharia")).thenReturn(Optional.of(1));

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        RespostaAnalisador analisador = new RespostaAnalisador(
                cursoService, habilidadeService, voluntarioService, validator, "ATIVO");

        RespostaFormulario resposta = new RespostaFormulario("resposta-1", null, Map.of(
                PerguntasPadrao.NOME, "Ana",
                PerguntasPadrao.SOBRENOME, "Silva",
                PerguntasPadrao.EMAIL, "ana@tedi.edu",
                PerguntasPadrao.RA, "1234567",
                PerguntasPadrao.CURSO, "Engenharia",
                PerguntasPadrao.PERIODO, "2º período",
                PerguntasPadrao.COEFICIENTE, "8,5"));

        RespostaAnalisador.Analise analise = analisador.analisar(resposta);

        assertTrue(analise.problemas().contains("Período inválido."));
    }
}
