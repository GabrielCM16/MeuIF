package com.example.meuif;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private EditText entrada;
    private ProgressBar progressBar;
    private TextView textView;
    public FirebaseFirestore db;

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
        Button botao = (Button) findViewById(R.id.meubotao);
        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                buscar(view);
            }
        });
    }

    public void salvarDados(String chave, String valor){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(chave, valor);
        editor.commit();
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioAtual != null){
            telaPrincipal();
        }
    }

    public void buscar(View v){
        String matricula = entrada.getText().toString();
        if (!matricula.isEmpty()){
            setGetInicial(matricula, v);
        } else if (matricula.isEmpty()) {
            abrirSnakbar("insira uma matricula", v);
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    public void setGetInicial(String nMatricula, View v){
        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(nMatricula).document("dados");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String auxnome = document.getString("nome");
                        salvarDados("nome", auxnome);
                        salvarDados("matricula", nMatricula);
                        Log.d("TAGLER", "deu bom");
                        Log.d("TAGLER", auxnome);
                        proximaTela();
                    } else {
                        Log.d("TAGLER", "Documento não encontrado");
                        progressBar.setVisibility(View.INVISIBLE);
                        abrirSnakbar("Erro ao procurar matricula", v);
                    }
                } else {
                    Log.d("TAGLER", "Falhou em ", task.getException());
                    progressBar.setVisibility(View.INVISIBLE);
                    abrirSnakbar("Erro tente novamente", v);
                }
            }
        });
    }

    public void proximaTela(){
        // Criar a Intent
        Intent intent = new Intent(MainActivity.this, tela_entrar.class);
        // Iniciar a atividade de destino
        startActivity(intent);

    }

    public void abrirSnakbar(String texto, View v){
        Snackbar snackbar = Snackbar.make(v, texto, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }

    public void telaPrincipal(){
        // Criar a Intent
        Intent intent = new Intent(MainActivity.this, Tela_Principal.class);
        // Iniciar a atividade de destino
        startActivity(intent);
        finish();
    }

}