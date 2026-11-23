package br.edu.tedi.formulario.service;

import br.edu.tedi.formulario.dto.FormularioCriacaoDTO;
import br.edu.tedi.formulario.dto.FormularioDTO;
import br.edu.tedi.formulario.integration.FormularioGateway;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FormularioService {

    private final FormularioGateway gateway;

    public FormularioService(FormularioGateway gateway) { this.gateway = gateway; }

    public FormularioDTO criarFormulario(FormularioCriacaoDTO dto) { return gateway.criarFormulario(dto); }

    public List<FormularioDTO> listarFormularios() { return gateway.listarFormularios(); }

    public FormularioDTO consultarFormulario(String id) { return gateway.consultarFormulario(id); }

    public FormularioDTO atualizarFormulario(String id, FormularioCriacaoDTO dto) {
        return gateway.atualizarFormulario(id, dto);
    }

    public FormularioDTO publicarFormulario(String id) {
        gateway.publicarFormulario(id);
        return gateway.consultarFormulario(id);
    }

    public FormularioDTO encerrarFormulario(String id) {
        gateway.encerrarFormulario(id);
        return gateway.consultarFormulario(id);
    }
}
