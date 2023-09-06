package com.example.meuif.sepae.autorizacoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.meuif.R;
import com.example.meuif.sepae.telaPrincipalSepae;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class TelaAutorizarEntradaAula extends AppCompatActivity {
    private EditText entradaMatriculaAtrasada;
    private ConstraintLayout constraintRegistrarEntrada;
    private FirebaseFirestore db;
    private ProgressBar progressBarRegistrarEntrada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_autorizar_entrada_aula);

        inicializarComponentes();

        constraintRegistrarEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarRegistrarEntrada.setVisibility(View.VISIBLE);
                String matricula = entradaMatriculaAtrasada.getText().toString();
                procurarDadosAluno(matricula);
            }
        });
    }

    private void inicializarComponentes() {
        db = FirebaseFirestore.getInstance();
        entradaMatriculaAtrasada = findViewById(R.id.entradaMatriculaAtrasada);
        progressBarRegistrarEntrada = findViewById(R.id.progressBarRegistrarEntrada);
        constraintRegistrarEntrada = findViewById(R.id.constraintRegistrarEntrada);
        progressBarRegistrarEntrada.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        ActionBar actionBar = getSupportActionBar();
        setTitle("Autorizar Entrada Atrasada");
        // Adiciona um ícone de ação à direita
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24); // Define o ícone de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilita o botão de navegação
    }

    private void procurarDadosAluno(String matricula){
        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(matricula).document("dados");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nomeAluno = (String) document.get("nome");
                        String turma = (String) document.get("turma");
                        progressBarRegistrarEntrada.setVisibility(View.INVISIBLE);
                        abrirDialogInfAluno(nomeAluno, turma, matricula);

                    } else {
                        // O documento não existe
                    }
                } else {
                    // Falha ao obter o documento
                }
            }
        });
    }

    private void abrirDialogInfAluno(String nomeAluno, String turma, String matricula) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        //configurar titulo e mensagem
        dialog.setTitle("Confira os dados" );
        dialog.setMessage("O nome é: " + nomeAluno + "\nA turma é: " + turma);

        //configurar cancelamento do alert dialog
        dialog.setCancelable(false);

        //configurar icone
        //dialog.setIcon(android.R.drawable.ic_btn_speak_now);

        //configurar açoes para sim e nâo
        dialog.setPositiveButton("Correto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBarRegistrarEntrada.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Processando...", Toast.LENGTH_SHORT).show();
                registrarEntrada(matricula);
            }
        });
        dialog.setNegativeButton("Incorreto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Confira os dados e tente novamente", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.create();
        dialog.show();
    }

    private void registrarEntrada(String matricula){

        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(matricula).document("autorizacoes");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, List<Map<String, Timestamp>>> entradas = (Map<String, List<Map<String, Timestamp>>>) document.get("entradaAula");

                        String nome = recuperarDados("nome");
                        Timestamp novoTimestamp = Timestamp.now();

                        String dia = getDayAndMonth();

                        Map<String, Timestamp> aux = new HashMap<>();
                        aux.put(nome, novoTimestamp);

                        if (entradas.containsKey(dia)){
                            List<Map<String, Timestamp>> auxdia = entradas.get(dia);
                            auxdia.add(aux);
                            entradas.put(dia, auxdia);
                        } else {
                            List<Map<String, Timestamp>> auxdia = new ArrayList<>();
                            auxdia.add(aux);
                            entradas.put(dia, auxdia);
                        }


                        Map<String, Object> updates = new HashMap<>();
                        updates.put("entradaAula", entradas);


                        docRef.update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    // Sucesso na atualização
                                    Log.d("Firestore", "Documento atualizado com sucesso!");
                                    progressBarRegistrarEntrada.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), "Entrada Registrada com sucesso!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Erro na atualização
                                    Log.w("Firestore", "Erro ao atualizar documento", e);
                                    progressBarRegistrarEntrada.setVisibility(View.INVISIBLE);
                                });


                    } else {
                        Log.d("TAGG", "Documento de turma não encontrado");
                    }
                } else {
                    Log.d("TAGG", "Falhou em ", task.getException());

                }
            }
        });
    }
    private String getDayAndMonth() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3")); // Defina o fuso horário desejado
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
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