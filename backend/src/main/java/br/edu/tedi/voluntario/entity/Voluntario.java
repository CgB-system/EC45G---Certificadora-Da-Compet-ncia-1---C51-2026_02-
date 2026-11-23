package br.edu.tedi.voluntario.entity;

import br.edu.tedi.curso.entity.Curso;
import br.edu.tedi.gerenciador.entity.Gerenciador;
import br.edu.tedi.habilidade.entity.Habilidade;
import br.edu.tedi.usuario.entity.Usuario;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tbl_Voluntario")
@PrimaryKeyJoinColumn(name = "usu_id")
public class Voluntario extends Usuario {

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "vol_ra", length = 7, nullable = false)
    private String ra;

    @Column(name = "vol_periodo", nullable = false)
    private Byte periodo;

    @Column(name = "vol_coef", precision = 5, scale = 4, nullable = false)
    private BigDecimal coeficiente;

    @Column(name = "vol_experiencia", length = 250)
    private String experiencia;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "vol_status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cur_id")
    private Curso curso;

    @OneToMany(mappedBy = "voluntario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoluntarioHabilidade> habilidades = new ArrayList<>();

    @OneToMany(mappedBy = "voluntario")
    private List<Gerenciador> gerenciamentos = new ArrayList<>();

    public String getRa() { return ra; }
    public void setRa(String ra) { this.ra = ra; }
    public Byte getPeriodo() { return periodo; }
    public void setPeriodo(Byte periodo) { this.periodo = periodo; }
    public BigDecimal getCoeficiente() { return coeficiente; }
    public void setCoeficiente(BigDecimal coeficiente) { this.coeficiente = coeficiente; }
    public String getExperiencia() { return experiencia; }
    public void setExperiencia(String experiencia) { this.experiencia = experiencia; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }
    public List<VoluntarioHabilidade> getHabilidades() { return habilidades; }
    public void setHabilidades(List<VoluntarioHabilidade> habilidades) { this.habilidades = habilidades; }
    public List<Gerenciador> getGerenciamentos() { return gerenciamentos; }
    public void setGerenciamentos(List<Gerenciador> gerenciamentos) { this.gerenciamentos = gerenciamentos; }

    public void atualizarDados(String nome, String sobrenome, String email, String ra, Byte periodo,
                               BigDecimal coeficiente, String experiencia, Curso curso) {
        setNome(nome);
        setSobrenome(sobrenome);
        setEmail(email);
        this.ra = ra;
        this.periodo = periodo;
        this.coeficiente = coeficiente;
        this.experiencia = experiencia;
        this.curso = curso;
    }

    public void alterarStatus(String novoStatus) {
        this.status = novoStatus;
    }

    public void adicionarHabilidade(Habilidade habilidade) {
        VoluntarioHabilidade vh = new VoluntarioHabilidade();
        vh.setVoluntario(this);
        vh.setHabilidade(habilidade);
        habilidades.add(vh);
    }

    public void removerHabilidade(Integer habilidadeId) {
        habilidades.removeIf(vh -> vh.getHabilidade().getId().equals(habilidadeId));
    }
}
