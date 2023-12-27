package com.example.meuif;

import static com.google.common.collect.ComparisonChain.start;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.meuif.databinding.FragmentInformacoesPessoaisBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Informacoes_pessoais extends Fragment {

    private TextView mostrarEmail;
    private TextView mostrarNome;
    private TextView mostrarIDUser;
    private TextView mostrarTurma;
    private TextView mostrarMatricula;
    private FirebaseFirestore db;
    private FragmentInformacoesPessoaisBinding binding;
    private VideoView vv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentInformacoesPessoaisBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        vv = root.findViewById(R.id.videoViewInformacoes);
        mostrarEmail = root.findViewById(R.id.mostrarEmail);
        mostrarNome = root.findViewById(R.id.mostrarNome);
        mostrarIDUser = root.findViewById(R.id.mostrarIDUser);
        mostrarTurma = root.findViewById(R.id.mostrarTurma);
        mostrarMatricula = root.findViewById(R.id.mostrarMatricula);

        carregarVideo();
        carregarDados();

        return root;
    }

    private void carregarVideo() {
        vv.setBackgroundColor(Color.TRANSPARENT);
        vv.setVideoPath("android.resource://" + requireContext().getPackageName() + "/" + R.raw.introducaofundoazulfracomaiortempo);

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

    private void carregarDados(){
        String matricula = recuperarDados("matricula");

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

    public String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }

}

