package br.edu.tedi.resposta.dto;

import java.util.List;

public record ImportacaoRespostasDTO(int total, int pendentes, int convertidas, int descartadas,
                                     List<RespostaResponseDTO> respostas) {}
