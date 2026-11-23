package br.edu.tedi.gerenciador.entity;

import br.edu.tedi.administrador.entity.Administrador;
import br.edu.tedi.voluntario.entity.Voluntario;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_Gerenciador")
public class Gerenciador {

    @EmbeddedId
    private GerenciadorId id = new GerenciadorId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("administradorId")
    @JoinColumn(name = "adm_usu_id")
    private Administrador administrador;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("voluntarioId")
    @JoinColumn(name = "vol_usu_id")
    private Voluntario voluntario;

    @Column(name = "ger_data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "ger_data_fim")
    private LocalDate dataFim;

    public GerenciadorId getId() { return id; }
    public void setId(GerenciadorId id) { this.id = id; }
    public Administrador getAdministrador() { return administrador; }
    public void setAdministrador(Administrador administrador) { this.administrador = administrador; }
    public Voluntario getVoluntario() { return voluntario; }
    public void setVoluntario(Voluntario voluntario) { this.voluntario = voluntario; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public void encerrar(LocalDate data) { this.dataFim = data; }
}
