package br.edu.tedi.voluntario.validation;

import br.edu.tedi.common.exception.ValidacaoException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class StatusVoluntarioValidador {

    private static final String CONSULTA = """
            SELECT COLUMN_TYPE
              FROM information_schema.COLUMNS
             WHERE TABLE_SCHEMA = DATABASE()
               AND TABLE_NAME = 'tbl_Voluntario'
               AND COLUMN_NAME = 'vol_status'
            """;

    private final JdbcTemplate jdbcTemplate;

    public StatusVoluntarioValidador(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void validar(String status) {
        if (status == null || status.isBlank()) {
            throw new ValidacaoException("O status do voluntário é obrigatório.");
        }

        List<String> tipos = jdbcTemplate.query(CONSULTA,
                (resultado, numero) -> resultado.getString("COLUMN_TYPE"));
        if (tipos.isEmpty()) {
            throw new IllegalStateException("A coluna tbl_Voluntario.vol_status não foi encontrada no banco atual.");
        }

        String tipo = tipos.get(0);
        if (!tipo.regionMatches(true, 0, "enum(", 0, 5)) {
            throw new IllegalStateException("A coluna tbl_Voluntario.vol_status não está definida como ENUM no banco atual.");
        }

        List<String> valores = extrairValores(tipo);
        boolean permitido = valores.stream().anyMatch(valor -> valor.equals(status.trim()));
        if (!permitido) {
            throw new ValidacaoException("Status inválido. Valores aceitos pelo banco: " + String.join(", ", valores) + ".");
        }
    }

    private List<String> extrairValores(String definicao) {
        int inicio = definicao.indexOf('(');
        int fim = definicao.lastIndexOf(')');
        if (inicio < 0 || fim <= inicio) {
            throw new IllegalStateException("Não foi possível interpretar a definição ENUM de vol_status.");
        }

        List<String> valores = new ArrayList<>();
        int indice = inicio + 1;
        while (indice < fim) {
            while (indice < fim && (Character.isWhitespace(definicao.charAt(indice)) || definicao.charAt(indice) == ',')) {
                indice++;
            }
            if (indice >= fim) break;
            if (definicao.charAt(indice) != '\'') {
                throw new IllegalStateException("Definição ENUM de vol_status em formato inesperado.");
            }

            indice++;
            StringBuilder valor = new StringBuilder();
            boolean fechado = false;
            while (indice < fim) {
                char atual = definicao.charAt(indice++);
                if (atual == '\\' && indice < fim) {
                    valor.append(definicao.charAt(indice++));
                } else if (atual == '\'') {
                    if (indice < fim && definicao.charAt(indice) == '\'') {
                        valor.append('\'');
                        indice++;
                    } else {
                        fechado = true;
                        break;
                    }
                } else {
                    valor.append(atual);
                }
            }
            if (!fechado) {
                throw new IllegalStateException("Definição ENUM de vol_status não foi encerrada corretamente.");
            }
            valores.add(valor.toString());
        }
        if (valores.isEmpty()) {
            throw new IllegalStateException("A definição ENUM de vol_status não contém valores.");
        }
        return valores;
    }
}
