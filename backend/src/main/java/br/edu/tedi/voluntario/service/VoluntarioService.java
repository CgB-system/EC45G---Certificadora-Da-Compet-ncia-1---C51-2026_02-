package br.edu.tedi.voluntario.service;

import br.edu.tedi.common.exception.DadosDuplicadosException;
import br.edu.tedi.curso.service.CursoService;
import br.edu.tedi.habilidade.service.HabilidadeService;
import br.edu.tedi.telefone.dto.TelefoneDTO;
import br.edu.tedi.telefone.dto.TelefoneResponseDTO;
import br.edu.tedi.telefone.service.TelefoneService;
import br.edu.tedi.usuario.repository.UsuarioRepository;
import br.edu.tedi.voluntario.dto.VoluntarioAtualizacaoDTO;
import br.edu.tedi.voluntario.dto.VoluntarioCadastroDTO;
import br.edu.tedi.voluntario.dto.VoluntarioResponseDTO;
import br.edu.tedi.voluntario.entity.Voluntario;
import br.edu.tedi.voluntario.mapper.VoluntarioMapper;
import br.edu.tedi.voluntario.repository.VoluntarioRepository;
import br.edu.tedi.voluntario.validation.StatusVoluntarioValidador;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoluntarioService {

    private final VoluntarioRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final VoluntarioConsultaService consultaService;
    private final CursoService cursoService;
    private final HabilidadeService habilidadeService;
    private final TelefoneService telefoneService;
    private final VoluntarioMapper mapper;
    private final PasswordEncoder encoder;
    private final StatusVoluntarioValidador statusValidador;

    public VoluntarioService(VoluntarioRepository repository, UsuarioRepository usuarioRepository,
                             VoluntarioConsultaService consultaService, CursoService cursoService,
                             HabilidadeService habilidadeService, TelefoneService telefoneService,
                             VoluntarioMapper mapper, PasswordEncoder encoder,
                             StatusVoluntarioValidador statusValidador) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.consultaService = consultaService;
        this.cursoService = cursoService;
        this.habilidadeService = habilidadeService;
        this.telefoneService = telefoneService;
        this.mapper = mapper;
        this.encoder = encoder;
        this.statusValidador = statusValidador;
    }

    @Transactional
    public synchronized VoluntarioResponseDTO cadastrarVoluntario(VoluntarioCadastroDTO dto) {
        String email = dto.email().trim().toLowerCase();
        verificarDuplicidade(email, dto.ra().trim(), null);
        statusValidador.validar(dto.status());

        Voluntario v = new Voluntario();
        v.atualizarDados(dto.nome().trim(), dto.sobrenome().trim(), email, dto.ra().trim(), dto.periodo(),
                dto.coeficiente(), dto.experiencia(), cursoService.buscarEntidade(dto.cursoId()));
        v.alterarStatus(dto.status().trim());
        v.setAtivo(true);
        v.setSenha(encoder.encode(UUID.randomUUID().toString()));
        verificarHabilidadesRepetidas(dto.habilidadesIds());
        if (dto.habilidadesIds() != null) {
            dto.habilidadesIds().forEach(id -> v.adicionarHabilidade(habilidadeService.buscarEntidade(id)));
        }
        repository.save(v);

        List<TelefoneResponseDTO> telefonesCriados = new ArrayList<>();
        if (dto.telefones() != null) {
            for (TelefoneDTO telefone : dto.telefones()) {
                telefonesCriados.add(telefoneService.adicionarTelefone(v.getId(), telefone));
            }
        }
        VoluntarioResponseDTO resposta = mapper.toResponse(v);
        if (resposta == null || telefonesCriados.isEmpty()) {
            return resposta;
        }

        return new VoluntarioResponseDTO(resposta.id(), resposta.nome(), resposta.sobrenome(), resposta.email(),
                resposta.ra(), resposta.periodo(), resposta.coeficiente(), resposta.experiencia(),
                resposta.status(), resposta.ativo(), resposta.curso(), resposta.habilidades(), telefonesCriados);
    }

    @Transactional
    public synchronized VoluntarioResponseDTO atualizarVoluntario(Integer id, VoluntarioAtualizacaoDTO dto) {
        Voluntario v = consultaService.buscarEntidade(id);
        String email = dto.email().trim().toLowerCase();
        verificarDuplicidade(email, dto.ra().trim(), id);

        v.atualizarDados(dto.nome().trim(), dto.sobrenome().trim(), email, dto.ra().trim(), dto.periodo(),
                dto.coeficiente(), dto.experiencia(), cursoService.buscarEntidade(dto.cursoId()));
        atualizarHabilidades(v, dto.habilidadesIds());
        return mapper.toResponse(v);
    }

    @Transactional
    public void desativarVoluntario(Integer id) {
        consultaService.buscarEntidade(id).desativar();
    }

    public void validarStatus(String status) {
        statusValidador.validar(status);
    }

    public void verificarDuplicidade(String email, String ra, Integer ignorarId) {
        boolean emailDuplicado = ignorarId == null
                ? usuarioRepository.existsByEmailIgnoreCase(email)
                : usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, ignorarId);
        if (emailDuplicado) {
            throw new DadosDuplicadosException("E-mail já cadastrado.");
        }
        boolean raDuplicado = ignorarId == null
                ? repository.existsByRa(ra)
                : repository.existsByRaAndIdNot(ra, ignorarId);
        if (raDuplicado) {
            throw new DadosDuplicadosException("RA já cadastrado.");
        }
    }

    private void atualizarHabilidades(Voluntario v, List<Integer> ids) {
        if (ids == null) {
            return;
        }
        verificarHabilidadesRepetidas(ids);
        Set<Integer> novos = new HashSet<>(ids);
        v.getHabilidades().removeIf(vh -> !novos.contains(vh.getHabilidade().getId()));
        Set<Integer> atuais = v.getHabilidades().stream()
                .map(vh -> vh.getHabilidade().getId()).collect(Collectors.toSet());
        for (Integer id : ids) {
            if (!atuais.contains(id)) {
                v.adicionarHabilidade(habilidadeService.buscarEntidade(id));
            }
        }
    }

    private void verificarHabilidadesRepetidas(List<Integer> ids) {
        if (ids != null && new HashSet<>(ids).size() != ids.size()) {
            throw new DadosDuplicadosException("A mesma habilidade foi informada mais de uma vez.");
        }
    }
}
