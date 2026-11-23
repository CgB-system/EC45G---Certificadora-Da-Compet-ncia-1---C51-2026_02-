package br.edu.tedi.habilidade.service;

import br.edu.tedi.common.exception.RecursoNaoEncontradoException;
import br.edu.tedi.habilidade.dto.HabilidadeResponseDTO;
import br.edu.tedi.habilidade.entity.Habilidade;
import br.edu.tedi.habilidade.repository.HabilidadeRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HabilidadeService {

    private final HabilidadeRepository repository;

    public HabilidadeService(HabilidadeRepository repository) { this.repository = repository; }

    public List<HabilidadeResponseDTO> listarHabilidades() {
        return repository.findAll().stream().map(HabilidadeResponseDTO::de).toList();
    }

    public HabilidadeResponseDTO buscarHabilidadePorId(Integer id) {
        return HabilidadeResponseDTO.de(buscarEntidade(id));
    }

    public Habilidade buscarEntidade(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Habilidade não encontrada."));
    }

    public Optional<Integer> buscarIdPorNome(String nome) {
        return repository.findByNomeIgnoreCase(nome.trim()).map(Habilidade::getId);
    }
}
