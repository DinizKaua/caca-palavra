package com.diniz.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.Test;

import com.diniz.model.Coordenada;
import com.diniz.model.Difficulty;
import com.diniz.model.Game;
import com.diniz.model.GameResult;
import com.diniz.repository.WordRepository;
import com.diniz.service.WordSelectionService;
import com.diniz.view.GameView;

public class TimeAttackControllerTest {
    @Test
    public void randomizationUpdatesOnlyTheRequestedEntry() {
        RecordingView view = openEasyWordEntry();

        view.onRandomize.accept(2, List.of("casa", "sol", "", "", ""));

        assertEquals(2, view.updatedPosition);
        assertNotNull(view.updatedWord);
        assertTrue(!"casa".equalsIgnoreCase(view.updatedWord));
        assertTrue(!"sol".equalsIgnoreCase(view.updatedWord));
    }

    @Test
    public void duplicateManualWordsDoNotStartAGame() {
        RecordingView view = openEasyWordEntry();

        view.onStart.accept(List.of("casa", "CASA", "sol", "lua", "mar"));

        assertNull(view.game);
        assertEquals("Não repita palavras na mesma partida.", view.error);
    }

    private RecordingView openEasyWordEntry() {
        RecordingView view = new RecordingView();
        TimeAttackController controller = new TimeAttackController(view, new WordSelectionService(new WordRepository()));
        controller.showMainMenu();
        view.onTimeAttack.run();
        view.onDifficulty.accept(Difficulty.EASY);
        return view;
    }

    private static class RecordingView implements GameView {
        private Runnable onTimeAttack;
        private Consumer<Difficulty> onDifficulty;
        private Consumer<List<String>> onStart;
        private BiConsumer<Integer, List<String>> onRandomize;
        private int updatedPosition = -1;
        private String updatedWord;
        private String error;
        private Game game;

        @Override public void showMainMenu(Runnable onTimeAttack, Runnable onExit) { this.onTimeAttack = onTimeAttack; }
        @Override public void showDifficultySelection(Consumer<Difficulty> onDifficulty, Runnable onBack) { this.onDifficulty = onDifficulty; }
        @Override public void showWordEntry(Difficulty difficulty, Consumer<List<String>> onStart,
                BiConsumer<Integer, List<String>> onRandomize, Runnable onBack) {
            this.onStart = onStart;
            this.onRandomize = onRandomize;
        }
        @Override public void updateWordEntry(int position, String word) { updatedPosition = position; updatedWord = word; }
        @Override public void showGame(Game game, BiConsumer<Coordenada, Coordenada> onSelection) { this.game = game; }
        @Override public void refreshGame(Game game) { }
        @Override public void showResult(GameResult result, Runnable onPlayAgain, Runnable onMainMenu) { }
        @Override public void showError(String message) { error = message; }
    }
}
