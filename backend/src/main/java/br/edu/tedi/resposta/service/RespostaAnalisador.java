package br.edu.tedi.resposta.service;

import static br.edu.tedi.formulario.integration.PerguntasPadrao.*;

import br.edu.tedi.common.exception.DadosDuplicadosException;
import br.edu.tedi.curso.service.CursoService;
import br.edu.tedi.formulario.dto.RespostaFormulario;
import br.edu.tedi.habilidade.service.HabilidadeService;
import br.edu.tedi.telefone.dto.TelefoneDTO;
import br.edu.tedi.voluntario.dto.VoluntarioCadastroDTO;
import br.edu.tedi.voluntario.service.VoluntarioService;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RespostaAnalisador {

    public record Analise(VoluntarioCadastroDTO dto, List<String> problemas) {
        public boolean valida() { return problemas.isEmpty(); }
    }

    private final CursoService cursoService;
    private final HabilidadeService habilidadeService;
    private final VoluntarioService voluntarioService;
    private final Validator validator;
    private final String statusInicial;

    public RespostaAnalisador(CursoService cursoService, HabilidadeService habilidadeService,
                              VoluntarioService voluntarioService, Validator validator,
                              @Value("${tedi.voluntario.status-inicial}") String statusInicial) {
        this.cursoService = cursoService;
        this.habilidadeService = habilidadeService;
        this.voluntarioService = voluntarioService;
        this.validator = validator;
        this.statusInicial = statusInicial;
    }

    public Analise analisar(RespostaFormulario resposta) {
        Map<String, String> d = resposta.respostas();
        List<String> problemas = new ArrayList<>();

        Integer cursoId = null;
        String nomeCurso = texto(d, CURSO);
        if (nomeCurso != null) {
            cursoId = cursoService.buscarIdPorNome(nomeCurso).orElse(null);
            if (cursoId == null) {
                problemas.add("Curso não encontrado: " + nomeCurso + ".");
            }
        }

        if (statusInicial == null || statusInicial.isBlank()) {
            problemas.add("O status inicial do voluntário não está configurado.");
        } else {
            try {
                voluntarioService.validarStatus(statusInicial);
            } catch (br.edu.tedi.common.exception.ValidacaoException e) {
                problemas.add(e.getMessage());
            }
        }

        VoluntarioCadastroDTO dto = new VoluntarioCadastroDTO(
                texto(d, NOME), texto(d, SOBRENOME), texto(d, EMAIL), texto(d, RA),
                lerPeriodo(texto(d, PERIODO), problemas), lerCoeficiente(texto(d, COEFICIENTE), problemas),
                cursoId, texto(d, EXPERIENCIA), statusInicial,
                lerHabilidades(texto(d, HABILIDADES), problemas), lerTelefone(texto(d, TELEFONE), problemas));

        validator.validate(dto).forEach(v -> {
            boolean statusSemConfiguracao = (statusInicial == null || statusInicial.isBlank())
                    && "status".equals(v.getPropertyPath().toString());
            if (!statusSemConfiguracao) {
                problemas.add(v.getMessage());
            }
        });

        if (problemas.isEmpty()) {
            try {
                voluntarioService.verificarDuplicidade(dto.email().trim().toLowerCase(), dto.ra().trim(), null);
            } catch (DadosDuplicadosException e) {
                problemas.add(e.getMessage());
            }
        }
        return new Analise(dto, problemas);
    }

    private String texto(Map<String, String> dados, String chave) {
        String v = dados.get(chave);
        return v == null || v.isBlank() ? null : v.trim();
    }

    private Byte lerPeriodo(String valor, List<String> problemas) {
        if (valor == null) return null;
        try {
            return Byte.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            problemas.add("Período inválido.");
            return null;
        }
    }

    private BigDecimal lerCoeficiente(String valor, List<String> problemas) {
        if (valor == null) return null;
        try {
            return new BigDecimal(valor.replace(',', '.'));
        } catch (NumberFormatException e) {
            problemas.add("Coeficiente inválido.");
            return null;
        }
    }

    private List<Integer> lerHabilidades(String valor, List<String> problemas) {
        List<Integer> ids = new ArrayList<>();
        if (valor == null) return ids;
        for (String nome : valor.split(",")) {
            String n = nome.trim();
            if (n.isEmpty()) continue;
            Optional<Integer> id = habilidadeService.buscarIdPorNome(n);
            if (id.isEmpty()) {
                problemas.add("Habilidade não encontrada: " + n + ".");
            } else if (!ids.contains(id.get())) {
                ids.add(id.get());
            }
        }
        return ids;
    }

    private List<TelefoneDTO> lerTelefone(String valor, List<String> problemas) {
        if (valor == null) return List.of();
        String digitos = valor.replaceAll("\\D", "");
        if (digitos.length() < 10 || digitos.length() > 11) {
            problemas.add("Telefone inválido.");
            return List.of();
        }
        return List.of(new TelefoneDTO("+55", digitos.substring(0, 2), digitos.substring(2)));
    }
}
