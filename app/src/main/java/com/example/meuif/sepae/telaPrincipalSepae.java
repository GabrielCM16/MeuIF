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
import com.example.meuif.sepae.chamada.TelaChamadaLideres;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class telaPrincipalSepae extends AppCompatActivity {

    private CRUD crud = new CRUD();
    private TextView textViewBemVindo;
    private Button botaosair;
    private ProgressBar loadingSepae;
    private ConstraintLayout merenda;
    private ConstraintLayout carteirinha;
    private ConstraintLayout chamada;
    private ConstraintLayout faltas;

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

        faltas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contabilizarFaltas();
            }
        });

        carteirinha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaPortaria();
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
        carteirinha = findViewById(R.id.constraintPasseCarteirinha);
        botaosair = findViewById(R.id.botaoSair);
        loadingSepae = findViewById(R.id.loadingSepae);
        faltas = findViewById(R.id.constraintFaltas);
        chamada = findViewById(R.id.constraintChamada);
        loadingSepae.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    }

    private void telaPortaria(){
        // Criar a Intent
        Intent intent = new Intent(telaPrincipalSepae.this, PassePortaria.class);
        // Iniciar a atividade de destino
        startActivity(intent);
    }

    private void contabilizarFaltas(){
        loadingSepae.setVisibility(View.VISIBLE);
        ContabilizarFaltas contabil = new ContabilizarFaltas(getApplicationContext(), "04092023", diaAtualSemAcentos());
        contabil.contarFaltas(new ContabilizarFaltas.Callback() {
            @Override
            public void onComplete() {
                loadingSepae.setVisibility(View.INVISIBLE);
            }
        });

    }

        public String diaAtualSemAcentos() {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-3"));
            int diaSemana = calendar.get(Calendar.DAY_OF_WEEK);

            String nomeDiaSemana = "";

            switch (diaSemana) {
                case Calendar.SUNDAY:
                    nomeDiaSemana = "Domingo";
                    break;
                case Calendar.MONDAY:
                    nomeDiaSemana = "Segunda-feira";
                    break;
                case Calendar.TUESDAY:
                    nomeDiaSemana = "Terca-feira";
                    break;
                case Calendar.WEDNESDAY:
                    nomeDiaSemana = "Quarta-feira";
                    break;
                case Calendar.THURSDAY:
                    nomeDiaSemana = "Quinta-feira";
                    break;
                case Calendar.FRIDAY:
                    nomeDiaSemana = "Sexta-feira";
                    break;
                case Calendar.SATURDAY:
                    nomeDiaSemana = "Sabado";
                    break;
            }

            return nomeDiaSemana;
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