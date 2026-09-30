package com.diniz.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Carrega uma vez as palavras disponíveis no classpath. */
public class WordRepository {
    private static final String RESOURCE_NAME = "palavras.txt";
    private final List<String> words;

    public WordRepository() {
        this(openResource());
    }

    WordRepository(Reader reader) {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            words = bufferedReader.lines()
                    .map(String::trim)
                    .filter(word -> !word.isEmpty())
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível ler a base de palavras.", exception);
        }
    }

    public List<String> getWords() {
        return words;
    }

    private static Reader openResource() {
        InputStream input = WordRepository.class.getClassLoader().getResourceAsStream(RESOURCE_NAME);
        if (input == null) {
            throw new IllegalStateException("Recurso " + RESOURCE_NAME + " não encontrado no classpath.");
        }
        return new InputStreamReader(input, StandardCharsets.UTF_8);
    }
}
