package br.edu.tedi.curso.service;

import br.edu.tedi.common.exception.RecursoNaoEncontradoException;
import br.edu.tedi.curso.dto.CursoResponseDTO;
import br.edu.tedi.curso.entity.Curso;
import br.edu.tedi.curso.repository.CursoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CursoService {

    private final CursoRepository repository;

    public CursoService(CursoRepository repository) { this.repository = repository; }

    public List<CursoResponseDTO> listarCursos() {
        return repository.findAll().stream().map(CursoResponseDTO::de).toList();
    }

    public CursoResponseDTO buscarCursoPorId(Integer id) {
        return CursoResponseDTO.de(buscarEntidade(id));
    }

    public Curso buscarEntidade(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado."));
    }

    public Optional<Integer> buscarIdPorNome(String nome) {
        return repository.findByNomeIgnoreCase(nome.trim()).map(Curso::getId);
    }
}
