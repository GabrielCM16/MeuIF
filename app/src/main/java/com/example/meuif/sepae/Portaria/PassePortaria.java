package com.example.meuif.sepae.Portaria;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meuif.CaptureAct;
import com.example.meuif.Informacoes_pessoais;
import com.example.meuif.R;
import com.example.meuif.databinding.FragmentInformacoesPessoaisBinding;
import com.example.meuif.events.Events;
import com.example.meuif.events.SalvarEvento;
import com.example.meuif.events.TelaNovoEvento;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

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


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passe_portaria);
        botao = findViewById(R.id.botaoPasse);
        entradaMatriculaAcesso = findViewById(R.id.entradaMatriculaAcesso);
        constraintRegistrarAcesso = findViewById(R.id.constraintRegistrarAcesso);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.error);
        sucessPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sucess);

        db = FirebaseFirestore.getInstance();
        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sCanCode();
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
                } else {
                    Toast.makeText(getApplicationContext(), "Matricula Invalida", Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    private String diaAtual(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-3"));

        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int ano = calendar.get(Calendar.YEAR);

        String data = String.format("%02d%02d%d", dia, mes, ano);
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
                //String[] aux = result.getContents().split("/");

                //long valorCurrent = Long.parseLong(aux[1]);

               // if ( System.currentTimeMillis() - valorCurrent <= tempoValidade){
                    String data = diaAtual();
                    atualizarMatricula(aux, data);
                     atualizarAcessoSepae(aux);
                //} else {
               //     playSuccessSound();
               // }

                sCanCode();
            }
            else {
                Toast.makeText(this,"Nao tem barra", Toast.LENGTH_SHORT).show();;
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