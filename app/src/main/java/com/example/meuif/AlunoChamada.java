package com.example.meuif;

public class AlunoChamada {
    private Boolean chamadaTurno1;
    private Boolean chamadaTurno2;
    private String nome;

    public AlunoChamada(Boolean chamadaTurno1, Boolean chamadaTurno2, String nome) {
        this.chamadaTurno1 = chamadaTurno1;
        this.chamadaTurno2 = chamadaTurno2;
        this.nome = nome;
    }


    public Boolean getChamadaTurno1() {
        return chamadaTurno1;
    }

    public Boolean getChamadaTurno2() {
        return chamadaTurno2;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setChamadaTurno1(){
        if (chamadaTurno1 != null){
            this.chamadaTurno1 = !this.chamadaTurno1;
        } else {
            this.chamadaTurno1 = false ;
        }

    }
    public void setChamadaTurno2(){
        if (chamadaTurno2 != null){
            this.chamadaTurno2 = !this.chamadaTurno2;
        } else {
            this.chamadaTurno2 = false;
        }
    }
}
