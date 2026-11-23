package br.edu.tedi.voluntario;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import br.edu.tedi.common.exception.DadosDuplicadosException;
import br.edu.tedi.common.exception.RecursoNaoEncontradoException;
import br.edu.tedi.common.pagination.PaginaDTO;
import br.edu.tedi.curso.entity.Curso;
import br.edu.tedi.curso.service.CursoService;
import br.edu.tedi.habilidade.entity.Habilidade;
import br.edu.tedi.habilidade.service.HabilidadeService;
import br.edu.tedi.telefone.service.TelefoneService;
import br.edu.tedi.usuario.repository.UsuarioRepository;
import br.edu.tedi.voluntario.dto.*;
import br.edu.tedi.voluntario.entity.Voluntario;
import br.edu.tedi.voluntario.mapper.VoluntarioMapper;
import br.edu.tedi.voluntario.repository.VoluntarioRepository;
import br.edu.tedi.voluntario.service.VoluntarioConsultaService;
import br.edu.tedi.voluntario.service.VoluntarioService;
import br.edu.tedi.voluntario.service.VoluntarioStatusService;
import br.edu.tedi.voluntario.validation.StatusVoluntarioValidador;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class VoluntarioServiceTest {

    @Mock VoluntarioRepository repository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock CursoService cursoService;
    @Mock HabilidadeService habilidadeService;
    @Mock TelefoneService telefoneService;
    @Mock VoluntarioMapper mapper;
    @Mock PasswordEncoder encoder;
    @Mock StatusVoluntarioValidador statusValidador;

    private VoluntarioConsultaService consultaService;
    private VoluntarioService service;

    @BeforeEach
    void preparar() {
        consultaService = new VoluntarioConsultaService(repository, mapper);
        service = new VoluntarioService(repository, usuarioRepository, consultaService, cursoService,
                habilidadeService, telefoneService, mapper, encoder, statusValidador);
    }

    private VoluntarioCadastroDTO dtoCadastro(List<Integer> habilidades) {
        return new VoluntarioCadastroDTO("Ana", "Silva", "Ana@Tedi.edu", "1234567", (byte) 3,
                new BigDecimal("7.5000"), 1, "Monitoria", "ATIVO", habilidades, null);
    }

    @Test
    void cadastroValidoSalvaVoluntario() {
        Curso curso = new Curso();
        curso.setId(1);
        Habilidade hab = new Habilidade();
        hab.setId(10);
        when(usuarioRepository.existsByEmailIgnoreCase("ana@tedi.edu")).thenReturn(false);
        when(repository.existsByRa("1234567")).thenReturn(false);
        when(cursoService.buscarEntidade(1)).thenReturn(curso);
        when(habilidadeService.buscarEntidade(10)).thenReturn(hab);
        when(encoder.encode(anyString())).thenReturn("hash");

        service.cadastrarVoluntario(dtoCadastro(List.of(10)));

        ArgumentCaptor<Voluntario> captor = ArgumentCaptor.forClass(Voluntario.class);
        verify(repository).save(captor.capture());
        Voluntario salvo = captor.getValue();
        assertEquals("ana@tedi.edu", salvo.getEmail());
        assertEquals("ATIVO", salvo.getStatus());
        assertTrue(salvo.getAtivo());
        assertEquals(1, salvo.getHabilidades().size());
        assertEquals("hash", salvo.getSenha());
    }

    @Test
    void cadastroComRaDuplicadoLancaExcecao() {
        when(usuarioRepository.existsByEmailIgnoreCase("ana@tedi.edu")).thenReturn(false);
        when(repository.existsByRa("1234567")).thenReturn(true);

        assertThrows(DadosDuplicadosException.class, () -> service.cadastrarVoluntario(dtoCadastro(null)));
        verify(repository, never()).save(any());
    }

    @Test
    void habilidadeRepetidaLancaExcecao() {
        when(usuarioRepository.existsByEmailIgnoreCase("ana@tedi.edu")).thenReturn(false);
        when(repository.existsByRa("1234567")).thenReturn(false);
        when(cursoService.buscarEntidade(1)).thenReturn(new Curso());
        when(encoder.encode(anyString())).thenReturn("hash");

        assertThrows(DadosDuplicadosException.class, () -> service.cadastrarVoluntario(dtoCadastro(List.of(5, 5))));
    }

    @Test
    void consultaPorIdInexistenteLancaNaoEncontrado() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> consultaService.buscarVoluntarioPorId(99));
    }

    @Test
    void atualizacaoAlteraDadosEHabilidades() {
        Voluntario v = new Voluntario();
        v.setId(5);
        Habilidade antiga = new Habilidade();
        antiga.setId(1);
        v.adicionarHabilidade(antiga);
        Habilidade nova = new Habilidade();
        nova.setId(2);
        when(repository.findById(5)).thenReturn(Optional.of(v));
        when(cursoService.buscarEntidade(1)).thenReturn(new Curso());
        when(habilidadeService.buscarEntidade(2)).thenReturn(nova);

        service.atualizarVoluntario(5, new VoluntarioAtualizacaoDTO("Ana", "Souza", "novo@tedi.edu", "7654321",
                (byte) 4, new BigDecimal("8.0000"), 1, null, List.of(2)));

        assertEquals("Souza", v.getSobrenome());
        assertEquals(1, v.getHabilidades().size());
        assertEquals(2, v.getHabilidades().get(0).getHabilidade().getId());
    }

    @Test
    void alterarStatusAtualizaVoluntario() {
        Voluntario v = new Voluntario();
        when(repository.findById(5)).thenReturn(Optional.of(v));
        VoluntarioStatusService statusService = new VoluntarioStatusService(consultaService, mapper, statusValidador);

        statusService.alterarStatus(5, new AlterarStatusDTO("INATIVO"));

        assertEquals("INATIVO", v.getStatus());
    }

    @Test
    @SuppressWarnings("unchecked")
    void filtrosCombinadosUsamConsultaPaginada() {
        Voluntario v = new Voluntario();
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(v)));
        VoluntarioFiltroDTO filtro = new VoluntarioFiltroDTO("ana", null, null, 1, (byte) 3,
                new BigDecimal("7"), null, 10, "ATIVO", true);

        PaginaDTO<VoluntarioResponseDTO> pagina = consultaService.filtrarVoluntarios(filtro, PageRequest.of(0, 20));

        assertEquals(1, pagina.totalElementos());
    }
}
