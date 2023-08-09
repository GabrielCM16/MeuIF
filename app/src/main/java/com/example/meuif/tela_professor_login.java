package com.example.meuif;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class tela_professor_login extends Activity {

    private EditText entrada;
    private ProgressBar progressBar;
    private Button botao;
    public FirebaseFirestore db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_professor_login);

        db = FirebaseFirestore.getInstance();
        carregarComponentes();
        estilizarHint();

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscar(view);
            }
        });
    }

    private void buscar(View v){
        String siape = entrada.getText().toString();
        if (!siape.isEmpty()){
            progressBar.setVisibility(View.VISIBLE);
            procurarNome(siape, v);
        } else if (siape.isEmpty()) {
            abrirSnakbar("insira uma número valido", v);
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    private void carregarComponentes(){
        entrada = findViewById(R.id.entradaSIAPE);
        progressBar = findViewById(R.id.progressBar);
        botao = findViewById(R.id.botaoMereda);
        //muda a cor do progressBar pra preto
        progressBar.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    }

    private void estilizarHint(){
        StyleSpan italicSpan = new StyleSpan(Typeface.ITALIC);
        // Obtenha o texto de hint original do EditText
        String hintOriginal = entrada.getHint().toString();

        // Crie um SpannableStringBuilder para aplicar estilos personalizados ao hint
        SpannableStringBuilder spannableentrada = new SpannableStringBuilder(hintOriginal);

        // Aplicar sublinhado ao hint
        UnderlineSpan underlineSpanentrada = new UnderlineSpan();
        spannableentrada.setSpan(underlineSpanentrada, 0, hintOriginal.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableentrada.setSpan(italicSpan,  0, hintOriginal.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Defina o hint estilizado no EditText
        entrada.setHint(spannableentrada);
    }

    private void procurarNome(String siape, View v){
        DocumentReference docRef = db.collection("Usuarios").document("Professores").collection(siape).document("dados");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String auxnome = document.getString("nome");
                        salvarDados("nome", auxnome);
                        salvarDados("siape", siape);
                        proximaTela();
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        Log.d("TAGLER", "Documento não encontrado");
                        progressBar.setVisibility(View.INVISIBLE);
                        abrirSnakbar("Erro ao procurar SIAPE", v);
                    }
                } else {
                    Log.d("TAGLER", "Falhou em ", task.getException());
                    progressBar.setVisibility(View.INVISIBLE);
                    abrirSnakbar("Erro tente novamente", v);
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

    public void salvarDados(String chave, String valor){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(chave, valor);
        editor.commit();
    }

    public void proximaTela(){
        // Criar a Intent
        Intent intent = new Intent(tela_professor_login.this, tela_professor_entrar.class);
        // Iniciar a atividade de destino
        startActivity(intent);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
