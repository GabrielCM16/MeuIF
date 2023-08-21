package com.example.meuif.sepae;


import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.meuif.CaptureAct;
import com.example.meuif.R;
import com.example.meuif.sepae.recyclerMerenda.AdapterMerenda;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class telaMerendaEscolar extends AppCompatActivity {

    private FirebaseFirestore db;
    private Button botao;
    private AdapterMerenda adapter;
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


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_merenda_escolar);

        carregarComponentes();


        ActionBar actionBar = getSupportActionBar();
        setTitle("Lista Merenda");
        // Adiciona um ícone de ação à direita
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24); // Define o ícone de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilita o botão de navegação

        carregarCamera();
    }

    private void carregarComponentes(){
        botao = findViewById(R.id.botaoCamera);
        db = FirebaseFirestore.getInstance();
        spinnerDiasMerenda = findViewById(R.id.spinnerDiasMerenda);
        spinnerMesesMerenda = findViewById(R.id.spinnerMesesMerenda);
        spinnerTurmasMerenda = findViewById(R.id.spinnerTurmasMerenda);
    }

    protected void onStart() {

        super.onStart();
        setarSpinnerTurmas(new Callback() {
            @Override
            public void onComplete() {

            }
        });
        listarDiasMerendados();
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
                    //Map<String, Object> data = new HashMap<>();
                    //data.put("campoASerCriado", valorInicial);
                    //docRef.set(data);
                    callback.onComplete();
                }
            } else {
                // Houve um erro ao buscar o documento
                Log.d("Firestore", "Erro: " + task.getException());
                callback.onComplete();
            }
        });

    }

    private String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }

    private void listarDiasMerendados(){

        db.collection("MerendaEscolar")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obter um mapa de campos e valores do documento
                            Map<String, Object> data = document.getData();

                            // Percorrer todas as chaves e valores do mapa
                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                String chave = entry.getKey();
                                Object valor = entry.getValue();

                                Log.d("TAG", "onde = " + document.getId()+ " " + "Chave: " + chave + ", Valor: " + valor);
                            }
                        }
                    } else {
                        // Tratar erro aqui, se necessário
                    }
                });
    }

        // Recuperar o documento do Firestore
//        db.collection("MerendaEscolar")
//                .document("10082023")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()) {
//                                Log.d("TAG", "Documento recuperado: " + document.getData());
//
//                                // Iterar sobre todos os campos do documento
//                                for (String campo : document.getData().keySet()) {
//                                    Log.d("TAG", "Campo: " + campo + ", Valor: " + document.get(campo));
//
//                                }
//
//                                List<HashMap<String, Timestamp>> existingList = (List<HashMap<String, Timestamp>>) document.get("todos");
//
//
//                                if (existingList != null) {
//                                    for (HashMap<String, Timestamp> map : existingList) {
//                                        for (Map.Entry<String, Timestamp> entry : map.entrySet()) {
//                                            String matricula = entry.getKey();
//                                            Timestamp timestamp = entry.getValue();
//
//                                            Log.d("TAG", "Matrícula: " + matricula + ", Timestamp: " + timestamp.toString() + existingList.size());
//                                            stringList.add(matricula);
//                                        }
//                                    }
//                                }
//
//                                callback.onComplete();
//
//
//                            } else {
//                                Log.d("TAG", "Documento não existe");
//                                callback.onComplete();
//                            }
//                        } else {
//                            Log.d("TAG", "Erro ao recuperar documento: ", task.getException());
//                            callback.onComplete();
//                        }
//                    }
//                });
//        callback.onComplete();
//    }
}