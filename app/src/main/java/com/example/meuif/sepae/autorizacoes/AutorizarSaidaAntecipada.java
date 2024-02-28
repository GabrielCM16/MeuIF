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
import android.graphics.drawable.ColorDrawable;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class AutorizarSaidaAntecipada extends AppCompatActivity {
    private TextView textViewMostarSaidasNoDia;
    private FirebaseFirestore db;
    private ConstraintLayout constraintRegistrarSaidaQRcode;
    private EditText entradaMatriculaSaida;
    private List<SepaeAutorizacoesEntradaAtrasada> stringList = new ArrayList<>();
    private ConstraintLayout constraintRegistrarSaida;
    private ProgressBar progressBarRegistrarSaida;
    private RecyclerView RecyclerSaidasAutorizadasHoje;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autorizar_saida_antecipada);

        inicializarComponentes();
        String dia = getDayAndMonth();
        textViewMostarSaidasNoDia.setText("Saídas Autorizadas hoje: " + dia);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.error);
        mostrarAutorizacoesHoje(dia);
        constraintRegistrarSaida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarRegistrarSaida.setVisibility(View.VISIBLE);
                String matricula = entradaMatriculaSaida.getText().toString();
                if (!matricula.equals("") && matricula != null) {
                    progressBarRegistrarSaida.setVisibility(View.VISIBLE);
                    procurarDadosAluno(matricula);
                } else{
                    Toast.makeText(getApplicationContext(), "Matrícula Invalida", Toast.LENGTH_SHORT).show();
                    progressBarRegistrarSaida.setVisibility(View.INVISIBLE);
                }
            }
        });

        constraintRegistrarSaidaQRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sCanCode();
            }
        });

    }

    private void inicializarComponentes() {
        db = FirebaseFirestore.getInstance();
        textViewMostarSaidasNoDia = findViewById(R.id.textViewMostarSaidasNoDia);
        entradaMatriculaSaida = findViewById(R.id.entradaMatriculaSaida);
        constraintRegistrarSaida = findViewById(R.id.constraintRegistrarSaida);
        progressBarRegistrarSaida = findViewById(R.id.progressBarRegistrarSaida);
        RecyclerSaidasAutorizadasHoje = findViewById(R.id.RecyclerSaidasAutorizadasHoje);
        progressBarRegistrarSaida.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        constraintRegistrarSaidaQRcode = findViewById(R.id.constraintRegistrarSaidaQRcode);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Defina a cor de background desejada (por exemplo, cor vermelha)
            ColorDrawable colorDrawable = new ColorDrawable(0xff23729a);
            actionBar.setBackgroundDrawable(colorDrawable);
        }
        setTitle("Autorizar Saída Antecipada");
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
                        Map<String, Object> entradas = (Map<String, Object>) document.get("saidasAntecipadas");


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
    private void listarDiasAtrasados(Map<String, List<Map<String, Timestamp>>>  aux) {
        stringList.clear();
        TimeZone timeZone = TimeZone.getTimeZone("GMT-3");
        String motivoPessoal = "";

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
                auxCont++;
                if (mapaInterno.containsKey("motivo")) {
                    motivoPessoal = (String) String.valueOf(mapaInterno.get("motivo"));
                }
                for (Map.Entry<String, Timestamp> entradaInterna : mapaInterno.entrySet()) {

                    String chaveInterna = entradaInterna.getKey();

                    if (!chaveInterna.equals("motivo")) {
                        Timestamp timestamp = (Timestamp) entradaInterna.getValue();

                        //chave interna = quem autorizou
                        // chave externa = matricula
                        String dataFormatada = sdf.format(timestamp.toDate());

                        Log.d("testes", "Chave Interna: " + chaveInterna + " timestamp: " + timestamp + "Chave Externa " + chaveExterna + "motivo" + motivoPessoal);
                        novaEntrada(chaveExterna, chaveInterna, dataFormatada, String.valueOf(auxCont), motivoPessoal);
                    }
                }
                motivoPessoal = "";
            }

            AdapterSEPAEautorizacoesHoje adapter = new AdapterSEPAEautorizacoesHoje(stringList);

            //Configurar RecyclerView
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            RecyclerSaidasAutorizadasHoje.setLayoutManager(layoutManager);
            RecyclerSaidasAutorizadasHoje.setHasFixedSize(true);
            RecyclerSaidasAutorizadasHoje.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
            RecyclerSaidasAutorizadasHoje.setAdapter(adapter);
        }
    }

    private void novaEntrada(String matricula, String quemAutorizou, String data, String cont, String motivo){
        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerSaidasAutorizadasHoje.setLayoutManager(layoutManager);
        RecyclerSaidasAutorizadasHoje.setHasFixedSize(true);
        RecyclerSaidasAutorizadasHoje.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));

        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(matricula).document("dados");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nomeAluno = (String) document.get("nome");
                        String turma = (String) document.get("turma");
                        SepaeAutorizacoesEntradaAtrasada sepaeAutorizacoesEntradaAtrasada = new SepaeAutorizacoesEntradaAtrasada(nomeAluno, data, cont, quemAutorizou, turma, motivo);
                        stringList.add(sepaeAutorizacoesEntradaAtrasada);
                        AdapterSEPAEautorizacoesHoje adapter = new AdapterSEPAEautorizacoesHoje(stringList);

                         RecyclerSaidasAutorizadasHoje.setAdapter(adapter);


                    } else {
                        // O documento não existe
                        Toast.makeText(getApplicationContext(), "Erro ao procurar matrícula, matrícula inexistente", Toast.LENGTH_SHORT).show();
                        SepaeAutorizacoesEntradaAtrasada sepaeAutorizacoesEntradaAtrasada = new SepaeAutorizacoesEntradaAtrasada("Matrícula inexistente", data, cont, quemAutorizou, "Matrícula inexistente", "Matrícula inexistente");
                        stringList.add(sepaeAutorizacoesEntradaAtrasada);
                        AdapterSEPAEautorizacoesHoje adapter = new AdapterSEPAEautorizacoesHoje(stringList);

                        RecyclerSaidasAutorizadasHoje.setAdapter(adapter);

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
                        progressBarRegistrarSaida.setVisibility(View.INVISIBLE);
                        abrirDialogInfAluno(nomeAluno, turma, matricula);

                    } else {
                        // O documento não existe
                        Toast.makeText(getApplicationContext(), "Erro ao procurar matrícula, matrícula inexistente", Toast.LENGTH_SHORT).show();
                        progressBarRegistrarSaida.setVisibility(View.INVISIBLE);
                    }
                } else {
                    // Falha ao obter o documento
                    Toast.makeText(getApplicationContext(), "Falha inesperada + " + task.getException(), Toast.LENGTH_SHORT).show();
                    progressBarRegistrarSaida.setVisibility(View.INVISIBLE);
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
                progressBarRegistrarSaida.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Processando...", Toast.LENGTH_SHORT).show();
                motivoDoAtrasado(matricula);
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

    private void motivoDoAtrasado(String matricula){
        AlertDialog.Builder dialog2 = new AlertDialog.Builder(this);
        dialog2.setTitle("Justificativa" );
        dialog2.setMessage("Defina a justificativa");
        dialog2.setCancelable(false);

        EditText editText = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        editText.setLayoutParams(params);

        dialog2.setView(editText);

        dialog2.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBarRegistrarSaida.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Processando...", Toast.LENGTH_SHORT).show();
                String motivo = editText.getText().toString();
                registrarSaida(matricula, motivo);
            }
        });
        dialog2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBarRegistrarSaida.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Confira os dados e tente novamente", Toast.LENGTH_SHORT).show();
            }
        });
        dialog2.create();
        dialog2.show();
    }

    private void registrarSaida(String matricula, String motivo){
        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(matricula).document("autorizacoes");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, List<Map<String, Object>>> entradas = (Map<String, List<Map<String, Object>>>) document.get("saidaAntecipada");

                        String nome = recuperarDados("nome");
                        Timestamp novoTimestamp = Timestamp.now();

                        String dia = getDayAndMonth();

                        Map<String, Object> aux = new HashMap<>();
                        aux.put(nome, novoTimestamp);
                        aux.put("motivo", motivo);

                        if (entradas.containsKey(dia)){
                            List<Map<String, Object>> auxdia = entradas.get(dia);
                            auxdia.add(aux);
                            entradas.put(dia, auxdia);
                        } else {
                            List<Map<String, Object>> auxdia = new ArrayList<>();
                            auxdia.add(aux);
                            entradas.put(dia, auxdia);
                        }


                        Map<String, Object> updates = new HashMap<>();
                        updates.put("saidaAntecipada", entradas);


                        docRef.update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    // Sucesso na atualização
                                    Log.d("Firestore", "Documento atualizado com sucesso!");
                                    progressBarRegistrarSaida.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), "Saida Registrada com sucesso!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Erro na atualização
                                    Log.w("Firestore", "Erro ao atualizar Saida", e);
                                    progressBarRegistrarSaida.setVisibility(View.INVISIBLE);
                                });

                        atualizarSEPAE(matricula, novoTimestamp, nome, motivo);


                    } else {
                        Log.d("TAGG", "Documento de turma não encontrado");
                    }
                } else {
                    Log.d("TAGG", "Falhou em ", task.getException());

                }
            }
        });
    }

    private void atualizarSEPAE(String matricula, Timestamp timestamp, String quem, String motivo){
        Toast.makeText(getApplicationContext(), "Atualizando SEPAE...", Toast.LENGTH_SHORT).show();
        progressBarRegistrarSaida.setVisibility(View.VISIBLE);
        DocumentReference docRef = db.collection("MaisInformacoes").document("autorizacoes");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Map<String, List<Map<String, Object>>>> entradas = (Map<String, Map<String, List<Map<String, Object>>>>) document.get("saidasAntecipadas");

                        String dia = getDayAndMonth();


                        if (entradas.containsKey(dia)){
                            Map<String, List<Map<String, Object>>> auxdia = (Map<String, List<Map<String, Object>>>) entradas.get(dia);
                            Map<String, Object> a = new HashMap<>();
                            a.put(quem, timestamp);
                            a.put("motivo", motivo);
                            if (auxdia.containsKey(matricula)){
                                auxdia.get(matricula).add(a);
                            }else{
                                List<Map<String, Object>> b = new ArrayList<>();
                                b.add(a);
                                auxdia.put(matricula, b);
                            }
                        } else {
                            Map<String, List<Map<String, Object>>> auxdia = new HashMap<>();
                            Map<String, Object> a = new HashMap<>();
                            a.put(quem, timestamp);
                            a.put("motivo", motivo);
                            List<Map<String, Object>> b = new ArrayList<>();
                            b.add(a);
                            auxdia.put(matricula, b);
                            entradas.put(dia, auxdia);
                        }


                        Map<String, Object> updates = new HashMap<>();
                        updates.put("saidasAntecipadas", entradas);


                        docRef.update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    // Sucesso na atualização
                                    Log.d("Firestore", "SEPAE atualizada com sucesso!");
                                    progressBarRegistrarSaida.setVisibility(View.INVISIBLE);
                                    mostrarAutorizacoesHoje(dia);
                                    Toast.makeText(getApplicationContext(), "SEPAE Registrada com sucesso!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Erro na atualização
                                    Log.w("Firestore", "Erro ao atualizar documento", e);
                                    progressBarRegistrarSaida.setVisibility(View.INVISIBLE);
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

                procurarDadosAluno(aux.toString());

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