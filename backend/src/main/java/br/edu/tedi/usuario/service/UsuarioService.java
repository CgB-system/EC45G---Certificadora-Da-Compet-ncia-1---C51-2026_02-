package br.edu.tedi.usuario.service;

import br.edu.tedi.common.exception.DadosDuplicadosException;
import br.edu.tedi.common.exception.RecursoNaoEncontradoException;
import br.edu.tedi.common.pagination.PaginaDTO;
import br.edu.tedi.common.pagination.OrdenacaoPermitida;
import java.util.Set;
import br.edu.tedi.usuario.dto.UsuarioAtualizacaoDTO;
import br.edu.tedi.usuario.dto.UsuarioCadastroDTO;
import br.edu.tedi.usuario.dto.UsuarioResponseDTO;
import br.edu.tedi.usuario.entity.Usuario;
import br.edu.tedi.usuario.mapper.UsuarioMapper;
import br.edu.tedi.usuario.repository.UsuarioRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private static final Set<String> ORDENACAO_PERMITIDA =
            Set.of("id", "nome", "sobrenome", "email", "ativo");

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final UsuarioMapper mapper;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder encoder, UsuarioMapper mapper) {
        this.repository = repository;
        this.encoder = encoder;
        this.mapper = mapper;
    }

    @Transactional
    public UsuarioResponseDTO cadastrarUsuario(UsuarioCadastroDTO dto) {
        String email = dto.email().trim().toLowerCase();
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new DadosDuplicadosException("E-mail já cadastrado.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome().trim());
        usuario.setSobrenome(dto.sobrenome().trim());
        usuario.setEmail(email);
        usuario.setSenha(encoder.encode(dto.senha()));
        usuario.setAtivo(true);
        return mapper.toResponse(repository.save(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarUsuarioPorId(Integer id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public PaginaDTO<UsuarioResponseDTO> listarUsuarios(Pageable pageable) {
        OrdenacaoPermitida.validar(pageable, ORDENACAO_PERMITIDA);
        return PaginaDTO.de(repository.findAll(pageable).map(mapper::toResponse));
    }

    @Transactional
    public UsuarioResponseDTO atualizarUsuario(Integer id, UsuarioAtualizacaoDTO dto) {
        Usuario usuario = buscarEntidade(id);
        String email = dto.email().trim().toLowerCase();
        if (repository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DadosDuplicadosException("E-mail já cadastrado.");
        }
        usuario.setNome(dto.nome().trim());
        usuario.setSobrenome(dto.sobrenome().trim());
        usuario.setEmail(email);
        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(encoder.encode(dto.senha()));
        }
        return mapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponseDTO alterarStatus(Integer id, boolean ativo) {
        Usuario usuario = buscarEntidade(id);
        if (ativo) {
            usuario.ativar();
        } else {
            usuario.desativar();
        }
        return mapper.toResponse(usuario);
    }

    public Usuario buscarEntidade(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }
}
