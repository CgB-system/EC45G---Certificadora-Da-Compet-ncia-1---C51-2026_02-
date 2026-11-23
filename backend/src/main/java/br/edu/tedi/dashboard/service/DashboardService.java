package br.edu.tedi.dashboard.service;

import br.edu.tedi.curso.repository.CursoRepository;
import br.edu.tedi.dashboard.dto.ContagemDTO;
import br.edu.tedi.dashboard.dto.ResumoDTO;
import br.edu.tedi.habilidade.repository.HabilidadeRepository;
import br.edu.tedi.voluntario.repository.VoluntarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final VoluntarioRepository voluntarioRepository;
    private final CursoRepository cursoRepository;
    private final HabilidadeRepository habilidadeRepository;

    public DashboardService(VoluntarioRepository voluntarioRepository, CursoRepository cursoRepository,
                            HabilidadeRepository habilidadeRepository) {
        this.voluntarioRepository = voluntarioRepository;
        this.cursoRepository = cursoRepository;
        this.habilidadeRepository = habilidadeRepository;
    }

    public ResumoDTO obterResumo() {
        long ativos = voluntarioRepository.countByAtivo(true);
        long inativos = voluntarioRepository.countByAtivo(false);
        return new ResumoDTO(ativos + inativos, ativos, inativos, cursoRepository.count(), habilidadeRepository.count());
    }

    public List<ContagemDTO> contarVoluntariosPorStatus() { return voluntarioRepository.contarPorStatus(); }

    public List<ContagemDTO> contarVoluntariosPorCurso() { return voluntarioRepository.contarPorCurso(); }

    public List<ContagemDTO> contarVoluntariosPorPeriodo() { return voluntarioRepository.contarPorPeriodo(); }

    public List<ContagemDTO> consultarDistribuicaoHabilidades() { return voluntarioRepository.contarPorHabilidade(); }
}
