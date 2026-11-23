package br.edu.tedi.formulario.dto;

import java.util.Map;

public record RespostaFormulario(String id, String criadaEm, Map<String, String> respostas) {}
