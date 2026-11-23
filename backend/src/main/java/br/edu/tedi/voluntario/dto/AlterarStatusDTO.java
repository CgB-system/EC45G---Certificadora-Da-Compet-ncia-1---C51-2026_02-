package br.edu.tedi.voluntario.dto;

import jakarta.validation.constraints.NotBlank;

public record AlterarStatusDTO(@NotBlank(message = "O status é obrigatório.") String status) {}
