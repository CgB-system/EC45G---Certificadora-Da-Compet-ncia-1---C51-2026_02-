package br.edu.tedi.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResposta(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<CampoErro> errors) {

    public record CampoErro(String field, String message) {}
}
