package br.edu.tedi.dashboard.controller;

import br.edu.tedi.dashboard.dto.ContagemDTO;
import br.edu.tedi.dashboard.dto.ResumoDTO;
import br.edu.tedi.dashboard.service.DashboardService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) { this.service = service; }

    @GetMapping("/resumo")
    public ResumoDTO resumo() { return service.obterResumo(); }

    @GetMapping("/voluntarios/status")
    public List<ContagemDTO> porStatus() { return service.contarVoluntariosPorStatus(); }

    @GetMapping("/voluntarios/cursos")
    public List<ContagemDTO> porCurso() { return service.contarVoluntariosPorCurso(); }

    @GetMapping("/voluntarios/periodos")
    public List<ContagemDTO> porPeriodo() { return service.contarVoluntariosPorPeriodo(); }

    @GetMapping("/voluntarios/habilidades")
    public List<ContagemDTO> porHabilidade() { return service.consultarDistribuicaoHabilidades(); }
}
