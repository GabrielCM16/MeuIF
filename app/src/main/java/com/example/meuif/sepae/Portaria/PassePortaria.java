package com.example.meuif.sepae.Portaria;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meuif.CaptureAct;
import com.example.meuif.Informacoes_pessoais;
import com.example.meuif.R;
import com.example.meuif.databinding.FragmentInformacoesPessoaisBinding;
import com.example.meuif.events.Events;
import com.example.meuif.events.SalvarEvento;
import com.example.meuif.events.TelaNovoEvento;
import com.example.meuif.faltasPessoais.AdapterAcessosAluno;
import com.example.meuif.faltasPessoais.ModelAcessoAluno;
import com.example.meuif.sepae.telaMerendaEscolar;
import com.example.meuif.sepae.telaPrincipalSepae;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class PassePortaria extends AppCompatActivity {

    private FirebaseFirestore db;
    private ConstraintLayout botao;
    private long tempoValidade = 30000;
    private MediaPlayer mediaPlayer;
    private MediaPlayer sucessPlayer;
    final PassePortaria activity= this;
    private EditText entradaMatriculaAcesso;
    private ConstraintLayout constraintRegistrarAcesso;
    private ImageView imageViewEsquerdaCarteirinhaSEPAE;
    private ImageView imageViewDireitaCarteirinhaSEPAE;
    private TextView textViewSaidaDiaAcessosCarteirinhaSEPAE;
    private ImageView imageViewCalendarioAcessoSEPAE;
    private Map<String, String> nomesAlunos = new HashMap<>();
    private Map<String, String> turmasAlunos = new HashMap<>();
    private RecyclerView recyclerViewAcessosRegistradosSEPAE;
    private String lastedMatricula = "";
    private int countLastedMatricula = 0;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passe_portaria);
        db = FirebaseFirestore.getInstance();
        pegarNomesAlunos();
        pegarTurmaAlunos();
        carregarComponentes();

    }

    private void carregarComponentes(){
        imageViewEsquerdaCarteirinhaSEPAE = findViewById(R.id.imageViewEsquerdaCarteirinhaSEPAE);
        imageViewDireitaCarteirinhaSEPAE = findViewById(R.id.imageViewDireitaCarteirinhaSEPAE);
        textViewSaidaDiaAcessosCarteirinhaSEPAE = findViewById(R.id.textViewSaidaDiaAcessosCarteirinhaSEPAE);
        textViewSaidaDiaAcessosCarteirinhaSEPAE.setText(diaAtualB());
        imageViewCalendarioAcessoSEPAE = findViewById(R.id.imageViewCalendarioAcessoSEPAE);
        botao = findViewById(R.id.botaoPasse);
        entradaMatriculaAcesso = findViewById(R.id.entradaMatriculaAcesso);
        constraintRegistrarAcesso = findViewById(R.id.constraintRegistrarAcesso);
        recyclerViewAcessosRegistradosSEPAE = findViewById(R.id.recyclerViewAcessosRegistradosSEPAE);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.error);
        sucessPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sucess);
        ActionBar actionBar = getSupportActionBar();
        setTitle("Acessos Portaria");
        // Adiciona um ícone de ação à direita
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24); // Define o ícone de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilita o botão de navegação
        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sCanCode();
            }
        });
        imageViewEsquerdaCarteirinhaSEPAE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menosUmDia();
            }
        });
        imageViewDireitaCarteirinhaSEPAE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maisUmDia();
            }
        });
        constraintRegistrarAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = diaAtual();
                String aux = entradaMatriculaAcesso.getText().toString();
                if (aux != null && !aux.equals("")){
                    atualizarMatricula(aux, data);
                    atualizarAcessoSepae(aux);
                    Toast.makeText(getApplicationContext(), "Acesso de aux Registrado", Toast.LENGTH_LONG).show();
                    entradaMatriculaAcesso.setText("");
                    pegarAcessosPorDia(formatarData(textViewSaidaDiaAcessosCarteirinhaSEPAE.getText().toString()));
                } else {
                    Toast.makeText(getApplicationContext(), "Matricula Invalida", Toast.LENGTH_LONG).show();
                }

            }
        });

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

    private void menosUmDia(){
        String diaHj = textViewSaidaDiaAcessosCarteirinhaSEPAE.getText().toString();
        String dia = diaHj.substring(0,2);
        if (!dia.contains("/")){
            int i = Integer.valueOf(dia);
            if (i > 1){
                i -= 1;
                String mesAno = diaHj.substring(2);
                String aux = String.valueOf(i) + mesAno;
                textViewSaidaDiaAcessosCarteirinhaSEPAE.setText(aux);
            }
        } else {
            dia = diaHj.substring(0,1);
            int i = Integer.valueOf(dia);
            if (i > 1){
                i -= 1;
                String mesAno = diaHj.substring(1);
                String aux = String.valueOf(i) + mesAno;
                textViewSaidaDiaAcessosCarteirinhaSEPAE.setText(aux);
            }
        }
       pegarAcessosPorDia(formatarData(textViewSaidaDiaAcessosCarteirinhaSEPAE.getText().toString()));
    }

    public String formatarData(String data) {
        // Remova as barras da data original
        String dataFormatada = data.replace("/", "");

        // Certifique-se de que a data tem pelo menos 8 caracteres (DDMMAAAA)
        while (dataFormatada.length() < 8) {
            dataFormatada = "0" + dataFormatada;
        }

        return dataFormatada;
    }

    private void pegarAcessosPorDia(String dia){
        DocumentReference docRef = db.collection("AcessosCampus").document("AcessosAlunos");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.contains(dia)){
                            List<Map<String, Timestamp>> lista = (List<Map<String, Timestamp>>) document.get(dia);
                            carregarAcessos(lista);
                        } else {
                            List<Map<String, Timestamp>> lista  = new ArrayList<>();
                            carregarAcessos(lista);
                        }
                    } else {
                        Log.d("TAGLER", "Documento não encontrado");
                    }
                } else {
                    Log.d("TAGLER", "Falhou em ", task.getException());
                }
            }
        });
    }

    private void carregarAcessos(List<Map<String, Timestamp>> lista){
        List<ModelAcessoAluno> modelAcessoAlunos = new ArrayList<>();
        int count = lista.size();
        Map<String, String> flag = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-3"));

// Primeiro loop para definir as flags pessoais
        for (Map<String, Timestamp> map : lista) {
            for (Map.Entry<String, Timestamp> entry : map.entrySet()) {
                String matricula = entry.getKey();
                String flagPessoal = flag.getOrDefault(matricula, "Saida"); // Obter a flag anterior ou "Saida" como padrão
                flag.put(matricula, flagPessoal.equals("Entrada") ? "Saida" : "Entrada");
            }
        }

        int lastIndex = lista.size() - 1;

        for (int i = lastIndex; i >= 0; i--) {
            Map<String, Timestamp> map = lista.get(i);

            for (Map.Entry<String, Timestamp> entry : map.entrySet()) {
                String matricula = entry.getKey();
                Timestamp valor = entry.getValue();
                Date date = valor.toDate();
                String formattedDate = sdf.format(date);

                String nome = nomesAlunos.getOrDefault(matricula, "Erro Em Nome");
                String flagPessoal = flag.get(matricula);
                String turmaAluno = turmasAlunos.getOrDefault(matricula, "Erro na Matricula");

                String turmaMatricula = matricula + " - " + turmaAluno;

                ModelAcessoAluno modelAcessoAluno = new ModelAcessoAluno(nome, formattedDate, String.valueOf(count), flagPessoal, turmaMatricula);
                modelAcessoAlunos.add(modelAcessoAluno);
                count--;
                flagPessoal = flagPessoal.equals("Entrada") ? "Saida" : "Entrada";
                flag.put(matricula, flagPessoal);
            }
        }

        AdapterAcessosAluno adapter = new AdapterAcessosAluno(modelAcessoAlunos);

// Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewAcessosRegistradosSEPAE.setLayoutManager(layoutManager);
        recyclerViewAcessosRegistradosSEPAE.setHasFixedSize(true);
        recyclerViewAcessosRegistradosSEPAE.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerViewAcessosRegistradosSEPAE.setAdapter(adapter);

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
                        Log.d("TAG", "turmas alunos ==== " + turmasAlunos.toString());
                        pegarAcessosPorDia(formatarData(textViewSaidaDiaAcessosCarteirinhaSEPAE.getText().toString()));

                    } else {
                        // O documento não existe
                    }
                } else {
                    // Falha ao obter o documento
                }
            }
        });

    }

    private void maisUmDia(){
        Map<Integer, Integer> meses = new HashMap<>();
        meses.put(1,31);
        meses.put(2,28);
        meses.put(3,31);
        meses.put(4,30);
        meses.put(5,31);
        meses.put(6,30);
        meses.put(7,31);
        meses.put(8,31);
        meses.put(9,30);
        meses.put(10,31);
        meses.put(11,30);
        meses.put(12,31);

        String diaHj = textViewSaidaDiaAcessosCarteirinhaSEPAE.getText().toString();
        String dia = diaHj.substring(0,2);

        String mes = diaHj.substring(3,5);
        if (!mes.contains("/")){
            if (meses.containsKey(Integer.valueOf(mes))){
                int num = meses.get(Integer.valueOf(mes));
                if (!dia.contains("/")){
                    int i = Integer.valueOf(dia);
                    if (i < num){
                        i += 1;
                        String mesAno = diaHj.substring(2);
                        String aux = String.valueOf(i) + mesAno;
                        textViewSaidaDiaAcessosCarteirinhaSEPAE.setText(aux);
                    }
                } else {
                    dia = diaHj.substring(0,1);
                    int i = Integer.valueOf(dia);
                    if (i < num){
                        i += 1;
                        String mesAno = diaHj.substring(1);
                        String aux = String.valueOf(i) + mesAno;
                        textViewSaidaDiaAcessosCarteirinhaSEPAE.setText(aux);
                    }
                }
            }
        } else {
            mes = diaHj.substring(2,4);
            if (meses.containsKey(Integer.valueOf(mes))){
                int num = meses.get(Integer.valueOf(mes));
                if (!dia.contains("/")){
                    int i = Integer.valueOf(dia);
                    if (i < num){
                        i += 1;
                        String mesAno = diaHj.substring(2);
                        String aux = String.valueOf(i) + mesAno;
                        textViewSaidaDiaAcessosCarteirinhaSEPAE.setText(aux);
                    }
                } else {
                    dia = diaHj.substring(0,1);
                    int i = Integer.valueOf(dia);
                    if (i < num){
                        i += 1;
                        String mesAno = diaHj.substring(1);
                        String aux = String.valueOf(i) + mesAno;
                        textViewSaidaDiaAcessosCarteirinhaSEPAE.setText(aux);
                    }
                }
            }
        }
        pegarAcessosPorDia(formatarData(textViewSaidaDiaAcessosCarteirinhaSEPAE.getText().toString()));
    }

    private String diaAtual(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-3"));

        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int ano = calendar.get(Calendar.YEAR);

        String data = String.format("%02d%02d%d", dia, mes, ano);
        return data;
    }
    private String diaAtualB(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-3"));

        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int ano = calendar.get(Calendar.YEAR);

        String data = String.format("%02d/%02d/%d", dia, mes, ano);
        return data;
    }

    private void playErrorSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private void playSucessSound() {
        if (sucessPlayer != null) {
            sucessPlayer.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Libere os recursos do MediaPlayer ao encerrar a atividade
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (sucessPlayer != null) {
            sucessPlayer.release();
            sucessPlayer = null;
        }
    }

    private void sCanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCameraId(1);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
            String aux = result.getContents();
            if (aux.length() == 11 && (!lastedMatricula.equals(aux) || countLastedMatricula >= 2)) {
                //String[] aux = result.getContents().split("/");

                //long valorCurrent = Long.parseLong(aux[1]);

               // if ( System.currentTimeMillis() - valorCurrent <= tempoValidade){
                    String data = diaAtual();
                    atualizarMatricula(aux, data);
                     atualizarAcessoSepae(aux);
                //} else {
               //     playSuccessSound();
               // }
                countLastedMatricula = 0;
                lastedMatricula = aux;
                sCanCode();
            } else {
                Toast.makeText(this,"QR-Code Invalido ou repetido", Toast.LENGTH_SHORT).show();;
                countLastedMatricula++;
                playErrorSound();
                sCanCode();
            }
        }
    });

    private void atualizarAcessoSepae(String matricula){
        String data = diaAtual();
        Timestamp novoTimestamp = Timestamp.now();

        DocumentReference docRef = db.collection("AcessosCampus").document("AcessosAlunos");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Map<String, Timestamp>> passes;

                if (task.getResult().exists() && task.getResult().contains(data)) {
                    passes = (List<Map<String, Timestamp>>) task.getResult().get(data);
                    Log.d("sim", "sim " + passes.toString());
                } else {
                   passes = new ArrayList<>();
                }

                Map<String, Timestamp> a = new HashMap<>();
                a.put(matricula, novoTimestamp);

                passes.add(a);

                Log.d("sim", "nao + " + passes.toString());

                docRef.update(data, passes)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                playSucessSound();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                playErrorSound();
                            }
                        });
            } else {
                System.out.println("Erro ao obter o documento: " + task.getException().getMessage());
                playErrorSound();
            }
        });
    }

    private void atualizarMatricula(String matricula, String data){

        Timestamp novoTimestamp = Timestamp.now();

        // Envie os dados para o Firestore
        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(matricula)
                .document("chamadaPessoal");

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Timestamp> timestampsList;

                // Verifique se o documento existe e se o campo "timestamps" já foi criado
                if (task.getResult().exists() && task.getResult().contains(data)) {
                    timestampsList = (List<Timestamp>) task.getResult().get(data);
                } else {
                    // Se o documento não existir ou o campo da data não tiver sido criado, crie uma nova lista vazia
                    timestampsList = new ArrayList<>();
                    //adicionando uma presença a mais
                    if (task.getResult().exists() && task.getResult().contains("presencas")){
                        DocumentSnapshot document = task.getResult();
                        String valorPresenca = document.getString("presencas");

                        // Converta o valor atual para inteiro
                        int valorAtualInt = Integer.parseInt(valorPresenca);

                        // Atualize o campo com o novo valor convertido em string
                        docRef.update("presencas", String.valueOf(valorAtualInt + 1))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        playSucessSound();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        playErrorSound();
                                    }
                                });

                    } else {
                        System.out.println("O documento não existe.");
                        playErrorSound();
                    }
                }

                // Adicione o novo timestamp à lista
                timestampsList.add(novoTimestamp);

                // Use o método update() para atualizar o campo "timestamps" no documento
                docRef.update(data, timestampsList)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                playSucessSound();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                playErrorSound();
                            }
                        });

                if (task.getResult().exists() && task.getResult().contains("possivelStatus")){
                    DocumentSnapshot document = task.getResult();
                    String status = document.getString("possivelStatus");

                    if (status.equals("Entrada")){
                        docRef.update("possivelStatus", "Saida")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        playSucessSound();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        playErrorSound();
                                    }
                                });
                    } else if (status.equals("Saida")){
                        docRef.update("possivelStatus", "Entrada")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        playSucessSound();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        playErrorSound();
                                    }
                                });
                    }
                }
            } else {
                System.out.println("Erro ao obter o documento: " + task.getException().getMessage());
                playErrorSound();
            }
        });
    }
}