package com.example.meuif.sepae;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meuif.MostrarAtualizacoes;
import com.example.meuif.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class telaCardapio extends AppCompatActivity {
    private TextView textViewCardapioAtualSEPAE;
    private EditText entradaItemCardapio;
    private ConstraintLayout constraintSalvar;
    private ProgressBar progressBarMerendaSepae;
    private ConstraintLayout constraintAdicionar;
    private ConstraintLayout constraintVerCardapioSEPAE;
    private ConstraintLayout constraintLimpar;
    private String merendaOfc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cardapio);

        inicializarComponentes();
        setarCardapio();

        constraintLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogCerteza();
            }
        });
        constraintAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarItem();
            }
        });
        constraintSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarMerenda();
            }
        });
        constraintVerCardapioSEPAE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCardapioAluno();
            }
        });
    }

    private void inicializarComponentes() {
        textViewCardapioAtualSEPAE = findViewById(R.id.textViewCardapioAtualSEPAE);
        entradaItemCardapio = findViewById(R.id.entradaItemCardapio);
        constraintSalvar = findViewById(R.id.constraintSalvar);
        constraintAdicionar = findViewById(R.id.constraintAdicionar);
        constraintVerCardapioSEPAE = findViewById(R.id.constraintVerCardapioSEPAE);
        constraintLimpar = findViewById(R.id.constraintLimpar);
        progressBarMerendaSepae = findViewById(R.id.progressBarMerendaSepae);
        progressBarMerendaSepae.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);


        ActionBar actionBar = getSupportActionBar();
        setTitle("Chamada Líderes");
        // Adiciona um ícone de ação à direita
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24); // Define o ícone de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilita o botão de navegação
    }

    private void mostrarCardapioAluno(){
        MostrarAtualizacoes atualizacoes = new MostrarAtualizacoes();
        String dia = getDayAndMonth();
        atualizacoes.abrirDialogCardapio(this, dia);
    }

    private String getDayAndMonth() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3")); // Defina o fuso horário desejado
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void salvarMerenda(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        //configurar titulo e mensagem
        dialog.setTitle("Salvar a Merenda?" );
        dialog.setMessage("Não será possivel recuperar a lista caso for Salva!");

        //configurar cancelamento do alert dialog
        dialog.setCancelable(false);

        //configurar icone
        //dialog.setIcon(android.R.drawable.ic_btn_speak_now);

        //configurar açoes para sim e nâo
        dialog.setPositiveButton("Não Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Merenda não foi Salva" , Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setNegativeButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBarMerendaSepae.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Merenda Esta sendo Salva!" , Toast.LENGTH_SHORT).show();
                MostrarAtualizacoes atualizacoes = new MostrarAtualizacoes();
                atualizacoes.salvarNovaMerenda(merendaOfc, new MostrarAtualizacoes.Callback() {
                    @Override
                    public void onComplete() {
                        progressBarMerendaSepae.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        dialog.create();
        dialog.show();

    }

    private void adicionarItem(){
        StringBuilder builder = new StringBuilder();
        builder.append(merendaOfc);
        String item = entradaItemCardapio.getText().toString();
        builder.append(item + "\n");
        merendaOfc = builder.toString();
        textViewCardapioAtualSEPAE.setText(merendaOfc);
    }

    private void mostrarDialogCerteza(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        //configurar titulo e mensagem
        dialog.setTitle("Resetar a Merenda?" );
        dialog.setMessage("Não será possivel recuperar a lista caso for resetada!");

        //configurar cancelamento do alert dialog
        dialog.setCancelable(false);

        //configurar icone
        //dialog.setIcon(android.R.drawable.ic_btn_speak_now);

        //configurar açoes para sim e nâo
        dialog.setPositiveButton("Não resetar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Merenda não resetada!" , Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setNegativeButton("Resetar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Merenda foi resetada!" , Toast.LENGTH_SHORT).show();
                merendaOfc = "";
                textViewCardapioAtualSEPAE.setText(merendaOfc);
            }
        });

        dialog.create();
        dialog.show();
    }

    private void setarCardapio(){
        MostrarAtualizacoes atualizacoes = new MostrarAtualizacoes();
        atualizacoes.getCardapioSEPAE(new MostrarAtualizacoes.Callback() {
            @Override
            public void onComplete() {
                merendaOfc = atualizacoes.returnStringCardapioSEPAE();
                textViewCardapioAtualSEPAE.setText(merendaOfc);
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Verifica se o item clicado é o botão do ActionBar
        if (item.getItemId() == android.R.id.home) {
            // Chame o método que você deseja executar quando o ActionBar for clicado
            telaVoltar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void telaVoltar(){
        // Criar a Intent
        Intent intent = new Intent(this, telaPrincipalSepae.class);
        // Iniciar a atividade de destino
        startActivity(intent);
    }
}