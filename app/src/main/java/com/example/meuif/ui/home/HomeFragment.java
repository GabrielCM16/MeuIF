package com.example.meuif.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.meuif.R;
import com.example.meuif.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView textViewBemVindo;
    private String nomeCompleto;
    private FirebaseFirestore db;
    private ProgressBar progressBarCentral;
    private String aulasTotais;
    private String ausencias;
    private String presencas;
    private TextView saidaNumAulas;
    private TextView saidaNumAusencias;
    private TextView saidaNumPresencas;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        progressBarCentral = root.findViewById(R.id.progressBarCentral);
        progressBarCentral.setVisibility(View.VISIBLE);

        //muda a cor do progressBar pra preto
        progressBarCentral.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        db = FirebaseFirestore.getInstance();
        textViewBemVindo = root.findViewById(R.id.textViewBemVindo);
        saidaNumAulas = root.findViewById(R.id.saidaNumAulas);
        saidaNumAusencias = root.findViewById(R.id.saidaNumAusencias);
        saidaNumPresencas = root.findViewById(R.id.saidaNumPresencas);
        nomeCompleto = recuperarDados("nome");




        mostrarPresenca();
        setarBemVindo();
        atualizaPresenca();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }

    public void salvarDados(String chave, String valor){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(chave, valor);
        editor.commit();
    }

    private void setarBemVindo(){
        String hora = getDateTime();
        int h = Integer.parseInt(hora);
        String ola;
        if (h > 19 || h <= 5){
            ola = "Boa noite ";
        } else if (h < 12 && h >= 6){
            ola = "Bom dia ";
        } else {
            ola = "Boa tarde ";
        }

        String[] primeiroNome = nomeCompleto.split(" ");

        textViewBemVindo.setText(ola + primeiroNome[0] + "!");

    }

    private void mostrarPresenca(){
        presencas = recuperarDados("presencas");
        ausencias = recuperarDados("ausencias");

        if (!TextUtils.isEmpty(presencas) && !TextUtils.isEmpty(ausencias)) {
            int num1 = Integer.parseInt(presencas);
            int num2 = Integer.parseInt(ausencias);

            //  Somar os números inteiros
            int soma = num1 + num2;
            //Converter o resultado em uma string
            aulasTotais = String.valueOf(soma);

            saidaNumAulas.setText(aulasTotais);
            saidaNumAusencias.setText(ausencias);
            saidaNumPresencas.setText(presencas);
            progressBarCentral.setVisibility(View.INVISIBLE);
        }
    }

    private void atualizaPresenca(){
        String nMatricula = recuperarDados("matricula");

        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(nMatricula).document("chamadaPessoal");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ausencias = document.getString("faltas");
                        presencas = document.getString("presencas");
                        salvarDados("ausencias", ausencias);
                        salvarDados("presencas", presencas);
                    } else {
                        Log.d("TAGLER", "Documento não encontrado");
                    }
                } else {
                    Log.d("TAGLER", "Falhou em ", task.getException());
                }
            }
        });
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH");
        Date date = new Date();
        return dateFormat.format(date);
    }

}