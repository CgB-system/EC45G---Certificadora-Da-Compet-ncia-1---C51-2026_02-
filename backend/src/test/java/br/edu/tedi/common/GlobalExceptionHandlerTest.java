package br.edu.tedi.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.tedi.common.exception.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

class GlobalExceptionHandlerTest {

    @RestController
    static class ControllerFalso {
        @GetMapping("/nao-encontrado") void naoEncontrado() { throw new RecursoNaoEncontradoException("Voluntário não encontrado."); }
        @GetMapping("/duplicado") void duplicado() { throw new DadosDuplicadosException("RA já cadastrado."); }
        @GetMapping("/integracao") void integracao() { throw new IntegracaoExternaException("Google indisponível."); }
        @GetMapping("/proibido") void proibido() { throw new AcessoNegadoException("Acesso negado."); }
        @GetMapping("/erro") void erro() { throw new IllegalStateException("detalhe interno secreto"); }
        @PostMapping("/validar") void validar(@Valid @RequestBody Corpo corpo) {}
    }

    public record Corpo(@NotBlank(message = "O RA é obrigatório.") String ra) {}

    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new ControllerFalso())
            .setControllerAdvice(new GlobalExceptionHandler()).build();

    @Test
    void recursoInexistenteRetorna404() throws Exception {
        mvc.perform(get("/nao-encontrado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Voluntário não encontrado."))
                .andExpect(jsonPath("$.path").value("/nao-encontrado"));
    }

    @Test
    void dadosDuplicadosRetorna409() throws Exception {
        mvc.perform(get("/duplicado")).andExpect(status().isConflict());
    }

    @Test
    void dadosInvalidosRetorna400ComCampos() throws Exception {
        mvc.perform(post("/validar").contentType(MediaType.APPLICATION_JSON).content("{\"ra\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("ra"))
                .andExpect(jsonPath("$.errors[0].message").value("O RA é obrigatório."));
    }

    @Test
    void acessoNegadoRetorna403() throws Exception {
        mvc.perform(get("/proibido"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Acesso negado."));
    }

    @Test
    void falhaDeIntegracaoRetorna502() throws Exception {
        mvc.perform(get("/integracao")).andExpect(status().isBadGateway());
    }

    @Test
    void erroInesperadoRetorna500SemExporDetalhes() throws Exception {
        mvc.perform(get("/erro"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno. Tente novamente mais tarde."));
    }
}
