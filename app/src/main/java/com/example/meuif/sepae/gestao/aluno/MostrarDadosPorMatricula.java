package com.example.meuif.sepae.gestao.aluno;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.meuif.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MostrarDadosPorMatricula extends AppCompatActivity {
    private TextView mostrarEmail;
    private TextView mostrarNome;
    private TextView mostrarIDUser;
    private TextView mostrarTurma;
    private TextView mostrarMatricula;
    private FirebaseFirestore db;
    private VideoView vv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_dados_por_matricula);

        db = FirebaseFirestore.getInstance();
        carregarComponents();

    }

    private void carregarComponents(){
        vv = findViewById(R.id.videoViewInformacoes);
        mostrarEmail = findViewById(R.id.mostrarEmail);
        mostrarNome = findViewById(R.id.mostrarNome);
        mostrarIDUser = findViewById(R.id.mostrarIDUser);
        mostrarTurma = findViewById(R.id.mostrarTurma);
        mostrarMatricula = findViewById(R.id.mostrarMatricula);
        // Receber a Intent
        Intent intent = getIntent();

        // Verificar se a Intent contém a chave
        if (intent.hasExtra("matricula")) {
            // Obter a string da Intent
            String matricula = intent.getStringExtra("matricula");
            Log.d("mostrarDados", "matricula " + matricula);
            carregarDados(matricula);
        }

        carregarVideo();
    }

    private void carregarDados(String matricula){

        mostrarMatricula.setText("Matrícula: " + matricula);

        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(matricula).document("id");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String mail = document.getString("email");
                        String id = document.getString("idUser");
                        mostrarEmail.setText("Email: " + mail);
                        mostrarIDUser.setText("ID-User: " + id);

                    } else {
                        Log.d("TAGLER", "Documento não encontrado");
                    }
                } else {
                    Log.d("TAGLER", "Falhou em ", task.getException());
                }
            }
        });

        docRef = db.collection("Usuarios").document("Alunos").collection(matricula).document("dados");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nome = document.getString("nome");
                        String turma = document.getString("turma");
                        mostrarNome.setText("Nome: " + nome);
                        mostrarTurma.setText("Turma: " + turma);

                    } else {
                        Log.d("TAGLER", "Documento não encontrado");
                    }
                } else {
                    Log.d("TAGLER", "Falhou em ", task.getException());
                }
            }
        });
    }

    private void carregarVideo() {
        vv.setBackgroundColor(Color.TRANSPARENT);
        vv.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.introducaofundoazulfracomaiortempo);

        // Desativa os controles padrão do VideoView
        vv.setMediaController(null);

        // Define um listener para detectar o término do vídeo
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                vv.seekTo(0);
                vv.start();
            }
        });

        // Desativa a interação com o VideoView
        vv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true; // Impede que o usuário toque no VideoView
            }
        });

        vv.setVisibility(View.VISIBLE);
        vv.start();
    }
}