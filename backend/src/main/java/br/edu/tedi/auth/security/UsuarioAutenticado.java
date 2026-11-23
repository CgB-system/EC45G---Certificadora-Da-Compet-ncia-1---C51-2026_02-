package br.edu.tedi.auth.security;

public record UsuarioAutenticado(Integer id, String email, boolean administrador) {}
