package com.example.meuif.sepae.gestao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meuif.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelaCadastrarNovoUsuario extends AppCompatActivity {
    private EditText entradaIdentificacaoSIAPE;
    private EditText entradaNomeNovoCadastro;
    private ProgressBar progressBarNovoUserSEPAE;
    private Switch switchSEPAE;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        setContentView(R.layout.activity_tela_cadastrar_novo_usuario);

        carregarComponentes();
    }

    private void carregarComponentes() {
        entradaIdentificacaoSIAPE = findViewById(R.id.entradaIdentificacaoSIAPE);
        entradaNomeNovoCadastro = findViewById(R.id.entradaNomeNovoCadastro);
        switchSEPAE = findViewById(R.id.switchSEPAE);
        progressBarNovoUserSEPAE = findViewById(R.id.progressBarNovoUserSEPAE);
        progressBarNovoUserSEPAE.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        setTitle("Novo Usuário");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_novo_evento);
        TextView cancelar = findViewById(R.id.TextCancelar);
        TextView salvar = findViewById(R.id.TextSalvar);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarUsuario();
            }
        });
    }

    private void salvarUsuario() {
        progressBarNovoUserSEPAE.setVisibility(View.VISIBLE);
        String nome = entradaNomeNovoCadastro.getText().toString();
        String siape = entradaIdentificacaoSIAPE.getText().toString();

        Map<String, Object> dados = new HashMap<>();
        dados.put("nome", nome);

        Map<String, Object> id = new HashMap<>();
        id.put("email", "");
        id.put("idUser", "");

        Map<String, Object> professor = new HashMap<>();
        professor.put("SEPAE", "SEPAE");

        if (switchSEPAE.isChecked()) {
            String[] turmasArray = getResources().getStringArray(R.array.turmas);
            List<String> turmasList = Arrays.asList(turmasArray);
            professor.put("turmas", turmasList);
        }else {
            professor.put("turmas", "");
        }

        progressBarNovoUserSEPAE.setVisibility(View.VISIBLE);
        db.collection("Usuarios").document("Professores").collection(siape)
                .document("dados")
                .set(dados)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       Log.d("user", "dados salvos");
                        Toast.makeText(getApplicationContext(), "dados Salvos" , Toast.LENGTH_SHORT).show();
                        progressBarNovoUserSEPAE.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("user", "dados nao salvos");
                        Toast.makeText(getApplicationContext(), "Erro" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        progressBarNovoUserSEPAE.setVisibility(View.INVISIBLE);
                    }
                });

        progressBarNovoUserSEPAE.setVisibility(View.VISIBLE);
        db.collection("Usuarios").document("Professores").collection(siape)
                .document("id")
                .set(id)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("user", "id salvos");
                        Toast.makeText(getApplicationContext(), "id Salvos" , Toast.LENGTH_SHORT).show();
                        progressBarNovoUserSEPAE.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("user", "id nao salvos");
                        Toast.makeText(getApplicationContext(), "Erro" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        progressBarNovoUserSEPAE.setVisibility(View.INVISIBLE);
                    }
                });

        progressBarNovoUserSEPAE.setVisibility(View.VISIBLE);
        db.collection("Usuarios").document("Professores").collection(siape)
                .document("professor")
                .set(professor)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("user", "professor salvos");
                        Toast.makeText(getApplicationContext(), "professor Salvo" , Toast.LENGTH_SHORT).show();
                        progressBarNovoUserSEPAE.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("user", "professor nao salvos");
                        Toast.makeText(getApplicationContext(), "Erro" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        progressBarNovoUserSEPAE.setVisibility(View.INVISIBLE);
                    }
                });

        Toast.makeText(getApplicationContext(), "Usuario criado com Êxito!" , Toast.LENGTH_SHORT).show();

    }
}