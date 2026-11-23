package br.edu.tedi.auth.dto;

public record TokenDTO(String accessToken, String refreshToken, String tipo, long expiraEmSegundos) {}
