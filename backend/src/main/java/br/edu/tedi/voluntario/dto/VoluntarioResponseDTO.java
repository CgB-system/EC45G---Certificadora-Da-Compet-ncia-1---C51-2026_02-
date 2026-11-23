package br.edu.tedi.voluntario.dto;

import br.edu.tedi.curso.dto.CursoResponseDTO;
import br.edu.tedi.habilidade.dto.HabilidadeResponseDTO;
import br.edu.tedi.telefone.dto.TelefoneResponseDTO;
import java.math.BigDecimal;
import java.util.List;

public record VoluntarioResponseDTO(
        Integer id, String nome, String sobrenome, String email, String ra, Byte periodo,
        BigDecimal coeficiente, String experiencia, String status, Boolean ativo,
        CursoResponseDTO curso, List<HabilidadeResponseDTO> habilidades, List<TelefoneResponseDTO> telefones) {}
