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
import java.util.List;

public class MostrarAtualizacoes {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> atualizacoesNovas = new ArrayList<>();

    public void abrirDialogAtualizacoes(Context context, String nome){
        StringBuilder builder = new StringBuilder();
        getAtualiza(new Callback() {
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

    public void getAtualiza(Callback callback){
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

    private interface Callback {
        void onComplete();
    }
}
