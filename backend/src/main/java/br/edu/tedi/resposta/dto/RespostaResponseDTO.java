package br.edu.tedi.resposta.dto;

import java.util.Map;

public record RespostaResponseDTO(String id, String formularioId, String criadaEm,
                                  Map<String, String> respostas, StatusResposta status) {}
