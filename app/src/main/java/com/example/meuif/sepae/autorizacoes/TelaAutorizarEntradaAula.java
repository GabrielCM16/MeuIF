package com.example.meuif.sepae.autorizacoes;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meuif.CaptureAct;
import com.example.meuif.R;
import com.example.meuif.autorizacoes.AdapterAutorizacaoEntrada;
import com.example.meuif.autorizacoes.AlunoAutorizacaoEntrada;
import com.example.meuif.sepae.telaMerendaEscolar;
import com.example.meuif.sepae.telaPrincipalSepae;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

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
    private ConstraintLayout constraintRegistrarEntradaQRcode;
    private FirebaseFirestore db;
    private RecyclerView RecyclerEntradasAutorizadasHoje;
    private TextView textViewMostarEntradasNoDia;
    private  Map<String, String> nomesAlunos = new HashMap<String, String>();
    private ProgressBar progressBarRegistrarEntrada;
    private List<SepaeAutorizacoesEntradaAtrasada> stringList = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private List<Map<String, Timestamp>> diaAtrasado = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_autorizar_entrada_aula);

        inicializarComponentes();
        String dia = getDayAndMonth();
        textViewMostarEntradasNoDia.setText("Entradas Autorizadas hoje: " + dia);
        mostrarAutorizacoesHoje(dia);

        constraintRegistrarEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarRegistrarEntrada.setVisibility(View.VISIBLE);
                String matricula = entradaMatriculaAtrasada.getText().toString();
                procurarDadosAluno(matricula);
            }
        });

        constraintRegistrarEntradaQRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sCanCode();
            }
        });

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.error);
    }

    private void inicializarComponentes() {
        db = FirebaseFirestore.getInstance();
        entradaMatriculaAtrasada = findViewById(R.id.entradaMatriculaAtrasada);
        progressBarRegistrarEntrada = findViewById(R.id.progressBarRegistrarEntrada);
        constraintRegistrarEntrada = findViewById(R.id.constraintRegistrarEntrada);
        RecyclerEntradasAutorizadasHoje = findViewById(R.id.RecyclerEntradasAutorizadasHoje);
        textViewMostarEntradasNoDia = findViewById(R.id.textViewMostarEntradasNoDia);
        constraintRegistrarEntradaQRcode = findViewById(R.id.constraintRegistrarEntradaQRcode);
        progressBarRegistrarEntrada.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        ActionBar actionBar = getSupportActionBar();
        setTitle("Autorizar Entrada Atrasada");
        // Adiciona um ícone de ação à direita
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24); // Define o ícone de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilita o botão de navegação
    }

    private void mostrarAutorizacoesHoje(String dia){
        DocumentReference docRef = db.collection("MaisInformacoes").document("autorizacoes");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> entradas = (Map<String, Object>) document.get("entradasAtrasadas");


                        if (entradas.containsKey(dia)){
                            Map<String, List<Map<String, Timestamp>>> aux = (Map<String, List<Map<String, Timestamp>>>) entradas.get(dia);
                            listarDiasAtrasados(aux);
                        }

                    } else {
                        Log.d("TAGG", "Documento de turma não encontrado");
                    }
                } else {
                    Log.d("TAGG", "Falhou em ", task.getException());

                }
            }
        });
    }
    private void listarDiasAtrasados(Map<String, List<Map<String, Timestamp>>>  aux){
        stringList.clear();
        TimeZone timeZone = TimeZone.getTimeZone("GMT-3");

        // Crie um SimpleDateFormat usando o fuso horário definido
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sdf.setTimeZone(timeZone);

        int auxCont = 0;

        // Percorra o mapa externo (chave externa)
        for (Map.Entry<String, List<Map<String, Timestamp>>> entradaExterna : aux.entrySet()) {
            String chaveExterna = entradaExterna.getKey();
            List<Map<String, Timestamp>> listaInterna = entradaExterna.getValue();

            // Agora, percorra a lista interna
            for (Map<String, Timestamp> mapaInterno : listaInterna) {
                // Percorra o mapa interno (chave interna)
                for (Map.Entry<String, Timestamp> entradaInterna : mapaInterno.entrySet()) {
                    auxCont++;
                    String chaveInterna = entradaInterna.getKey();
                    Timestamp timestamp = entradaInterna.getValue();

                    //chave interna = quem autorizou
                    // chave externa = matricula
                    String dataFormatada = sdf.format(timestamp.toDate());

                Log.d("testes", "Chave Interna: " + chaveInterna + " timestamp: " + timestamp + "Chave Externa " + chaveExterna);
                    novaEntrada(chaveExterna, chaveInterna,dataFormatada, String.valueOf(auxCont));
                }
            }
        }

        AdapterSEPAEautorizacoesHoje adapter = new AdapterSEPAEautorizacoesHoje(stringList);

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerEntradasAutorizadasHoje.setLayoutManager(layoutManager);
        RecyclerEntradasAutorizadasHoje.setHasFixedSize(true);
        RecyclerEntradasAutorizadasHoje.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        RecyclerEntradasAutorizadasHoje.setAdapter(adapter);


    }

    private void novaEntrada(String matricula, String quemAutorizou, String data, String cont){
        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(matricula).document("dados");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nomeAluno = (String) document.get("nome");
                        String turma = (String) document.get("turma");
                        SepaeAutorizacoesEntradaAtrasada sepaeAutorizacoesEntradaAtrasada = new SepaeAutorizacoesEntradaAtrasada(nomeAluno, data, cont, quemAutorizou, turma);
                        stringList.add(sepaeAutorizacoesEntradaAtrasada);
                        AdapterSEPAEautorizacoesHoje adapter = new AdapterSEPAEautorizacoesHoje(stringList);

                        //Configurar RecyclerView
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        RecyclerEntradasAutorizadasHoje.setLayoutManager(layoutManager);
                        RecyclerEntradasAutorizadasHoje.setHasFixedSize(true);
                        RecyclerEntradasAutorizadasHoje.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
                        RecyclerEntradasAutorizadasHoje.setAdapter(adapter);


                    } else {
                        // O documento não existe
                        Toast.makeText(getApplicationContext(), "Erro ao procurar matrícula, matrícula inexistente", Toast.LENGTH_SHORT).show();
                        SepaeAutorizacoesEntradaAtrasada sepaeAutorizacoesEntradaAtrasada = new SepaeAutorizacoesEntradaAtrasada("Matrícula inexistente", data, cont, quemAutorizou, "Matrícula inexistente");
                        stringList.add(sepaeAutorizacoesEntradaAtrasada);
                        AdapterSEPAEautorizacoesHoje adapter = new AdapterSEPAEautorizacoesHoje(stringList);

                        //Configurar RecyclerView
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        RecyclerEntradasAutorizadasHoje.setLayoutManager(layoutManager);
                        RecyclerEntradasAutorizadasHoje.setHasFixedSize(true);
                        RecyclerEntradasAutorizadasHoje.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
                        RecyclerEntradasAutorizadasHoje.setAdapter(adapter);

                    }
                } else {
                    // Falha ao obter o documento
                    Toast.makeText(getApplicationContext(), "Falha inesperada + " + task.getException(), Toast.LENGTH_SHORT).show();

                }
            }
        });
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
                        Toast.makeText(getApplicationContext(), "Erro ao procurar matrícula, matrícula inexistente", Toast.LENGTH_SHORT).show();
                        progressBarRegistrarEntrada.setVisibility(View.INVISIBLE);
                    }
                } else {
                    // Falha ao obter o documento
                    Toast.makeText(getApplicationContext(), "Falha inesperada + " + task.getException(), Toast.LENGTH_SHORT).show();
                    progressBarRegistrarEntrada.setVisibility(View.INVISIBLE);
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

                        atualizarSEPAE(matricula, novoTimestamp, nome);


                    } else {
                        Log.d("TAGG", "Documento de turma não encontrado");
                    }
                } else {
                    Log.d("TAGG", "Falhou em ", task.getException());

                }
            }
        });
    }

    private void atualizarSEPAE(String matricula, Timestamp timestamp, String quem){
        Toast.makeText(getApplicationContext(), "Atualizando SEPAE...", Toast.LENGTH_SHORT).show();
        progressBarRegistrarEntrada.setVisibility(View.VISIBLE);
        DocumentReference docRef = db.collection("MaisInformacoes").document("autorizacoes");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Map<String, List<Map<String, Timestamp>>>> entradas = (Map<String, Map<String, List<Map<String, Timestamp>>>>) document.get("entradasAtrasadas");

                        String dia = getDayAndMonth();


                        if (entradas.containsKey(dia)){
                            Map<String, List<Map<String, Timestamp>>> auxdia = (Map<String, List<Map<String, Timestamp>>>) entradas.get(dia);
                            Map<String, Timestamp> a = new HashMap<>();
                            a.put(quem, timestamp);
                            if (auxdia.containsKey(matricula)){
                                auxdia.get(matricula).add(a);
                            }else{
                                List<Map<String, Timestamp>> b = new ArrayList<>();
                                b.add(a);
                                auxdia.put(matricula, b);
                            }
                        } else {
                            Map<String, List<Map<String, Timestamp>>> auxdia = new HashMap<>();
                            Map<String, Timestamp> a = new HashMap<>();
                            a.put(quem, timestamp);
                            List<Map<String, Timestamp>> b = new ArrayList<>();
                            b.add(a);
                            auxdia.put(matricula, b);
                            entradas.put(dia, auxdia);
                        }


                        Map<String, Object> updates = new HashMap<>();
                        updates.put("entradasAtrasadas", entradas);


                        docRef.update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    // Sucesso na atualização
                                    Log.d("Firestore", "SEPAE atualizada com sucesso!");
                                    progressBarRegistrarEntrada.setVisibility(View.INVISIBLE);
                                    mostrarAutorizacoesHoje(dia);
                                    Toast.makeText(getApplicationContext(), "SEPAE Registrada com sucesso!", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Libere os recursos do MediaPlayer ao encerrar a atividade
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void sCanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
            String aux = result.getContents();
            if (aux.length() == 11) {
                procurarDadosAluno(aux.toString());
            }
            else {
                Toast.makeText(this,"Matrícula invalida", Toast.LENGTH_SHORT).show();;
            }
        }
    });

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