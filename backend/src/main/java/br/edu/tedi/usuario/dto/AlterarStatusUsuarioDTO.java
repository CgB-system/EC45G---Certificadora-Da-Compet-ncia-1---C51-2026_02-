package br.edu.tedi.usuario.dto;

import jakarta.validation.constraints.NotNull;

public record AlterarStatusUsuarioDTO(@NotNull(message = "Informe se o usuário está ativo.") Boolean ativo) {}
