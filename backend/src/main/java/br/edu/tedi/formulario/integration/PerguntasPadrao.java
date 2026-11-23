package br.edu.tedi.formulario.integration;

import java.util.List;

public final class PerguntasPadrao {

    public static final String NOME = "Nome";
    public static final String SOBRENOME = "Sobrenome";
    public static final String EMAIL = "E-mail";
    public static final String RA = "RA";
    public static final String CURSO = "Curso";
    public static final String PERIODO = "Período";
    public static final String COEFICIENTE = "Coeficiente";
    public static final String HABILIDADES = "Habilidades (separadas por vírgula)";
    public static final String EXPERIENCIA = "Experiência";
    public static final String TELEFONE = "Telefone (DDD + número)";

    public static final List<String> TODAS = List.of(NOME, SOBRENOME, EMAIL, RA, CURSO, PERIODO,
            COEFICIENTE, HABILIDADES, EXPERIENCIA, TELEFONE);

    private PerguntasPadrao() {}
}
