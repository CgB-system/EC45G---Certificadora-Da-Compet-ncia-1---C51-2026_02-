package br.edu.tedi.curso.repository;

import br.edu.tedi.curso.entity.Curso;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Integer> {

    Optional<Curso> findByNomeIgnoreCase(String nome);
}
