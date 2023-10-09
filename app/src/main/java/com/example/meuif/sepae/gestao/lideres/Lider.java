package com.example.meuif.sepae.gestao.lideres;

public class Lider {
    private String nome;
    private String matricula;
    private String cargo;

    public Lider(String nome, String matricula, String cargo) {
        this.nome = nome;
        this.matricula = matricula;
        this.cargo = cargo;
    }

    // Métodos getter e setter para o nome
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // Métodos getter e setter para a matrícula
    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }


    public String getCargo() {
        return cargo;
    }
}
