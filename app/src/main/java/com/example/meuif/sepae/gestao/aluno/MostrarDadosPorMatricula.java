package com.example.meuif.sepae.gestao.aluno;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.meuif.R;

public class MostrarDadosPorMatricula extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_dados_por_matricula);

        carregarComponents();

    }

    private void carregarComponents(){
        // Receber a Intent
        Intent intent = getIntent();

        // Verificar se a Intent contém a chave
        if (intent.hasExtra("matricula")) {
            // Obter a string da Intent
            String matricula = intent.getStringExtra("matricula");
        }
    }
}