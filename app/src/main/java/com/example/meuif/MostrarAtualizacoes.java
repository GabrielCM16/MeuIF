package com.example.meuif;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.meuif.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MostrarAtualizacoes {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> atualizacoesNovas = new ArrayList<>();
    public String cardapioSEPAE = "";
    private final List<String> atualizacoesCarpio = new ArrayList<>();

    public void abrirDialogAtualizacoes(Context context, String nome){
        StringBuilder builder = new StringBuilder();
        getAtualizaVersao(new Callback() {
            @Override
            public void onComplete() {
                for (Object item : atualizacoesNovas) {
                    builder.append(item.toString()).append("\n\n");
                }


                AlertDialog.Builder dialog = new AlertDialog.Builder(context);

                //configurar titulo e mensagem
                dialog.setTitle("o que há de novo" );
                dialog.setMessage(builder.toString());

                //configurar cancelamento do alert dialog
                dialog.setCancelable(false);

                //configurar icone
                //dialog.setIcon(android.R.drawable.ic_btn_speak_now);

                //configurar açoes para sim e nâo
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Tenha um Bom Dia " + nome, Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.create();
                dialog.show();
            }
        });
    }

    public void getAtualizaVersao(Callback callback){
        DocumentReference docRef = db.collection("MaisInformacoes").document("atualizacoes");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<Object> arrayCampo = (List<Object>) document.get("atualizou");

                        // Agora você pode percorrer o array e fazer o que precisa com os dados
                        for (Object item : arrayCampo) {
                            // Faça algo com o item do array
                            Log.d("Firestore", "Item do array: " + item.toString());
                            atualizacoesNovas.add(item.toString());
                        }

                        callback.onComplete();
                    } else {
                        Log.d("TAGG", "Documento de turma não encontrado");
                        callback.onComplete();
                    }
                } else {
                    Log.d("TAGG", "Falhou em ", task.getException());
                    callback.onComplete();
                }
            }
        });
    }

    public void getAtualizaCardapio(Callback callback){
        DocumentReference docRef = db.collection("MaisInformacoes").document("cardapio");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<Object> arrayCampo = (List<Object>) document.get("itens");

                        // Agora você pode percorrer o array e fazer o que precisa com os dados
                        for (Object item : arrayCampo) {
                            // Faça algo com o item do array
                            Log.d("Firestore", "Item do array: " + item.toString());
                            atualizacoesCarpio.add(item.toString());
                            Log.d("aux", "na func mostrar ca " + atualizacoesCarpio.toString());
                        }

                        callback.onComplete();
                    } else {
                        Log.d("TAGG", "Documento de turma não encontrado");
                        callback.onComplete();
                    }
                } else {
                    Log.d("TAGG", "Falhou em ", task.getException());
                    callback.onComplete();
                }
            }
        });
    }

    public void abrirDialogCardapio(Context context, String dia){
        StringBuilder builder = new StringBuilder();

        builder.append("De 12:00 até 13:00\n\n");
        getAtualizaCardapio(new Callback() {
            @Override
            public void onComplete() {
                for (Object item : atualizacoesCarpio) {
                    builder.append(item.toString()).append("\n\n");
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(context);

                //configurar titulo e mensagem
                dialog.setTitle("Cardapio de " + dia );
                dialog.setMessage(builder.toString());

                //configurar cancelamento do alert dialog
                dialog.setCancelable(false);

                //configurar icone
                //dialog.setIcon(android.R.drawable.ic_btn_speak_now);

                //configurar açoes para sim e nâo
                dialog.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Lembre-se não será possivel pegar depois do horário de termino " , Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.create();
                dialog.show();
            }
        });
    }

    public void getCardapioSEPAE(Callback callback1){
        StringBuilder builder = new StringBuilder();
        getAtualizaCardapio(new Callback() {
            @Override
            public void onComplete() {
                Log.d("aux", "dps do on  " + atualizacoesCarpio.toString());
                for (Object item : atualizacoesCarpio) {
                    builder.append(item.toString()).append("\n");
                }
                cardapioSEPAE = builder.toString();
                callback1.onComplete();
            }
        });
    }

    public String returnStringCardapioSEPAE(){
        return cardapioSEPAE;
    }

    public void salvarNovaMerenda(String novaMerenda, Callback callback){
        DocumentReference docRef = db.collection("MaisInformacoes").document("cardapio");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<Object> arrayCampo = (List<Object>) document.get("itens");

                        List<String> novaList = new ArrayList<String>();
                        // Divida a string com base em quebras de linha
                        String[] frases = novaMerenda.split("\n");

                        Collections.addAll(novaList, frases);

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("itens", novaList);

// Atualize o documento no Firestore
                        docRef.update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    // Sucesso na atualização
                                    Log.d("Firestore", "Documento atualizado com sucesso!");
                                })
                                .addOnFailureListener(e -> {
                                    // Erro na atualização
                                    Log.w("Firestore", "Erro ao atualizar documento", e);
                                });
                        callback.onComplete();


                    } else {
                        Log.d("TAGG", "Documento de turma não encontrado");
                        callback.onComplete();
                    }
                } else {
                    Log.d("TAGG", "Falhou em ", task.getException());
                    callback.onComplete();

                }
            }
        });
    }

    public interface Callback {
        void onComplete();
    }
}
