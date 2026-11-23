package br.edu.tedi.resposta.controller;

import br.edu.tedi.resposta.dto.*;
import br.edu.tedi.resposta.service.RespostaService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/formularios/{formId}/respostas")
public class RespostaController {

    private final RespostaService service;

    public RespostaController(RespostaService service) { this.service = service; }

    @GetMapping
    public List<RespostaResponseDTO> listar(@PathVariable String formId) { return service.listarRespostas(formId); }

    @GetMapping("/{responseId}")
    public RespostaResponseDTO buscar(@PathVariable String formId, @PathVariable String responseId) {
        return service.buscarResposta(formId, responseId);
    }

    @PostMapping("/importar")
    public ImportacaoRespostasDTO importar(@PathVariable String formId) { return service.importarRespostas(formId); }

    @PostMapping("/{responseId}/validar")
    public ValidacaoRespostaDTO validar(@PathVariable String formId, @PathVariable String responseId) {
        return service.validarResposta(formId, responseId);
    }

    @PostMapping("/{responseId}/converter")
    @ResponseStatus(HttpStatus.CREATED)
    public ConversaoRespostaDTO converter(@PathVariable String formId, @PathVariable String responseId) {
        return service.converterResposta(formId, responseId);
    }

    @PostMapping("/{responseId}/descartar")
    public RespostaResponseDTO descartar(@PathVariable String formId, @PathVariable String responseId) {
        return service.descartarResposta(formId, responseId);
    }
}
