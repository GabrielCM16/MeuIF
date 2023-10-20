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
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
        pegarAcessosPorDia(dia());
    }

    private void pegarAcessosPorDia(String dia){
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
                        } else {
                            List<Timestamp> lista = new ArrayList<Timestamp>();
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

        imageViewEsquerdaCarteirinha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menosUmDia();
            }
        });
        imageViewDireitaCarteirinha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maisUmDia();
            }
        });
    }

    private void menosUmDia(){
        String diaHj = textViewSaidaDiaAcessosCarteirinha.getText().toString();
        String dia = diaHj.substring(0,2);
        if (!dia.contains("/")){
            int i = Integer.valueOf(dia);
            if (i > 1){
                i -= 1;
                String mesAno = diaHj.substring(2);
                String aux = String.valueOf(i) + mesAno;
                textViewSaidaDiaAcessosCarteirinha.setText(aux);
            }
        } else {
            dia = diaHj.substring(0,1);
            int i = Integer.valueOf(dia);
            if (i > 1){
                i -= 1;
                String mesAno = diaHj.substring(1);
                String aux = String.valueOf(i) + mesAno;
                textViewSaidaDiaAcessosCarteirinha.setText(aux);
            }
        }
        pegarAcessosPorDia(formatarData(textViewSaidaDiaAcessosCarteirinha.getText().toString()));
    }

    private void maisUmDia(){
        Map<Integer, Integer> meses = new HashMap<>();
        meses.put(1,31);
        meses.put(2,28);
        meses.put(3,31);
        meses.put(4,30);
        meses.put(5,31);
        meses.put(6,30);
        meses.put(7,31);
        meses.put(8,31);
        meses.put(9,30);
        meses.put(10,31);
        meses.put(11,30);
        meses.put(12,31);

        String diaHj = textViewSaidaDiaAcessosCarteirinha.getText().toString();
        String dia = diaHj.substring(0,2);

        String mes = diaHj.substring(3,5);
        if (!mes.contains("/")){
            if (meses.containsKey(Integer.valueOf(mes))){
                int num = meses.get(Integer.valueOf(mes));
                if (!dia.contains("/")){
                    int i = Integer.valueOf(dia);
                    if (i < num){
                        i += 1;
                        String mesAno = diaHj.substring(2);
                        String aux = String.valueOf(i) + mesAno;
                        textViewSaidaDiaAcessosCarteirinha.setText(aux);
                    }
                } else {
                    dia = diaHj.substring(0,1);
                    int i = Integer.valueOf(dia);
                    if (i < num){
                        i += 1;
                        String mesAno = diaHj.substring(1);
                        String aux = String.valueOf(i) + mesAno;
                        textViewSaidaDiaAcessosCarteirinha.setText(aux);
                    }
                }
            }
        } else {
            mes = diaHj.substring(2,4);
            if (meses.containsKey(Integer.valueOf(mes))){
                int num = meses.get(Integer.valueOf(mes));
                if (!dia.contains("/")){
                    int i = Integer.valueOf(dia);
                    if (i < num){
                        i += 1;
                        String mesAno = diaHj.substring(2);
                        String aux = String.valueOf(i) + mesAno;
                        textViewSaidaDiaAcessosCarteirinha.setText(aux);
                    }
                } else {
                    dia = diaHj.substring(0,1);
                    int i = Integer.valueOf(dia);
                    if (i < num){
                        i += 1;
                        String mesAno = diaHj.substring(1);
                        String aux = String.valueOf(i) + mesAno;
                        textViewSaidaDiaAcessosCarteirinha.setText(aux);
                    }
                }
            }
        }
        pegarAcessosPorDia(formatarData(textViewSaidaDiaAcessosCarteirinha.getText().toString()));
    }

    public String formatarData(String data) {
        // Remova as barras da data original
        String dataFormatada = data.replace("/", "");

        // Certifique-se de que a data tem pelo menos 8 caracteres (DDMMAAAA)
        while (dataFormatada.length() < 8) {
            dataFormatada = "0" + dataFormatada;
        }

        return dataFormatada;
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
                        if (document.contains("todos")) {
                            Map<String, Integer> mapFaltas = (Map<String, Integer>) document.get("todos");
                            Log.d("tagler", "map " + mapFaltas.toString() + " tipos " + String.valueOf(mapFaltas.get("Segunda-feira")));
                            valores.add(Integer.parseInt(String.valueOf(mapFaltas.get("Segunda-feira"))));
                            valores.add(Integer.parseInt(String.valueOf(mapFaltas.get("Terca-feira"))));
                            valores.add(Integer.parseInt(String.valueOf(mapFaltas.get("Quarta-feira"))));
                            valores.add(Integer.parseInt(String.valueOf(mapFaltas.get("Quinta-feira"))));
                            valores.add(Integer.parseInt(String.valueOf(mapFaltas.get("Sexta-feira"))));

                            mostrargraficoSemanal(valores);
                        } else {
                            //precisa criar o campo "todos"
                            criarTodos();
                            valores.add(0);
                            valores.add(0);
                            valores.add(0);
                            valores.add(0);
                            valores.add(0);
                            mostrargraficoSemanal(valores);
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

    private void criarTodos(){
        String matricula = recuperarDados("matricula");
        Map<String , Integer> todos = new HashMap<>();
        todos.put("Segunda-feira", 0);
        todos.put("Terca-feira", 0);
        todos.put("Quarta-feira", 0);
        todos.put("Quinta-feira", 0);
        todos.put("Sexta-feira", 0);
        DocumentReference usuarioDocRef = db.collection("Usuarios").document("Alunos").collection(matricula).document("chamadaPessoal");

        usuarioDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    usuarioDocRef.update("todos", todos)
                            .addOnSuccessListener(aVoid -> {
                                // Sucesso ao adicionar o timestamp
                                Log.d("Firestore", "Timestamp adicionado com sucesso");
                            })
                            .addOnFailureListener(e -> {
                                // Falha ao adicionar o timestamp
                                Log.e("Firestore", "Erro ao adicionar o timestamp", e);
                            });
                } else {
                    Log.d("Firestore", "Documento não encontrado");
                }
            } else {
                Log.d("Firestore", "Falha em obter o documento", task.getException());
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
        graficoFaltaDiaSemana.animateY(3000);

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