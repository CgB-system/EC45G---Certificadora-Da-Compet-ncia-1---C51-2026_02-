package br.edu.tedi.telefone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TelefoneDTO(
        @NotBlank(message = "O código do país é obrigatório.") @Pattern(regexp = "\\+?\\d{1,3}", message = "Código do país inválido.") String codigo,
        @NotBlank(message = "O DDD é obrigatório.") @Pattern(regexp = "\\d{2}", message = "DDD inválido.") String ddd,
        @NotBlank(message = "O número é obrigatório.") @Pattern(regexp = "\\d{8,9}", message = "Número de telefone inválido.") String numero) {}
