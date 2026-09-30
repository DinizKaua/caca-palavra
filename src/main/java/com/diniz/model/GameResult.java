package com.diniz.model;

/** Resultado imutável de uma partida encerrada e regra centralizada de estrelas. */
public class GameResult {
    private final boolean victory;
    private final int stars;
    private final int remainingSeconds;
    private final Difficulty difficulty;

    private GameResult(boolean victory, int stars, int remainingSeconds, Difficulty difficulty) {
        this.victory = victory;
        this.stars = stars;
        this.remainingSeconds = remainingSeconds;
        this.difficulty = difficulty;
    }

    public static GameResult from(Game game) {
        boolean victory = game.getStatus() == GameStatus.WON;
        int stars = 0;
        if (victory) {
            // Uma vitória sempre vale uma estrela; três requerem ao menos metade do tempo inicial.
            stars = game.getRemainingSeconds() * 2 >= game.getDifficulty().getTimeLimitSeconds() ? 3 : 1;
        }
        return new GameResult(victory, stars, game.getRemainingSeconds(), game.getDifficulty());
    }

    public boolean isVictory() { return victory; }
    public int getStars() { return stars; }
    public int getRemainingSeconds() { return remainingSeconds; }
    public Difficulty getDifficulty() { return difficulty; }
}
