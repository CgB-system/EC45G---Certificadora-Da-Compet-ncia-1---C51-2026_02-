package br.edu.tedi.dashboard.dto;

public record ResumoDTO(long totalVoluntarios, long voluntariosAtivos, long voluntariosInativos,
                        long totalCursos, long totalHabilidades) {}
