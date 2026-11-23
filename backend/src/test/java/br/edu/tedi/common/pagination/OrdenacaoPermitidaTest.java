package br.edu.tedi.common.pagination;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.edu.tedi.common.exception.ValidacaoException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

class OrdenacaoPermitidaTest {

    @Test
    void aceitaCampoPermitido() {
        assertDoesNotThrow(() -> OrdenacaoPermitida.validar(
                PageRequest.of(0, 20, Sort.by("nome")), Set.of("nome", "email")));
    }

    @Test
    void rejeitaCampoNaoPermitido() {
        assertThrows(ValidacaoException.class, () -> OrdenacaoPermitida.validar(
                PageRequest.of(0, 20, Sort.by("senha")), Set.of("nome", "email")));
    }
}
