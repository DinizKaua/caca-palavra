package com.diniz.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.diniz.model.Difficulty;
import com.diniz.repository.WordRepository;

/** Seleciona palavras compatíveis com a partida, sem repetir as já utilizadas. */
public class WordSelectionService {
    private final WordRepository repository;
    private final Random random;

    public WordSelectionService(WordRepository repository) {
        this(repository, new Random());
    }

    public WordSelectionService(WordRepository repository, Random random) {
        this.repository = repository;
        this.random = random;
    }

    public Optional<String> selectRandomWord(Difficulty difficulty, Collection<String> occupiedWords) {
        Set<String> occupied = new HashSet<>();
        for (String word : occupiedWords) {
            if (word != null && !word.isBlank()) occupied.add(canonical(word));
        }

        List<String> candidates = repository.getWords().stream()
                .filter(word -> word.length() <= difficulty.getBoardSize())
                .filter(word -> !occupied.contains(canonical(word)))
                .toList();
        if (candidates.isEmpty()) return Optional.empty();
        return Optional.of(candidates.get(random.nextInt(candidates.size())));
    }

    private String canonical(String word) {
        return word.trim().toUpperCase(Locale.ROOT);
    }
}
