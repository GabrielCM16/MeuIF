package com.example.meuif.sepae.gestao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meuif.R;
import com.example.meuif.Tela_Principal;
import com.example.meuif.sepae.gestao.lideres.AdapterLideres;
import com.example.meuif.sepae.gestao.lideres.Lider;
import com.example.meuif.sepae.recyclerMerenda.AdapterMerenda;
import com.example.meuif.sepae.telaMerendaEscolar;
import com.example.meuif.tela_chamada_dia;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GerenciarLideres extends AppCompatActivity {
    private Switch switchLider;
    private Switch switchViceLider;
    private Spinner spinnerTurmasLideres;
    private String turma;
    private RecyclerView recyclerlideres;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> matriculaLideres = new ArrayList<String>();
    private Map<String, String> matriculaNome = new HashMap<String, String>();
    private Map<String, String> matriculaTurma = new HashMap<String, String>();
    private Map<String, String> matriculaCargo = new HashMap<String, String>();
    private ProgressBar progressBarGerenciarLideres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        setContentView(R.layout.activity_gerenciar_lideres);

        carregarComponentes();
        pegarNomes();
        pegarTurmaAlunos();
        pegarLideres();
        setarSpinnerTurmas();
    }

    private void carregarComponentes(){
        switchLider = findViewById(R.id.switchLider);
        switchViceLider = findViewById(R.id.switchViceLider);
        spinnerTurmasLideres = findViewById(R.id.spinnerTurmasLideres);
        recyclerlideres = findViewById(R.id.recyclerlideres);
        progressBarGerenciarLideres = findViewById(R.id.progressBarGerenciarLideres);
        progressBarGerenciarLideres.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_actionbar);
        ImageView leftImage = findViewById(R.id.leftImage);
        ImageView rightImage = findViewById(R.id.rightImage); //baixar pdf
        TextView titleText = findViewById(R.id.titleText);
        titleText.setText("Gerenciar Lideres");
        rightImage.setImageResource(R.drawable.baseline_question_mark_24);

        leftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaVoltar();
            }
        });

        rightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //duvidaChamada();
            }
        });

        switchLider.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mostrarLideres(matriculaLideres);
            }
        });

        switchViceLider.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mostrarLideres(matriculaLideres);
            }
        });

        recyclerlideres.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerlideres,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(),
                                "local: " +  matriculaLideres.get(position),
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));

    }
    private void telaVoltar(){
        finish();
    }
    private void pegarNomes(){
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
                            matriculaNome.put(matricula, nome);
                        }
                        Log.d("TAG", "nomes alunos ==== " + matriculaNome.toString());

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
                            matriculaTurma.put(matricula, turma);
                        }
                        Log.d("TAG", "turmas alunos ==== " + matriculaTurma.toString());

                    } else {
                        // O documento não existe
                    }
                } else {
                    // Falha ao obter o documento
                }
            }
        });
    }

    private void setarSpinnerTurmas(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.turmas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurmasLideres.setAdapter(adapter);

        spinnerTurmasLideres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTurma = parent.getItemAtPosition(position).toString();

                turma = selectedTurma;
                mostrarLideres(matriculaLideres);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                turma = "Todas";
            }
        });
    }

    private void pegarLideres(){
        String[] turmasArray = getResources().getStringArray(R.array.turmas);
        List<String> turmasList = Arrays.asList(turmasArray);
        Log.d("documento", "array" + turmasList.toString());

        for (String turmas : turmasList){
            progressBarGerenciarLideres.setVisibility(View.VISIBLE);
            Log.d("documento", turmas);
            DocumentReference docRef = db.collection("ChamadaTurma").document(turmas);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Verifique se os campos "Lider" e "ViceLider" existem no documento
                            if (document.contains("Lider") && document.contains("ViceLider")) {
                                String lider = document.getString("Lider");
                                String viceLider = document.getString("ViceLider");

                                matriculaLideres.add(lider);
                                matriculaCargo.put(lider, "lider");
                                matriculaLideres.add(viceLider);
                                matriculaCargo.put(viceLider, "vice");
                                mostrarLideres(matriculaLideres);
                                Log.d("Lideres", "lideres na func " + matriculaLideres.toString());
                            } else {
                                // Os campos "Lider" e/ou "ViceLider" não existem no documento
                                Log.d("Campos", "Lider e/ou ViceLider não encontrados no documento");
                            }
                        } else {
                            // O documento não existe
                            Log.d("Documento", "Documento não encontrado");
                        }
                    } else {
                        // Falha ao obter o documento
                        Log.d("Firestore", "Falha na leitura do documento", task.getException());
                    }
                }
            });
            progressBarGerenciarLideres.setVisibility(View.INVISIBLE);
        }
    }

    private void mostrarLideres(List<String> matriculaLideres) {
        progressBarGerenciarLideres.setVisibility(View.VISIBLE);
        List<Lider> liders = new ArrayList<Lider>();

        for (String matricula : matriculaLideres) {
            String turmaAtual = matriculaTurma.getOrDefault(matricula, "Turma Incorreta");
            if (turmaAtual.equals(turma) || turma.equals("Todas")){
                String nome = matriculaNome.getOrDefault(matricula, "Nome Incorreto");
                String cargo = matriculaCargo.getOrDefault(matricula, "falha");
                String n = matricula + " - " + turmaAtual;
                if (switchLider.isChecked() && cargo.equals("lider")) {
                    Lider lider = new Lider(nome, n, cargo);
                    liders.add(lider);
                }
                if (switchViceLider.isChecked() && cargo.equals("vice")) {
                    Lider lider = new Lider(nome, n, cargo);
                    liders.add(lider);
                }

            }
        }

        AdapterLideres adapter = new AdapterLideres(liders);


        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerlideres.setLayoutManager(layoutManager);
        recyclerlideres.setHasFixedSize(true);
        recyclerlideres.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerlideres.setAdapter(adapter); //criar adapter
        progressBarGerenciarLideres.setVisibility(View.INVISIBLE);
    }

}