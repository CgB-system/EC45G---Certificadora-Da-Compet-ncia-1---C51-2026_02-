package br.edu.tedi.formulario.integration;

import br.edu.tedi.common.exception.IntegracaoExternaException;
import br.edu.tedi.common.exception.RecursoNaoEncontradoException;
import br.edu.tedi.common.exception.ValidacaoException;
import br.edu.tedi.formulario.dto.FormularioCriacaoDTO;
import br.edu.tedi.formulario.dto.FormularioDTO;
import br.edu.tedi.formulario.dto.RespostaFormulario;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleFormsAdapter implements FormularioGateway {

    private static final String FORMS_URL = "https://forms.googleapis.com/v1/forms";
    private static final String DRIVE_URL = "https://www.googleapis.com/drive/v3/files";
    private static final List<String> ESCOPOS = List.of(
            "https://www.googleapis.com/auth/forms.body",
            "https://www.googleapis.com/auth/forms.responses.readonly",
            "https://www.googleapis.com/auth/drive.metadata.readonly");

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper;
    private final String caminhoCredenciais;
    private GoogleCredentials credenciais;

    public GoogleFormsAdapter(ObjectMapper mapper, @Value("${tedi.google.credentials-path:}") String caminhoCredenciais) {
        this.mapper = mapper;
        this.caminhoCredenciais = caminhoCredenciais;
    }

    @Override
    public List<FormularioDTO> listarFormularios() {
        String consulta = "mimeType='application/vnd.google-apps.form' and trashed=false";
        String campos = codificar("files(id,name),nextPageToken,incompleteSearch");
        String baseUrl = DRIVE_URL + "?q=" + codificar(consulta) + "&pageSize=1000&fields=" + campos;
        List<FormularioDTO> lista = new ArrayList<>();
        String token = null;
        do {
            String url = baseUrl + (token == null ? "" : "&pageToken=" + codificar(token));
            JsonNode pagina = chamar("GET", url, null);
            if (pagina.path("incompleteSearch").asBoolean(false)) {
                throw new IntegracaoExternaException("O Google Drive não conseguiu concluir a busca de formulários.");
            }
            for (JsonNode f : pagina.path("files")) {
                lista.add(new FormularioDTO(f.path("id").asText(), f.path("name").asText(), null, null, null));
            }
            token = pagina.path("nextPageToken").asText(null);
        } while (token != null && !token.isBlank());
        return lista;
    }

    @Override
    public FormularioDTO criarFormulario(FormularioCriacaoDTO dto) {
        Map<String, Object> info = Map.of("title", dto.titulo(), "documentTitle", dto.titulo());
        JsonNode criado = chamar("POST", FORMS_URL + "?unpublished=true", Map.of("info", info));
        String id = criado.path("formId").asText();

        List<Object> requisicoes = new ArrayList<>();
        if (dto.descricao() != null && !dto.descricao().isBlank()) {
            requisicoes.add(atualizarInfo(dto.titulo(), dto.descricao()));
        }
        List<String> perguntas = PerguntasPadrao.TODAS;
        for (int i = 0; i < perguntas.size(); i++) {
            String titulo = perguntas.get(i);
            boolean experiencia = PerguntasPadrao.EXPERIENCIA.equals(titulo);
            requisicoes.add(itemTexto(titulo, i, !experiencia, experiencia));
        }
        chamar("POST", url(id) + ":batchUpdate", Map.of("requests", requisicoes));
        return consultarFormulario(id);
    }

    @Override
    public FormularioDTO consultarFormulario(String id) {
        JsonNode f = chamar("GET", url(id), null);
        return new FormularioDTO(f.path("formId").asText(), f.path("info").path("title").asText(),
                f.path("info").path("description").asText(null), f.path("responderUri").asText(null),
                "https://docs.google.com/forms/d/" + id + "/edit");
    }

    @Override
    public FormularioDTO atualizarFormulario(String id, FormularioCriacaoDTO dto) {
        String descricao = dto.descricao() == null ? "" : dto.descricao();
        chamar("POST", url(id) + ":batchUpdate",
                Map.of("requests", List.of(atualizarInfo(dto.titulo(), descricao))));
        return consultarFormulario(id);
    }

    @Override
    public boolean aceitandoRespostas(String id) {
        JsonNode estado = chamar("GET", url(id), null).path("publishSettings").path("publishState");
        return estado.path("isPublished").asBoolean(false) && estado.path("isAcceptingResponses").asBoolean(false);
    }

    @Override
    public void publicarFormulario(String id) { definirPublicacao(id, true); }

    @Override
    public void encerrarFormulario(String id) { definirPublicacao(id, false); }

    @Override
    public List<RespostaFormulario> listarRespostas(String id) {
        Map<String, String> titulos = obterTitulos(id);
        List<RespostaFormulario> lista = new ArrayList<>();
        String token = null;
        do {
            String endereco = url(id) + "/responses"
                    + (token == null ? "" : "?pageToken=" + codificar(token));
            JsonNode pagina = chamar("GET", endereco, null);
            for (JsonNode r : pagina.path("responses")) {
                lista.add(converterResposta(r, titulos));
            }
            token = pagina.path("nextPageToken").asText(null);
        } while (token != null && !token.isBlank());
        return lista;
    }

    @Override
    public RespostaFormulario buscarResposta(String formularioId, String respostaId) {
        if (respostaId == null || !respostaId.matches("[A-Za-z0-9_-]+")) {
            throw new ValidacaoException("Identificador de resposta inválido.");
        }
        Map<String, String> titulos = obterTitulos(formularioId);
        JsonNode resposta = chamar("GET", url(formularioId) + "/responses/" + respostaId, null);
        return converterResposta(resposta, titulos);
    }

    private Map<String, String> obterTitulos(String formularioId) {
        JsonNode form = chamar("GET", url(formularioId), null);
        Map<String, String> titulos = new HashMap<>();
        for (JsonNode item : form.path("items")) {
            String questionId = item.path("questionItem").path("question").path("questionId").asText(null);
            if (questionId != null) {
                titulos.put(questionId, item.path("title").asText());
            }
        }
        return titulos;
    }

    private RespostaFormulario converterResposta(JsonNode r, Map<String, String> titulos) {
        Map<String, String> valores = new LinkedHashMap<>();
        r.path("answers").fields().forEachRemaining(e -> {
            String titulo = titulos.getOrDefault(e.getKey(), e.getKey());
            String valor = e.getValue().path("textAnswers").path("answers").path(0).path("value").asText("");
            valores.put(titulo, valor);
        });
        return new RespostaFormulario(r.path("responseId").asText(), r.path("createTime").asText(null), valores);
    }

    private void definirPublicacao(String id, boolean aceitandoRespostas) {
        Map<String, Object> estado = Map.of("isPublished", true, "isAcceptingResponses", aceitandoRespostas);
        chamar("POST", url(id) + ":setPublishSettings",
                Map.of("publishSettings", Map.of("publishState", estado), "updateMask", "publishState"));
    }

    private Map<String, Object> atualizarInfo(String titulo, String descricao) {
        return Map.of("updateFormInfo", Map.of(
                "info", Map.of("title", titulo, "description", descricao),
                "updateMask", "title,description"));
    }

    private Map<String, Object> itemTexto(String titulo, int indice, boolean obrigatoria, boolean paragrafo) {
        Map<String, Object> pergunta = Map.of("required", obrigatoria, "textQuestion", Map.of("paragraph", paragrafo));
        Map<String, Object> item = Map.of("title", titulo, "questionItem", Map.of("question", pergunta));
        return Map.of("createItem", Map.of("item", item, "location", Map.of("index", indice)));
    }

    private String url(String id) {
        if (id == null || !id.matches("[A-Za-z0-9_-]+")) {
            throw new ValidacaoException("Identificador de formulário inválido.");
        }
        return FORMS_URL + "/" + id;
    }

    private String codificar(String valor) { return URLEncoder.encode(valor, StandardCharsets.UTF_8); }

    private synchronized String obterToken() {
        if (caminhoCredenciais == null || caminhoCredenciais.isBlank()) {
            throw new IntegracaoExternaException("Integração com o Google Forms não configurada.");
        }
        try {
            if (credenciais == null) {
                try (InputStream in = new FileInputStream(caminhoCredenciais)) {
                    credenciais = GoogleCredentials.fromStream(in).createScoped(ESCOPOS);
                }
            }
            credenciais.refreshIfExpired();
            return credenciais.getAccessToken().getTokenValue();
        } catch (IOException e) {
            throw new IntegracaoExternaException("Falha na autenticação com o Google.", e);
        }
    }

    private JsonNode chamar(String metodo, String url, Object corpo) {
        try {
            HttpRequest.BodyPublisher bp = corpo == null
                    ? HttpRequest.BodyPublishers.noBody()
                    : HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(corpo));
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + obterToken())
                    .header("Content-Type", "application/json")
                    .method(metodo, bp)
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            int status = resp.statusCode();
            if (status == 404) {
                throw new RecursoNaoEncontradoException("Formulário não encontrado.");
            }
            if (status >= 400) {
                throw new IntegracaoExternaException("O Google retornou erro ao processar a solicitação (HTTP " + status + ").");
            }
            return resp.body().isBlank() ? mapper.createObjectNode() : mapper.readTree(resp.body());
        } catch (IOException e) {
            throw new IntegracaoExternaException("Falha de comunicação com o Google.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IntegracaoExternaException("Comunicação com o Google interrompida.", e);
        }
    }
}
