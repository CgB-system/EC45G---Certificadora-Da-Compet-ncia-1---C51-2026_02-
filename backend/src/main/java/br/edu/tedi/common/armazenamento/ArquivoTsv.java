package br.edu.tedi.common.armazenamento;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public final class ArquivoTsv {

    private ArquivoTsv() {}

    public static Path prepararDiretorio(String diretorio) {
        try {
            return Files.createDirectories(Path.of(diretorio));
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível criar o diretório de dados: " + diretorio, e);
        }
    }

    public static String linha(String... campos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                sb.append('\t');
            }
            sb.append(escapar(campos[i]));
        }
        return sb.toString();
    }

    public static String[] campos(String linha) {
        String[] partes = linha.split("\t", -1);
        for (int i = 0; i < partes.length; i++) {
            partes[i] = desescapar(partes[i]);
        }
        return partes;
    }

    public static void anexar(Path arquivo, String linha) throws IOException {
        Files.writeString(arquivo, linha + "\n", StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public static List<String> ler(Path arquivo) throws IOException {
        if (!Files.exists(arquivo)) {
            return List.of();
        }
        return Files.readAllLines(arquivo, StandardCharsets.UTF_8).stream()
                .filter(l -> !l.isBlank())
                .toList();
    }

    static String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(valor.length());
        for (char c : valor.toCharArray()) {
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '\t' -> sb.append("\\t");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    static String desescapar(String valor) {
        StringBuilder sb = new StringBuilder(valor.length());
        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            if (c == '\\' && i + 1 < valor.length()) {
                char proximo = valor.charAt(++i);
                switch (proximo) {
                    case 't' -> sb.append('\t');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    default -> sb.append(proximo);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
