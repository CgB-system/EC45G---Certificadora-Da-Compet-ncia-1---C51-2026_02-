package br.edu.tedi.voluntario.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import br.edu.tedi.common.exception.ValidacaoException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.mockito.ArgumentMatchers;

class StatusVoluntarioValidadorTest {

    @Test
    void aceitaValorDefinidoNoEnumDoBanco() {
        JdbcTemplate jdbc = org.mockito.Mockito.mock(JdbcTemplate.class);
        when(jdbc.query(anyString(), ArgumentMatchers.<RowMapper<String>>any())).thenReturn(List.of("enum('ATIVO','INATIVO')"));
        StatusVoluntarioValidador validador = new StatusVoluntarioValidador(jdbc);

        assertDoesNotThrow(() -> validador.validar("ATIVO"));
    }

    @Test
    void rejeitaValorAusenteDoEnumDoBanco() {
        JdbcTemplate jdbc = org.mockito.Mockito.mock(JdbcTemplate.class);
        when(jdbc.query(anyString(), ArgumentMatchers.<RowMapper<String>>any())).thenReturn(List.of("enum('ATIVO','INATIVO')"));
        StatusVoluntarioValidador validador = new StatusVoluntarioValidador(jdbc);

        assertThrows(ValidacaoException.class, () -> validador.validar("PENDENTE"));
    }
}
