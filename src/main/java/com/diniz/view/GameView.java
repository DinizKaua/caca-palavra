package com.diniz.view;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.diniz.model.Coordenada;
import com.diniz.model.Difficulty;
import com.diniz.model.Game;
import com.diniz.model.GameResult;

public interface GameView {
    void showMainMenu(Runnable onTimeAttack, Runnable onExit);
    void showDifficultySelection(Consumer<Difficulty> onDifficulty, Runnable onBack);
    void showWordEntry(Difficulty difficulty, Consumer<List<String>> onStart,
            BiConsumer<Integer, List<String>> onRandomize, Runnable onBack);
    void updateWordEntry(int position, String word);
    void showGame(Game game, BiConsumer<Coordenada, Coordenada> onSelection);
    void refreshGame(Game game);
    void showResult(GameResult result, Runnable onPlayAgain, Runnable onMainMenu);
    void showError(String message);
}
