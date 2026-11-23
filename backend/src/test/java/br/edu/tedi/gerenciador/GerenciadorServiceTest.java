package br.edu.tedi.gerenciador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.edu.tedi.administrador.entity.Administrador;
import br.edu.tedi.administrador.repository.AdministradorRepository;
import br.edu.tedi.common.exception.DadosDuplicadosException;
import br.edu.tedi.common.exception.RegraNegocioException;
import br.edu.tedi.gerenciador.dto.AtribuirVoluntarioDTO;
import br.edu.tedi.gerenciador.dto.GerenciamentoResponseDTO;
import br.edu.tedi.gerenciador.entity.Gerenciador;
import br.edu.tedi.gerenciador.entity.GerenciadorId;
import br.edu.tedi.gerenciador.repository.GerenciadorRepository;
import br.edu.tedi.gerenciador.service.GerenciadorService;
import br.edu.tedi.voluntario.entity.Voluntario;
import br.edu.tedi.voluntario.service.VoluntarioConsultaService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GerenciadorServiceTest {

    @Mock GerenciadorRepository repository;
    @Mock AdministradorRepository administradorRepository;
    @Mock VoluntarioConsultaService voluntarioConsultaService;
    @InjectMocks GerenciadorService service;

    private Administrador adm;
    private Voluntario vol;

    @BeforeEach
    void preparar() {
        adm = new Administrador();
        adm.setId(1);
        adm.setNome("Profa");
        vol = new Voluntario();
        vol.setId(2);
        vol.setNome("Ana");
    }

    private Gerenciador gerenciamento(LocalDate inicio, LocalDate fim) {
        GerenciadorId id = new GerenciadorId();
        id.setAdministradorId(1);
        id.setVoluntarioId(2);
        Gerenciador g = new Gerenciador();
        g.setId(id);
        g.setAdministrador(adm);
        g.setVoluntario(vol);
        g.setDataInicio(inicio);
        g.setDataFim(fim);
        return g;
    }

    @Test
    void atribuirVoluntarioCriaGerenciamento() {
        when(administradorRepository.findById(1)).thenReturn(Optional.of(adm));
        when(voluntarioConsultaService.buscarEntidade(2)).thenReturn(vol);
        when(repository.existsById(any(GerenciadorId.class))).thenReturn(false);
        when(repository.save(any(Gerenciador.class))).thenAnswer(i -> i.getArgument(0));

        GerenciamentoResponseDTO r = service.atribuirVoluntario(new AtribuirVoluntarioDTO(1, 2, null));

        assertEquals(1, r.administradorId());
        assertEquals(2, r.voluntarioId());
        assertEquals(LocalDate.now(), r.dataInicio());
        assertNull(r.dataFim());
    }

    @Test
    void atribuirDuplicadoLancaExcecao() {
        when(administradorRepository.findById(1)).thenReturn(Optional.of(adm));
        when(voluntarioConsultaService.buscarEntidade(2)).thenReturn(vol);
        when(repository.existsById(any(GerenciadorId.class))).thenReturn(true);

        assertThrows(DadosDuplicadosException.class,
                () -> service.atribuirVoluntario(new AtribuirVoluntarioDTO(1, 2, null)));
    }

    @Test
    void finalizarDefineDataFim() {
        Gerenciador g = gerenciamento(LocalDate.of(2026, 1, 1), null);
        when(repository.findById(any(GerenciadorId.class))).thenReturn(Optional.of(g));

        GerenciamentoResponseDTO r = service.finalizarGerenciamento(1, 2, LocalDate.of(2026, 6, 1));

        assertEquals(LocalDate.of(2026, 6, 1), r.dataFim());
    }

    @Test
    void finalizarJaEncerradoLancaExcecao() {
        Gerenciador g = gerenciamento(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1));
        when(repository.findById(any(GerenciadorId.class))).thenReturn(Optional.of(g));

        assertThrows(RegraNegocioException.class, () -> service.finalizarGerenciamento(1, 2, null));
    }

    @Test
    void consultarPeriodosDoVoluntario() {
        when(repository.listarPorVoluntario(2)).thenReturn(List.of(gerenciamento(LocalDate.of(2026, 1, 1), null)));

        assertEquals(1, service.consultarPeriodosDoVoluntario(2).size());
    }
}
