package com.diniz.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.diniz.model.Difficulty;
import com.diniz.repository.WordRepository;

public class WordSelectionServiceTest {
    @Test
    public void selectsOnlyWordsCompatibleWithTheBoardAndNotAlreadyUsed() {
        WordSelectionService service = new WordSelectionService(new WordRepository(), new Random(7));
        List<String> occupied = new ArrayList<>(List.of("abacaxi", "abelha"));

        for (int i = 0; i < 5; i++) {
            String selected = service.selectRandomWord(Difficulty.EASY, occupied).orElseThrow();
            assertTrue(selected.length() <= Difficulty.EASY.getBoardSize());
            assertTrue(!occupied.stream().anyMatch(word -> word.equalsIgnoreCase(selected)));
            occupied.add(selected);
        }
    }
}
