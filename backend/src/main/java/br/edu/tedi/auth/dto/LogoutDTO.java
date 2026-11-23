package br.edu.tedi.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutDTO(@NotBlank(message = "O refresh token é obrigatório para encerrar a sessão.") String refreshToken) {}
