package com.example.meuif;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.meuif.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class tela_chamada_dia extends AppCompatActivity {
    private Button botaoDias;
    private TextView saidaData;
    private String diaAtual;
    private RecyclerView recycler;
    private nomesAdapter adapter;
    private ArrayList<nomes> itens;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_chamada_dia);

        botaoDias = findViewById(R.id.botaoDias);
        botaoDias.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        botaoDias.setPadding(30, botaoDias.getPaddingTop(), 15, botaoDias.getPaddingBottom());
        saidaData = findViewById(R.id.saidaData);

        diaAtual = diaAtual();
        saidaData.setText(diaAtual);

        ActionBar actionBar = getSupportActionBar();

        setTitle("Chamada Diaria de Classe");

        // Obtém uma referência para a ActionBar ou Toolbar
         // ou Toolbar toolbar = findViewById(R.id.toolbar);

        // Adiciona um ícone de ação à direita
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24); // Define o ícone de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilita o botão de navegação


        setarrecylerView();




    }

    private void setarrecylerView(){
        recycler = findViewById(R.id.recycler);
        itens = new ArrayList<nomes>();
        itens.add(new nomes("Gabriel"));
        itens.add(new nomes("Heloisa"));
        itens.add(new nomes("Pedro"));

        adapter = new nomesAdapter(tela_chamada_dia.this , itens);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(tela_chamada_dia.this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
    }

    private String diaAtual(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-3"));

        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int ano = calendar.get(Calendar.YEAR);

        String data = "Hoje, " + dia + "/" + mes + "/" + ano;
        return data;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                telaVoltar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void telaVoltar(){
        // Criar a Intent
       Intent intent = new Intent(tela_chamada_dia.this, Tela_Principal.class);

        // Iniciar a atividade de destino
        startActivity(intent);
    }
}