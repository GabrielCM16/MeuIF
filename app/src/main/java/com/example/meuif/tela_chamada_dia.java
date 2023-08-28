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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class tela_chamada_dia extends AppCompatActivity {
    private Button botaoDias;
    private TextView saidaData;
    private ProgressBar progressBarChamada;
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
        progressBarChamada = findViewById(R.id.progressBarChamada);

        progressBarChamada.setVisibility(View.VISIBLE);

        //muda a cor do progressBar pra preto
        progressBarChamada.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        nomesChamada = new ArrayList<>();
        chamdaImages = new ArrayList<>();

        diaAtual = diaAtual();
        saidaData.setText(diaAtual);

        db = FirebaseFirestore.getInstance();

        ActionBar actionBar = getSupportActionBar();

        setTitle("Chamada Diaria de Classe");
        progressBarChamada.setVisibility(View.INVISIBLE);

        // Obtém uma referência para a ActionBar ou Toolbar
         // ou Toolbar toolbar = findViewById(R.id.toolbar);

        // Adiciona um ícone de ação à direita
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24); // Define o ícone de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilita o botão de navegação coo


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
        int firstVisibleItemPosition = listView.getFirstVisiblePosition();
        Log.d("TAGGG", nomesChamada.toString());
        List<String> nomesComPrefixo = new ArrayList<>();
        for (int i = 0; i < nomesChamada.size(); i++) {
            String nome = nomesChamada.get(i);
            String nomeComPrefixo = (i + 1) + " - " + nome;
            nomesComPrefixo.add(nomeComPrefixo);
        }
        Log.d("TAGGG", nomesComPrefixo.toString());
        CustomChamadaAdapter customChamadaAdapter = new CustomChamadaAdapter(getApplicationContext(), nomesComPrefixo, chamdaImages);
        listView.setAdapter(customChamadaAdapter);
        listView.setSelection(firstVisibleItemPosition);
    }

    private void abrirToast(String texto){
        Toast.makeText(
                getApplicationContext(),
                texto,
                Toast.LENGTH_LONG
        ).show();
    }


    private void realizarChamada(){
        progressBarChamada.setVisibility(View.VISIBLE);
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
                                atualizarListaNomes(mapData);
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
                    abrirToast("Erro inesperado");
                }
            }
        });
    }

    public void criarChamada(String data, List<String> mapData, List<Integer> images){
        progressBarChamada.setVisibility(View.VISIBLE);

        Map<String, Boolean> mapDataChamada = new HashMap<>();

        for (int i = 0; i < mapData.size(); i++) {
            //String nomeAux = mapData.get(i).substring(4);
            String nomeAux = mapData.get(i);
            if (images.get(i) == R.drawable.falta){
                mapDataChamada.put(nomeAux, false);
            } else if (images.get(i) == R.drawable.presenca){
                mapDataChamada.put(nomeAux, true);
            }
        }


        db.collection("ChamadaTurma").document(turma)
                .update(data, mapDataChamada).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "Campo do tipo Map criado com sucesso!");
                        abrirToast("Chamada Salva Com Sucesso");
                        progressBarChamada.setVisibility(View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Erro ao criar o campo do tipo Map: " + e.getMessage());
                        abrirToast("Erro ao Salvar");
                        progressBarChamada.setVisibility(View.INVISIBLE);
                    }
                });

    }

    private String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }

    private void atualizarListaNomes(List<String> nomes){
        progressBarChamada.setVisibility(View.VISIBLE);
        int cont = 0;
        for (String stringValue : nomes) {
            cont += 1;
            Log.d("TAG", "String da chamada list view: " + stringValue); // Exibir no logcat
            nomesChamada.add(stringValue);
            chamdaImages.add(R.drawable.presenca);
        }
        progressBarChamada.setVisibility(View.INVISIBLE);
        atualizarListView();
    }

    private void atualizarListaNomes(Map<String, Boolean> mapData){
        progressBarChamada.setVisibility(View.VISIBLE);
        Log.d("ATU", "antes de atu" + mapData);

        List<Map.Entry<String, Boolean>> sortedEntries = new ArrayList<>(mapData.entrySet());

        Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Boolean>>() {
            @Override
            public int compare(Map.Entry<String, Boolean> entry1, Map.Entry<String, Boolean> entry2) {
                return entry1.getKey().compareTo(entry2.getKey());
            }
        });

        Map<String, Boolean> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : sortedEntries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        Log.d("ATU", "depois de atu" + sortedMap);

        int cont = 1;

        for (Map.Entry<String, Boolean> entry : sortedMap.entrySet()) {
            String key = entry.getKey();
            Boolean value = entry.getValue();
            Log.d("TAG", "Key: " + key + ", Value: " + value);
            String nomeAux = key;
            nomesChamada.add(nomeAux);
            if (value){
                chamdaImages.add(R.drawable.presenca);
            } else if (!value){
                chamdaImages.add(R.drawable.falta);
            }
            cont++;
        }

        progressBarChamada.setVisibility(View.INVISIBLE);
        atualizarListView();
    }

    private void listarNomes(){
        progressBarChamada.setVisibility(View.VISIBLE);
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
                            progressBarChamada.setVisibility(View.INVISIBLE);
                            atualizarListaNomes(stringArray);
                            for (String stringValue : stringArray) {
                                Log.d("TAG", "String da chamda: " + stringValue); // Exibir no logcat
                            }

                        } else {
                            Log.d("TAG", "Campo da matriz não encontrado no documento!");
                            abrirToast("Erro ao encontrar nomes");
                            progressBarChamada.setVisibility(View.INVISIBLE);
                        }

                        Log.d("TAGBUSCANOMES", " achou o ducumento");
                    } else {
                        Log.d("TAGBUSCANOMES", "Documento de turma não encontrado");
                        abrirToast("Erro ao encontrar turma");
                        progressBarChamada.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Log.d("TAGBUSCANOMES", "Falhou em ", task.getException());
                    abrirToast("Erro");
                    progressBarChamada.setVisibility(View.INVISIBLE);
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

    @Override
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
       Intent intent = new Intent(tela_chamada_dia.this, Tela_Principal.class);

        // Iniciar a atividade de destino
        startActivity(intent);
    }
}