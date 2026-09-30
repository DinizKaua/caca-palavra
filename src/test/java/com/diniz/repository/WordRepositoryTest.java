package com.diniz.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import org.junit.Test;

public class WordRepositoryTest {
    @Test
    public void loadsClasspathResourceUsingUtf8() {
        WordRepository repository = new WordRepository();

        assertFalse(repository.getWords().isEmpty());
        assertTrue(repository.getWords().contains("água"));
        assertTrue(repository.getWords().contains("coração"));
    }

    @Test
    public void ignoresEmptyLinesAndTrimsWords() {
        WordRepository repository = new WordRepository(new StringReader("\n árvore \n  \nágua\n"));

        assertEquals(2, repository.getWords().size());
        assertEquals("árvore", repository.getWords().get(0));
        assertEquals("água", repository.getWords().get(1));
    }
}
