package com.diniz;

import javax.swing.SwingUtilities;

import com.diniz.controller.TimeAttackController;
import com.diniz.view.GameFrame;

/** Ponto de entrada da aplicação gráfica. */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame view = new GameFrame();
            new TimeAttackController(view).showMainMenu();
            view.setVisible(true);
        });
    }
}
