package com.example.meuif;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class tela_professor_entrar extends AppCompatActivity {

    private TextView textViewOla;
    private FirebaseFirestore db;
    private String nome;
    private String siape;
    private String idUser;
    private EditText entradaEmail;
    private EditText entradaSenha;
    private Boolean senha1Visivel = false;
    private EditText entradaSenha2;
    private Button botao;
    private Boolean criar = false;
    private ProgressBar progressBar;
    private String[] mensagens = {"Preencha todos os campos", "As senhas devem ser iguais" ,"Cadastro realizado com sucesso!"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_professor_entrar);

        inicarComponentes();
        progressBar.setVisibility(View.VISIBLE);
        siape = recuperarDados("siape");
        verificarConta(siape);
        setarTela();
        progressBar.setVisibility(View.INVISIBLE);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void inicarComponentes(){
        textViewOla = findViewById(R.id.textViewOla);
        db = FirebaseFirestore.getInstance();
        entradaEmail = findViewById(R.id.entradaEmail);
        entradaSenha = findViewById(R.id.entradaSenha);
        entradaSenha2 = findViewById(R.id.entradaSenha2);
        entradaSenha2.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        botao = findViewById(R.id.meubotao);
        siape = recuperarDados("siape");
    }

    public void setarTela(){
        String nome = recuperarDados("nome").toString();
        String[] primeiroNome = nome.split(" ");
        idUser = recuperarDados("idUser");
        if (idUser != ""){
            entradaSenha2.setVisibility(View.GONE);
            textViewOla.setText("Ola " + primeiroNome[0] + "! entre com seu email e senha");
        } else {
            entradaSenha2.setVisibility(View.VISIBLE);
            textViewOla.setText("Olá " + primeiroNome[0] + ", foi verificado que você ainda não possui uma conta! crie uma agora mesmo" );
            criar = true;
        }


    }

    private void verificarConta(String sipae){
        DocumentReference docRef = db.collection("Usuarios").document("Professores").collection(sipae).document("id");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        idUser = document.getString("idUser");
                        Log.d("TAGLER", "deu bom");
                        Log.d("TAGLER", idUser);
                        salvarDados("idUser", idUser);
                        if (idUser != ""){
                            criar = false;
                        } else {
                            criar = true;
                        }

                    } else {
                        Log.d("TAGLER", "Documento não encontrado");
                    }
                } else {
                    Log.d("TAGLER", "Falhou em ", task.getException());
                }
            }
        });
    }

    public void abrirSnakbar(String texto, View v){
        Snackbar snackbar = Snackbar.make(v, texto, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }
    private String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }
    public void salvarDados(String chave, String valor){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(chave, valor);
        editor.commit();
    }
}