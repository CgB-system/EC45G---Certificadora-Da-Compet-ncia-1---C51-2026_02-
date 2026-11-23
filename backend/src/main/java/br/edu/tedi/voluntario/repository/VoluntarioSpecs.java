package br.edu.tedi.voluntario.repository;

import br.edu.tedi.voluntario.dto.VoluntarioFiltroDTO;
import br.edu.tedi.voluntario.entity.Voluntario;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class VoluntarioSpecs {

    private VoluntarioSpecs() {}

    public static Specification<Voluntario> de(VoluntarioFiltroDTO f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (preenchido(f.nome())) {
                p.add(cb.like(cb.lower(root.get("nome")), "%" + f.nome().trim().toLowerCase() + "%"));
            }
            if (preenchido(f.sobrenome())) {
                p.add(cb.like(cb.lower(root.get("sobrenome")), "%" + f.sobrenome().trim().toLowerCase() + "%"));
            }
            if (preenchido(f.ra())) {
                p.add(cb.equal(root.get("ra"), f.ra().trim()));
            }
            if (f.cursoId() != null) {
                p.add(cb.equal(root.get("curso").get("id"), f.cursoId()));
            }
            if (f.periodo() != null) {
                p.add(cb.equal(root.get("periodo"), f.periodo()));
            }
            if (f.coeficienteMinimo() != null) {
                p.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("coeficiente"), f.coeficienteMinimo()));
            }
            if (f.coeficienteMaximo() != null) {
                p.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("coeficiente"), f.coeficienteMaximo()));
            }
            if (preenchido(f.status())) {
                p.add(cb.equal(root.get("status"), f.status().trim()));
            }
            if (f.ativo() != null) {
                p.add(cb.equal(root.get("ativo"), f.ativo()));
            }
            if (f.habilidadeId() != null) {
                query.distinct(true);
                Join<Object, Object> vh = root.join("habilidades");
                p.add(cb.equal(vh.get("habilidade").get("id"), f.habilidadeId()));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    private static boolean preenchido(String s) { return s != null && !s.isBlank(); }
}
