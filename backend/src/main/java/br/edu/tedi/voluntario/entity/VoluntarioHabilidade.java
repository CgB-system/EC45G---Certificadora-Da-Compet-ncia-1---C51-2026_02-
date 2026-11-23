package br.edu.tedi.voluntario.entity;

import br.edu.tedi.habilidade.entity.Habilidade;
import jakarta.persistence.*;

@Entity
@Table(name = "tbl_Voluntario_Habilidade")
public class VoluntarioHabilidade {

    @EmbeddedId
    private VoluntarioHabilidadeId id = new VoluntarioHabilidadeId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("voluntarioId")
    @JoinColumn(name = "usu_id")
    private Voluntario voluntario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("habilidadeId")
    @JoinColumn(name = "hab_id")
    private Habilidade habilidade;

    public VoluntarioHabilidadeId getId() { return id; }
    public void setId(VoluntarioHabilidadeId id) { this.id = id; }
    public Voluntario getVoluntario() { return voluntario; }
    public void setVoluntario(Voluntario voluntario) { this.voluntario = voluntario; }
    public Habilidade getHabilidade() { return habilidade; }
    public void setHabilidade(Habilidade habilidade) { this.habilidade = habilidade; }
}
