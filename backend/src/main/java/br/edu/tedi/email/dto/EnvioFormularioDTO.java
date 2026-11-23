package br.edu.tedi.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record EnvioFormularioDTO(
        @NotEmpty(message = "Informe ao menos um destinatário.")
        @Size(max = 200, message = "Informe no máximo 200 destinatários por envio.")
        List<@NotBlank(message = "Destinatário vazio.") @Email(message = "E-mail inválido.") String> destinatarios,
        @NotBlank(message = "O assunto é obrigatório.") String assunto,
        String mensagem,
        String formularioId,
        @Pattern(regexp = "https://(forms\\.gle|docs\\.google\\.com/forms)/\\S+",
                message = "O link deve ser de um Google Forms (https://forms.gle/... ou https://docs.google.com/forms/...).")
        String linkFormulario) {}
