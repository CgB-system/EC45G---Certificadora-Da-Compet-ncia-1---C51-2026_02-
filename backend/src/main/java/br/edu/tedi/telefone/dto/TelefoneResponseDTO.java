package br.edu.tedi.telefone.dto;

import br.edu.tedi.telefone.entity.Telefone;

public record TelefoneResponseDTO(Integer id, String codigo, String ddd, String numero) {

    public static TelefoneResponseDTO de(Telefone t) {
        return new TelefoneResponseDTO(t.getId(), t.getCodigo(), t.getDdd(), t.getNumero());
    }
}
