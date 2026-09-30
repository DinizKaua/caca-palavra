package com.diniz.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class GameTest {
    @Test
    public void difficultyKeepsAllBalanceSettingsCentralized() {
        assertEquals(8, Difficulty.EASY.getBoardSize());
        assertEquals(5, Difficulty.EASY.getWordCount());
        assertEquals(120, Difficulty.EASY.getTimeLimitSeconds());
        assertEquals(12, Difficulty.MEDIUM.getBoardSize());
        assertEquals(8, Difficulty.MEDIUM.getWordCount());
        assertEquals(90, Difficulty.MEDIUM.getTimeLimitSeconds());
        assertEquals(16, Difficulty.HARD.getBoardSize());
        assertEquals(12, Difficulty.HARD.getWordCount());
        assertEquals(60, Difficulty.HARD.getTimeLimitSeconds());
    }

    @Test
    public void acceptsAWordInBothDirectionsAndCalculatesThreeStars() {
        Palavra java = new Palavra("JAVA");
        java.addCoordenada(0, 0);
        java.addCoordenada(1, 0);
        java.addCoordenada(2, 0);
        java.addCoordenada(3, 0);
        Game game = new Game(Difficulty.EASY, new char[8][8], List.of(java));

        assertTrue(game.findSelection(List.of(new Coordenada(3, 0), new Coordenada(2, 0), new Coordenada(1, 0), new Coordenada(0, 0))).isPresent());
        game.win();

        assertTrue(game.allWordsFound());
        assertEquals(GameStatus.WON, game.getStatus());
        assertEquals(3, GameResult.from(game).getStars());
    }

    @Test
    public void timeExpirationEndsTheGameWithNoStars() {
        Game game = new Game(Difficulty.HARD, new char[16][16], List.of());
        for (int i = 0; i < Difficulty.HARD.getTimeLimitSeconds(); i++) game.advanceTime();

        assertEquals(GameStatus.LOST, game.getStatus());
        assertEquals(0, GameResult.from(game).getStars());
        assertFalse(GameResult.from(game).isVictory());
    }

    @Test
    public void aVictoryWithLessThanHalfTheTimeGetsOneStar() {
        Palavra word = new Palavra("SOL");
        word.addCoordenada(0, 0);
        word.addCoordenada(1, 0);
        word.addCoordenada(2, 0);
        Game game = new Game(Difficulty.HARD, new char[16][16], List.of(word));
        for (int i = 0; i < 31; i++) game.advanceTime();
        game.findSelection(word.getPosicoes());
        game.win();

        assertEquals(1, GameResult.from(game).getStars());
    }

    @Test
    public void existingGeneratorCreatesConfiguredBoardAndRegistersPositions() {
        List<Palavra> words = List.of(new Palavra("JAVA"), new Palavra("CODIGO"));
        GeradorMatriz generator = new GeradorMatriz(8, words);
        assertTrue(generator.gerarTabuleiro());
        assertEquals(8, generator.getMatriz().length);
        assertEquals(4, words.get(0).getPosicoes().size());
        assertEquals(6, words.get(1).getPosicoes().size());
    }
}
