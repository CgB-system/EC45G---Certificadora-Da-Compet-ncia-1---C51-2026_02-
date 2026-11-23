package br.edu.tedi.historico.aspect;

import br.edu.tedi.email.dto.EnvioFormularioDTO;
import br.edu.tedi.email.dto.ResultadoEnvioDTO;
import br.edu.tedi.formulario.dto.FormularioDTO;
import br.edu.tedi.gerenciador.dto.GerenciamentoResponseDTO;
import br.edu.tedi.historico.dto.TipoEvento;
import br.edu.tedi.historico.service.HistoricoService;
import br.edu.tedi.resposta.dto.ConversaoRespostaDTO;
import br.edu.tedi.resposta.dto.ImportacaoRespostasDTO;
import br.edu.tedi.resposta.dto.RespostaResponseDTO;
import br.edu.tedi.usuario.dto.UsuarioResponseDTO;
import br.edu.tedi.voluntario.dto.VoluntarioResponseDTO;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Component
@Order(0)
public class AuditoriaAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaAspect.class);

    private final HistoricoService historico;

    public AuditoriaAspect(HistoricoService historico) { this.historico = historico; }

    @AfterReturning(pointcut = "execution(* br.edu.tedi.voluntario.service.VoluntarioService.cadastrarVoluntario(..))",
            returning = "r")
    public void voluntarioCadastrado(Object r) {
        if (r instanceof VoluntarioResponseDTO v) {
            seguro(() -> historico.registrar(TipoEvento.CADASTRO, HistoricoService.VOLUNTARIO, v.id(),
                    "Voluntário cadastrado (RA " + v.ra() + ")."));
        }
    }

    @AfterReturning("execution(* br.edu.tedi.voluntario.service.VoluntarioService.atualizarVoluntario(..))")
    public void voluntarioAtualizado(JoinPoint jp) {
        seguro(() -> historico.registrar(TipoEvento.EDICAO, HistoricoService.VOLUNTARIO,
                (Integer) jp.getArgs()[0], "Dados do voluntário atualizados."));
    }

    @AfterReturning("execution(* br.edu.tedi.voluntario.service.VoluntarioService.desativarVoluntario(..))")
    public void voluntarioDesativado(JoinPoint jp) {
        seguro(() -> historico.registrar(TipoEvento.EXCLUSAO, HistoricoService.VOLUNTARIO,
                (Integer) jp.getArgs()[0], "Voluntário desativado (exclusão lógica; histórico preservado)."));
    }

    @AfterReturning(pointcut = "execution(* br.edu.tedi.voluntario.service.VoluntarioStatusService.alterarStatus(..))",
            returning = "r")
    public void voluntarioStatus(Object r) {
        if (r instanceof VoluntarioResponseDTO v) {
            seguro(() -> historico.registrar(TipoEvento.ALTERACAO_STATUS, HistoricoService.VOLUNTARIO, v.id(),
                    "Status alterado para " + v.status() + "."));
        }
    }

    @AfterReturning(pointcut = "execution(* br.edu.tedi.usuario.service.UsuarioService.cadastrarUsuario(..))",
            returning = "r")
    public void usuarioCadastrado(Object r) {
        if (r instanceof UsuarioResponseDTO u) {
            seguro(() -> historico.registrar(TipoEvento.CADASTRO, HistoricoService.USUARIO, u.id(),
                    "Usuário cadastrado."));
        }
    }

    @AfterReturning("execution(* br.edu.tedi.usuario.service.UsuarioService.atualizarUsuario(..))")
    public void usuarioAtualizado(JoinPoint jp) {
        seguro(() -> historico.registrar(TipoEvento.EDICAO, HistoricoService.USUARIO,
                (Integer) jp.getArgs()[0], "Dados do usuário atualizados."));
    }

    @AfterReturning(pointcut = "execution(* br.edu.tedi.usuario.service.UsuarioService.alterarStatus(..))",
            returning = "r")
    public void usuarioStatus(Object r) {
        if (r instanceof UsuarioResponseDTO u) {
            seguro(() -> historico.registrar(TipoEvento.ALTERACAO_STATUS, HistoricoService.USUARIO, u.id(),
                    Boolean.TRUE.equals(u.ativo()) ? "Usuário ativado." : "Usuário desativado."));
        }
    }

    @AfterReturning("execution(* br.edu.tedi.telefone.service.TelefoneService.*Telefone(..))")
    public void telefoneAlterado(JoinPoint jp) {
        String acao = switch (jp.getSignature().getName()) {
            case "adicionarTelefone" -> "Telefone adicionado.";
            case "atualizarTelefone" -> "Telefone atualizado.";
            case "removerTelefone" -> "Telefone removido.";
            default -> "Telefone alterado.";
        };
        seguro(() -> historico.registrar(TipoEvento.TELEFONE, HistoricoService.TELEFONE,
                (Integer) jp.getArgs()[0], acao));
    }

    @AfterReturning(pointcut = "execution(* br.edu.tedi.gerenciador.service.GerenciadorService.atribuirVoluntario(..))",
            returning = "r")
    public void gerenciamentoAtribuido(Object r) {
        if (r instanceof GerenciamentoResponseDTO g) {
            seguro(() -> historico.registrar(TipoEvento.GERENCIAMENTO, HistoricoService.GERENCIAMENTO,
                    g.voluntarioId(), "Atribuído ao administrador " + g.administradorNome()
                            + " (id " + g.administradorId() + ") desde " + g.dataInicio() + "."));
        }
    }

    @AfterReturning(pointcut = "execution(* br.edu.tedi.gerenciador.service.GerenciadorService.finalizarGerenciamento(..))",
            returning = "r")
    public void gerenciamentoFinalizado(Object r) {
        if (r instanceof GerenciamentoResponseDTO g) {
            seguro(() -> historico.registrar(TipoEvento.GERENCIAMENTO, HistoricoService.GERENCIAMENTO,
                    g.voluntarioId(), "Gerenciamento do administrador " + g.administradorNome()
                            + " (id " + g.administradorId() + ") encerrado em " + g.dataFim() + "."));
        }
    }

    @AfterReturning(pointcut = "execution(* br.edu.tedi.formulario.service.FormularioService.criarFormulario(..))",
            returning = "r")
    public void formularioCriado(Object r) {
        if (r instanceof FormularioDTO f) {
            seguro(() -> historico.registrar(TipoEvento.CRIACAO_FORMULARIO, HistoricoService.FORMULARIO, null,
                    "Formulário " + f.id() + " criado."));
        }
    }

    @AfterReturning(pointcut = "execution(* br.edu.tedi.formulario.service.FormularioService.atualizarFormulario(..))",
            returning = "r")
    public void formularioAtualizado(Object r) {
        if (r instanceof FormularioDTO f) {
            seguro(() -> historico.registrar(TipoEvento.EDICAO_FORMULARIO, HistoricoService.FORMULARIO, null,
                    "Formulário " + f.id() + " atualizado."));
        }
    }

    @AfterReturning(pointcut = "execution(* br.edu.tedi.formulario.service.FormularioService.publicarFormulario(..))",
            returning = "r")
    public void formularioPublicado(Object r) {
        if (r instanceof FormularioDTO f) {
            seguro(() -> historico.registrar(TipoEvento.PUBLICACAO_FORMULARIO, HistoricoService.FORMULARIO, null,
                    "Formulário " + f.id() + " publicado."));
        }
    }

    @AfterReturning(pointcut = "execution(* br.edu.tedi.formulario.service.FormularioService.encerrarFormulario(..))",
            returning = "r")
    public void formularioEncerrado(Object r) {
        if (r instanceof FormularioDTO f) {
            seguro(() -> historico.registrar(TipoEvento.ENCERRAMENTO_FORMULARIO, HistoricoService.FORMULARIO, null,
                    "Formulário " + f.id() + " encerrado."));
        }
    }

    @AfterReturning(pointcut = "execution(* br.edu.tedi.resposta.service.RespostaService.importarRespostas(..))",
            returning = "r")
    public void respostasImportadas(JoinPoint jp, Object r) {
        if (r instanceof ImportacaoRespostasDTO i) {
            String formularioId = (String) jp.getArgs()[0];
            seguro(() -> historico.registrar(TipoEvento.IMPORTACAO_RESPOSTAS, HistoricoService.FORMULARIO, null,
                    "Importação do formulário " + formularioId + ": " + i.total() + " respostas consultadas."));
        }
    }

    @AfterReturning(pointcut = "execution(* br.edu.tedi.resposta.service.RespostaService.descartarResposta(..))",
            returning = "r")
    public void respostaDescartada(Object r) {
        if (r instanceof RespostaResponseDTO resposta) {
            seguro(() -> historico.registrar(TipoEvento.DESCARTE_RESPOSTA, HistoricoService.FORMULARIO, null,
                    "Resposta " + resposta.id() + " do formulário " + resposta.formularioId() + " descartada."));
        }
    }

    @AfterReturning(pointcut = "execution(* br.edu.tedi.resposta.service.RespostaService.converterResposta(..))",
            returning = "r")
    public void respostaConvertida(Object r) {
        if (r instanceof ConversaoRespostaDTO c && c.voluntario() != null) {
            seguro(() -> historico.registrar(TipoEvento.CONVERSAO_RESPOSTA, HistoricoService.VOLUNTARIO,
                    c.voluntario().id(), "Cadastro originado da resposta " + c.respostaId()
                            + " do formulário " + c.formularioId() + "."));
        }
    }

    @AfterReturning(pointcut = "execution(* br.edu.tedi.email.service.EmailService.enviarFormulario(..))",
            returning = "r")
    public void formularioEnviado(JoinPoint jp, Object r) {
        if (!(r instanceof List<?> resultados) || !(jp.getArgs()[0] instanceof EnvioFormularioDTO dto)) {
            return;
        }
        List<String> enviados = resultados.stream()
                .filter(ResultadoEnvioDTO.class::isInstance)
                .map(ResultadoEnvioDTO.class::cast)
                .filter(ResultadoEnvioDTO::enviado)
                .map(ResultadoEnvioDTO::destinatario)
                .toList();
        String origem = dto.formularioId() != null && !dto.formularioId().isBlank()
                ? "formulário " + dto.formularioId().trim()
                : "link " + dto.linkFormulario().trim();
        String descricao = "Envio do " + origem + ": " + enviados.size() + " de " + resultados.size()
                + " e-mails enviados" + (enviados.isEmpty() ? "." : " (" + String.join(", ", enviados) + ").");
        seguro(() -> historico.registrar(TipoEvento.ENVIO_FORMULARIO, HistoricoService.FORMULARIO, null, descricao));
    }

    private void seguro(Runnable acao) {
        if (TransactionSynchronizationManager.isActualTransactionActive()
                && TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    executarComSeguranca(acao);
                }
            });
            return;
        }
        executarComSeguranca(acao);
    }

    private void executarComSeguranca(Runnable acao) {
        try {
            acao.run();
        } catch (RuntimeException e) {
            log.warn("Não foi possível registrar o evento de histórico: {}", e.getMessage());
        }
    }
}
