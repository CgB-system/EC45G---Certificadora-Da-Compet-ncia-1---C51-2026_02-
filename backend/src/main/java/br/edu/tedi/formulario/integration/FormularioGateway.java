package br.edu.tedi.formulario.integration;

import br.edu.tedi.formulario.dto.FormularioCriacaoDTO;
import br.edu.tedi.formulario.dto.FormularioDTO;
import br.edu.tedi.formulario.dto.RespostaFormulario;
import java.util.List;

public interface FormularioGateway {

    List<FormularioDTO> listarFormularios();

    FormularioDTO criarFormulario(FormularioCriacaoDTO dto);

    FormularioDTO consultarFormulario(String id);

    FormularioDTO atualizarFormulario(String id, FormularioCriacaoDTO dto);

    void publicarFormulario(String id);

    void encerrarFormulario(String id);

    boolean aceitandoRespostas(String id);

    List<RespostaFormulario> listarRespostas(String id);

    RespostaFormulario buscarResposta(String formularioId, String respostaId);
}
