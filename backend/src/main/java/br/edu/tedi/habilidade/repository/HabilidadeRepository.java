package br.edu.tedi.habilidade.repository;

import br.edu.tedi.habilidade.entity.Habilidade;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabilidadeRepository extends JpaRepository<Habilidade, Integer> {

    Optional<Habilidade> findByNomeIgnoreCase(String nome);
}
