package br.edu.tedi.voluntario.service;

import br.edu.tedi.common.exception.RecursoNaoEncontradoException;
import br.edu.tedi.common.pagination.PaginaDTO;
import br.edu.tedi.common.pagination.OrdenacaoPermitida;
import br.edu.tedi.common.exception.ValidacaoException;
import java.util.Set;
import br.edu.tedi.voluntario.dto.VoluntarioFiltroDTO;
import br.edu.tedi.voluntario.dto.VoluntarioResponseDTO;
import br.edu.tedi.voluntario.entity.Voluntario;
import br.edu.tedi.voluntario.mapper.VoluntarioMapper;
import br.edu.tedi.voluntario.repository.VoluntarioRepository;
import br.edu.tedi.voluntario.repository.VoluntarioSpecs;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoluntarioConsultaService {

    private static final Set<String> ORDENACAO_PERMITIDA = Set.of(
            "id", "nome", "sobrenome", "email", "ativo", "ra", "periodo",
            "coeficiente", "status", "curso.nome");

    private final VoluntarioRepository repository;
    private final VoluntarioMapper mapper;

    public VoluntarioConsultaService(VoluntarioRepository repository, VoluntarioMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public VoluntarioResponseDTO buscarVoluntarioPorId(Integer id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public PaginaDTO<VoluntarioResponseDTO> filtrarVoluntarios(VoluntarioFiltroDTO filtro, Pageable pageable) {
        OrdenacaoPermitida.validar(pageable, ORDENACAO_PERMITIDA);
        if (filtro.coeficienteMinimo() != null && filtro.coeficienteMaximo() != null
                && filtro.coeficienteMinimo().compareTo(filtro.coeficienteMaximo()) > 0) {
            throw new ValidacaoException("O coeficiente mínimo não pode ser maior que o coeficiente máximo.");
        }
        return PaginaDTO.de(repository.findAll(VoluntarioSpecs.de(filtro), pageable).map(mapper::toResponse));
    }

    public Voluntario buscarEntidade(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Voluntário não encontrado."));
    }
}
