package com.example.meuif.autorizacoes;

public class AlunoAutorizacaoEntrada {
    private String nome;
    private String hora;
    private String numero;
    private String motivo;

    public AlunoAutorizacaoEntrada(String nome, String hora, String numero, String motivo) {
        this.nome = nome;
        this.hora = hora;
        this.numero = numero;
        this.motivo = motivo;
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

    public String getMotivo() {
        return motivo;
    }
}
