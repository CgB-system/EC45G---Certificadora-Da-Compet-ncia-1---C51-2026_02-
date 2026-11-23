package br.edu.tedi.gerenciador.repository;

import br.edu.tedi.gerenciador.entity.Gerenciador;
import br.edu.tedi.gerenciador.entity.GerenciadorId;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GerenciadorRepository extends JpaRepository<Gerenciador, GerenciadorId> {

    @Query("select g from Gerenciador g where (:adm is null or g.id.administradorId = :adm) "
            + "and (:vol is null or g.id.voluntarioId = :vol)")
    Page<Gerenciador> buscar(@Param("adm") Integer administradorId, @Param("vol") Integer voluntarioId, Pageable pageable);

    @Query("select g from Gerenciador g where g.id.voluntarioId = :vol order by g.dataInicio desc")
    List<Gerenciador> listarPorVoluntario(@Param("vol") Integer voluntarioId);
}
