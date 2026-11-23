package br.edu.tedi.voluntario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class VoluntarioHabilidadeId implements Serializable {

    @Column(name = "usu_id")
    private Integer voluntarioId;

    @Column(name = "hab_id")
    private Integer habilidadeId;

    public Integer getVoluntarioId() { return voluntarioId; }
    public void setVoluntarioId(Integer voluntarioId) { this.voluntarioId = voluntarioId; }
    public Integer getHabilidadeId() { return habilidadeId; }
    public void setHabilidadeId(Integer habilidadeId) { this.habilidadeId = habilidadeId; }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) return true;
        if (!(objeto instanceof VoluntarioHabilidadeId outro)) return false;
        return Objects.equals(voluntarioId, outro.voluntarioId)
                && Objects.equals(habilidadeId, outro.habilidadeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(voluntarioId, habilidadeId);
    }
}
