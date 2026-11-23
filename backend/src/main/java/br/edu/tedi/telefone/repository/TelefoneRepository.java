package br.edu.tedi.telefone.repository;

import br.edu.tedi.telefone.entity.Telefone;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelefoneRepository extends JpaRepository<Telefone, Integer> {

    List<Telefone> findByUsuarioId(Integer usuarioId);

    Optional<Telefone> findByIdAndUsuarioId(Integer id, Integer usuarioId);
}
