package br.edu.tedi.gerenciador.service;

import br.edu.tedi.administrador.entity.Administrador;
import br.edu.tedi.administrador.repository.AdministradorRepository;
import br.edu.tedi.common.exception.DadosDuplicadosException;
import br.edu.tedi.common.exception.RecursoNaoEncontradoException;
import br.edu.tedi.common.exception.RegraNegocioException;
import br.edu.tedi.common.pagination.PaginaDTO;
import br.edu.tedi.common.pagination.OrdenacaoPermitida;
import java.util.Set;
import br.edu.tedi.gerenciador.dto.AtribuirVoluntarioDTO;
import br.edu.tedi.gerenciador.dto.GerenciamentoResponseDTO;
import br.edu.tedi.gerenciador.entity.Gerenciador;
import br.edu.tedi.gerenciador.entity.GerenciadorId;
import br.edu.tedi.gerenciador.repository.GerenciadorRepository;
import br.edu.tedi.voluntario.entity.Voluntario;
import br.edu.tedi.voluntario.service.VoluntarioConsultaService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GerenciadorService {

    private static final Set<String> ORDENACAO_PERMITIDA = Set.of(
            "dataInicio", "dataFim", "administrador.id", "voluntario.id");

    private final GerenciadorRepository repository;
    private final AdministradorRepository administradorRepository;
    private final VoluntarioConsultaService voluntarioConsultaService;

    public GerenciadorService(GerenciadorRepository repository, AdministradorRepository administradorRepository,
                              VoluntarioConsultaService voluntarioConsultaService) {
        this.repository = repository;
        this.administradorRepository = administradorRepository;
        this.voluntarioConsultaService = voluntarioConsultaService;
    }

    @Transactional
    public GerenciamentoResponseDTO atribuirVoluntario(AtribuirVoluntarioDTO dto) {
        Administrador adm = administradorRepository.findById(dto.administradorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Administrador não encontrado."));
        Voluntario vol = voluntarioConsultaService.buscarEntidade(dto.voluntarioId());

        GerenciadorId id = criarId(adm.getId(), vol.getId());
        if (repository.existsById(id)) {
            throw new DadosDuplicadosException("Este administrador já gerencia (ou já gerenciou) este voluntário.");
        }
        Gerenciador g = new Gerenciador();
        g.setId(id);
        g.setAdministrador(adm);
        g.setVoluntario(vol);
        g.setDataInicio(dto.dataInicio() != null ? dto.dataInicio() : LocalDate.now());
        return paraResposta(repository.save(g));
    }

    @Transactional
    public GerenciamentoResponseDTO finalizarGerenciamento(Integer administradorId, Integer voluntarioId, LocalDate dataFim) {
        Gerenciador g = buscarEntidade(administradorId, voluntarioId);
        if (g.getDataFim() != null) {
            throw new RegraNegocioException("Este gerenciamento já foi encerrado.");
        }
        LocalDate fim = dataFim != null ? dataFim : LocalDate.now();
        if (fim.isBefore(g.getDataInicio())) {
            throw new RegraNegocioException("A data de fim não pode ser anterior à data de início.");
        }
        g.encerrar(fim);
        return paraResposta(g);
    }

    @Transactional(readOnly = true)
    public PaginaDTO<GerenciamentoResponseDTO> consultarGerenciamentos(Integer administradorId, Integer voluntarioId,
                                                                       Pageable pageable) {
        OrdenacaoPermitida.validar(pageable, ORDENACAO_PERMITIDA);
        return PaginaDTO.de(repository.buscar(administradorId, voluntarioId, pageable).map(this::paraResposta));
    }

    @Transactional(readOnly = true)
    public GerenciamentoResponseDTO buscarGerenciamento(Integer administradorId, Integer voluntarioId) {
        return paraResposta(buscarEntidade(administradorId, voluntarioId));
    }

    @Transactional(readOnly = true)
    public List<GerenciamentoResponseDTO> consultarPeriodosDoVoluntario(Integer voluntarioId) {
        voluntarioConsultaService.buscarEntidade(voluntarioId);
        return repository.listarPorVoluntario(voluntarioId).stream().map(this::paraResposta).toList();
    }

    private Gerenciador buscarEntidade(Integer administradorId, Integer voluntarioId) {
        return repository.findById(criarId(administradorId, voluntarioId))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Gerenciamento não encontrado."));
    }

    private GerenciadorId criarId(Integer administradorId, Integer voluntarioId) {
        GerenciadorId id = new GerenciadorId();
        id.setAdministradorId(administradorId);
        id.setVoluntarioId(voluntarioId);
        return id;
    }

    private GerenciamentoResponseDTO paraResposta(Gerenciador g) {
        Administrador a = g.getAdministrador();
        Voluntario v = g.getVoluntario();
        return new GerenciamentoResponseDTO(a.getId(), a.getNome(), v.getId(), v.getNome(),
                g.getDataInicio(), g.getDataFim());
    }
}
