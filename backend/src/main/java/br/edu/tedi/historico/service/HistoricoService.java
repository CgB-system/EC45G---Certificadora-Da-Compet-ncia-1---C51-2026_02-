package br.edu.tedi.historico.service;

import br.edu.tedi.auth.security.UsuarioAutenticado;
import br.edu.tedi.common.pagination.PaginaDTO;
import br.edu.tedi.historico.dto.EventoHistorico;
import br.edu.tedi.historico.dto.TipoEvento;
import br.edu.tedi.historico.repository.HistoricoArmazenamento;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class HistoricoService {

    public static final String VOLUNTARIO = "VOLUNTARIO";
    public static final String USUARIO = "USUARIO";
    public static final String TELEFONE = "TELEFONE";
    public static final String GERENCIAMENTO = "GERENCIAMENTO";
    public static final String FORMULARIO = "FORMULARIO";

    private final HistoricoArmazenamento armazenamento;

    public HistoricoService(HistoricoArmazenamento armazenamento) { this.armazenamento = armazenamento; }

    public void registrar(TipoEvento tipo, String entidade, Integer alvoId, String descricao) {
        Integer autorId = null;
        String autorEmail = null;
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao != null && autenticacao.getPrincipal() instanceof UsuarioAutenticado u) {
            autorId = u.id();
            autorEmail = u.email();
        }
        armazenamento.adicionar(new EventoHistorico(UUID.randomUUID().toString(), LocalDateTime.now(),
                tipo, entidade, alvoId, descricao, autorId, autorEmail));
    }

    public List<EventoHistorico> historicoDoAlvo(Integer alvoId) {
        return armazenamento.filtrar(null, alvoId, null);
    }

    public PaginaDTO<EventoHistorico> consultar(String entidade, Integer alvoId, TipoEvento tipo,
                                                int pagina, int tamanho) {
        int t = Math.min(Math.max(tamanho, 1), 100);
        int p = Math.max(pagina, 0);
        List<EventoHistorico> todos = armazenamento.filtrar(entidade, alvoId, tipo);
        int de = (int) Math.min((long) p * t, todos.size());
        int ate = Math.min(de + t, todos.size());
        int totalPaginas = (int) Math.ceil(todos.size() / (double) t);
        return new PaginaDTO<>(new ArrayList<>(todos.subList(de, ate)), p, t, todos.size(), totalPaginas);
    }
}
