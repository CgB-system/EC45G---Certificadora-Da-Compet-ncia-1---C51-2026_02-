package br.edu.tedi.gerenciador.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AtribuirVoluntarioDTO(
        @NotNull(message = "O administrador é obrigatório.") Integer administradorId,
        @NotNull(message = "O voluntário é obrigatório.") Integer voluntarioId,
        LocalDate dataInicio) {}
