package br.edu.tedi.historico.controller;

import br.edu.tedi.common.exception.RecursoNaoEncontradoException;
import br.edu.tedi.common.pagination.PaginaDTO;
import br.edu.tedi.historico.dto.EventoHistorico;
import br.edu.tedi.historico.dto.OrigemVoluntarioDTO;
import br.edu.tedi.historico.dto.TipoEvento;
import br.edu.tedi.historico.service.HistoricoService;
import br.edu.tedi.resposta.service.RespostaStatusStore;
import br.edu.tedi.voluntario.service.VoluntarioConsultaService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HistoricoController {

    private final HistoricoService historicoService;
    private final VoluntarioConsultaService voluntarioConsultaService;
    private final RespostaStatusStore respostaStore;

    public HistoricoController(HistoricoService historicoService, VoluntarioConsultaService voluntarioConsultaService,
                               RespostaStatusStore respostaStore) {
        this.historicoService = historicoService;
        this.voluntarioConsultaService = voluntarioConsultaService;
        this.respostaStore = respostaStore;
    }

    @GetMapping("/voluntarios/{id}/historico")
    public List<EventoHistorico> historicoDoVoluntario(@PathVariable Integer id) {
        voluntarioConsultaService.buscarEntidade(id);
        return historicoService.historicoDoAlvo(id);
    }

    @GetMapping("/voluntarios/{id}/origem")
    public OrigemVoluntarioDTO origem(@PathVariable Integer id) {
        voluntarioConsultaService.buscarEntidade(id);
        return respostaStore.origemDoVoluntario(id)
                .map(r -> new OrigemVoluntarioDTO(r.formularioId(), r.respostaId(), r.em()))
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Este voluntário não foi originado de uma resposta de formulário."));
    }

    @GetMapping("/historico")
    public PaginaDTO<EventoHistorico> registro(@RequestParam(required = false) String entidade,
                                               @RequestParam(required = false) Integer alvoId,
                                               @RequestParam(required = false) TipoEvento tipo,
                                               @RequestParam(defaultValue = "0") int pagina,
                                               @RequestParam(defaultValue = "20") int tamanho) {
        return historicoService.consultar(entidade, alvoId, tipo, pagina, tamanho);
    }
}
