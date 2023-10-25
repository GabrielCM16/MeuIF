package com.example.meuif.sepae.Portaria;

public class ModelFiltroPortaria {
    private String nome;
    private String matricula;
    private Boolean manha;
    private Boolean tarde;
    private Boolean noite;
    private String turma;

    public ModelFiltroPortaria(String nome, String matricula, Boolean manha, Boolean tarde, Boolean noite, String turma) {
        this.nome = nome;
        this.matricula = matricula;
        this.manha = manha;
        this.tarde = tarde;
        this.noite = noite;
        this.turma = turma;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Boolean getManha() {
        return manha;
    }

    public void setManha(Boolean manha) {
        this.manha = manha;
    }

    public Boolean getTarde() {
        return tarde;
    }

    public void setTarde(Boolean tarde) {
        this.tarde = tarde;
    }

    public Boolean getNoite() {
        return noite;
    }

    public void setNoite(Boolean noite) {
        this.noite = noite;
    }

    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }
}
