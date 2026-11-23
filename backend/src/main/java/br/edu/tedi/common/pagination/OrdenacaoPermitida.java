package br.edu.tedi.common.pagination;

import br.edu.tedi.common.exception.ValidacaoException;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class OrdenacaoPermitida {

    private OrdenacaoPermitida() {}

    public static void validar(Pageable pageable, Set<String> propriedadesPermitidas) {
        if (pageable == null) {
            throw new ValidacaoException("Parâmetros de paginação inválidos.");
        }
        for (Sort.Order ordem : pageable.getSort()) {
            if (!propriedadesPermitidas.contains(ordem.getProperty())) {
                throw new ValidacaoException("Ordenação não permitida para o campo: " + ordem.getProperty() + ".");
            }
        }
    }
}
