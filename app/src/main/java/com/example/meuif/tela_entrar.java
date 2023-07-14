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
import android.widget.Toast;

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

public class tela_entrar extends AppCompatActivity {

    private TextView textViewOla;
    public FirebaseFirestore db;
    public String nome;
    public String matricula;
    public String idUser;
    private EditText entradaEmail;
    private EditText entradaSenha;
    private EditText entradaSenha2;
    private Button botao;
    private Boolean criar = false;
    private ProgressBar progressBar;
    private String nomeName;
    String[] mensagens = {"Preencha todos os campos", "As senhas devem ser iguais" ,"Cadastro realizado com sucesso!"};

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
        entradaEmail = findViewById(R.id.entradaEmail);
        entradaSenha = findViewById(R.id.entradaSenha);
        entradaSenha2 = findViewById(R.id.entradaSenha2);
        progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        botao = findViewById(R.id.botao);
        setGetIdUser(matricula);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if (criar){
                    criarConta(view);
                }
            }
        });
    }

    public void setarTela(){
        String[] nomeCompleto = nome.split(" ");
        nomeName = nomeCompleto[0];
        if (idUser != ""){
            textViewOla.setText("Ola " + nomeCompleto[0] + "! entre com seu email e senha");
        } else {
            textViewOla.setText("Olá " + nomeCompleto[0] + " foi verificado que você ainda não possui uma conta! crie uma agora mesmo" );
            criar = true;
        }


    }

    public void setGetIdUser(String nMatricula){
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
                    abrirSnakbar("Bem vindo " + nomeName, v);

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
                      erro = "Erro ao cadastrar, tente novamente";
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

        DocumentReference local = db.collection("Usuarios").document("Alunos").collection(matricula).document("id");

        local.update("email", email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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
                Log.d("db", "Sucesso ao salvar os dados");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "Falhou ao atualizar", e);
            }
        });
    }

    public void abrirSnakbar(String texto, View v){
        Snackbar snackbar = Snackbar.make(v, texto, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }


}