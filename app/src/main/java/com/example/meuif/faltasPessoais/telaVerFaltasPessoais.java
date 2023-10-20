package com.example.meuif.faltasPessoais;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.meuif.R;
import com.example.meuif.Tela_Principal;
import com.example.meuif.autorizacoes.AdapterAutorizacaoEntrada;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
    private ImageView imageViewEsquerdaCarteirinha;
    private ImageView imageViewDireitaCarteirinha;
    private RecyclerView recyclerAcessosRegistrados;
    private TextView textViewSaidaDiaAcessosCarteirinha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_ver_faltas_pessoais);

        db = FirebaseFirestore.getInstance();
        carregarComponentes();
        textViewSaidaDiaAcessosCarteirinha.setText(diaAtual());
        atualizaPresenca();
        atualizarGraficoSemanal();
        pegarAcessosPorDia();
    }

    private void pegarAcessosPorDia(){
        String dia = dia();
        String nMatricula = recuperarDados("matricula");
        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(nMatricula).document("chamadaPessoal");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.contains(dia)){
                            List<Timestamp> lista = (List<Timestamp>) document.get(dia);
                            Log.d("lista", "lsita " + lista.toString());
                            carregarAcessos(lista);
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

    private void carregarAcessos(List<Timestamp> lista){
        List<ModelAcessoAluno> modelAcessoAlunos = new ArrayList<>();
        int count = 1;
        String nome = recuperarDados("nome");
        String flag = "Entrada";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-3"));

        for (Timestamp timestamp : lista) {
            Date date = timestamp.toDate(); // Converta o Timestamp em um objeto Date
            String formattedDate = sdf.format(date); // Agora você pode formatá-lo
            ModelAcessoAluno modelAcessoAluno = new ModelAcessoAluno(nome, formattedDate, String.valueOf(count), flag);
            modelAcessoAlunos.add(modelAcessoAluno);
            count++;
            if (flag.equals("Entrada")) {flag = "Saida";}
            else {flag = "Entrada";}
        }
        AdapterAcessosAluno adapter = new AdapterAcessosAluno(modelAcessoAlunos);

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerAcessosRegistrados.setLayoutManager(layoutManager);
        recyclerAcessosRegistrados.setHasFixedSize(true);
        recyclerAcessosRegistrados.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerAcessosRegistrados.setAdapter(adapter);

    }

    private void carregarComponentes() {
        ActionBar actionBar = getSupportActionBar();
        setTitle("Minhas Faltas");
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (actionBar != null) {
            // Defina a cor de background desejada (por exemplo, cor vermelha)
            ColorDrawable colorDrawable = new ColorDrawable(0xff23729a);
            actionBar.setBackgroundDrawable(colorDrawable);
        }


        saidaNumAulas = findViewById(R.id.saidaNumAulas2);
        saidaNumAusencias = findViewById(R.id.saidaNumAusencias2);
        saidaNumPresencas = findViewById(R.id.saidaNumPresencas2);
        pieChart = findViewById(R.id.pie_chart);
        imageViewEsquerdaCarteirinha = findViewById(R.id.imageViewEsquerdaCarteirinha);
        imageViewDireitaCarteirinha = findViewById(R.id.imageViewDireitaCarteirinha);
        graficoFaltaDiaSemana = findViewById(R.id.graficoFaltaDiaSemana);
        textViewSaidaDiaAcessosCarteirinha = findViewById(R.id.textViewSaidaDiaAcessosCarteirinha);
        recyclerAcessosRegistrados = findViewById(R.id.recyclerAcessosRegistrados);
    }

    private String diaAtual(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-3"));

        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int ano = calendar.get(Calendar.YEAR);

        String data = String.format("%02d/%02d/%d", dia, mes, ano);
        return data;
    }

    private String dia(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-3"));
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int ano = calendar.get(Calendar.YEAR);
        String data = String.format("%02d%02d%d", dia, mes, ano);
        return data;
    }
    @Override
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
        Intent intent = new Intent(this, Tela_Principal.class);

        // Iniciar a atividade de destino
        startActivity(intent);
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
        ArrayList<Integer> valores = new ArrayList<>();
        String nMatricula = recuperarDados("matricula");

        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(nMatricula).document("chamadaPessoal");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Integer> mapFaltas = (Map<String, Integer>) document.get("todos");
                        Log.d("tagler", "map " + mapFaltas.toString() + " tipos " + String.valueOf(mapFaltas.get("Segunda-feira")));
                        valores.add(Integer.parseInt(String.valueOf(mapFaltas.get("Segunda-feira"))));
                        valores.add(Integer.parseInt(String.valueOf(mapFaltas.get("Terca-feira"))));
                        valores.add(Integer.parseInt(String.valueOf(mapFaltas.get("Quarta-feira"))));
                        valores.add(Integer.parseInt(String.valueOf(mapFaltas.get("Quinta-feira"))));
                        valores.add(Integer.parseInt(String.valueOf(mapFaltas.get("Sexta-feira"))));

                        mostrargraficoSemanal(valores);
                    } else {
                        Log.d("TAGLER", "Documento não encontrado");
                    }
                } else {
                    Log.d("TAGLER", "Falhou em ", task.getException());
                }
            }
        });
    }

    private void mostrargraficoSemanal(ArrayList<Integer> valores) {
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
        Description description = new Description();
        description.setText("Faltas por dia da semana");
        graficoFaltaDiaSemana.setDescription(description);

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