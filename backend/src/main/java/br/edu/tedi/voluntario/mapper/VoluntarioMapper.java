package br.edu.tedi.voluntario.mapper;

import br.edu.tedi.curso.dto.CursoResponseDTO;
import br.edu.tedi.habilidade.dto.HabilidadeResponseDTO;
import br.edu.tedi.telefone.dto.TelefoneResponseDTO;
import br.edu.tedi.voluntario.dto.VoluntarioResponseDTO;
import br.edu.tedi.voluntario.entity.Voluntario;
import org.springframework.stereotype.Component;

@Component
public class VoluntarioMapper {

    public VoluntarioResponseDTO toResponse(Voluntario v) {
        return new VoluntarioResponseDTO(
                v.getId(), v.getNome(), v.getSobrenome(), v.getEmail(), v.getRa(), v.getPeriodo(),
                v.getCoeficiente(), v.getExperiencia(), v.getStatus(), v.getAtivo(),
                CursoResponseDTO.de(v.getCurso()),
                v.getHabilidades().stream().map(vh -> HabilidadeResponseDTO.de(vh.getHabilidade())).toList(),
                v.getTelefones().stream().map(TelefoneResponseDTO::de).toList());
    }
}
