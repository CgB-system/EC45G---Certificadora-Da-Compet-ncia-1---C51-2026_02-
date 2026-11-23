package br.edu.tedi.curso.dto;

import br.edu.tedi.curso.entity.Curso;

public record CursoResponseDTO(Integer id, String nome) {

    public static CursoResponseDTO de(Curso c) { return new CursoResponseDTO(c.getId(), c.getNome()); }
}
