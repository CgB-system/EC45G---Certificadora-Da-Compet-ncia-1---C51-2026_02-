package br.edu.tedi.resposta.dto;

import br.edu.tedi.voluntario.dto.VoluntarioResponseDTO;

public record ConversaoRespostaDTO(String formularioId, String respostaId, VoluntarioResponseDTO voluntario) {}
