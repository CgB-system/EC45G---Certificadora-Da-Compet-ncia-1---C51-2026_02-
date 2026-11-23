package br.edu.tedi.usuario.entity;

import br.edu.tedi.telefone.entity.Telefone;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_Usuario")
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usu_id")
    private Integer id;

    @Column(name = "usu_nome", length = 50, nullable = false)
    private String nome;

    @Column(name = "usu_sobrenome", length = 60, nullable = false)
    private String sobrenome;

    @Column(name = "usu_email", length = 80, nullable = false)
    private String email;

    @Column(name = "usu_senha", length = 150, nullable = false)
    private String senha;

    @Column(name = "usu_ativo", nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "usuario")
    private List<Telefone> telefones = new ArrayList<>();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public List<Telefone> getTelefones() { return telefones; }
    public void setTelefones(List<Telefone> telefones) { this.telefones = telefones; }

    public void ativar() { this.ativo = true; }
    public void desativar() { this.ativo = false; }
}
