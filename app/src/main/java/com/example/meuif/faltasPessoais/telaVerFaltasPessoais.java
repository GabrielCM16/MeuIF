package com.example.meuif.faltasPessoais;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.meuif.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class telaVerFaltasPessoais extends AppCompatActivity {
    private String ausencias;
    private String presencas;
    private String aulasTotais;
    private TextView saidaNumAulas;
    private TextView saidaNumAusencias;
    private TextView saidaNumPresencas;
    public PieChart pieChart;
    private BarChart graficoFaltaDiaSemana;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_ver_faltas_pessoais);

        db = FirebaseFirestore.getInstance();
        carregarComponentes();
        atualizaPresenca();
        atualizarGraficoSemanal();
    }

    private void carregarComponentes() {
        saidaNumAulas = findViewById(R.id.saidaNumAulas2);
        saidaNumAusencias = findViewById(R.id.saidaNumAusencias2);
        saidaNumPresencas = findViewById(R.id.saidaNumPresencas2);
        pieChart = findViewById(R.id.pie_chart);
        graficoFaltaDiaSemana = findViewById(R.id.graficoFaltaDiaSemana);
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
        }
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

    private void atualizarGraficoSemanal(){
        // Suponha que você tenha um ArrayList de inteiros representando os valores para cada dia da semana.
        ArrayList<Integer> valores = new ArrayList<>();
        valores.add(10); // Segunda-feira
        valores.add(15); // Terça-feira
        valores.add(8);  // Quarta-feira
        valores.add(12); // Quinta-feira
        valores.add(5);  // Sexta-feira

// Crie um ArrayList de String para os rótulos do eixo X.
        ArrayList<String> diasDaSemana = new ArrayList<>();
        diasDaSemana.add("Segunda");
        diasDaSemana.add("Terça");
        diasDaSemana.add("Quarta");
        diasDaSemana.add("Quinta");
        diasDaSemana.add("Sexta");

// Crie um ArrayList de objetos BarEntry com valores e índices correspondentes.
        ArrayList<BarEntry> barEntriesArrayList = new ArrayList<>();
        for (int i = 0; i < valores.size(); i++) {
            barEntriesArrayList.add(new BarEntry(i, valores.get(i)));
        }

// Crie um conjunto de dados de barras com os valores e rótulos.
        BarDataSet barDataSet = new BarDataSet(barEntriesArrayList, "Faltas por dia da semana");

// Defina cores diferentes para as barras.
        barDataSet.setColors(new int[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA});

// Configure o gráfico de barras.
        BarData barData = new BarData(barDataSet);
        graficoFaltaDiaSemana.setData(barData);

// Configure o eixo X para mostrar os rótulos da semana.
        XAxis xAxis = graficoFaltaDiaSemana.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(diasDaSemana));
        xAxis.setLabelCount(diasDaSemana.size()); // Isso faz com que todos os rótulos sejam visíveis.

// Atualize o gráfico.
        graficoFaltaDiaSemana.invalidate();

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
                        mostrarPresenca();
                        atualizarGrafico(presencas, ausencias);
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