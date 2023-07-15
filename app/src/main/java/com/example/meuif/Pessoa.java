package com.example.meuif;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.firestore.FirebaseFirestore;


public class Pessoa {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static String nome;
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




}
