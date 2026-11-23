package br.edu.tedi.auth.security;

import br.edu.tedi.common.exception.ErroResposta;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class RespostaErroSeguranca implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper mapper;

    public RespostaErroSeguranca(ObjectMapper mapper) { this.mapper = mapper; }

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex) throws IOException {
        escrever(req, res, HttpStatus.UNAUTHORIZED, "Autenticação necessária ou token inválido.");
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex) throws IOException {
        escrever(req, res, HttpStatus.FORBIDDEN, "Você não tem permissão para executar esta operação.");
    }

    private void escrever(HttpServletRequest req, HttpServletResponse res, HttpStatus status, String msg) throws IOException {
        res.setStatus(status.value());
        res.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(res.getWriter(), new ErroResposta(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), msg, req.getRequestURI(), null));
    }
}
