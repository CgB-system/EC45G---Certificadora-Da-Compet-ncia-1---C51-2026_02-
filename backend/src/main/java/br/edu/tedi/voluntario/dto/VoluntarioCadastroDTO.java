package br.edu.tedi.voluntario.dto;

import br.edu.tedi.telefone.dto.TelefoneDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record VoluntarioCadastroDTO(
        @NotBlank(message = "O nome é obrigatório.") @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres.") String nome,
        @NotBlank(message = "O sobrenome é obrigatório.") @Size(max = 60, message = "O sobrenome deve ter no máximo 60 caracteres.") String sobrenome,
        @NotBlank(message = "O e-mail é obrigatório.") @Email(message = "E-mail inválido.") @Size(max = 80, message = "O e-mail deve ter no máximo 80 caracteres.") String email,
        @NotBlank(message = "O RA é obrigatório.") @Pattern(regexp = "[A-Za-z0-9]{7}", message = "O RA deve ter 7 caracteres.") String ra,
        @NotNull(message = "O período é obrigatório.") Byte periodo,
        @NotNull(message = "O coeficiente é obrigatório.") @DecimalMin(value = "0.0", message = "Coeficiente inválido.") @Digits(integer = 1, fraction = 4, message = "Coeficiente fora do formato permitido (ex.: 7.5000).") BigDecimal coeficiente,
        @NotNull(message = "O curso é obrigatório.") Integer cursoId,
        @Size(max = 250, message = "A experiência deve ter no máximo 250 caracteres.") String experiencia,
        @NotBlank(message = "O status é obrigatório.") String status,
        List<Integer> habilidadesIds,
        List<@Valid TelefoneDTO> telefones) {}
