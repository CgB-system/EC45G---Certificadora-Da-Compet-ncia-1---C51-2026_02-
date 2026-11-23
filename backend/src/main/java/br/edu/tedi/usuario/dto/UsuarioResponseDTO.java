package br.edu.tedi.usuario.dto;

public record UsuarioResponseDTO(Integer id, String nome, String sobrenome, String email, Boolean ativo, String tipo) {}
