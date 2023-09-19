package com.example.meuif;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meuif.autorizacoes.RecyclerItemClickListener;
import com.example.meuif.chamadaLideres.AdapterChamadaLideres;
import com.example.meuif.sepae.autorizacoes.AdapterSEPAEautorizacoesHoje;
import com.example.meuif.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class tela_chamada_dia extends AppCompatActivity {
    private Button botaoDias;
    private TextView saidaData;
    private ProgressBar progressBarChamada;
    private String diaAtual;
    private RecyclerView recyclerChamdaLider;
    private Boolean TercaQuinta;
    private String turma;
    private Button botaoSalvar;
    private FirebaseFirestore db;
    private String dataBDAtual;
    private List<AlunoChamada> listaAlunos = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_chamada_dia);

        botaoDias = findViewById(R.id.botaoDias);
        botaoDias.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        botaoDias.setPadding(30, botaoDias.getPaddingTop(), 15, botaoDias.getPaddingBottom());
        saidaData = findViewById(R.id.saidaData);
        botaoSalvar = findViewById(R.id.botaoSalvar);
        progressBarChamada = findViewById(R.id.progressBarChamada);
        recyclerChamdaLider = findViewById(R.id.recyclerChamdaLider);

        progressBarChamada.setVisibility(View.VISIBLE);

        //muda a cor do progressBar pra preto
        progressBarChamada.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

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

        TercaQuinta = isTercaOuQuinta();
        Log.d("dia", TercaQuinta.toString());

        pegarDadosChamada();



        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                diaAtual = diaAtual();
                salvarChamadaBD(dataBDAtual);
            }
        });
    }

    private void salvarChamadaBD(String diaAtual) {
        progressBarChamada.setVisibility(View.VISIBLE);

        String chamadaTarde = diaAtual + "T";

        Map<String, Boolean> mapDataChamada = new HashMap<>();

        if (isTercaOuQuinta()){
            Map<String, Boolean> mapDataChamada2 = new HashMap<>();

            for(AlunoChamada aluno: listaAlunos){
                String nomeCompleto = "";
                String nome = aluno.getNome();
                Boolean b = aluno.getChamadaTurno1();
                Boolean b2 = aluno.getChamadaTurno2();

                // Usar uma expressão regular para encontrar o nome
                Pattern pattern = Pattern.compile("\\d+ - (.+)");
                Matcher matcher = pattern.matcher(nome);

                if (matcher.find()) {
                    nomeCompleto = matcher.group(1); // O grupo 1 contém o nome
                }

                mapDataChamada.put(nomeCompleto, b);
                mapDataChamada2.put(nomeCompleto, b2);
            }

            if (!mapDataChamada2.isEmpty()) {
                db.collection("ChamadaTurma").document(turma)
                        .update(chamadaTarde, mapDataChamada2).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG", "Campo do tipo Map criado com sucesso!");
                                abrirToast("Chamada Turno Tarde Salvo Com Sucesso");
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
        } else {
            for(AlunoChamada aluno: listaAlunos){
                String nomeCompleto = "";
                String nome = aluno.getNome();
                Boolean b = aluno.getChamadaTurno1();

                // Usar uma expressão regular para encontrar o nome
                Pattern pattern = Pattern.compile("\\d+ - (.+)");
                Matcher matcher = pattern.matcher(nome);

                if (matcher.find()) {
                    nomeCompleto = matcher.group(1); // O grupo 1 contém o nome
                }

                mapDataChamada.put(nomeCompleto, b);
            }
        }


        if (!mapDataChamada.isEmpty()){
            db.collection("ChamadaTurma").document(turma)
                    .update(diaAtual, mapDataChamada).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("TAG", "Campo do tipo Map criado com sucesso!");
                            abrirToast("Chamada Turno Manhã Salva Com Sucesso");
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
    }

    public static boolean isTercaOuQuinta() {
        // Obtém uma instância do Calendário com a data e hora atuais
        Calendar calendario = Calendar.getInstance();

        // Define a zona de tempo para Brasília
        calendario.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));

        // Obtém o dia da semana (0 = Domingo, 1 = Segunda, 2 = Terça, etc.)
        int diaDaSemana = calendario.get(Calendar.DAY_OF_WEEK);

        // Verifique se é terça-feira (3) ou quinta-feira (5)
        return diaDaSemana == Calendar.TUESDAY || diaDaSemana == Calendar.THURSDAY;
    }


    private void pegarDadosChamada() {
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
                            String tardeChamda = dataBDAtual + "T";
                            Object fieldValue = document.get(dataBDAtual);

                            if (isTercaOuQuinta()){

                                Object fieldValue2 = document.get(tardeChamda);

                                if (fieldValue instanceof Map && fieldValue2 instanceof Map) {

                                    Map<String, Boolean> mapData = (Map<String, Boolean>) fieldValue;
                                    Map<String, Boolean> mapData2 = (Map<String, Boolean>) fieldValue2;

                                    for (Map.Entry<String, Boolean> entry : mapData.entrySet()) {
                                        String key = entry.getKey();
                                        Boolean value = entry.getValue();
                                        Boolean value2 = mapData2.get(key);
                                        AlunoChamada aluno = new AlunoChamada(value, value2, key);
                                        Log.d("TAG", "Key: " + key + ", Value: " + value);
                                        Log.d("aluno", "aluno: " + aluno.getNome() + " 1 " + aluno.getChamadaTurno1() + " " + aluno.getChamadaTurno2());
                                        listaAlunos.add(aluno);

                                    }
                            } else {
                                    if (fieldValue instanceof Map) {
                                        Map<String, Boolean> mapData = (Map<String, Boolean>) fieldValue;


                                        for (Map.Entry<String, Boolean> entry : mapData.entrySet()) {

                                            String key = entry.getKey();
                                            Boolean value = entry.getValue();

                                            AlunoChamada aluno = new AlunoChamada(value, null, key);

                                            listaAlunos.add(aluno);

                                        }
                                    }
                                }

                                Log.d("aluno", listaAlunos.toString());
                                mostrarChamda(listaAlunos);

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

    private void mostrarChamda(List<AlunoChamada> listaAlunos){
        progressBarChamada.setVisibility(View.VISIBLE);
        // Ordene a lista pelo nome
        Collections.sort(listaAlunos, new Comparator<AlunoChamada>() {
            @Override
            public int compare(AlunoChamada aluno1, AlunoChamada aluno2) {
                // Comparar os nomes dos alunos
                return aluno1.getNome().compareTo(aluno2.getNome());
            }
        });
        int count = 1;

        for (AlunoChamada aluno : listaAlunos){
            aluno.setNome(String.valueOf(count) + " - " + aluno.getNome());
            count++;
        }

        AdapterChamadaLideres adapter = new AdapterChamadaLideres(listaAlunos, isTercaOuQuinta());

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerChamdaLider.setLayoutManager(layoutManager);
        recyclerChamdaLider.setHasFixedSize(true);
        recyclerChamdaLider.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerChamdaLider.setAdapter(adapter);
        progressBarChamada.setVisibility(View.INVISIBLE);

    }



    private void abrirToast(String texto){
        Toast.makeText(
                getApplicationContext(),
                texto,
                Toast.LENGTH_LONG
        ).show();
    }
    private String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
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

                            TercaQuinta = isTercaOuQuinta();

                            if (TercaQuinta){
                                for (String stringValue : stringArray) {
                                    AlunoChamada alunoChamada = new AlunoChamada(true,
                                            true,
                                            stringValue);
                                    listaAlunos.add(alunoChamada);
                                }
                            } else {
                                for (String stringValue : stringArray) {
                                    AlunoChamada alunoChamada = new AlunoChamada(true,
                                            null,
                                            stringValue);
                                    listaAlunos.add(alunoChamada);
                                }
                            }

                            Log.d("aluno", listaAlunos.toArray().toString());
                            mostrarChamda(listaAlunos);
                            progressBarChamada.setVisibility(View.INVISIBLE);


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