package com.example.meuif.sepae.gestao.aluno;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.meuif.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GerenciarAluno extends AppCompatActivity {
    private RecyclerView listagemGerenciarNomes;
    private FirebaseFirestore db;
    private EditText editTextBusca;
    private Map<String, String> nomesAlunos = new HashMap<>();
    private Map<String, String> turmasAlunos = new HashMap<>();
    private Boolean nomesExecutado = false;
    private Boolean turmaExecutado = false;
    private List<ModelGerenciarAluno> listagem = new ArrayList<>();
    private  AdapterGerenciarAluno adapter = new AdapterGerenciarAluno(new ArrayList<>());
    private List<ModelGerenciarAluno> listaFiltrada = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_aluno);

        db = FirebaseFirestore.getInstance();
        pegarNomesAlunos();
        pegarTurmaAlunos();
        inicializarComponents();

        listagemGerenciarNomes.setAdapter(adapter);

        editTextBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        carregarNomes();
    }

    private void inicializarComponents() {
        listagemGerenciarNomes = findViewById(R.id.listagemGerenciarNomes);

        editTextBusca = findViewById(R.id.editTextBusca);

        //evento recyclerView
        listagemGerenciarNomes.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                listagemGerenciarNomes, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String matricula = listaFiltrada.get(position).getMatricula();

                String primeiraParte = matricula.substring(0);

                Intent intent = new Intent(getApplicationContext(), MostrarDadosPorMatricula.class);

                intent.putExtra("matricula", primeiraParte);

                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }

    private void filtrar(String texto) {
        if (!texto.isEmpty()){
                listaFiltrada.clear();
                String filtro = texto.toLowerCase().trim();
                    for (ModelGerenciarAluno item : listagem) {
                        if (item.getNome().toLowerCase().contains(filtro) || item.getMatricula().toLowerCase().contains(filtro)) {
                            listaFiltrada.add(item);
                        }
                    }
                    adapter = new AdapterGerenciarAluno(listaFiltrada);
                listagemGerenciarNomes.setAdapter(adapter);

        }
    }

    private void carregarNomes(){
        if (turmaExecutado && nomesExecutado){

            editTextBusca.setHint("Nome ou matrícula - " + nomesAlunos.size());

            if (nomesAlunos.size() == turmasAlunos.size()) {
                for (Map.Entry<String, String> entry : nomesAlunos.entrySet()) {
                    String matricula = entry.getKey();
                    String nome = entry.getValue();

                    String turma = turmasAlunos.getOrDefault(matricula, "erro Turma");

                    String matriculaTurma = matricula + " - " + turma;

                    ModelGerenciarAluno modelGerenciarAluno = new ModelGerenciarAluno();
                    modelGerenciarAluno.setNome(nome);
                    modelGerenciarAluno.setMatricula(matriculaTurma);
                    listagem.add(modelGerenciarAluno);
                }
            }

            adapter = new AdapterGerenciarAluno(listagem);

            //Configurar RecyclerView
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            listagemGerenciarNomes.setLayoutManager(layoutManager);
            listagemGerenciarNomes.setHasFixedSize(true);
            listagemGerenciarNomes.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
            listagemGerenciarNomes.setAdapter(adapter);


        } else {
            // Atraso de 3 segundos antes de chamar a função novamente
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    carregarNomes();
                }
            }, 1000);
        }
    }

    private void pegarNomesAlunos(){
        DocumentReference docRef = db.collection("Usuarios").document("Alunos");

// Obtém os dados do documento
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtém o campo "NomesAlunos" como um Map<String, String>
                        Map<String, String> nomesaux = (Map<String, String>) document.get("NomesAlunos");

                        // Agora você pode iterar sobre o Map e acessar os nomes dos alunos
                        for (Map.Entry<String, String> entry : nomesaux.entrySet()) {
                            String matricula = entry.getKey();
                            String nome = entry.getValue();
                            // Faça algo com as informações...
                            nomesAlunos.put(matricula, nome);
                        }
                        Log.d("TAG", "nomes alunos ==== " + nomesAlunos.toString());
                        nomesExecutado = true;

                    } else {
                        // O documento não existe
                    }
                } else {
                    // Falha ao obter o documento
                }
            }
        });
    }

    private void pegarTurmaAlunos(){
        DocumentReference docRef = db.collection("Usuarios").document("Alunos");

// Obtém os dados do documento
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, String> turmaAux = (Map<String, String>) document.get("TurmasAlunos");

                        // Agora você pode iterar sobre o Map e acessar os nomes dos alunos
                        for (Map.Entry<String, String> entry : turmaAux.entrySet()) {
                            String matricula = entry.getKey();
                            String turma = entry.getValue();
                            // Faça algo com as informações...
                            turmasAlunos.put(matricula, turma);
                        }
                        turmaExecutado = true;
                    } else {
                        // O documento não existe
                    }
                } else {
                    // Falha ao obter o documento
                }
            }
        });

    }
}