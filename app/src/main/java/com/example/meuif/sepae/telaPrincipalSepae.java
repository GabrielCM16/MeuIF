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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.meuif.CRUD;
import com.example.meuif.MainActivity;
import com.example.meuif.R;
import com.example.meuif.sepae.chamada.TelaChamadaLideres;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class telaPrincipalSepae extends AppCompatActivity {

    private CRUD crud = new CRUD();
    private TextView textViewBemVindo;
    private Button botaosair;
    private ConstraintLayout merenda;
    private ConstraintLayout chamada;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal_sepae);

        inicializarComponentes();
        setarBemVindo();

        merenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaMerenda();
            }
        });

        chamada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaChamada();
            }
        });

        botaosair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                limparDados();

                Intent intent = new Intent(telaPrincipalSepae.this, MainActivity.class);
                // Iniciar a atividade de destino
                startActivity(intent);
                finish();
            }
        });

    }

    private void inicializarComponentes(){
        textViewBemVindo = findViewById(R.id.textViewBemVindo);
        merenda = findViewById(R.id.constraintMerenda);
        botaosair = findViewById(R.id.botaoSair);
        chamada = findViewById(R.id.constraintChamada);
    }

    public void limparDados(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Limpando todos os dados armazenados no SharedPreferences
        editor.clear();
        editor.commit();
    }

    private void telaChamada(){
        // Criar a Intent
        Intent intent = new Intent(telaPrincipalSepae.this, TelaChamadaLideres.class);
        // Iniciar a atividade de destino
        startActivity(intent);
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