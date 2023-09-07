package com.example.meuif.autorizacoes;

public class AlunoAutorizacaoEntrada {
    private String nome;
    private String hora;
    private String numero;

    public AlunoAutorizacaoEntrada(String nome, String hora, String numero) {
        this.nome = nome;
        this.hora = hora;
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public String getHora() {
        return hora;
    }

    public String getNumero() {
        return numero;
    }
}
