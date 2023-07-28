package com.example.meuif;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meuif.databinding.FragmentInformacoesPessoaisBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Informacoes_pessoais extends Fragment {

    private TextView teste;
    private FirebaseFirestore db;
    private FragmentInformacoesPessoaisBinding binding;
    private Button botao;
    private long tempoValidade = 30000;
    TextView textView;
    final Informacoes_pessoais activity= this;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentInformacoesPessoaisBinding.inflate(inflater, container, false);


        View root = binding.getRoot();


        teste = root.findViewById(R.id.testeaaa);
        botao = root.findViewById(R.id.botao);
        teste.setText("ola");
        db = FirebaseFirestore.getInstance();
        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sCanCode();
            }
        });

        return root;

    }

    private String diaAtual(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-3"));

        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int ano = calendar.get(Calendar.YEAR);

        String data = String.format("%02d%02d%d", dia, mes, ano);
        return data;
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
            if (result.getContents().contains("/")) {
                String[] aux = result.getContents().split("/");

                long valorCurrent = Long.parseLong(aux[1]);

                if ( System.currentTimeMillis() - valorCurrent <= tempoValidade){
                    String data = diaAtual();
                    atualizarMatricula(aux[0], data);
                }

                sCanCode();
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
                } else {
                    System.out.println("Erro ao obter o documento: " + task.getException().getMessage());
                }
            });
        }
    }

