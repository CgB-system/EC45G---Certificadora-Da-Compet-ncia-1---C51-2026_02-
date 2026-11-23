package br.edu.tedi.gerenciador.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GerenciadorId implements Serializable {

    @Column(name = "adm_usu_id")
    private Integer administradorId;

    @Column(name = "vol_usu_id")
    private Integer voluntarioId;

    public Integer getAdministradorId() { return administradorId; }
    public void setAdministradorId(Integer administradorId) { this.administradorId = administradorId; }
    public Integer getVoluntarioId() { return voluntarioId; }
    public void setVoluntarioId(Integer voluntarioId) { this.voluntarioId = voluntarioId; }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) return true;
        if (!(objeto instanceof GerenciadorId outro)) return false;
        return Objects.equals(administradorId, outro.administradorId)
                && Objects.equals(voluntarioId, outro.voluntarioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(administradorId, voluntarioId);
    }
}
