package br.edu.tedi.habilidade.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_Habilidade")
public class Habilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hab_id")
    private Integer id;

    @Column(name = "hab_nome", length = 60, nullable = false)
    private String nome;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
