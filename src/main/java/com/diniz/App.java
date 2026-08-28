package com.diniz;

import java.util.List;

import com.diniz.model.Coordenada;
import com.diniz.model.GeradorMatriz;
import com.diniz.model.Palavra;

public class App {
    
    // Códigos ANSI para colorir o terminal
    public static final String COR_VERDE = "\u001B[32m";
    public static final String COR_RESET = "\u001B[0m"; 

    public static void main(String[] args) {
        Palavra p1 = new Palavra("Amarelo");
        Palavra p2 = new Palavra("Azul");
        Palavra p3 = new Palavra("Verde");
        Palavra p4 = new Palavra("Branco");
        List<Palavra> palavras = List.of(p1, p2, p3, p4);

        GeradorMatriz g = new GeradorMatriz(15, palavras);
        
        if(g.gerarTabuleiro()){
            char[][] matriz = g.getMatriz();
            
            boolean[][] mapaDestaque = new boolean[matriz.length][matriz.length];
            
            for (Palavra p : palavras) {
                for (Coordenada c : p.getPosicoes()) {
                    mapaDestaque[c.getY()][c.getX()] = true; 
                }
            }

            for(int i = 0; i < matriz.length; i++){
                for(int j = 0; j < matriz.length; j++){
                    
                    // Verifica se a coordenada atual [linha i][coluna j] faz parte de uma palavra
                    if (mapaDestaque[i][j]) {
                        // Imprime colorido e reseta a cor logo em seguida
                        System.out.print(COR_VERDE + matriz[i][j] + " " + COR_RESET);
                    } else {
                        char letra = matriz[i][j];
                        System.out.print((letra == '\u0000' ? "." : letra) + " ");
                    }
                }
                System.out.println();
            }
        } else {
            System.out.println("O tabuleiro é muito pequeno para essas palavras.");
        }
    }
}