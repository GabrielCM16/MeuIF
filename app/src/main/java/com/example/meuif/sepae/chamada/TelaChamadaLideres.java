package com.example.meuif.sepae.chamada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.meuif.R;
import com.example.meuif.sepae.telaMerendaEscolar;
import com.example.meuif.sepae.telaPrincipalSepae;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelaChamadaLideres extends AppCompatActivity {
    private Spinner spinnerMeses;
    private Spinner spinnerDias;
    private Spinner spinnerTurmas;
    private FirebaseFirestore db;
    private List<String> dias = new ArrayList<>();
    private List<String> meses = new ArrayList<>();
    private String turma = "";
    private Map<String, String> mesesAno = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_chamada_lideres);

        carregarComponentes();

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
        Intent intent = new Intent(getApplicationContext(), telaPrincipalSepae.class);
        // Iniciar a atividade de destino
        startActivity(intent);
        finish();
    }

    protected void onStart() {
        super.onStart();
        atribuirMesesAno();
        carregarComponentes();
        setarSpinnerTurmas(new Callback() {
            @Override
            public void onComplete() {
                pegarDadosDias(new Callback() {
                    @Override
                    public void onComplete() {
                        setarSpinnerMeses();
                        setarSpinnerDias();
                    }
                });
            }
        });

    }

    private void atribuirMesesAno(){
        mesesAno.put("01", "Janeiro");
        mesesAno.put("02", "Fevereiro");
        mesesAno.put("03", "Março");
        mesesAno.put("04", "Abril");
        mesesAno.put("05", "Maio");
        mesesAno.put("06", "Junho");
        mesesAno.put("07", "Julho");
        mesesAno.put("08", "Agosto");
        mesesAno.put("09", "Setembro");
        mesesAno.put("10", "Outubro");
        mesesAno.put("11", "Novembro");
        mesesAno.put("12", "Dezembro");
    }

    private void carregarComponentes(){
        db = FirebaseFirestore.getInstance();
        spinnerMeses = findViewById(R.id.spinnerMeses);
        spinnerDias = findViewById(R.id.spinnerDias);
        spinnerTurmas = findViewById(R.id.spinnerTurmas);

        ActionBar actionBar = getSupportActionBar();
        setTitle("Chamada Líderes");
        // Adiciona um ícone de ação à direita
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24); // Define o ícone de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilita o botão de navegação
    }

    private void setarSpinnerTurmas(Callback callback){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.turmas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurmas.setAdapter(adapter);

        spinnerTurmas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dias.clear();
                String selectedTurma = parent.getItemAtPosition(position).toString();

                turma = selectedTurma;
                pegarDadosDias(new Callback() {
                    @Override
                    public void onComplete() {
                        setarSpinnerMeses();
                        setarSpinnerDias();
                    }
                });
                callback.onComplete();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ação a ser tomada quando nada é selecionado (opcional)
            }
        });
    }

    private void setarSpinnerMeses(){
        meses.clear();
        for (int i = 0; i < dias.size(); i++){
            String dia = dias.get(i);
            String subString = dia.substring(2, 4);
            if (!meses.contains(mesesAno.getOrDefault(subString, "00"))){
                meses.add(mesesAno.getOrDefault(subString, "00"));
            }

        }
        Log.d("TAG", meses.toString());

        // Converter a List<String> em um array de strings simples
        String[] dataArray = new String[meses.size()];
        meses.toArray(dataArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeses.setAdapter(adapter);

    }

    private void pegarDadosDias(Callback callback){

        DocumentReference docRef = db.collection("ChamadaTurma").document(turma);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtendo todos os campos do documento
                        Map<String, Object> data = document.getData();

                        Log.d("TAG", "completo "+data.toString());

                        if (data != null) {
                            // Iterando pelos campos e imprimindo seus nomes
                            for (String fieldName : data.keySet()) {
                                Log.d("TAG", "Campo: " + fieldName);
                                if (!fieldName.equals("Lider") && !fieldName.equals("nomesSala")){
                                    if (!dias.contains(fieldName)){
                                        dias.add(fieldName);
                                    }
                                }
                            }
                            callback.onComplete();
                        }
                    } else {
                        Log.d("TAG", "O documento não existe.");
                        callback.onComplete();
                    }
                } else {
                    Log.d("TAG", "Erro ao obter o documento: " + task.getException());
                    callback.onComplete();
                }
            }
        });


    }

    private void setarSpinnerDias(){
        Log.d("TAG", dias.toString());

        // Converter a List<String> em um array de strings simples
        String[] dataArray = new String[dias.size()];
        for(int i = 0; i< dias.size(); i++){
            dataArray[i] = dias.get(i).substring(0, 2);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDias.setAdapter(adapter);
    }

    private interface Callback {
        void onComplete();
    }


}