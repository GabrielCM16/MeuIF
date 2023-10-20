package com.example.meuif.faltasPessoais;

public class ModelAcessoAluno {
    private String nome;
    private String hora;
    private String numero;
    private String flag;

    public ModelAcessoAluno(String nome, String hora, String numero, String flag) {
        this.nome = nome;
        this.hora = hora;
        this.numero = numero;
        this.flag = flag;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
