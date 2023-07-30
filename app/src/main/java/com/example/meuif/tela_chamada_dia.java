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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.meuif.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class tela_chamada_dia extends AppCompatActivity {
    private Button botaoDias;
    private TextView saidaData;
    private String diaAtual;
    private ListView listView;
    private String turma;
    private Button botaoSalvar;
    private FirebaseFirestore db;
    private String dataBDAtual;
    List<String> nomesChamada;
    List<Integer> chamdaImages;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_chamada_dia);

        botaoDias = findViewById(R.id.botaoDias);
        botaoDias.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        botaoDias.setPadding(30, botaoDias.getPaddingTop(), 15, botaoDias.getPaddingBottom());
        saidaData = findViewById(R.id.saidaData);
        listView = (ListView) findViewById(R.id.listViewChamada);
        botaoSalvar = findViewById(R.id.botaoSalvar);
        nomesChamada = new ArrayList<>();
        chamdaImages = new ArrayList<>();

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

        CustomChamadaAdapter customChamadaAdapter = new CustomChamadaAdapter(getApplicationContext(), nomesChamada, chamdaImages);
        listView.setAdapter(customChamadaAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (chamdaImages.get(i) == R.drawable.falta){
                    chamdaImages.set(i, R.drawable.presenca);
                } else if (chamdaImages.get(i) == R.drawable.presenca ){
                    chamdaImages.set(i, R.drawable.falta);
                }
                atualizarListView();
            }
        });

        realizarChamada();


        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaAtual = diaAtual();
                criarChamada(dataBDAtual, nomesChamada, chamdaImages);
            }
        });


    }

    private void atualizarListView(){
        CustomChamadaAdapter customChamadaAdapter = new CustomChamadaAdapter(getApplicationContext(), nomesChamada, chamdaImages);
        listView.setAdapter(customChamadaAdapter);
    }

    private void realizarChamada(){
        turma = recuperarDados("turma");
        DocumentReference docRef = db.collection("ChamadaTurma").document(turma);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        if (document.contains(dataBDAtual)) {
                            Object fieldValue = document.get(dataBDAtual);
                            if (fieldValue instanceof Map) {
                                Map<String, Boolean> mapData = (Map<String, Boolean>) fieldValue;
                                // Agora você tem o Map e pode fazer o que quiser com ele
                                for (Map.Entry<String, Boolean> entry : mapData.entrySet()) {
                                    String key = entry.getKey();
                                    Boolean value = entry.getValue();
                                    Log.d("TAG", "Key: " + key + ", Value: " + value);
                                }

                            } else {
                                Log.d("TAG", "O campo não contém um Map válido!");
                            }
                        } else {
                            Log.d("TAG", "Campo com o Map da data não existe no documento!");
                            listarNomes();
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

    public void criarChamada(String data, List<String> mapData, List<Integer> images){

        Map<String, Boolean> mapDataChamada = new HashMap<>();

        for (int i = 0; i < mapData.size(); i++) {
            if (images.get(i) == R.drawable.falta){
                mapDataChamada.put(mapData.get(i), false);
            } else if (images.get(i) == R.drawable.presenca){
                mapDataChamada.put(mapData.get(i), true);
            }
        }


        db.collection("ChamadaTurma").document(turma)
                .update(data, mapDataChamada).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "Campo do tipo Map criado com sucesso!");
                        //setarrecylerView(mapData);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Erro ao criar o campo do tipo Map: " + e.getMessage());
                    }
                });

    }

    private String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }

    private void atualizarListaNomes(List<String> nomes){
        for (String stringValue : nomes) {
            Log.d("TAG", "String da chamada list view: " + stringValue); // Exibir no logcat
            nomesChamada.add(stringValue);
            chamdaImages.add(R.drawable.presenca);
        }
        atualizarListView();
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
                            atualizarListaNomes(stringArray);
                            for (String stringValue : stringArray) {
                                Log.d("TAG", "String da chamda: " + stringValue); // Exibir no logcat
                            }

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

    private String diaAtual(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-3"));

        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int ano = calendar.get(Calendar.YEAR);

        dataBDAtual = String.format("%02d%02d%d", dia, mes, ano);


        String data = "Hoje, " + String.format("%02d/%02d/%04d", dia, mes, ano);
        return data;
    }


    private void telaVoltar(){
        // Criar a Intent
       Intent intent = new Intent(tela_chamada_dia.this, Tela_Principal.class);

        // Iniciar a atividade de destino
        startActivity(intent);
    }
}