package com.example.meuif.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.meuif.R;
import com.example.meuif.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

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
    public PieChart pieChart;
    private Button botaoChamada;
    private String turma;
    private String matricula;
    private boolean LiderDeTurma = false;
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
        botaoChamada = root.findViewById(R.id.botaoChamda);
        nomeCompleto = recuperarDados("nome");
        pieChart = root.findViewById(R.id.pie_chart);

        botaoChamada.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        botaoChamada.setPadding(30, botaoChamada.getPaddingTop(), 15, botaoChamada.getPaddingBottom());

        turma = recuperarDados("turma");
        matricula = recuperarDados("matricula");


        mostrarPresenca();
        setarBemVindo();
        atualizarGrafico(presencas, ausencias);
        atualizaPresenca();
        if (!matricula.isEmpty()){
            liderDeSala(matricula);
        }

        if (LiderDeTurma){
            botaoChamada.setVisibility(View.VISIBLE);
        } else {
            botaoChamada.setVisibility(View.GONE);
        }



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

    private void liderDeSala(String nMatricula){
        turma = recuperarDados("turma");
        DocumentReference docRef = db.collection("ChamadaTurma").document(turma);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String matriculaLider = document.getString("Lider");
                        String matriculaViceLider = document.getString("ViceLider");

                        assert matriculaLider != null;
                        if (matriculaLider.equals(nMatricula) || Objects.equals(matriculaViceLider, nMatricula)){
                            LiderDeTurma = true;
                            salvarDados("lider", "sim");
                        }

                    } else {
                        Log.d("TAGLERLIDER", "Documento de turma não encontrado");
                    }
                } else {
                    Log.d("TAGLERLIDER", "Falhou em ", task.getException());
                }
            }
        });
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

    private void atualizarGrafico(String pre, String au){
        if (!TextUtils.isEmpty(pre) && !TextUtils.isEmpty(au)) {
            int presen = Integer.parseInt(pre);
            int ausen = Integer.parseInt(au);
            ArrayList<PieEntry> entiers = new ArrayList<>();
            entiers.add(new PieEntry(presen, "Presenças"));
            entiers.add(new PieEntry(ausen, "Ausencias"));

            // Configurar cores para cada fatia do gráfico
            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(Color.GREEN);
            colors.add(Color.RED);

            PieDataSet pieDataSet = new PieDataSet(entiers, "");
            pieDataSet.setColors(colors);

            PieData pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);

            pieData.setDrawValues(false); // Desabilitar valores nas fatias

            // Desabilitar legenda em cima do grafico
            //pieChart.setDrawEntryLabels(false);

            // Desabilitar legenda completa
            pieChart.getLegend().setEnabled(false);

            //deixa o grafico completo
            pieChart.setHoleRadius(0f);
            pieChart.setTransparentCircleRadius(0f);


            pieChart.getDescription().setEnabled(false);
            pieChart.animateY(3000);
            pieChart.setHoleColor(Color.parseColor("#2196F3"));
            pieChart.invalidate();
        }

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