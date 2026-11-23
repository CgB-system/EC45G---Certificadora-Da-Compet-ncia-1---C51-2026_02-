package br.edu.tedi.resposta;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.edu.tedi.common.exception.IntegracaoExternaException;
import br.edu.tedi.common.exception.ProcessamentoRespostaException;
import br.edu.tedi.common.exception.RegraNegocioException;
import br.edu.tedi.formulario.dto.FormularioCriacaoDTO;
import br.edu.tedi.formulario.dto.RespostaFormulario;
import br.edu.tedi.formulario.integration.FormularioGateway;
import br.edu.tedi.formulario.service.FormularioService;
import br.edu.tedi.resposta.dto.ConversaoRespostaDTO;
import br.edu.tedi.resposta.dto.RespostaResponseDTO;
import br.edu.tedi.resposta.dto.StatusResposta;
import br.edu.tedi.resposta.service.RespostaAnalisador;
import br.edu.tedi.resposta.service.RespostaService;
import br.edu.tedi.resposta.service.RespostaStatusStore;
import br.edu.tedi.voluntario.dto.VoluntarioCadastroDTO;
import br.edu.tedi.voluntario.service.VoluntarioService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RespostaServiceTest {

    @Mock FormularioGateway gateway;
    @Mock RespostaAnalisador analisador;
    @Mock VoluntarioService voluntarioService;

    private RespostaService service;
    private final RespostaFormulario resposta = new RespostaFormulario("r1", "2026-11-15T10:00:00Z", Map.of("Nome", "Ana"));
    private final VoluntarioCadastroDTO dto = new VoluntarioCadastroDTO("Ana", "Silva", "ana@tedi.edu", "1234567",
            (byte) 3, new BigDecimal("7.5000"), 1, null, "ATIVO", List.of(), List.of());

    @BeforeEach
    void preparar() {
        service = new RespostaService(gateway, new RespostaStatusStore(), analisador, voluntarioService);
    }

    @Test
    void criaFormularioPeloGateway() {
        FormularioService formularioService = new FormularioService(gateway);
        FormularioCriacaoDTO criacao = new FormularioCriacaoDTO("Voluntários TEDI", null);

        formularioService.criarFormulario(criacao);

        verify(gateway).criarFormulario(criacao);
    }

    @Test
    void listaRespostasComStatusPendente() {
        when(gateway.listarRespostas("f1")).thenReturn(List.of(resposta));

        List<RespostaResponseDTO> lista = service.listarRespostas("f1");

        assertEquals(StatusResposta.PENDENTE, lista.get(0).status());
    }

    @Test
    void falhaDoGoogleEhPropagada() {
        when(gateway.listarRespostas("f1")).thenThrow(new IntegracaoExternaException("Google indisponível"));

        assertThrows(IntegracaoExternaException.class, () -> service.listarRespostas("f1"));
    }

    @Test
    void converterCriaVoluntarioEImpedeSegundaConversao() {
        when(gateway.buscarResposta("f1", "r1")).thenReturn(resposta);
        when(analisador.analisar(resposta)).thenReturn(new RespostaAnalisador.Analise(dto, List.of()));

        ConversaoRespostaDTO resultado = service.converterResposta("f1", "r1");

        assertEquals("r1", resultado.respostaId());
        verify(voluntarioService).cadastrarVoluntario(dto);
        assertThrows(RegraNegocioException.class, () -> service.converterResposta("f1", "r1"));
        verify(voluntarioService, times(1)).cadastrarVoluntario(dto);
    }

    @Test
    void respostaInvalidaNaoEhConvertida() {
        when(gateway.buscarResposta("f1", "r1")).thenReturn(resposta);
        when(analisador.analisar(resposta))
                .thenReturn(new RespostaAnalisador.Analise(null, List.of("RA já cadastrado.")));

        assertThrows(ProcessamentoRespostaException.class, () -> service.converterResposta("f1", "r1"));
        verify(voluntarioService, never()).cadastrarVoluntario(any());
    }

    @Test
    void respostaDescartadaNaoPodeSerConvertida() {
        when(gateway.buscarResposta("f1", "r1")).thenReturn(resposta);

        service.descartarResposta("f1", "r1");

        assertThrows(RegraNegocioException.class, () -> service.converterResposta("f1", "r1"));
    }
}
