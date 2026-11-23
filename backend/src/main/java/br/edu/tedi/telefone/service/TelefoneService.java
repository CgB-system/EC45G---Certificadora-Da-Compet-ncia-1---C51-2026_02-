package br.edu.tedi.telefone.service;

import br.edu.tedi.common.exception.RecursoNaoEncontradoException;
import br.edu.tedi.telefone.dto.TelefoneDTO;
import br.edu.tedi.telefone.dto.TelefoneResponseDTO;
import br.edu.tedi.telefone.entity.Telefone;
import br.edu.tedi.telefone.repository.TelefoneRepository;
import br.edu.tedi.usuario.service.UsuarioService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TelefoneService {

    private final TelefoneRepository repository;
    private final UsuarioService usuarioService;

    public TelefoneService(TelefoneRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public TelefoneResponseDTO adicionarTelefone(Integer usuarioId, TelefoneDTO dto) {
        Telefone telefone = new Telefone();
        telefone.setUsuario(usuarioService.buscarEntidade(usuarioId));
        preencher(telefone, dto);
        return TelefoneResponseDTO.de(repository.save(telefone));
    }

    @Transactional(readOnly = true)
    public List<TelefoneResponseDTO> listarTelefones(Integer usuarioId) {
        usuarioService.buscarEntidade(usuarioId);
        return repository.findByUsuarioId(usuarioId).stream().map(TelefoneResponseDTO::de).toList();
    }

    @Transactional
    public TelefoneResponseDTO atualizarTelefone(Integer usuarioId, Integer telefoneId, TelefoneDTO dto) {
        Telefone telefone = buscar(usuarioId, telefoneId);
        preencher(telefone, dto);
        return TelefoneResponseDTO.de(telefone);
    }

    @Transactional
    public void removerTelefone(Integer usuarioId, Integer telefoneId) {
        repository.delete(buscar(usuarioId, telefoneId));
    }

    private Telefone buscar(Integer usuarioId, Integer telefoneId) {
        return repository.findByIdAndUsuarioId(telefoneId, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Telefone não encontrado."));
    }

    private void preencher(Telefone telefone, TelefoneDTO dto) {
        telefone.setCodigo(dto.codigo());
        telefone.setDdd(dto.ddd());
        telefone.setNumero(dto.numero());
    }
}
