package com.example.meuif;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Pessoa {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String nome;
    private String cpf;
    private String rg;
    private String turma;
    private String datanasc;

//
//    public void criar() {
//
//        Map<String, Object> colecao = new HashMap<>();
//        colecao.put("registro", entrada.getText().toString());
//
//        db.collection("Usuarios")
//                .add(colecao)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        textView2.setText(documentReference.getId());
//                        textView.setText("Cadastrado!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("TAGCadastro", "Erro ao cadastrar", e);
//                    }
//                });
//    }

    //funcionaa
//    public void atualizar(){
//        db.collection("Usuarios").document("Alunos").collection(entrada.getText().toString()).document("id")
//                .update("email", "email22").addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        textView.setText("Atualizado!");
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("TAG", "Falhou ao atualizar", e);
//                    }
//                });
//    }



    public void setNome(String name){
        nome = name;
    }

    public String getNome(){
        return nome;
    }

}
