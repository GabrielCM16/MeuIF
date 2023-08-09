package com.example.meuif.sepae;

import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meuif.CRUD;
import com.example.meuif.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class telaPrincipalSepae extends AppCompatActivity {

    private CRUD crud = new CRUD();
    private TextView textViewBemVindo;
    private Button botao;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal_sepae);

        inicializarComponentes();
        setarBemVindo();

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaMerenda();
            }
        });

    }

    private void inicializarComponentes(){
        textViewBemVindo = findViewById(R.id.textViewBemVindo);
        botao = findViewById(R.id.botaoCamera);
    }

    private void telaMerenda(){
        // Criar a Intent
        Intent intent = new Intent(telaPrincipalSepae.this, telaMerendaEscolar.class);
        // Iniciar a atividade de destino
        startActivity(intent);
    }

    private void setarBemVindo(){
        String hora = getDateTime();
        int h = Integer.parseInt(hora);
        String ola;
        if (h > 19 || h <= 5){
            ola = "Boa noite ";
        } else if (h < 12 && h >= 6){
            ola = "Bom dia ";
        } else {
            ola = "Boa tarde ";
        }
        String nomeCompleto = recuperarDados("nome");
        String[] primeiroNome = nomeCompleto.split(" ");
        textViewBemVindo.setText(ola + primeiroNome[0] + "!");

    }



    public String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH");
        Date date = new Date();
        return dateFormat.format(date);
    }
}