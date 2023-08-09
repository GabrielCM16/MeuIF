package com.example.meuif;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.meuif.sepae.telaPrincipalSepae;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
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
        verificarSEPAE();
        setarTela();
        progressBar.setVisibility(View.INVISIBLE);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (criar){
                    progressBar.setVisibility(View.VISIBLE);
                    criarConta(view);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    logarUsuario(view);
                }
            }
        });

        entradaSenha.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int Right = 2;
                if (event.getAction()==MotionEvent.ACTION_UP){
                    if (event.getRawX()>=entradaSenha.getRight()-entradaSenha.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = entradaSenha.getSelectionEnd();
                        if (senha1Visivel){
                            entradaSenha.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_off_24,0);
                            entradaSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            senha1Visivel=false;
                        } else {
                            entradaSenha.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_24,0);
                            entradaSenha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            senha1Visivel=true;
                        }
                        entradaSenha.setSelection(selection);
                        return true;
                    }
                }
                return false;
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

    private void verificarSEPAE(){
        siape = recuperarDados("siape");
        DocumentReference docRef = db.collection("Usuarios").document("Professores").collection(siape).document("professor");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String SEPAE = document.getString("SEPAE");
                        salvarDados("SEPAE", SEPAE);

                    } else {
                        Log.d("TAGLER", "Documento não encontrado");
                    }
                } else {
                    Log.d("TAGLER", "Falhou em ", task.getException());
                }
            }
        });
    }

    private void logarUsuario(View v){
        String email = entradaEmail.getText().toString();
        String senha = entradaSenha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    salvarDados("email", email);
                    abrirSnakbar("login efetuado", v);
                    telaProfessor();
                    progressBar.setVisibility(View.INVISIBLE);
                } else{
                    String erro;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        erro = "E-mail e/ou senha invalido";
                    } catch (Exception e){
                        erro = "Erro ao logar usuario, tente novamente";
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    abrirSnakbar(erro, v);
                }
            }
        });
    }

    private void telaProfessor(){
        // Criar a Intent
        Intent intent = new Intent(tela_professor_entrar.this, telaPrincipalSepae.class);
        // Iniciar a atividade de destino
        startActivity(intent);
        finish();
    }

    public void criarConta(View v){
        String email = entradaEmail.getText().toString();
        String senha1 = entradaSenha.getText().toString();
        String senha2 = entradaSenha2.getText().toString();

        if (!senha1.equals(senha2)){
            abrirSnakbar(mensagens[1], v);
            progressBar.setVisibility(View.INVISIBLE);
        } else if (email.isEmpty() || senha1.isEmpty() || senha2.isEmpty() ){
            abrirSnakbar(mensagens[0], v);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            cadastrarUsuario(v);
        }


    }

    public void cadastrarUsuario(View v){
        String email = entradaEmail.getText().toString();
        String senha = entradaSenha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    salvarDadosUsuario();

                    abrirSnakbar(mensagens[2], v);
                    abrirSnakbar("Bem vindo ", v);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    String erro;
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        erro = "Digite uma senha com no minimo 6 caracteres";
                    } catch (FirebaseAuthUserCollisionException e){
                        erro = "Este E-mail já foi ultilizado";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        erro = "E-mail invalido";
                    } catch (Exception e){
                        erro = "Erro inesperado ao cadastrar, tente novamente";
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    abrirSnakbar(erro, v);
                }
            }
        });
    }

    public void salvarDadosUsuario(){
        String email = entradaEmail.getText().toString();
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference local = db.collection("Usuarios").document("Professores").collection(siape).document("id");

        local.update("email", email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                salvarDados("email", email);
                Log.d("db", "Sucesso ao salvar os dados");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "Falhou ao atualizar", e);
            }
        });

        local.update("idUser", usuarioID).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                salvarDados("idUser", usuarioID);
                Log.d("db", "Sucesso ao salvar os dados");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "Falhou ao atualizar", e);
            }
        });
    }

    public void setarTela(){
        String nome = recuperarDados("nome").toString();
        String[] primeiroNome = nome.split(" ");
        verificarConta(siape);
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