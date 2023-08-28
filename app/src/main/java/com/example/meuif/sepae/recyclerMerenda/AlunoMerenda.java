package com.example.meuif.sepae.recyclerMerenda;

public class AlunoMerenda {
    private String nome;
    private String matricula;
    private String hora;
    private String numero;

    public AlunoMerenda(String nome, String matricula, String hora, String numero) {
        this.nome = nome;
        this.matricula = matricula;
        this.hora = hora;
        this.numero = numero;
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

    // Métodos getter e setter para a hora
    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNumero() {
        return numero;
    }
}
