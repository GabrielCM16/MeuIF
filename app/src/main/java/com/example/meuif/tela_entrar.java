package com.example.meuif;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class tela_entrar extends AppCompatActivity {

    private TextView textViewOla;
    public FirebaseFirestore db;
    public String nome;
    public String matricula;
    public String idUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_entrar);
        textViewOla = findViewById(R.id.textViewOla);
        db = FirebaseFirestore.getInstance();

        //Recuperate a string do extra da Intent
        Intent intent = getIntent();
        nome = intent.getStringExtra("nome");
        matricula = intent.getStringExtra("matricula");
        setGetInicial(matricula);
    }

    public void setarTela(){
        String[] nomeCompleto = nome.split(" ");
        if (idUser != ""){
            textViewOla.setText("Ola " + nomeCompleto[0] + "! entre com seu email e senha");
        } else {
            textViewOla.setText("Ola " + nomeCompleto[0] + " foi verificado que você ainda não possui uma conta! crie uma agora mesmo com apenas email e senha" );
        }


    }

    public void setGetInicial(String nMatricula){
        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(nMatricula).document("id");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        idUser = document.getString("idUser");
                        Log.d("TAGLER", "deu bom");
                        Log.d("TAGLER", idUser);
                        setarTela();
                    } else {
                        Log.d("TAGLER", "Documento não encontrado");
                    }
                } else {
                    Log.d("TAGLER", "Falhou em ", task.getException());
                }
            }
        });
    }
}