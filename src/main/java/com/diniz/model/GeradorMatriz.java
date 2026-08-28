package com.diniz.model;


import java.util.List;
import java.util.Random;

public class GeradorMatriz {
    private char[][] matriz;
    private List<Palavra> palavras;
    private Random r = new Random();

    // Arrays de direção: Norte, Sul, Leste, Oeste, Nordeste, Noroeste, Sudeste, Sudoeste
    private final int[] dirX = { 0,  0,  1, -1,  1, -1,  1, -1 };
    private final int[] dirY = {-1,  1,  0,  0, -1, -1,  1,  1 };

    public GeradorMatriz(int tamanho, List<Palavra> palavras){
        this.matriz = new char[tamanho][tamanho];
        this.palavras = palavras;
    }

    public boolean gerarTabuleiro(){
        
        int x, y, incX, incY, direcao;
        for(Palavra p: palavras){

            boolean conseguiuInserir = false;
            int tentativas = 0;
            while(!conseguiuInserir && tentativas < 100){
                // sorteia uma posicao e direcao para iniciar a palavra
                x = r.nextInt(matriz.length);
                y = r.nextInt(matriz.length);
                direcao = r.nextInt(8);
                incX = dirX[direcao];
                incY = dirY[direcao];

                // verifica se pode inserir e insere
                if(temComoInseriPalavra(p.getPalavra(), x, y, incY, incX)){
                    inserirPalavra(p, x, y, incX, incY);
                    conseguiuInserir = true;
                }

                // se n tiver como inserir, pula o loop e sorteia outra posicao
                tentativas++;

            }
            if(!conseguiuInserir) return false;
        }
        // Preenche as posicoes vazias da matriz com letras aleatorios
        preencherVazios();
        return true;
    }

    private boolean temComoInseriPalavra(String texto, int x, int y, int incY, int incX){
        for(int i = 0; i < texto.length(); i++ ){
            int xAtual = x + (i * incX);
            int yAtual = y + (i * incY);

            // verifica estouro da matrizz
            if(xAtual >= matriz.length || xAtual < 0 || yAtual >= matriz.length || yAtual < 0){
                return false;
            }

            char letraPalavra = texto.charAt(i);
            char letraMatriz = matriz[yAtual][xAtual];

            // verifica se n ta vazio ou se a letra no momento ŕ diferente
            if(letraMatriz != '\u0000' && letraMatriz != letraPalavra){
                return false;
            }

        }
        return true;
    }

    private void inserirPalavra(Palavra p, int x, int y, int incX, int incY){
        for(int i = 0; i < p.getPalavra().length(); i++){
            matriz[y + (i * incY)][x +(i * incX)] = p.getPalavra().charAt(i);
            p.addCoordenada(x +(i * incX), y + (i * incY));
        }
    }

    private void preencherVazios(){
        for(int i = 0; i < matriz.length; i++){
            for(int j = 0; j < matriz.length; j++){
                if(matriz[i][j] == '\u0000'){
                    matriz[i][j] = (char) ('A' + r.nextInt(26));
                }
            }
        }
    }

    public char[][] getMatriz(){
        return matriz;
    }
}
