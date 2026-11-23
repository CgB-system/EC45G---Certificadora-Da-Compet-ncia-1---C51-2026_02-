package br.edu.tedi.administrador.entity;

import br.edu.tedi.gerenciador.entity.Gerenciador;
import br.edu.tedi.usuario.entity.Usuario;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_Administrador")
@PrimaryKeyJoinColumn(name = "usu_id")
public class Administrador extends Usuario {

    @Column(name = "adm_id", nullable = false)
    private Byte administradorId;

    @OneToMany(mappedBy = "administrador")
    private List<Gerenciador> gerenciamentos = new ArrayList<>();

    public Byte getAdministradorId() { return administradorId; }
    public void setAdministradorId(Byte administradorId) { this.administradorId = administradorId; }
    public List<Gerenciador> getGerenciamentos() { return gerenciamentos; }
    public void setGerenciamentos(List<Gerenciador> gerenciamentos) { this.gerenciamentos = gerenciamentos; }
}
