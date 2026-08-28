package com.diniz.model;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Palavra {
    private String palavra;
    private List<Coordenada> posicoes;

    public Palavra(String str){
        if(!palavraValida(str)){
            throw new IllegalArgumentException();
        }

        this.palavra = str.toUpperCase();
        this.posicoes = new ArrayList<>();
    }

    private boolean palavraValida(String str){
        if(str == null){
            return false;
        }

        char c;
        for(int i = 0; i < str.length(); i++){
            c = str.charAt(i);
            if(!Character.isLetter(c)){
                return false;
            } 
        }
        return true;
    }

    private String removerAcentos(String str) {
        if(str == null){
            return null;
        }

        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public void addCoordenada(int x, int y){
        this.posicoes.add(new Coordenada(x, y));
    }

    public String getPalavra(){
        return this.palavra;
    }

    public List<Coordenada> getPosicoes(){
        return posicoes;
    }
}
