package com.example.meuif;

public class Aulas {
    private String nomeAula;
    private String nomeProfessor;
    private String comecoAula;
    private String fimAula;

    public Aulas(String aula, String prof, String comeco, String fim){
        this.nomeAula = aula;
        this.nomeProfessor = prof;
        this.comecoAula = comeco;
        this.fimAula = fim;
    }

    public String getNomeAula() {
        return nomeAula;
    }

    public String getNomeProfessor() {
        return nomeProfessor;
    }

    public String getComecoAula() {
        return comecoAula;
    }

    public String getFimAula() {
        return fimAula;
    }
}
