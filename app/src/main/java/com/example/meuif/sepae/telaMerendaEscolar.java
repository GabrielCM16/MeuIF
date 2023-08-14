package com.example.meuif.sepae;

import static android.app.PendingIntent.getActivity;

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
import android.view.View;
import android.widget.Button;

import com.example.meuif.CaptureAct;
import com.example.meuif.MainActivity;
import com.example.meuif.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class telaMerendaEscolar extends AppCompatActivity {

    private FirebaseFirestore db;
    private Button botao;
    private long tempoValidade = 30000;
    private MediaPlayer mediaPlayer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_merenda_escolar);

        botao = findViewById(R.id.botaoCamera);

        db = FirebaseFirestore.getInstance();



        ActionBar actionBar = getSupportActionBar();
        setTitle("Lista Merenda");
        // Adiciona um ícone de ação à direita
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24); // Define o ícone de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilita o botão de navegação

        carregarCamera();
    }

    protected void onStart() {

        super.onStart();
        listarDiasMerendados();
    }



    private void playSuccessSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
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
    }

    private void carregarCamera(){
        // Inicialize o MediaPlayer com o arquivo de som do sucesso (success_sound.mp3 ou success_sound.wav)
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.error);

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

                if ( aux.length() == 11){
                    String data = diaAtual();
                    atualizarMerenda(aux, data);
                } else {
                    playSuccessSound();
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

    private void atualizarMerenda(String matricula, String data){

        Timestamp novoTimestamp = Timestamp.now();

        DocumentReference docRef = db.collection("MerendaEscolar").document(data);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // O documento existe, atualize um campo nele
                    String turma = recuperarDados("turma");
                    List<Map<String, Timestamp>> existingList = (List<Map<String, Timestamp>>) document.get("todos");

                    Map<String, Timestamp> aux = new HashMap<String, Timestamp>();
                    aux.put(matricula, novoTimestamp);

                    existingList.add(aux);

                    docRef.update("todos", existingList);
                } else {
                    // O documento não existe, crie-o
                    //Map<String, Object> data = new HashMap<>();
                    //data.put("campoASerCriado", valorInicial);
                    //docRef.set(data);
                }
            } else {
                // Houve um erro ao buscar o documento
                Log.d("Firestore", "Erro: " + task.getException());
            }
        });

    }

    private String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }

    private void listarDiasMerendados(){
        CollectionReference colecRef = db.collection("MerendaEscolar");

        // Recuperar o documento do Firestore
        db.collection("MerendaEscolar")
                .document("10082023")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("TAG", "Documento recuperado: " + document.getData());

                                // Iterar sobre todos os campos do documento
                                for (String campo : document.getData().keySet()) {
                                    Log.d("TAG", "Campo: " + campo + ", Valor: " + document.get(campo));

                                }

                                List<HashMap<String, Timestamp>> existingList = (List<HashMap<String, Timestamp>>) document.get("todos");


                                if (existingList != null) {
                                    for (HashMap<String, Timestamp> map : existingList) {
                                        for (Map.Entry<String, Timestamp> entry : map.entrySet()) {
                                            String matricula = entry.getKey();
                                            Timestamp timestamp = entry.getValue();

                                            Log.d("TAG", "Matrícula: " + matricula + ", Timestamp: " + timestamp.toString() + existingList.size());
                                        }
                                    }
                                }


                            } else {
                                Log.d("TAG", "Documento não existe");
                            }
                        } else {
                            Log.d("TAG", "Erro ao recuperar documento: ", task.getException());
                        }
                    }
                });
    }
}