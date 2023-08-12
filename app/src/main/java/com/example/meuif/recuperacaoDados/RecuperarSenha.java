package com.example.meuif.recuperacaoDados;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.meuif.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarSenha extends AppCompatActivity {
    private String primeiroNome;
    private TextView ola;
    private Button botaoRecuperar;
    private EditText email;
    private ProgressBar carregar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        carregarComponentes();

        botaoRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                carregar.setVisibility(View.VISIBLE);
                String mail = email.getText().toString();
                if (!mail.isEmpty()){
                    recuperarSenha(mail, view);
                } else{
                    abrirSnakbar("coloque um Email", view);
                    carregar.setVisibility(View.INVISIBLE);
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

    protected void onStart() {

        super.onStart();

        carregarComponentes();

        String nome = recuperarDados("nome");
        primeiroNome = nome.split(" ")[0];

        ola.setText(primeiroNome + " Deseja recuperar sua senha?");
    }

    private void recuperarSenha(String mail, View view){
        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                    abrirSnakbar("Email Enviado para Recuperação", view);
                    carregar.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                abrirSnakbar("Erro ao Recuperar Senha", view);
                carregar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void carregarComponentes(){
        ola = findViewById(R.id.textViewOlaSenha);
        botaoRecuperar = findViewById(R.id.botaoSenha);
        email = findViewById(R.id.entradaEmailSenha);
        firebaseAuth = FirebaseAuth.getInstance();
        carregar = findViewById(R.id.progressBarSenha);
        carregar.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    }



    private String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }
}