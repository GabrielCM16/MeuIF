package com.example.meuif.sepae.Portaria;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meuif.CaptureAct;
import com.example.meuif.Informacoes_pessoais;
import com.example.meuif.R;
import com.example.meuif.databinding.FragmentInformacoesPessoaisBinding;
import com.example.meuif.events.Events;
import com.example.meuif.events.SalvarEvento;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class PassePortaria extends AppCompatActivity {

    private FirebaseFirestore db;
    private Button botao;
    private long tempoValidade = 30000;
    private MediaPlayer mediaPlayer;
    final PassePortaria activity= this;
    private Button botaoEvento;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passe_portaria);
        botao = findViewById(R.id.botaoPasse);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.error);

        db = FirebaseFirestore.getInstance();
        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sCanCode();
            }
        });



        // Obtém a data e hora atual
        Calendar calendar = Calendar.getInstance();

// Adiciona 1 dia à data atual
        calendar.add(Calendar.DAY_OF_MONTH, 1);

// Obtém a data resultante como um objeto Date
        Date data = calendar.getTime();

// Converte a data em um Timestamp do Firebase Firestore
        Timestamp timestamp = new Timestamp(data);


        botaoEvento = findViewById(R.id.botaoEvento);
        botaoEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Events events = new Events("Projeto de Extensão",
                        "treinamento para a OBI",
                        "OBI",
                        Timestamp.now(),
                        timestamp,
                        new ArrayList<>(),
                        new ArrayList<>(),
                "Odair",
                        "Geral",
                "lab 3");

                SalvarEvento salvarEvento = new SalvarEvento(events);
                salvarEvento.gravarEvent();
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
                                .addOnSuccessListener(aVoid -> System.out.println("Campo incrementado com sucesso!"))
                                .addOnFailureListener(e -> System.out.println("Erro ao incrementar campo: " + e.getMessage()));

                    } else {
                        System.out.println("O documento não existe.");
                    }
                }

                // Adicione o novo timestamp à lista
                timestampsList.add(novoTimestamp);

                // Use o método update() para atualizar o campo "timestamps" no documento
                docRef.update(data, timestampsList)
                        .addOnSuccessListener(aVoid -> System.out.println("Timestamp adicionado com sucesso!"))
                        .addOnFailureListener(e -> System.out.println("Erro ao adicionar timestamp: " + e.getMessage()));

                if (task.getResult().exists() && task.getResult().contains("possivelStatus")){
                    DocumentSnapshot document = task.getResult();
                    String status = document.getString("possivelStatus");

                    if (status.equals("Entrada")){
                        docRef.update("possivelStatus", "Saida")
                                .addOnSuccessListener(aVoid -> System.out.println("Campo incrementado com sucesso!"))
                                .addOnFailureListener(e -> System.out.println("Erro ao incrementar campo: " + e.getMessage()));
                    } else if (status.equals("Saida")){
                        docRef.update("possivelStatus", "Entrada")
                                .addOnSuccessListener(aVoid -> System.out.println("Campo incrementado com sucesso!"))
                                .addOnFailureListener(e -> System.out.println("Erro ao incrementar campo: " + e.getMessage()));
                    }
                }
            } else {
                System.out.println("Erro ao obter o documento: " + task.getException().getMessage());
            }
        });
    }
}