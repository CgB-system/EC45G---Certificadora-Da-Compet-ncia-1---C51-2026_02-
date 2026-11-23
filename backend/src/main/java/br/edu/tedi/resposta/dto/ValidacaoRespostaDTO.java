package br.edu.tedi.resposta.dto;

import java.util.List;

public record ValidacaoRespostaDTO(boolean valida, List<String> problemas) {}
