package com.example.meuif.sepae.gestao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.meuif.R;
import com.example.meuif.sepae.telaPrincipalSepae;
import com.example.meuif.ui.gallery.GalleryFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestaoUsuarios extends AppCompatActivity {
    private TextView saidaNumUsuarios;
    private ProgressBar progressBarAlunosTotal;
    private static FirebaseFirestore db;
    private int numeroAlunos = 0;
    private Map<String, String> nomes = new HashMap<>();
    private ConstraintLayout constraintLayoutGestaoAlunos;
    private ConstraintLayout constraintLayoutGestaosepae;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestao_usuarios);

        carregarComponentes();
        setarNumeroUsuarios();
    }

    private void carregarComponentes() {
        saidaNumUsuarios = findViewById(R.id.saidaNumUsuarios);
        progressBarAlunosTotal = findViewById(R.id.progressBarAlunosTotal);
        db = FirebaseFirestore.getInstance();
        constraintLayoutGestaoAlunos = findViewById(R.id.constraintLayoutGestaoAlunos);
        constraintLayoutGestaosepae = findViewById(R.id.constraintLayoutGestaosepae);
        progressBarAlunosTotal.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        setTitle("Gestão de Usuários");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24); // Define o ícone de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilita o botão de navegação

        constraintLayoutGestaosepae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaGestaoSEPAE();
            }
        });

        constraintLayoutGestaoAlunos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaGestaoAlunos();
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Verifica se o item clicado é o botão do ActionBar
        if (item.getItemId() == android.R.id.home) {
            // Chame o método que você deseja executar quando o ActionBar for clicado
            telaVoltar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void telaVoltar(){
        // Criar a Intent
        Intent intent = new Intent(this, telaPrincipalSepae.class);
        // Iniciar a atividade de destino
        startActivity(intent);
    }

    private void telaGestaoAlunos(){
        constraintLayoutGestaoAlunos.setVisibility(View.GONE);
        constraintLayoutGestaosepae.setVisibility(View.GONE);
        progressBarAlunosTotal.setVisibility(View.GONE);
        saidaNumUsuarios.setVisibility(View.GONE);
        // Criar uma instância do fragmento
        GestaoAlunos meuFragment = new GestaoAlunos();

        // Obter o FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Iniciar a transação de fragmento
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Substituir o conteúdo do LinearLayout pelo fragmento
        fragmentTransaction.replace(R.id.principalConstraintGestao, meuFragment);

        // Confirmar a transação
        fragmentTransaction.commit();
    }
    private void telaGestaoSEPAE(){
        constraintLayoutGestaoAlunos.setVisibility(View.GONE);
        constraintLayoutGestaosepae.setVisibility(View.GONE);
        progressBarAlunosTotal.setVisibility(View.GONE);
        saidaNumUsuarios.setVisibility(View.GONE);
        // Criar uma instância do fragmento
        GestaoSEPAE meuFragment = new GestaoSEPAE();

        // Obter o FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Iniciar a transação de fragmento
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Substituir o conteúdo do LinearLayout pelo fragmento
        fragmentTransaction.replace(R.id.principalConstraintGestao, meuFragment);

        // Confirmar a transação
        fragmentTransaction.commit();
    }

    private  void setarNumeroUsuarios() {
        retornaNumeroAlunosComConta(new Callback() {
            @Override
            public void onComplete() {
//                saidaNumUsuarios.setText("O Aplicativo MeuIF Possui ao total: "+
//                        numeroAlunos + " de Alunos Cadastrados");
            }
        });
    }
    public void retornaNumeroAlunosComConta(Callback callback){
        final int[] num = {0};
        final int[] progress = {0};
        pegarTurmasAlunos(new Callback() {
            @Override
            public void onComplete() {
                Log.d("alunos", nomes.toString());

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("Usuarios").document("Alunos");

                for (String key: nomes.keySet()){
                    Log.d("chave", key);
                    DocumentReference docRef2 = docRef.collection(key).document("id");
                    docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Verifique se o campo "TurmasAlunos" existe no documento
                                    if (document.contains("email") && document.contains("idUser")) {
                                        if (!document.get("idUser").equals("") && !document.get("idUser").equals("")){
                                            num[0]++;
                                            Log.d("key", "soma");
                                            saidaNumUsuarios.setText("O Aplicativo MeuIF Possui ao total "+
                                                    num[0] + " de Alunos Cadastrados");
                                        }
                                    } else {

                                    }
                                    progress[0]++;
                                    progressBarAlunosTotal.setProgress(progress[0]);
                                } else {
                                    // O documento não existe
                                    Log.d("Documento", "Não encontrado");
                                }
                            } else {
                                // Falha ao obter o documento
                                Log.d("Firestore", "Falha na leitura do documento", task.getException());
                            }
                        }
                    });

                }
                callback.onComplete();
            }
        });
    }

    public void pegarTurmasAlunos(Callback callback){

        DocumentReference docRef = db.collection("Usuarios").document("Alunos");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Verifique se o campo "TurmasAlunos" existe no documento
                        if (document.contains("TurmasAlunos")) {
                            Map<String, String> turmasAlunos = (Map<String, String>) document.get("TurmasAlunos");
                            Log.d("alunos", "TurmasAlunos existe" + turmasAlunos.toString());
                            progressBarAlunosTotal.setMax(turmasAlunos.size());

                            nomes.putAll(turmasAlunos);
                            callback.onComplete();
                        } else {
                            // O campo "TurmasAlunos" não existe no documento
                            Log.d("TurmasAlunos", "Campo não encontrado");
                            callback.onComplete();
                        }
                    } else {
                        // O documento não existe
                        Log.d("Documento", "Não encontrado");
                        callback.onComplete();
                    }
                } else {
                    // Falha ao obter o documento
                    Log.d("Firestore", "Falha na leitura do documento", task.getException());
                    callback.onComplete();
                }
            }
        });

    }

    public interface Callback {
        void onComplete();
    }


}