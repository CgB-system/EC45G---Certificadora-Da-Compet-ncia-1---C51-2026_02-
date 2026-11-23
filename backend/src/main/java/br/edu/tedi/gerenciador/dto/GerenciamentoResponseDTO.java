package br.edu.tedi.gerenciador.dto;

import java.time.LocalDate;

public record GerenciamentoResponseDTO(
        Integer administradorId, String administradorNome,
        Integer voluntarioId, String voluntarioNome,
        LocalDate dataInicio, LocalDate dataFim) {}
