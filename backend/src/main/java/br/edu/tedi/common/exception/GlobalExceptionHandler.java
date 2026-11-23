package br.edu.tedi.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({RecursoNaoEncontradoException.class, NoResourceFoundException.class})
    public ResponseEntity<ErroResposta> naoEncontrado(Exception ex, HttpServletRequest req) {
        String msg = ex instanceof RecursoNaoEncontradoException ? ex.getMessage() : "Recurso não encontrado.";
        return montar(HttpStatus.NOT_FOUND, msg, req, null);
    }

    @ExceptionHandler(DadosDuplicadosException.class)
    public ResponseEntity<ErroResposta> duplicado(DadosDuplicadosException ex, HttpServletRequest req) {
        return montar(HttpStatus.CONFLICT, ex.getMessage(), req, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> integridade(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Violação de integridade: {}", ex.getClass().getSimpleName());
        return montar(HttpStatus.CONFLICT, "Os dados informados violam uma regra de integridade do banco.", req, null);
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ErroResposta> validacao(ValidacaoException ex, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, ex.getMessage(), req, null);
    }

    @ExceptionHandler({RegraNegocioException.class, ProcessamentoRespostaException.class})
    public ResponseEntity<ErroResposta> regraNegocio(RuntimeException ex, HttpServletRequest req) {
        return montar(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req, null);
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErroResposta> acessoNegado(AcessoNegadoException ex, HttpServletRequest req) {
        return montar(HttpStatus.FORBIDDEN, ex.getMessage(), req, null);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResposta> credenciais(CredenciaisInvalidasException ex, HttpServletRequest req) {
        return montar(HttpStatus.UNAUTHORIZED, ex.getMessage(), req, null);
    }

    @ExceptionHandler(TentativasExcessivasException.class)
    public ResponseEntity<ErroResposta> tentativas(TentativasExcessivasException ex, HttpServletRequest req) {
        return montar(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), req, null);
    }

    @ExceptionHandler(IntegracaoExternaException.class)
    public ResponseEntity<ErroResposta> integracao(IntegracaoExternaException ex, HttpServletRequest req) {
        log.warn("Falha de integração externa: {}", ex.getMessage());
        return montar(HttpStatus.BAD_GATEWAY, ex.getMessage(), req, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> camposInvalidos(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ErroResposta.CampoErro> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ErroResposta.CampoErro(e.getField(), e.getDefaultMessage()))
                .toList();
        return montar(HttpStatus.BAD_REQUEST, "Existem campos inválidos.", req, campos);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErroResposta> requisicaoInvalida(Exception ex, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, "Requisição inválida.", req, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResposta> metodoNaoSuportado(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return montar(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP não suportado.", req, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> inesperado(Exception ex, HttpServletRequest req) {
        log.error("Erro inesperado", ex);
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno. Tente novamente mais tarde.", req, null);
    }

    private ResponseEntity<ErroResposta> montar(HttpStatus status, String msg, HttpServletRequest req,
                                                List<ErroResposta.CampoErro> campos) {
        ErroResposta corpo = new ErroResposta(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), msg, req.getRequestURI(), campos);
        return ResponseEntity.status(status).body(corpo);
    }
}
