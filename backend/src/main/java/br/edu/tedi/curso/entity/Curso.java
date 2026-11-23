package br.edu.tedi.curso.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_Curso")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cur_id")
    private Integer id;

    @Column(name = "cur_nome", length = 45, nullable = false)
    private String nome;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
