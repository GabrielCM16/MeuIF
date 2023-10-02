package com.example.meuif.sepae;


import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meuif.CaptureAct;
import com.example.meuif.R;
import com.example.meuif.sepae.Merenda.GraficosMerenda;
import com.example.meuif.sepae.recyclerMerenda.AdapterMerenda;
import com.example.meuif.sepae.recyclerMerenda.AlunoMerenda;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class telaMerendaEscolar extends AppCompatActivity {

    private FirebaseFirestore db;
    private Button botao;
    private AdapterMerenda adapter;
    private TextView saidaNumero;
    private List<String> stringList = new ArrayList<>();
    private long tempoValidade = 30000;
    private MediaPlayer somErro;
    private static Pattern p = Pattern.compile("[0-9]+");
    private MediaPlayer somSucess;
    private String ultimaMatricula = " ";
    private Boolean isNumeric;
    private Spinner spinnerMesesMerenda;
    private Spinner spinnerDiasMerenda;
    private Spinner spinnerTurmasMerenda;
    private List<String> dias = new ArrayList<>();
    private List<String> meses = new ArrayList<>();
    private String turma = "";
    private Map<String, Object> dataGlobal = new HashMap<>();
    private Map<String, String> mesesAno = new HashMap<>();
    private String diaSelecionado;
    private List<String> listDias = new ArrayList<>();
    private Map<String, Object> listar = new HashMap<>();
    private RecyclerView listardiasMerenda;
    private List<AlunoMerenda> alunosList = new ArrayList<>();
    private final Map<String, String> nomesAlunos = new HashMap<>();
    private final Map<String, String> turmasAlunos = new HashMap<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_merenda_escolar);

        carregarComponentes();

        carregarCamera();
    }

    private void carregarComponentes(){
        botao = findViewById(R.id.botaoCamera);
        db = FirebaseFirestore.getInstance();
        saidaNumero = findViewById(R.id.saidaNumero);
        spinnerDiasMerenda = findViewById(R.id.spinnerDiasMerenda);
        spinnerMesesMerenda = findViewById(R.id.spinnerMesesMerenda);
        spinnerTurmasMerenda = findViewById(R.id.spinnerTurmasMerenda);
        listardiasMerenda = findViewById(R.id.listardiasMerenda);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_actionbar);
        TextView titleText = findViewById(R.id.titleText);
        ImageView leftImage = findViewById(R.id.leftImage);
        ImageView rightImage = findViewById(R.id.rightImage); //baixar pdf
        ImageView imageTeclado = findViewById(R.id.imageTeclado);
        imageTeclado.setVisibility(View.VISIBLE);

        rightImage.setImageResource(R.drawable.baseline_insert_chart_outlined_24);

        titleText.setText("Merenda Escolar");

        imageTeclado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTeclado();
            }
        });

        leftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaVoltar();
            }
        });

        rightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { telaGraficos();
                            }
        });
    }

    protected void onStart() {

        super.onStart();
        pegarNomesAlunos(new Callback() {
            @Override
            public void onComplete() {
                pegarTurmaAlunos(new Callback() {
                    @Override
                    public void onComplete() {
                        setarSpinnerTurmas(new Callback() {
                            @Override
                            public void onComplete() {
                                listarDiasMerendados(new Callback() {
                                    @Override
                                    public void onComplete() {

                                        Log.d("TAG", "data" + dataGlobal.toString());

                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void abrirTeclado(){
        Log.d("teclado", "teclado");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_entrada_merenda, null);
        builder.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.editText);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String textoDigitado = editText.getText().toString();
                // Faça algo com o texto inserido, por exemplo, exiba-o em um Toast
                procurarDadosAluno(textoDigitado);
                dialog.dismiss(); // Fecha o diálogo
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
                       // .setVisibility(View.INVISIBLE);
                        abrirDialogInfAluno(nomeAluno, turma, matricula);

                    } else {
                        // O documento não existe
                        Toast.makeText(getApplicationContext(), "Erro ao procurar matrícula, matrícula inexistente", Toast.LENGTH_SHORT).show();
                      //  progressBarRegistrarEntrada.setVisibility(View.INVISIBLE);
                    }
                } else {
                    // Falha ao obter o documento
                    Toast.makeText(getApplicationContext(), "Falha inesperada + " + task.getException(), Toast.LENGTH_SHORT).show();
                   // progressBarRegistrarEntrada.setVisibility(View.INVISIBLE);
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
              //  progressBarRegistrarEntrada.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Processando...", Toast.LENGTH_SHORT).show();
              //  motivoDoAtrasado(matricula);
                atualizarMerenda(matricula, diaAtual(), new Callback() {
                    @Override
                    public void onComplete() {
                        playSucessSound();
                        Toast.makeText(getApplicationContext(), "Registro Realizado com sucesso", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void telaGraficos(){
        // Criar a Intent
        Intent intent = new Intent(getApplicationContext(), GraficosMerenda.class);
        // Iniciar a atividade de destino
        startActivity(intent);
        finish();
    }

    private void pegarNomesAlunos(Callback callback){
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
                        callback.onComplete();

                    } else {
                        // O documento não existe
                        callback.onComplete();
                    }
                } else {
                    // Falha ao obter o documento
                    callback.onComplete();
                }
            }
        });
    }

    private void pegarTurmaAlunos(Callback callback){
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
                        callback.onComplete();

                    } else {
                        // O documento não existe
                        callback.onComplete();
                    }
                } else {
                    // Falha ao obter o documento
                    callback.onComplete();
                }
            }
        });
    }

    private void setarSpinnerTurmas(Callback callback){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.turmas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurmasMerenda.setAdapter(adapter);

        spinnerTurmasMerenda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dias.clear();
                String selectedTurma = parent.getItemAtPosition(position).toString();

                turma = selectedTurma;
                callback.onComplete();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ação a ser tomada quando nada é selecionado (opcional)
            }
        });
    }

    private void setarSpinnerDias(List<String> lista){
        // Converter a List<String> em um array de strings simples
        Log.d("TAG", "lista por agrs" + lista.toString());
        String[] dataArray = new String[listDias.size()];
        listDias.toArray(dataArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiasMerenda.setAdapter(adapter);

        spinnerDiasMerenda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //dias.clear();
                String selectedDia = parent.getItemAtPosition(position).toString();

                diaSelecionado = selectedDia;
                Log.d("TAG", "dia sim " + diaSelecionado + lista.toString());
                for (int i = 0; i<listDias.size(); i++){
                    if (diaSelecionado.equals(dias.get(i).substring(0,2))){
                        Log.d("TAG", "dia" + lista.get(i));
                        atualizarRecycler(lista.get(i));
                        break;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ação a ser tomada quando nada é selecionado (opcional)
            }
        });
    }

    private void atualizarRecycler(String dia){
        alunosList.clear();
        Log.d("TAG", "naos sei mais" + dia);
        Object objeto = listar.get(dia);
        if (objeto != null) {
            Log.d("TAG", "teste22" + objeto.toString());
            if (objeto instanceof List) {
                // Defina o fuso horário UTC-3
                TimeZone timeZone = TimeZone.getTimeZone("GMT-3");

                // Crie um SimpleDateFormat usando o fuso horário definido
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                sdf.setTimeZone(timeZone);
                List<Map<String, Timestamp>> listaDeMapas = (List<Map<String, Timestamp>>) objeto;

                int auxNumero = 0;

                for (Map<String, Timestamp> mapa : listaDeMapas) {
                    for (Map.Entry<String, Timestamp> entry : mapa.entrySet()) {
                        String chave = entry.getKey();
                        Timestamp timestamp = entry.getValue();
                        String dataFormatada = sdf.format(timestamp.toDate());

                        auxNumero += 1;

                        Log.d("TAG", "Chave obj: " + chave + "Timestamp obj: " + dataFormatada + " nome na lista " + nomesAlunos.get(chave));
                        String nome = nomesAlunos.getOrDefault(chave, "Erro em nome");
                        String turmaAux = turmasAlunos.getOrDefault(chave, "Erro Turma");
                        String aux = chave + " - " + turmaAux;
                        if (turma.equals("Todas")) {
                            AlunoMerenda aluno = new AlunoMerenda(nome, aux, dataFormatada, String.valueOf(auxNumero));
                            alunosList.add(aluno);
                        } else if (turma.equals(turmaAux)){
                            AlunoMerenda aluno = new AlunoMerenda(nome, aux, dataFormatada, String.valueOf(auxNumero));
                            alunosList.add(aluno);
                        } else {
                            continue;
                        }


                    }
                }
            } else {
                Log.d("TAG", "Nao é um obj");
            }

            //seta recycler
            saidaNumero.setText(String.valueOf("Total de registros: " + alunosList.size()));
            AdapterMerenda adapter = new AdapterMerenda(alunosList);


            //Configurar RecyclerView
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            listardiasMerenda.setLayoutManager(layoutManager);
            listardiasMerenda.setHasFixedSize(true);
            listardiasMerenda.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
            listardiasMerenda.setAdapter(adapter); //criar adapter


        } else{
            Log.d("TAG", "nullo");
        }


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
        Intent intent = new Intent(getApplicationContext(), telaPrincipalSepae.class);
        // Iniciar a atividade de destino
        startActivity(intent);
        finish();
    }

    private interface Callback {
        void onComplete();
    }



    private void playErrorSound() {
        if (somErro != null) {
            somErro.start();
        }
    }

    private void playSucessSound() {
        if (somSucess != null) {
            somSucess.start();
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        // Libere os recursos do MediaPlayer ao encerrar a atividade
        if (somErro != null) {
            somErro.release();
            somErro = null;
        }

        if (somSucess != null) {
            somSucess.release();
            somSucess = null;
        }
    }

    private void carregarCamera(){
        // Inicialize o MediaPlayer com o arquivo de som do sucesso (success_sound.mp3 ou success_sound.wav)
        somErro = MediaPlayer.create(getApplicationContext(), R.raw.error);
        somSucess = MediaPlayer.create(getApplicationContext(), R.raw.sucess);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sCanCode();
            }
        });
    }

    private void sCanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
                String aux = result.getContents();
                isNumeric = (aux != null && p.matcher(aux).find());

                if ( aux.length() == 11 && isNumeric){
                    if (!ultimaMatricula.equals(aux)){
                        String data = diaAtual();
                        atualizarMerenda(aux, data, new Callback() {
                            @Override
                            public void onComplete() {
                                playSucessSound();
                                ultimaMatricula = aux;
                            }
                        });
                    } else{
                        playErrorSound();
                    }
                } else {
                    playErrorSound();
                }

                sCanCode();

        }
    });



    private String diaAtual(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-3"));

        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int ano = calendar.get(Calendar.YEAR);

        String data = String.format("%02d%02d%d", dia, mes, ano);
        return data;
    }

    private void atualizarMerenda(String matricula, String data, Callback callback){

        Timestamp novoTimestamp = Timestamp.now();

        DocumentReference docRef = db.collection("MerendaEscolar").document(data);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // O documento existe, atualize um campo nele
                    String turma = recuperarDados("turma");
                    List<Map<String, Timestamp>> existingList = (List<Map<String, Timestamp>>) document.get("todos");

                    Log.d("TAG", existingList.toString());

                    Map<String, Timestamp> aux = new HashMap<String, Timestamp>();
                    aux.put(matricula, novoTimestamp);

                    existingList.add(aux);

                    docRef.update("todos", existingList);
                    callback.onComplete();
                } else {
                    // O documento não existe, crie-o
                    Map<String, Object> dados = new HashMap<>();
                    dados.put(matricula, novoTimestamp); // Insere o timestamp atual

// Cria um array com um mapa dentro dele
                    List<Map<String, Object>> array = new ArrayList<>();
                    array.add(dados);

// Cria um mapa para o campo "todos"
                    Map<String, Object> mapaTodos = new HashMap<>();
                    mapaTodos.put("todos", array);

// Insere os dados no documento
                    docRef.set(mapaTodos)
                            .addOnSuccessListener(aVoid -> {
                                // Sucesso ao criar o documento
                            })
                            .addOnFailureListener(e -> {
                                // Falha ao criar o documento
                            });
                    callback.onComplete();
                }
            } else {
                // Houve um erro ao buscar o documento
                Log.d("Firestore", "Erro: " + task.getException());
                callback.onComplete();
            }
        });


        //atualizar aluno agora
        DocumentReference usuarioDocRef = db.collection("Usuarios").document("Alunos").collection(matricula).document("merendaPessoal");

        usuarioDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    boolean flagAtual = document.getBoolean("flag");

                    // Criar um novo valor para a flag (inverter o valor booleano)
                    boolean novoValorFlag = !flagAtual;

                    // Atualizar a flag no documento
                    usuarioDocRef.update("flag", novoValorFlag)
                            .addOnSuccessListener(aVoid -> {
                                // Sucesso ao atualizar a flag
                                Log.d("Firestore", "Flag atualizada com sucesso");
                            })
                            .addOnFailureListener(e -> {
                                // Falha ao atualizar a flag
                                Log.e("Firestore", "Erro ao atualizar a flag", e);
                            });

                    // Adicionar um timestamp ao array "passes"
                    List<String> passes = (List<String>) document.get("passes");
                    passes.add(String.valueOf(novoTimestamp)); // Adiciona um timestamp em milissegundos
                    usuarioDocRef.update("passes", passes)
                            .addOnSuccessListener(aVoid -> {
                                // Sucesso ao adicionar o timestamp
                                Log.d("Firestore", "Timestamp adicionado com sucesso");
                            })
                            .addOnFailureListener(e -> {
                                // Falha ao adicionar o timestamp
                                Log.e("Firestore", "Erro ao adicionar o timestamp", e);
                            });
                } else {
                    Log.d("Firestore", "Documento não encontrado");
                }
            } else {
                Log.d("Firestore", "Falha em obter o documento", task.getException());
            }
        });

    }

    private String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }

    private void listarDiasMerendados(Callback callback){

        dias.clear();
        listDias.clear();

        db.collection("MerendaEscolar")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obter um mapa de campos e valores do documento
                            Map<String, Object> data = document.getData();

                            dataGlobal = data;

                            String diaCompleto = document.getId();
                            dias.add(diaCompleto);
                            listDias.add(diaCompleto.substring(0, 2));


                            // Percorrer todas as chaves e valores do mapa
                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                String chave = entry.getKey();
                                Object valor = entry.getValue();

                                listar.put(document.getId(), valor);

                                Log.d("TAG", "onde = " + document.getId()+ " " + "Chave: " + chave + ", Valor: " + valor);
                            }

                        }
                        Log.d("TAG", "listar listar" + listar.toString());
                        Log.d("TAG", "diaslist" + listDias.toString());
                        Log.d("TAG", "teste dia" + listar.get("14082023"));
                        Log.d("TAG", "todos os dias = " + dias.toString());

                        setarSpinnerDias(dias);


                        callback.onComplete();
                    } else {
                        // Tratar erro aqui, se necessário
                        callback.onComplete();
                    }
                });
    }
}