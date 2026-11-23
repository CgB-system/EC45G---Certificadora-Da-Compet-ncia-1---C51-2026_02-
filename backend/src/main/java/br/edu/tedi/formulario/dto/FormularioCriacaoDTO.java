package br.edu.tedi.formulario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FormularioCriacaoDTO(
        @NotBlank(message = "O título é obrigatório.") @Size(max = 200, message = "O título deve ter no máximo 200 caracteres.") String titulo,
        @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres.") String descricao) {}
