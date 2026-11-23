package br.edu.tedi.email.service;

import br.edu.tedi.common.exception.IntegracaoExternaException;
import br.edu.tedi.common.exception.RegraNegocioException;
import br.edu.tedi.common.exception.ValidacaoException;
import br.edu.tedi.email.dto.EmailMensagem;
import br.edu.tedi.email.dto.EnvioEmailDTO;
import br.edu.tedi.email.dto.EnvioFormularioDTO;
import br.edu.tedi.email.dto.ResultadoEnvioDTO;
import br.edu.tedi.email.integration.EmailGateway;
import br.edu.tedi.formulario.dto.FormularioDTO;
import br.edu.tedi.formulario.integration.FormularioGateway;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final EmailGateway gateway;
    private final FormularioGateway formularioGateway;

    public EmailService(EmailGateway gateway, FormularioGateway formularioGateway) {
        this.gateway = gateway;
        this.formularioGateway = formularioGateway;
    }

    public List<ResultadoEnvioDTO> enviarEmail(EnvioEmailDTO dto) {
        return processarEnvios(List.of(new EmailMensagem(dto.destinatario(), dto.assunto(), dto.mensagem())));
    }

    public List<ResultadoEnvioDTO> enviarFormulario(EnvioFormularioDTO dto) {
        String link = resolverLink(dto);
        String texto = (dto.mensagem() == null || dto.mensagem().isBlank() ? "" : dto.mensagem().trim() + "\n\n")
                + "Responda o formulário: " + link;
        Map<String, String> destinatariosUnicos = new LinkedHashMap<>();
        for (String destinatario : dto.destinatarios()) {
            String email = destinatario.trim();
            destinatariosUnicos.putIfAbsent(email.toLowerCase(Locale.ROOT), email);
        }
        List<EmailMensagem> mensagens = destinatariosUnicos.values().stream()
                .map(d -> new EmailMensagem(d, dto.assunto(), texto))
                .toList();
        return processarEnvios(mensagens);
    }

    public List<ResultadoEnvioDTO> processarEnvios(List<EmailMensagem> mensagens) {
        return mensagens.stream().map(this::enviarUm).toList();
    }

    private String resolverLink(EnvioFormularioDTO dto) {
        boolean temId = dto.formularioId() != null && !dto.formularioId().isBlank();
        boolean temLink = dto.linkFormulario() != null && !dto.linkFormulario().isBlank();
        if (temId == temLink) {
            throw new ValidacaoException("Informe o formularioId ou o linkFormulario, apenas um dos dois.");
        }
        if (temLink) {
            return dto.linkFormulario().trim();
        }
        String id = dto.formularioId().trim();
        FormularioDTO formulario = formularioGateway.consultarFormulario(id);
        if (formulario.urlResposta() == null || formulario.urlResposta().isBlank()) {
            throw new RegraNegocioException("O formulário não possui link de resposta.");
        }
        if (!formularioGateway.aceitandoRespostas(id)) {
            throw new RegraNegocioException("O formulário não está publicado ou não está aceitando respostas.");
        }
        return formulario.urlResposta();
    }

    private ResultadoEnvioDTO enviarUm(EmailMensagem m) {
        try {
            gateway.enviar(m);
            return new ResultadoEnvioDTO(m.destinatario(), true, null);
        } catch (IntegracaoExternaException e) {
            log.warn("Falha ao enviar e-mail: {}", e.getMessage());
            return new ResultadoEnvioDTO(m.destinatario(), false, e.getMessage());
        }
    }
}
