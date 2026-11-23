package br.edu.tedi.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record UsuarioAtualizacaoDTO(
        @NotBlank(message = "O nome é obrigatório.") @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres.") String nome,
        @NotBlank(message = "O sobrenome é obrigatório.") @Size(max = 60, message = "O sobrenome deve ter no máximo 60 caracteres.") String sobrenome,
        @NotBlank(message = "O e-mail é obrigatório.") @Email(message = "E-mail inválido.") @Size(max = 80, message = "O e-mail deve ter no máximo 80 caracteres.") String email,
        @Size(max = 72, message = "A senha deve ter no máximo 72 caracteres.")
        @Pattern(regexp = "^$|(?s).{8,72}$", message = "A senha deve estar vazia ou ter entre 8 e 72 caracteres.") String senha) {}
