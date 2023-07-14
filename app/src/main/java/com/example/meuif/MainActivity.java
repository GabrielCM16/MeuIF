package com.example.meuif;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private EditText entrada;
    private ProgressBar progressBar;
    private TextView textView;
    public FirebaseFirestore db;
    public Pessoa pessoa = new Pessoa();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        entrada = findViewById(R.id.entradaMatricula);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textViewOla);
        db = FirebaseFirestore.getInstance();


        //muda a cor do progressBar pra preto
        progressBar.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        //iniciando o botao e fazendo um onclick
        Button botao = (Button) findViewById(R.id.botao);
        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                buscar();
            }
        });
    }

    public void buscar(){
        String matricula = entrada.getText().toString();
        setGetInicial(matricula);

    }

    public void setGetInicial(String nMatricula){
        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(nMatricula).document("dados");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String auxnome = document.getString("nome");
                        Log.d("TAGLER", "deu bom");
                        Log.d("TAGLER", auxnome);
                        proximaTela(auxnome, nMatricula);
                    } else {
                        Log.d("TAGLER", "Documento não encontrado");
                    }
                } else {
                    Log.d("TAGLER", "Falhou em ", task.getException());
                }
            }
        });
    }

    public void proximaTela(String name, String matricula){
        // Criar a Intent
        Intent intent = new Intent(MainActivity.this, tela_entrar.class);

        // Adicionar a string como um extra na Intent
        intent.putExtra("nome", name);
        intent.putExtra("matricula", matricula);

        // Iniciar a atividade de destino
        startActivity(intent);
    }

}