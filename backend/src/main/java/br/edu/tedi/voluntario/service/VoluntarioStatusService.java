package br.edu.tedi.voluntario.service;

import br.edu.tedi.voluntario.dto.AlterarStatusDTO;
import br.edu.tedi.voluntario.dto.VoluntarioResponseDTO;
import br.edu.tedi.voluntario.entity.Voluntario;
import br.edu.tedi.voluntario.mapper.VoluntarioMapper;
import br.edu.tedi.voluntario.validation.StatusVoluntarioValidador;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoluntarioStatusService {

    private final VoluntarioConsultaService consultaService;
    private final VoluntarioMapper mapper;
    private final StatusVoluntarioValidador statusValidador;

    public VoluntarioStatusService(VoluntarioConsultaService consultaService, VoluntarioMapper mapper,
                                   StatusVoluntarioValidador statusValidador) {
        this.consultaService = consultaService;
        this.mapper = mapper;
        this.statusValidador = statusValidador;
    }

    @Transactional
    public VoluntarioResponseDTO alterarStatus(Integer id, AlterarStatusDTO dto) {
        Voluntario v = consultaService.buscarEntidade(id);
        statusValidador.validar(dto.status());
        v.alterarStatus(dto.status().trim());
        return mapper.toResponse(v);
    }
}
