package com.example.meuif.sepae.gestao;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private Map<String, List<String>> turmaNomes = new HashMap<>();
    private ProgressBar progressBarGerenciarLideres;
    private String novoLider = "";
    private List<Lider> liders = new ArrayList<Lider>();

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
                        Lider l = liders.get(position);
                        String[] matriculaL = l.getMatricula().split(" ");
                        String m = matriculaL[0];
                        mudarLider(m);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));

    }
    @SuppressLint("MissingInflatedId")
    private void mudarLider(String matricula) {
        String nomeLider = matriculaNome.getOrDefault(matricula, "Erro Em Nome");
        String turmaLider = matriculaTurma.getOrDefault(matricula, "Erro Em Turma");
        String cargo = matriculaCargo.getOrDefault(matricula, "Erro Em Cargo");

// Use o contexto da atividade atual
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_trocar_lider, null);

        TextView textAlunoNome = dialogView.findViewById(R.id.textAlunoNome);
        TextView textMatriculaLider = dialogView.findViewById(R.id.textMatriculaLider);
        Spinner spinnerTurmaTrocaLider = dialogView.findViewById(R.id.spinnerTurmaTrocaLider);
        List<String> nomesSala = turmaNomes.get(turmaLider);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nomesSala);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurmaTrocaLider.setAdapter(adapter);

        spinnerTurmaTrocaLider.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedNome = parent.getItemAtPosition(position).toString();
                novoLider = selectedNome;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        textAlunoNome.setText(nomeLider);
        textMatriculaLider.setText(matricula);

        builder.setView(dialogView);

        String turmaCargo = turmaLider + " - " + cargo;
        View titleView = LayoutInflater.from(this).inflate(R.layout.title_centered_dialog, null);
        TextView textCenterDialog = titleView.findViewById(R.id.textCenterDialog);
        textCenterDialog.setText(turmaCargo);

        builder.setCustomTitle(titleView);

        builder.setCancelable(false);
        builder.setPositiveButton("Substituir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //mudar lider
                trocaLider(novoLider, matricula);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void trocaLider(String nome, String antigoLider){
        Log.d("nome", "nome" + nome);
        for (Map.Entry<String, String> entry : matriculaNome.entrySet()) {
            String matriculaAtual = entry.getKey();
            String nomeAtual = entry.getValue();
            if (nome.equals(nomeAtual)) {
                String turma = matriculaTurma.getOrDefault(matriculaAtual, "Error");
                String cargo = matriculaCargo.getOrDefault(antigoLider, "Error");
                DocumentReference docRef = db.collection("ChamadaTurma").document(turma);

// 2. Obtenha o documento atual.
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // 3. Atualize o campo da string desejado no documento.
                            Map<String, Object> updates = new HashMap<>();
                            if (cargo.equals("lider")){
                                updates.put("Lider", matriculaAtual);
                            } if (cargo.equals("vice")){
                                updates.put("ViceLider", matriculaAtual);
                            }


                            // 4. Envie o documento atualizado de volta para o Firestore.
                            docRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // A atualização foi bem-sucedida.
                                    Log.d("update", "onSuccess");
                                    Toast.makeText(getApplicationContext(), "Sucesso ao atualizar", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Ocorreu um erro durante a atualização.
                                    Toast.makeText(getApplicationContext(), "Erro ao atualizar", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    }
                });

            }
        }
    }
    private void telaVoltar(){
        finish();
    }
    private void pegarNomes(){
        progressBarGerenciarLideres.setVisibility(View.VISIBLE);
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
                        progressBarGerenciarLideres.setVisibility(View.INVISIBLE);

                    } else {
                        // O documento não existe
                        progressBarGerenciarLideres.setVisibility(View.INVISIBLE);
                    }
                } else {
                    // Falha ao obter o documento
                    progressBarGerenciarLideres.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void pegarTurmaAlunos(){
        progressBarGerenciarLideres.setVisibility(View.VISIBLE);
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
                        progressBarGerenciarLideres.setVisibility(View.INVISIBLE);

                    } else {
                        // O documento não existe
                        progressBarGerenciarLideres.setVisibility(View.INVISIBLE);
                    }
                } else {
                    // Falha ao obter o documento
                    progressBarGerenciarLideres.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setarSpinnerTurmas(){
        progressBarGerenciarLideres.setVisibility(View.VISIBLE);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.turmas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurmasLideres.setAdapter(adapter);
        progressBarGerenciarLideres.setVisibility(View.INVISIBLE);

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
        progressBarGerenciarLideres.setVisibility(View.VISIBLE);
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
                                List<String> nomesTurma = (List<String>) document.get("nomesSala");

                                matriculaLideres.add(lider);
                                matriculaCargo.put(lider, "lider");
                                matriculaLideres.add(viceLider);
                                matriculaCargo.put(viceLider, "vice");
                                turmaNomes.put(turmas, nomesTurma);
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
        liders.clear();

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