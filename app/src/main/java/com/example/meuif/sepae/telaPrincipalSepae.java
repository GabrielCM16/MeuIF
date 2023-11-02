package com.example.meuif.sepae;

import static android.app.PendingIntent.getActivity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ViewUtils;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.meuif.CRUD;
import com.example.meuif.MainActivity;
import com.example.meuif.R;
import com.example.meuif.sepae.Portaria.ContabilizarFaltas;
import com.example.meuif.sepae.Portaria.PassePortaria;
import com.example.meuif.sepae.autorizacoes.AutorizarSaidaAntecipada;
import com.example.meuif.sepae.autorizacoes.TelaAutorizarEntradaAula;
import com.example.meuif.sepae.chamada.TelaChamadaLideres;
import com.example.meuif.sepae.gestao.GestaoUsuarios;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class telaPrincipalSepae extends AppCompatActivity {
    private TextView textViewBemVindo;
    private ConstraintLayout botaosair;
    private ConstraintLayout cardapio;
    private ConstraintLayout constraintAutorizarSaida;
    private ProgressBar loadingSepae;
    private ConstraintLayout autorizarAula;
    private ConstraintLayout merenda;
    private ConstraintLayout carteirinha;
    private ConstraintLayout chamada;
    private ConstraintLayout constraintGerenciarUsers;

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

        carteirinha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaPortaria();
            }
        });

        cardapio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaCardapio();
            }
        });

        autorizarAula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaAutorizarAula();
            }
        });
        constraintAutorizarSaida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaAutorizarSaida();
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
        constraintGerenciarUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(telaPrincipalSepae.this, GestaoUsuarios.class);
                // Iniciar a atividade de destino
                startActivity(intent);
            }
        });

    }

    private void inicializarComponentes(){
        textViewBemVindo = findViewById(R.id.textViewBemVindo);
        merenda = findViewById(R.id.constraintMerenda);
        carteirinha = findViewById(R.id.constraintPasseCarteirinha);
        botaosair = findViewById(R.id.botaoSair);
        cardapio = findViewById(R.id.constraintCardapio);
        loadingSepae = findViewById(R.id.loadingSepae);
        autorizarAula = findViewById(R.id.constraintAutorizarAula);
        constraintAutorizarSaida = findViewById(R.id.constraintAutorizarSaida);
        constraintGerenciarUsers = findViewById(R.id.constraintGerenciarUsers);
        chamada = findViewById(R.id.constraintChamada);
        loadingSepae.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    }

    private void telaAutorizarSaida(){
        // Criar a Intent
        Intent intent = new Intent(telaPrincipalSepae.this, AutorizarSaidaAntecipada.class);
        // Iniciar a atividade de destino
        startActivity(intent);
    }

    private void telaAutorizarAula(){
// Criar a Intent
        Intent intent = new Intent(telaPrincipalSepae.this, TelaAutorizarEntradaAula.class);
        // Iniciar a atividade de destino
        startActivity(intent);
    }

    private void telaCardapio(){
        // Criar a Intent
        Intent intent = new Intent(telaPrincipalSepae.this, telaCardapio.class);
        // Iniciar a atividade de destino
        startActivity(intent);
    }

    private void telaPortaria(){
        // Criar a Intent
        Intent intent = new Intent(telaPrincipalSepae.this, PassePortaria.class);
        // Iniciar a atividade de destino
        startActivity(intent);
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

    private interface Callback {
        void onComplete();
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