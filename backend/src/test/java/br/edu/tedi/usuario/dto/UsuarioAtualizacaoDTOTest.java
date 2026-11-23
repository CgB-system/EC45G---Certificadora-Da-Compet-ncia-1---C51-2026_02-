package br.edu.tedi.usuario.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class UsuarioAtualizacaoDTOTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void senhaVaziaMantemSenhaAtual() {
        UsuarioAtualizacaoDTO dto = new UsuarioAtualizacaoDTO("Ana", "Silva", "ana@tedi.edu", "");
        assertFalse(validator.validate(dto).stream().anyMatch(e -> e.getPropertyPath().toString().equals("senha")));
    }

    @Test
    void senhaCurtaNaoVaziaEhInvalida() {
        UsuarioAtualizacaoDTO dto = new UsuarioAtualizacaoDTO("Ana", "Silva", "ana@tedi.edu", "1234567");
        assertTrue(validator.validate(dto).stream().anyMatch(e -> e.getPropertyPath().toString().equals("senha")));
    }
}
