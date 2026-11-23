package br.edu.tedi.voluntario.repository;

import br.edu.tedi.dashboard.dto.ContagemDTO;
import br.edu.tedi.voluntario.entity.Voluntario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface VoluntarioRepository extends JpaRepository<Voluntario, Integer>, JpaSpecificationExecutor<Voluntario> {

    boolean existsByRa(String ra);

    boolean existsByRaAndIdNot(String ra, Integer id);

    long countByAtivo(Boolean ativo);

    @Query("select new br.edu.tedi.dashboard.dto.ContagemDTO(v.status, count(v)) from Voluntario v group by v.status")
    List<ContagemDTO> contarPorStatus();

    @Query("select new br.edu.tedi.dashboard.dto.ContagemDTO(c.nome, count(v)) from Voluntario v join v.curso c group by c.nome")
    List<ContagemDTO> contarPorCurso();

    @Query("select new br.edu.tedi.dashboard.dto.ContagemDTO(cast(v.periodo as string), count(v)) from Voluntario v group by v.periodo order by v.periodo")
    List<ContagemDTO> contarPorPeriodo();

    @Query("select new br.edu.tedi.dashboard.dto.ContagemDTO(h.nome, count(vh)) from VoluntarioHabilidade vh join vh.habilidade h group by h.nome order by count(vh) desc")
    List<ContagemDTO> contarPorHabilidade();
}
