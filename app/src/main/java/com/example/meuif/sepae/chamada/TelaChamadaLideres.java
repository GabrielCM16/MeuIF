package com.example.meuif.sepae.chamada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.meuif.R;
import com.example.meuif.sepae.telaMerendaEscolar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_chamada_lideres);

        carregarComponentes();

    }

    protected void onStart() {
        super.onStart();
        carregarComponentes();
        setarSpinnerMeses();
        setarSpinnerTurmas();
        pegarDadosDias(new Callback() {
            @Override
            public void onComplete() {
                setarSpinnerDias();
            }
        });
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

    private void setarSpinnerTurmas(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.turmas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurmas.setAdapter(adapter);
    }

    private void setarSpinnerMeses(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.months_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeses.setAdapter(adapter);

        spinnerMeses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = parent.getItemAtPosition(position).toString();
                // Faça algo com o mês selecionado
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ação a ser tomada quando nada é selecionado (opcional)
            }
        });

    }

    private void pegarDadosDias(Callback callback){
        String turma = "3INF";

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
                                    dias.add(fieldName);
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
        dias.toArray(dataArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDias.setAdapter(adapter);
    }

    private interface Callback {
        void onComplete();
    }


}