package br.edu.tedi.common.pagination;

import java.util.List;
import org.springframework.data.domain.Page;

public record PaginaDTO<T>(List<T> conteudo, int pagina, int tamanho, long totalElementos, int totalPaginas) {

    public static <T> PaginaDTO<T> de(Page<T> p) {
        return new PaginaDTO<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }
}
