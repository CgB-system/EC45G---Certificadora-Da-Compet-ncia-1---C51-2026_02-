package br.edu.tedi.historico.dto;

import java.time.LocalDateTime;

public record OrigemVoluntarioDTO(String formularioId, String respostaId, LocalDateTime convertidaEm) {}
