package br.edu.tedi.telefone.entity;

import br.edu.tedi.usuario.entity.Usuario;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tbl_Telefone")
public class Telefone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tel_id")
    private Integer id;

    @Column(name = "tel_cod", length = 4, nullable = false)
    private String codigo;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "tel_ddd", length = 2, nullable = false)
    private String ddd;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "tel_numero", length = 9, nullable = false)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usu_id")
    private Usuario usuario;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDdd() { return ddd; }
    public void setDdd(String ddd) { this.ddd = ddd; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
