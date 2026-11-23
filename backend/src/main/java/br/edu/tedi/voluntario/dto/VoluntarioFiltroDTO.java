package br.edu.tedi.voluntario.dto;

import java.math.BigDecimal;

public record VoluntarioFiltroDTO(
        String nome,
        String sobrenome,
        String ra,
        Integer cursoId,
        Byte periodo,
        BigDecimal coeficienteMinimo,
        BigDecimal coeficienteMaximo,
        Integer habilidadeId,
        String status,
        Boolean ativo) {}
