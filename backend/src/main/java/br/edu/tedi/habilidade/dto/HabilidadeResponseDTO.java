package br.edu.tedi.habilidade.dto;

import br.edu.tedi.habilidade.entity.Habilidade;

public record HabilidadeResponseDTO(Integer id, String nome) {

    public static HabilidadeResponseDTO de(Habilidade h) { return new HabilidadeResponseDTO(h.getId(), h.getNome()); }
}
