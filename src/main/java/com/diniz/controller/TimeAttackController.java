package com.diniz.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import javax.swing.Timer;

import com.diniz.model.Coordenada;
import com.diniz.model.Difficulty;
import com.diniz.model.Game;
import com.diniz.model.GameResult;
import com.diniz.model.GameStatus;
import com.diniz.model.GeradorMatriz;
import com.diniz.model.Palavra;
import com.diniz.repository.WordRepository;
import com.diniz.service.WordSelectionService;
import com.diniz.view.GameView;

/** Controller do único modo disponível. Outros modos podem ganhar controllers próprios. */
public class TimeAttackController {
    private final GameView view;
    private final WordSelectionService wordSelectionService;
    private Timer timer;
    private Game game;
    private Difficulty selectedDifficulty;

    public TimeAttackController(GameView view) {
        this(view, new WordSelectionService(new WordRepository()));
    }

    TimeAttackController(GameView view, WordSelectionService wordSelectionService) {
        this.view = view;
        this.wordSelectionService = wordSelectionService;
    }

    public void showMainMenu() {
        stopTimer();
        view.showMainMenu(this::showDifficultySelection, () -> System.exit(0));
    }

    private void showDifficultySelection() {
        view.showDifficultySelection(this::showWordEntry, this::showMainMenu);
    }

    private void showWordEntry(Difficulty difficulty) {
        selectedDifficulty = difficulty;
        view.showWordEntry(difficulty, this::startGame, this::randomizeWord, this::showDifficultySelection);
    }

    private void randomizeWord(int position, List<String> entries) {
        wordSelectionService.selectRandomWord(selectedDifficulty, entries)
                .ifPresentOrElse(word -> view.updateWordEntry(position, word),
                        () -> view.showError("Não há outra palavra compatível disponível."));
    }

    private void startGame(List<String> entries) {
        if (entries.size() != selectedDifficulty.getWordCount()) {
            view.showError("Informe exatamente " + selectedDifficulty.getWordCount() + " palavras.");
            return;
        }
        Set<String> uniqueWords = new HashSet<>();
        for (String entry : entries) {
            if (entry == null || entry.trim().isEmpty()) {
                view.showError("Todas as palavras devem ser preenchidas.");
                return;
            }
            if (entry.trim().length() > selectedDifficulty.getBoardSize()) {
                view.showError("Cada palavra deve ter no máximo " + selectedDifficulty.getBoardSize() + " letras.");
                return;
            }
            if (!uniqueWords.add(canonical(entry))) {
                view.showError("Não repita palavras na mesma partida.");
                return;
            }
        }

        // O GeradorMatriz existente continua sendo a única implementação de geração.
        for (int attempt = 0; attempt < 5; attempt++) {
            try {
                List<Palavra> words = toWords(entries);
                GeradorMatriz generator = new GeradorMatriz(selectedDifficulty.getBoardSize(), words);
                if (generator.gerarTabuleiro()) {
                    game = new Game(selectedDifficulty, generator.getMatriz(), words);
                    view.showGame(game, this::handleSelection);
                    startTimer();
                    return;
                }
            } catch (IllegalArgumentException exception) {
                view.showError("Use apenas letras nas palavras.");
                return;
            }
        }
        view.showError("Não foi possível posicionar essas palavras. Tente outras palavras.");
    }

    private List<Palavra> toWords(List<String> entries) {
        List<Palavra> words = new ArrayList<>();
        for (String entry : entries) words.add(new Palavra(entry.trim()));
        return words;
    }

    private String canonical(String word) {
        return word.trim().toUpperCase(Locale.ROOT);
    }

    private void startTimer() {
        stopTimer();
        timer = new Timer(1000, event -> {
            game.advanceTime();
            if (game.getStatus() == GameStatus.LOST) finishGame();
            else view.refreshGame(game);
        });
        timer.start();
    }

    private void handleSelection(Coordenada start, Coordenada end) {
        if (game == null || game.getStatus() != GameStatus.IN_PROGRESS) return;
        Optional<Palavra> found = game.findSelection(cellsBetween(start, end));
        if (found.isPresent() && game.allWordsFound()) {
            game.win();
            finishGame();
        } else {
            view.refreshGame(game);
        }
    }

    /** Só aceita linhas, colunas e diagonais, exatamente as oito direções do gerador. */
    private List<Coordenada> cellsBetween(Coordenada start, Coordenada end) {
        int deltaX = end.getX() - start.getX();
        int deltaY = end.getY() - start.getY();
        int absX = Math.abs(deltaX);
        int absY = Math.abs(deltaY);
        if (!(deltaX == 0 || deltaY == 0 || absX == absY)) return List.of();
        int stepX = Integer.compare(deltaX, 0);
        int stepY = Integer.compare(deltaY, 0);
        int length = Math.max(absX, absY);
        List<Coordenada> result = new ArrayList<>();
        for (int i = 0; i <= length; i++) result.add(new Coordenada(start.getX() + i * stepX, start.getY() + i * stepY));
        return result;
    }

    private void finishGame() {
        stopTimer();
        GameResult result = GameResult.from(game);
        view.showResult(result, () -> showWordEntry(result.getDifficulty()), this::showMainMenu);
    }

    private void stopTimer() {
        if (timer != null) timer.stop();
    }
}
