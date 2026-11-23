package br.edu.tedi.email;

import static org.junit.jupiter.api.Assertions.*;

import br.edu.tedi.common.exception.IntegracaoExternaException;
import br.edu.tedi.common.exception.RegraNegocioException;
import br.edu.tedi.common.exception.ValidacaoException;
import br.edu.tedi.email.dto.EmailMensagem;
import br.edu.tedi.email.dto.EnvioFormularioDTO;
import br.edu.tedi.email.dto.ResultadoEnvioDTO;
import br.edu.tedi.email.integration.EmailGateway;
import br.edu.tedi.email.service.EmailService;
import br.edu.tedi.formulario.dto.FormularioCriacaoDTO;
import br.edu.tedi.formulario.dto.FormularioDTO;
import br.edu.tedi.formulario.dto.RespostaFormulario;
import br.edu.tedi.formulario.integration.FormularioGateway;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EmailServiceTest {

    static class EmailFalso implements EmailGateway {
        final List<EmailMensagem> enviadas = new ArrayList<>();
        final Set<String> quebram = new HashSet<>();

        @Override
        public void enviar(EmailMensagem mensagem) {
            if (quebram.contains(mensagem.destinatario())) {
                throw new IntegracaoExternaException("SMTP indisponível");
            }
            enviadas.add(mensagem);
        }
    }

    static class FormulariosFalso implements FormularioGateway {
        String urlResposta = "https://docs.google.com/forms/d/e/ABC/viewform";
        boolean aceitando = true;
        int consultas = 0;

        @Override
        public FormularioDTO consultarFormulario(String id) {
            consultas++;
            return new FormularioDTO(id, "TEDI", null, urlResposta, null);
        }

        @Override
        public boolean aceitandoRespostas(String id) { return aceitando; }

        @Override
        public List<FormularioDTO> listarFormularios() { return List.of(); }

        @Override
        public FormularioDTO criarFormulario(FormularioCriacaoDTO dto) { return null; }

        @Override
        public FormularioDTO atualizarFormulario(String id, FormularioCriacaoDTO dto) { return null; }

        @Override
        public void publicarFormulario(String id) {}

        @Override
        public void encerrarFormulario(String id) {}

        @Override
        public List<RespostaFormulario> listarRespostas(String id) { return List.of(); }

        @Override
        public RespostaFormulario buscarResposta(String formularioId, String respostaId) { return null; }
    }

    private final EmailFalso email = new EmailFalso();
    private final FormulariosFalso formularios = new FormulariosFalso();
    private final EmailService service = new EmailService(email, formularios);

    private EnvioFormularioDTO porLink(List<String> destinatarios, String mensagem) {
        return new EnvioFormularioDTO(destinatarios, "TEDI", mensagem, null, "https://forms.gle/abc");
    }

    private EnvioFormularioDTO porId(String id) {
        return new EnvioFormularioDTO(List.of("a@a.com"), "TEDI", "Participe!", id, null);
    }

    @Test
    void enviaLinkInformadoParaTodosOsDestinatarios() {
        List<ResultadoEnvioDTO> resultados = service.enviarFormulario(porLink(List.of("a@a.com", "b@b.com"), "Participe!"));

        assertTrue(resultados.stream().allMatch(ResultadoEnvioDTO::enviado));
        assertEquals(2, email.enviadas.size());
        assertEquals("Participe!\n\nResponda o formulário: https://forms.gle/abc", email.enviadas.get(1).corpo());
        assertEquals("TEDI", email.enviadas.get(0).assunto());
    }

    @Test
    void semMensagemOCorpoTrazApenasOLink() {
        service.enviarFormulario(porLink(List.of("a@a.com"), null));

        assertEquals("Responda o formulário: https://forms.gle/abc", email.enviadas.get(0).corpo());
    }

    @Test
    void usaOLinkDeRespostaDoFormularioPeloId() {
        List<ResultadoEnvioDTO> resultados = service.enviarFormulario(porId("  1FAIpQLSd  "));

        assertTrue(resultados.get(0).enviado());
        assertEquals("Participe!\n\nResponda o formulário: https://docs.google.com/forms/d/e/ABC/viewform",
                email.enviadas.get(0).corpo());
        assertEquals(1, formularios.consultas);
    }

    @Test
    void recusaFormularioNaoPublicadoOuEncerrado() {
        formularios.aceitando = false;

        assertThrows(RegraNegocioException.class, () -> service.enviarFormulario(porId("abc")));
        assertTrue(email.enviadas.isEmpty());
    }

    @Test
    void recusaFormularioSemLinkDeResposta() {
        formularios.urlResposta = null;

        assertThrows(RegraNegocioException.class, () -> service.enviarFormulario(porId("abc")));
        assertTrue(email.enviadas.isEmpty());
    }

    @Test
    void exigeExatamenteUmEntreIdELink() {
        EnvioFormularioDTO nenhum = new EnvioFormularioDTO(List.of("a@a.com"), "TEDI", null, null, null);
        EnvioFormularioDTO ambos = new EnvioFormularioDTO(List.of("a@a.com"), "TEDI", null, "abc", "https://forms.gle/abc");
        EnvioFormularioDTO vazios = new EnvioFormularioDTO(List.of("a@a.com"), "TEDI", null, " ", " ");

        assertThrows(ValidacaoException.class, () -> service.enviarFormulario(nenhum));
        assertThrows(ValidacaoException.class, () -> service.enviarFormulario(ambos));
        assertThrows(ValidacaoException.class, () -> service.enviarFormulario(vazios));
        assertTrue(email.enviadas.isEmpty());
    }

    @Test
    void removeDestinatariosRepetidosMesmoComDiferencaDeMaiusculas() {
        List<ResultadoEnvioDTO> resultados = service.enviarFormulario(porLink(List.of("ana@tedi.edu", " ANA@TEDI.EDU "), null));

        assertEquals(1, resultados.size());
        assertEquals(1, email.enviadas.size());
        assertEquals("ana@tedi.edu", email.enviadas.get(0).destinatario());
    }

    @Test
    void falhaNoSmtpNaoImpedeOsDemaisEnvios() {
        email.quebram.add("a@a.com");

        List<ResultadoEnvioDTO> resultados = service.enviarFormulario(porLink(List.of("a@a.com", "b@b.com"), null));

        assertFalse(resultados.get(0).enviado());
        assertEquals("SMTP indisponível", resultados.get(0).erro());
        assertTrue(resultados.get(1).enviado());
        assertEquals(1, email.enviadas.size());
    }
}
