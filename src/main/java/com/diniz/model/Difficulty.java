package com.diniz.model;

/** Configurações centralizadas para o balanceamento do modo contra o tempo. */
public enum Difficulty {
    EASY("Fácil", 8, 5, 120),
    MEDIUM("Médio", 12, 8, 90),
    HARD("Difícil", 16, 12, 60);

    private final String displayName;
    private final int boardSize;
    private final int wordCount;
    private final int timeLimitSeconds;

    Difficulty(String displayName, int boardSize, int wordCount, int timeLimitSeconds) {
        this.displayName = displayName;
        this.boardSize = boardSize;
        this.wordCount = wordCount;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public String getDisplayName() { return displayName; }
    public int getBoardSize() { return boardSize; }
    public int getWordCount() { return wordCount; }
    public int getTimeLimitSeconds() { return timeLimitSeconds; }
}
