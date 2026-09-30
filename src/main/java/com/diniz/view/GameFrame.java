package com.diniz.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import com.diniz.model.Coordenada;
import com.diniz.model.Difficulty;
import com.diniz.model.Game;
import com.diniz.model.GameResult;
import com.diniz.model.Palavra;

/** Implementação Swing da view. Ela somente renderiza o estado recebido do controller. */
public class GameFrame extends JFrame implements GameView {
    private static final Color FOUND_COLOR = new Color(181, 229, 178);
    private static final Color SELECTED_COLOR = new Color(255, 232, 158);
    private static final Border CELL_BORDER = BorderFactory.createLineBorder(new Color(180, 180, 180));
    private JLabel timerLabel;
    private JPanel boardPanel;
    private JPanel wordsPanel;
    private Coordenada selectionStart;
    private BiConsumer<Coordenada, Coordenada> selectionListener;
    private Game displayedGame;
    private JTextField[] wordEntryFields;

    public GameFrame() {
        super("Caça-Palavras");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(620, 650));
        setLocationByPlatform(true);
    }

    @Override
    public void showMainMenu(Runnable onTimeAttack, Runnable onExit) {
        JPanel panel = verticalPanel();
        panel.add(title("CAÇA-PALAVRAS", 30));
        panel.add(Box.createVerticalStrut(45));
        panel.add(actionButton("CONTRA O TEMPO", event -> onTimeAttack.run()));
        panel.add(Box.createVerticalStrut(14));
        panel.add(actionButton("SAIR", event -> onExit.run()));
        showPanel(panel);
    }

    @Override
    public void showDifficultySelection(Consumer<Difficulty> onDifficulty, Runnable onBack) {
        JPanel panel = verticalPanel();
        panel.add(title("CONTRA O TEMPO", 27));
        panel.add(Box.createVerticalStrut(12));
        panel.add(title("Escolha a dificuldade", 17));
        panel.add(Box.createVerticalStrut(28));
        panel.add(actionButton("FÁCIL", event -> onDifficulty.accept(Difficulty.EASY)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(actionButton("MÉDIO", event -> onDifficulty.accept(Difficulty.MEDIUM)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(actionButton("DIFÍCIL", event -> onDifficulty.accept(Difficulty.HARD)));
        panel.add(Box.createVerticalStrut(22));
        panel.add(actionButton("VOLTAR", event -> onBack.run()));
        showPanel(panel);
    }

    @Override
    public void showWordEntry(Difficulty difficulty, Consumer<List<String>> onStart,
            BiConsumer<Integer, List<String>> onRandomize, Runnable onBack) {
        JPanel panel = verticalPanel();
        panel.add(title("ESCOLHA AS PALAVRAS", 26));
        panel.add(Box.createVerticalStrut(8));
        panel.add(title(difficulty.getDisplayName() + " — digite ou sorteie " + difficulty.getWordCount() + " palavras", 17));
        panel.add(Box.createVerticalStrut(18));

        JPanel fields = new JPanel(new GridLayout(difficulty.getWordCount(), 3, 8, 8));
        fields.setOpaque(false);
        wordEntryFields = new JTextField[difficulty.getWordCount()];
        for (int i = 0; i < wordEntryFields.length; i++) {
            fields.add(new JLabel((i + 1) + ".", SwingConstants.RIGHT));
            wordEntryFields[i] = new JTextField(20);
            fields.add(wordEntryFields[i]);
            final int position = i;
            JButton randomButton = new JButton("🎲");
            randomButton.setToolTipText("Sortear palavra");
            randomButton.addActionListener(event -> onRandomize.accept(position, currentWordEntries()));
            fields.add(randomButton);
        }
        panel.add(fields);
        panel.add(Box.createVerticalStrut(20));
        panel.add(actionButton("COMEÇAR", event -> {
            onStart.accept(currentWordEntries());
        }));
        panel.add(Box.createVerticalStrut(10));
        panel.add(actionButton("VOLTAR", event -> onBack.run()));
        showPanel(panel);
    }

    @Override
    public void updateWordEntry(int position, String word) {
        if (wordEntryFields != null && position >= 0 && position < wordEntryFields.length) {
            wordEntryFields[position].setText(word);
        }
    }

    @Override
    public void showGame(Game game, BiConsumer<Coordenada, Coordenada> onSelection) {
        selectionListener = onSelection;
        selectionStart = null;
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JPanel header = new JPanel(new BorderLayout());
        JLabel mode = new JLabel("Contra o Tempo — " + game.getDifficulty().getDisplayName());
        mode.setFont(mode.getFont().deriveFont(Font.BOLD, 18f));
        timerLabel = new JLabel();
        timerLabel.setFont(timerLabel.getFont().deriveFont(Font.BOLD, 18f));
        header.add(mode, BorderLayout.WEST);
        header.add(timerLabel, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        boardPanel = new JPanel();
        panel.add(boardPanel, BorderLayout.CENTER);
        wordsPanel = new JPanel();
        wordsPanel.setLayout(new BoxLayout(wordsPanel, BoxLayout.Y_AXIS));
        JScrollPane wordScroll = new JScrollPane(wordsPanel);
        wordScroll.setPreferredSize(new Dimension(180, 0));
        wordScroll.setBorder(BorderFactory.createTitledBorder("Palavras"));
        panel.add(wordScroll, BorderLayout.EAST);
        setContentPane(panel);
        refreshGame(game);
        finishPanelChange();
    }

    @Override
    public void refreshGame(Game game) {
        if (timerLabel == null || boardPanel == null) return;
        displayedGame = game;
        timerLabel.setText("Tempo: " + formatTime(game.getRemainingSeconds()));
        boardPanel.removeAll();
        int size = game.getBoard().length;
        boardPanel.setLayout(new GridLayout(size, size, 2, 2));
        Set<String> foundCells = foundCells(game);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Coordenada coordinate = new Coordenada(x, y);
                JButton cell = new JButton(String.valueOf(game.getBoard()[y][x]));
                cell.setFont(cell.getFont().deriveFont(Font.BOLD));
                cell.setMargin(new java.awt.Insets(1, 1, 1, 1));
                cell.setBorder(CELL_BORDER);
                if (foundCells.contains(key(coordinate))) cell.setBackground(FOUND_COLOR);
                else if (selectionStart != null && same(selectionStart, coordinate)) cell.setBackground(SELECTED_COLOR);
                else cell.setBackground(Color.WHITE);
                cell.addActionListener(event -> selectCell(coordinate));
                boardPanel.add(cell);
            }
        }
        wordsPanel.removeAll();
        for (Palavra word : game.getWords()) {
            boolean found = game.getFoundWords().contains(word);
            JLabel item = new JLabel((found ? "✓ " : "   ") + word.getPalavra());
            item.setFont(item.getFont().deriveFont(found ? Font.BOLD : Font.PLAIN));
            item.setForeground(found ? new Color(33, 120, 49) : Color.DARK_GRAY);
            wordsPanel.add(item);
            wordsPanel.add(Box.createVerticalStrut(7));
        }
        boardPanel.revalidate();
        boardPanel.repaint();
        wordsPanel.revalidate();
        wordsPanel.repaint();
    }

    private void selectCell(Coordenada coordinate) {
        if (selectionStart == null) {
            selectionStart = coordinate;
            refreshGame(displayedGame);
            return;
        }
        Coordenada start = selectionStart;
        selectionStart = null;
        selectionListener.accept(start, coordinate);
    }

    @Override
    public void showResult(GameResult result, Runnable onPlayAgain, Runnable onMainMenu) {
        JPanel panel = verticalPanel();
        panel.add(title(result.isVictory() ? "PARABÉNS!" : "TEMPO ESGOTADO!", 29));
        panel.add(Box.createVerticalStrut(18));
        panel.add(title(result.isVictory() ? "Você encontrou todas as palavras!" : "Você não encontrou todas as palavras.", 17));
        panel.add(Box.createVerticalStrut(20));
        String stars = result.getStars() == 3 ? "★★★" : result.getStars() == 1 ? "★☆☆" : "☆☆☆";
        panel.add(title(stars, 34));
        panel.add(Box.createVerticalStrut(16));
        if (result.isVictory()) panel.add(title("Tempo restante: " + formatTime(result.getRemainingSeconds()), 17));
        else panel.add(title("Tempo esgotado!", 17));
        panel.add(Box.createVerticalStrut(28));
        panel.add(actionButton(result.isVictory() ? "JOGAR NOVAMENTE" : "TENTAR NOVAMENTE", event -> onPlayAgain.run()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(actionButton("MENU PRINCIPAL", event -> onMainMenu.run()));
        showPanel(panel);
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Dados inválidos", JOptionPane.WARNING_MESSAGE);
    }

    private JPanel verticalPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(55, 100, 45, 100));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private JLabel title(String text, int size) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, (float) size));
        label.setAlignmentX(CENTER_ALIGNMENT);
        return label;
    }

    private JButton actionButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(240, 40));
        button.addActionListener(listener);
        return button;
    }

    private void showPanel(JPanel panel) {
        setContentPane(panel);
        finishPanelChange();
    }

    private List<String> currentWordEntries() {
        List<String> words = new java.util.ArrayList<>();
        if (wordEntryFields != null) {
            for (JTextField entry : wordEntryFields) words.add(entry.getText());
        }
        return words;
    }

    private void finishPanelChange() {
        pack();
        setSize(Math.max(getWidth(), 620), Math.max(getHeight(), 650));
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private Set<String> foundCells(Game game) {
        Set<String> result = new HashSet<>();
        for (Palavra word : game.getFoundWords()) for (Coordenada cell : word.getPosicoes()) result.add(key(cell));
        return result;
    }

    private String key(Coordenada coordinate) { return coordinate.getX() + ":" + coordinate.getY(); }
    private boolean same(Coordenada first, Coordenada second) { return first.getX() == second.getX() && first.getY() == second.getY(); }
    private String formatTime(int seconds) { return String.format("%02d:%02d", seconds / 60, seconds % 60); }
}
