package br.edu.tedi.resposta.service;

import br.edu.tedi.common.exception.ProcessamentoRespostaException;
import br.edu.tedi.common.exception.RecursoNaoEncontradoException;
import br.edu.tedi.common.exception.RegraNegocioException;
import br.edu.tedi.formulario.dto.RespostaFormulario;
import br.edu.tedi.formulario.integration.FormularioGateway;
import br.edu.tedi.resposta.dto.*;
import br.edu.tedi.voluntario.dto.VoluntarioResponseDTO;
import br.edu.tedi.voluntario.service.VoluntarioService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RespostaService {

    private final FormularioGateway gateway;
    private final RespostaStatusStore store;
    private final RespostaAnalisador analisador;
    private final VoluntarioService voluntarioService;

    public RespostaService(FormularioGateway gateway, RespostaStatusStore store,
                           RespostaAnalisador analisador, VoluntarioService voluntarioService) {
        this.gateway = gateway;
        this.store = store;
        this.analisador = analisador;
        this.voluntarioService = voluntarioService;
    }

    public synchronized List<RespostaResponseDTO> listarRespostas(String formId) {
        return gateway.listarRespostas(formId).stream().map(r -> paraDTO(formId, r)).toList();
    }

    public synchronized RespostaResponseDTO buscarResposta(String formId, String respostaId) {
        return paraDTO(formId, buscarNoGoogle(formId, respostaId));
    }

    public synchronized ImportacaoRespostasDTO importarRespostas(String formId) {
        List<RespostaResponseDTO> lista = listarRespostas(formId);
        lista.forEach(r -> store.registrarImportada(formId, r.id()));
        return new ImportacaoRespostasDTO(lista.size(), contar(lista, StatusResposta.PENDENTE),
                contar(lista, StatusResposta.CONVERTIDA), contar(lista, StatusResposta.DESCARTADA), lista);
    }

    public synchronized ValidacaoRespostaDTO validarResposta(String formId, String respostaId) {
        RespostaAnalisador.Analise analise = analisador.analisar(buscarNoGoogle(formId, respostaId));
        return new ValidacaoRespostaDTO(analise.valida(), analise.problemas());
    }

    @Transactional
    public synchronized ConversaoRespostaDTO converterResposta(String formId, String respostaId) {
        verificarPendente(formId, respostaId);
        RespostaAnalisador.Analise analise = analisador.analisar(buscarNoGoogle(formId, respostaId));
        if (!analise.valida()) {
            throw new ProcessamentoRespostaException(
                    "Resposta inválida: " + String.join(" ", analise.problemas()));
        }
        VoluntarioResponseDTO voluntario = voluntarioService.cadastrarVoluntario(analise.dto());
        store.marcar(formId, respostaId, StatusResposta.CONVERTIDA, voluntario == null ? null : voluntario.id());
        return new ConversaoRespostaDTO(formId, respostaId, voluntario);
    }

    public synchronized RespostaResponseDTO descartarResposta(String formId, String respostaId) {
        verificarPendente(formId, respostaId);
        RespostaFormulario resposta = buscarNoGoogle(formId, respostaId);
        store.marcar(formId, respostaId, StatusResposta.DESCARTADA);
        return paraDTO(formId, resposta);
    }

    private void verificarPendente(String formId, String respostaId) {
        StatusResposta atual = store.obter(formId, respostaId);
        if (atual != StatusResposta.PENDENTE) {
            throw new RegraNegocioException("Esta resposta já foi " + (atual == StatusResposta.CONVERTIDA
                    ? "convertida em voluntário." : "descartada."));
        }
    }

    private RespostaFormulario buscarNoGoogle(String formId, String respostaId) {
        return gateway.buscarResposta(formId, respostaId);
    }

    private RespostaResponseDTO paraDTO(String formId, RespostaFormulario r) {
        return new RespostaResponseDTO(r.id(), formId, r.criadaEm(), r.respostas(), store.obter(formId, r.id()));
    }

    private int contar(List<RespostaResponseDTO> lista, StatusResposta status) {
        return (int) lista.stream().filter(r -> r.status() == status).count();
    }
}
