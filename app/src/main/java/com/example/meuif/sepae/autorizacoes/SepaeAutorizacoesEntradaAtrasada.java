package com.example.meuif.sepae.autorizacoes;

public class SepaeAutorizacoesEntradaAtrasada {
    private String nome;
    private String hora;
    private String numero;
    private String nomeSEPAE;
    private String turma;
    private String motivo;

    public SepaeAutorizacoesEntradaAtrasada(String nome, String hora, String numero, String nomeSEPAE, String turma, String motivo) {
        this.nome = nome;
        this.hora = hora;
        this.numero = numero;
        this.turma = turma;
        this.nomeSEPAE = nomeSEPAE;
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

    public String getNomeSEPAE() {
        return nomeSEPAE;
    }

    public String getTurma() {
        return turma;
    }

    public String getMotivo() {
        return motivo;
    }
}
