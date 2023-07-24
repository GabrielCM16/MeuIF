package com.example.meuif;

import android.widget.ImageView;

public class nomes {
    private String nome;
    private Boolean presencaOUausencia;

    public nomes(String strNome) {

        this.nome = strNome;
        this.presencaOUausencia = false;
    }


    public String getNome() {
        return nome;
    }

    public Boolean getPresencaOUausencia(){
        return presencaOUausencia;
    }

    public void setPresencaOUausencia(Boolean estado){
        this.presencaOUausencia = estado;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
