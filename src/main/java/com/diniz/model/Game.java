package com.diniz.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Estado e regras de domínio de uma partida. */
public class Game {
    private final Difficulty difficulty;
    private final char[][] board;
    private final List<Palavra> words;
    private final Set<Palavra> foundWords = new LinkedHashSet<>();
    private int remainingSeconds;
    private GameStatus status = GameStatus.IN_PROGRESS;

    public Game(Difficulty difficulty, char[][] board, List<Palavra> words) {
        this.difficulty = difficulty;
        this.board = board;
        this.words = new ArrayList<>(words);
        this.remainingSeconds = difficulty.getTimeLimitSeconds();
    }

    public Difficulty getDifficulty() { return difficulty; }
    public char[][] getBoard() { return board; }
    public List<Palavra> getWords() { return Collections.unmodifiableList(words); }
    public Set<Palavra> getFoundWords() { return Collections.unmodifiableSet(foundWords); }
    public int getRemainingSeconds() { return remainingSeconds; }
    public GameStatus getStatus() { return status; }

    /** Registra a palavra cuja sequência de coordenadas foi selecionada, inclusive no sentido inverso. */
    public Optional<Palavra> findSelection(List<Coordenada> selectedCells) {
        if (status != GameStatus.IN_PROGRESS) return Optional.empty();
        for (Palavra word : words) {
            if (!foundWords.contains(word) && matches(word.getPosicoes(), selectedCells)) {
                foundWords.add(word);
                return Optional.of(word);
            }
        }
        return Optional.empty();
    }

    public boolean allWordsFound() {
        return foundWords.size() == words.size();
    }

    public void win() {
        if (status == GameStatus.IN_PROGRESS && allWordsFound()) status = GameStatus.WON;
    }

    public void advanceTime() {
        if (status != GameStatus.IN_PROGRESS) return;
        remainingSeconds = Math.max(0, remainingSeconds - 1);
        if (remainingSeconds == 0) status = GameStatus.LOST;
    }

    private boolean matches(List<Coordenada> wordCells, List<Coordenada> selectedCells) {
        if (wordCells.size() != selectedCells.size()) return false;
        boolean forward = true;
        boolean backward = true;
        int last = wordCells.size() - 1;
        for (int i = 0; i < wordCells.size(); i++) {
            forward &= same(wordCells.get(i), selectedCells.get(i));
            backward &= same(wordCells.get(i), selectedCells.get(last - i));
        }
        return forward || backward;
    }

    private boolean same(Coordenada first, Coordenada second) {
        return first.getX() == second.getX() && first.getY() == second.getY();
    }
}
