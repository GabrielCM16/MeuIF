package com.example.meuif;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.meuif.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class tela_chamada_dia extends AppCompatActivity {
    private Button botaoDias;
    private TextView saidaData;
    private String diaAtual;
    private RecyclerView recycler;
    private nomesAdapter adapter;
    private ArrayList<nomes> itens;
    private String turma;
    private FirebaseFirestore db;

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

        db = FirebaseFirestore.getInstance();

        ActionBar actionBar = getSupportActionBar();

        setTitle("Chamada Diaria de Classe");

        // Obtém uma referência para a ActionBar ou Toolbar
         // ou Toolbar toolbar = findViewById(R.id.toolbar);

        // Adiciona um ícone de ação à direita
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24); // Define o ícone de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilita o botão de navegação

        listarNomes();


    }

    private String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }

    private void listarNomes(){
        turma = recuperarDados("turma");
        DocumentReference docRef = db.collection("ChamadaTurma").document(turma);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        List<String> stringArray = (List<String>) document.get("nomesSala");

                        if (stringArray != null) {
                            // Agora você tem a matriz de strings e pode fazer o que quiser com ela
                            for (String stringValue : stringArray) {
                                Log.d("TAG", "String: " + stringValue); // Exibir no logcat
                            }
                            setarrecylerView(stringArray);
                        } else {
                            Log.d("TAG", "Campo da matriz não encontrado no documento!");
                        }

                        Log.d("TAGBUSCANOMES", " achou o ducumento");
                    } else {
                        Log.d("TAGBUSCANOMES", "Documento de turma não encontrado");
                    }
                } else {
                    Log.d("TAGBUSCANOMES", "Falhou em ", task.getException());
                }
            }
        });
    }

    private void setarrecylerView(List<String> listaNomes){
        itens = new ArrayList<nomes>();

        if (listaNomes != null) {
            for (String nome : listaNomes) {
                itens.add(new nomes(nome));
            }
        }

        recycler = findViewById(R.id.recycler);


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