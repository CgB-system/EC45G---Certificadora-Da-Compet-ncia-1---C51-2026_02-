package br.edu.tedi.historico.dto;

import java.time.LocalDateTime;

public record EventoHistorico(String id, LocalDateTime data, TipoEvento tipo, String entidade,
                              Integer alvoId, String descricao, Integer autorId, String autorEmail) {}
