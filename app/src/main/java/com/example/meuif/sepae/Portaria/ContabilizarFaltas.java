package com.example.meuif.sepae.Portaria;

import static com.google.common.net.InetAddresses.increment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.meuif.sepae.telaMerendaEscolar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ContabilizarFaltas {

    private FirebaseFirestore db;
    private String diaSemana;
    private List<String> dias = new ArrayList<String>();
    private String dia;
    private final Map<String, String> nomesAlunos = new HashMap<>();

    public ContabilizarFaltas( String dia, String diaSemana) {
        this.dia = dia;
        this.diaSemana = diaSemana;
    }


    public static boolean ehDiaUtil() {
        Calendar calendar = Calendar.getInstance();
        int diaDaSemana = calendar.get(Calendar.DAY_OF_WEEK);

        // Verifica se o dia da semana é segunda-feira (2) a sexta-feira (6)
        return diaDaSemana >= Calendar.MONDAY && diaDaSemana <= Calendar.FRIDAY;
    }

    private void pegarNomesAlunos(Callback callback){
        DocumentReference docRef = db.collection("Usuarios").document("Alunos");
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


    public void contarFaltas(Callback callback){
        db = FirebaseFirestore.getInstance();
        Boolean aux = ehDiaUtil();
        if (aux){
            verificarDia(new Callback() {
                @Override
                public void onComplete() {
                    if (!dias.contains(dia)){
                        pegarNomesAlunos(new Callback() {
                            @Override
                            public void onComplete() {
                                //setarFalta("20213018108", dia);
                                for (String chave : nomesAlunos.keySet()) {
                                    setarFalta(chave, dia);
                                    Log.d("cont", chave);
                                }
                                atualizarDiasAulas();
                               // Toast.makeText(context, "As faltas foram contabilizadas!", Toast.LENGTH_SHORT).show();
                                callback.onComplete();
                            }
                        });
                    } else{
                       // Toast.makeText(context, "Hoje as faltas já foram contabilizadas!", Toast.LENGTH_SHORT).show();
                        callback.onComplete();

                    }
                }
            });
        }else {
            //final de semana
          //  Toast.makeText(context, "Final de Semana!", Toast.LENGTH_SHORT).show();
            callback.onComplete();

        }
    }

    private void atualizarDiasAulas(){
        DocumentReference docRef = db.collection("MaisInformacoes").document("IformacoesFaltas");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> diasAulas = (List<String>) document.get("diasAulas");

                    if (diasAulas != null) {
                        diasAulas.add(dia); // Adicione o novo elemento à lista

                        // Agora, atualize o campo "diasAulas" no Firestore
                        docRef.update("diasAulas", diasAulas)
                                .addOnSuccessListener(aVoid -> {
                                    // Sucesso na atualização
                                    // Faça algo aqui se necessário
                                })
                                .addOnFailureListener(e -> {
                                    // Lidar com erros na atualização
                                });
                    } else {
                        // O campo "diasAulas" está vazio ou não é um array
                    }
                } else {
                    // O documento não existe
                }
            } else {
                // Erro ao obter o documento
            }
        });
    }


    private void setarFalta(String matricula, String data){
        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(matricula)
                .document("chamadaPessoal");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                // Verifique se o documento existe e se o campo "timestamps" já foi criado
                if (task.getResult().exists() && task.getResult().contains(data)) {
                    //pessoa veio pra aula
                } else {
                    // nao veio pra aula
                    if (task.getResult().exists() && task.getResult().contains("faltas")){
                        DocumentSnapshot document = task.getResult();
                        String valorPresenca = document.getString("faltas");
                        Map<String, String> todosMap = (Map<String, String>) document.get("todos");

                        // Verifique se a chave existe no mapa
                        Log.d("cont", diaSemana);
                        Log.d("cont", todosMap.toString());
                        if (todosMap.containsKey(diaSemana)) {
                            String valorAtual =  String.valueOf(todosMap.get(diaSemana));

                            // Converte o valorLong para Integer, incrementa e coloca de volta no mapa
                            todosMap.put(diaSemana, String.valueOf(Integer.valueOf(valorAtual) + 1));
                        } else {
                            // Se a chave não existe no mapa, você pode simplesmente colocar o valorLong convertido em Integer no mapa
                            todosMap.put(diaSemana, String.valueOf(1));
                        }

                        // Converta o valor atual para inteiro
                        int valorAtualInt = Integer.parseInt(valorPresenca);

                        // Atualize o campo com o novo valor convertido em string
                        docRef.update("faltas", String.valueOf(valorAtualInt + 1))
                                .addOnSuccessListener(aVoid -> System.out.println("Campo incrementado com sucesso!"))
                                .addOnFailureListener(e -> System.out.println("Erro ao incrementar campo: " + e.getMessage()));

                        docRef.update("todos", todosMap)
                                .addOnSuccessListener(aVoid1 -> {
                                    System.out.println("Campo 'todos' atualizado com sucesso!");
                                })
                                .addOnFailureListener(e -> {
                                    System.out.println("Erro ao atualizar campo 'todos': " + e.getMessage());
                                });

                    }
                }
            }
        }).addOnFailureListener(task -> {
           //algum erro
        });
    }

    private void verificarDia(Callback callback) {
        DocumentReference docRef = db.collection("MaisInformacoes").document("IformacoesFaltas");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> diasAulas = (List<String>) document.get("diasAulas");

                    if (diasAulas != null) {
                        dias = diasAulas;
                        callback.onComplete();
                    } else {
                        // O campo "diasAulas" está vazio ou não é um array
                        callback.onComplete();
                    }
                } else {
                    // O documento não existe
                    callback.onComplete();
                }
            } else {
                // Erro ao obter o documento
                callback.onComplete();
            }
        });
    }



    public interface Callback {
        void onComplete();
    }

}
