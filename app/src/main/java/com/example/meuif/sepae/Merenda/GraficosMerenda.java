package com.example.meuif.sepae.Merenda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;



import com.example.meuif.R;
import com.example.meuif.sepae.telaMerendaEscolar;
import com.example.meuif.sepae.telaPrincipalSepae;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GraficosMerenda extends AppCompatActivity {
    public static final int REQUEST_STORAGE_PERMISSION = 123;
    FirebaseFirestore db;
    // variable for our bar chart
    BarChart barChart;
    // variable for our bar data.
    BarData barData;
    // variable for our bar data set.
    BarDataSet barDataSet;
    // array list for storing entries.
    ArrayList barEntriesArrayList;
    private Spinner spinnerMesesMerendaGrafico;
    private Spinner spinnerTurmaMerendaGrafico;
    private ProgressBar progressBarGrafico;
    private String mesSelecionado = "";
    private String turmaSelecionado = "";
    private ImageView baixargraficoPNAE;
    private Map<String, String> turmasAlunos = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficos_merenda);

        inicializarComponents();
        pegarTurmaAlunos(new Callback() {
            @Override
            public void onComplete() {
                setarSpinnerMeses();
                setarSpinnerTurma();
            }
        });

        baixargraficoPNAE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                someMethodRequiringPermissions();
            }
        });

    }
    private interface Callback {
        void onComplete();
    }

    private void baixarGrafico(){
        progressBarGrafico.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Preparando Gráfico para download...", Toast.LENGTH_SHORT).show();

        String nomeArquivo = "PNAE" + turmaSelecionado + mesSelecionado;
        barChart.saveToGallery(nomeArquivo, 100); // O segundo parâmetro é a qualidade da imagem (0-100).
        Toast.makeText(this, "Salvo na galeria", Toast.LENGTH_SHORT).show();
        progressBarGrafico.setVisibility(View.INVISIBLE);
    }

    private void someMethodRequiringPermissions() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
           baixarGrafico();
        } else {
            // A permissão ainda não foi concedida, solicite-a
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, REQUEST_STORAGE_PERMISSION, perms)
                            .setRationale("Precisamos da permissão para acessar o armazenamento.")
                            .setPositiveButtonText("Conceder")
                            .setNegativeButtonText("Cancelar")
                          //  .setTheme(R.style.AppTheme)
                            .build()
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Passe os resultados para EasyPermissions para tratamento
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_STORAGE_PERMISSION)
    public void onPermissionGranted() {
        // A permissão foi concedida, você pode prosseguir com a ação desejada.
        baixarGrafico();
    }

    public void onPermissionDenied() {
        // O usuário negou a permissão, você pode exibir uma mensagem ou tomar outra ação apropriada.
        Toast.makeText(this, "A permissão foi negada.", Toast.LENGTH_SHORT).show();
    }


    private void pegarTurmaAlunos(Callback callback){
        progressBarGrafico.setVisibility(View.VISIBLE);
        DocumentReference docRef = db.collection("Usuarios").document("Alunos");

// Obtém os dados do documento
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, String> turmaAux = (Map<String, String>) document.get("TurmasAlunos");

                        // Agora você pode iterar sobre o Map e acessar os nomes dos alunos
                        for (Map.Entry<String, String> entry : turmaAux.entrySet()) {
                            String matricula = entry.getKey();
                            String turma = entry.getValue();
                            // Faça algo com as informações...
                            turmasAlunos.put(matricula, turma);
                        }
                    Log.d("turmas", "turmas alunos" + turmasAlunos);
                        progressBarGrafico.setVisibility(View.INVISIBLE);
                        callback.onComplete();

                    } else {
                        // O documento não existe
                        progressBarGrafico.setVisibility(View.INVISIBLE);
                        callback.onComplete();
                    }
                } else {
                    // Falha ao obter o documento
                    progressBarGrafico.setVisibility(View.INVISIBLE);
                    callback.onComplete();
                }
            }
        });
    }

    private void inicializarComponents(){
        db = FirebaseFirestore.getInstance();
        barChart = findViewById(R.id.graficoMerenda);
        spinnerMesesMerendaGrafico = findViewById(R.id.spinnerMesesMerendaGrafico);
        spinnerTurmaMerendaGrafico = findViewById(R.id.spinnerTurmaMerendaGrafico);
        progressBarGrafico = findViewById(R.id.progressBarGrafico);
        progressBarGrafico.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        baixargraficoPNAE = findViewById(R.id.baixargraficoPNAE);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Defina a cor de background desejada (por exemplo, cor vermelha)
            ColorDrawable colorDrawable = new ColorDrawable(0xff23729a);
            actionBar.setBackgroundDrawable(colorDrawable);
        }
        setTitle("Graficos PNAE");
        // Adiciona um ícone de ação à direita
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24); // Define o ícone de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilita o botão de navegação

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
        Intent intent = new Intent(this, telaMerendaEscolar.class);
        // Iniciar a atividade de destino
        startActivity(intent);
    }

    private void carregarDiasMerenda(){
        progressBarGrafico.setVisibility(View.VISIBLE);
        barEntriesArrayList = new ArrayList<>();
        List<DataPoint> dataPoints = new ArrayList<>();
        String mes = mesParaDia();

        CollectionReference colecao = db.collection("MerendaEscolar");
        colecao.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> xAxisLabels = new ArrayList<>(); // Lista para rótulos no eixo X
                            int i = 0; // Índice para os rótulos no eixo X

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> dados = document.getData();

                                Log.d("Firebase", "Dados: " + dados.toString());

                                List<Object> key = (List<Object>) dados.get("todos");
                                String dia = document.getId();

                                if (mes.equals("00") && turmaSelecionado.equals("Todas")){

                                    int quantity = (int) key.size();
                                    String mesAux = dia.substring(0, 2) + "/" + dia.substring(2, 4);
                                    dataPoints.add(new DataPoint(mesAux, quantity));
                                    Collections.sort(dataPoints, new Comparator<DataPoint>() {
                                        @Override
                                        public int compare(DataPoint dataPoint1, DataPoint dataPoint2) {
                                            // Use compareTo para comparar as strings mesAux
                                            return dataPoint1.getMesAux().compareTo(dataPoint2.getMesAux());
                                        }
                                    });

                                    barEntriesArrayList.clear();
                                    xAxisLabels.clear();
                                    int a = 0;
                                    for (DataPoint dataPoint : dataPoints) {
                                        barEntriesArrayList.add(new BarEntry(a, dataPoint.getQuantity()));
                                        xAxisLabels.add(dataPoint.getDiaAux());
                                        a++;
                                    }

                                }else if (turmaSelecionado.equals("Todas")){
                                    if (dia.substring(2,4).equals(mes)){
                                        int quantity = (int) key.size();
                                        Log.d("Firebase", quantity + " dia + " + dia.substring(0,2));
                                        barEntriesArrayList.add(new BarEntry(i, quantity)); // Use o índice como valor no eixo X
                                        xAxisLabels.add(dia.substring(0, 2)); // Adicione o dia como rótulo no eixo X
                                        i++; // Incrementa o índice
                                    }
                                } else{
                                    if (mes.equals("00")){
                                        int quantidadeDoMesPorTurma = 0;
                                        for (Object obj : key) { //percorre o mes
                                            if (obj instanceof Map) {
                                                Map<String, Timestamp> map = (Map<String, Timestamp>) obj; //cada matricula e o timestamp

                                                // Agora você pode percorrer o mapa e extrair a chave e o valor
                                                for (Map.Entry<String, Timestamp> entry : map.entrySet()) {
                                                    String chave = entry.getKey(); //matricula
                                                    Timestamp valor = entry.getValue(); //timestamp

                                                    if (procurarTurma(chave)){
                                                        quantidadeDoMesPorTurma++;
                                                    }
                                                }
                                            }
                                        }//acabou o for do mes
                                        String mesAux = dia.substring(0, 2) + "/" + dia.substring(2, 4);
                                        dataPoints.add(new DataPoint(mesAux, quantidadeDoMesPorTurma));
                                        Collections.sort(dataPoints, new Comparator<DataPoint>() {
                                            @Override
                                            public int compare(DataPoint dataPoint1, DataPoint dataPoint2) {
                                                // Use compareTo para comparar as strings mesAux
                                                return dataPoint1.getMesAux().compareTo(dataPoint2.getMesAux());
                                            }
                                        });

                                        barEntriesArrayList.clear();
                                        xAxisLabels.clear();
                                        int a = 0;
                                        for (DataPoint dataPoint : dataPoints) {
                                            barEntriesArrayList.add(new BarEntry(a, dataPoint.getQuantity()));
                                            xAxisLabels.add(dataPoint.getDiaAux());
                                            a++;
                                        }
                                    } else {
                                        //seleciou um mes e selecionou uma turma
                                        if (dia.substring(2,4).equals(mes)){ //achou o mes
                                            int quantidadeDoMesPorTurma = 0;
                                            for (Object obj : key) { //percorre o mes
                                                if (obj instanceof Map) {
                                                    Map<String, Timestamp> map = (Map<String, Timestamp>) obj; //cada matricula e o timestamp

                                                    // Agora você pode percorrer o mapa e extrair a chave e o valor
                                                    for (Map.Entry<String, Timestamp> entry : map.entrySet()) {
                                                        String chave = entry.getKey(); //matricula
                                                        Timestamp valor = entry.getValue(); //timestamp

                                                        if (procurarTurma(chave)){
                                                            quantidadeDoMesPorTurma++;
                                                        }
                                                    }
                                                }
                                            }//acabou o for do mes
                                            barEntriesArrayList.add(new BarEntry(i, quantidadeDoMesPorTurma)); // Use o índice como valor no eixo X
                                            xAxisLabels.add(dia.substring(0, 2)); // Adicione o dia como rótulo no eixo X
                                            i++; // Incrementa o índice
                                        }
                                    }
                                }
                            }

                            setarGrafico(xAxisLabels);


                        } else {
                            // A consulta falhou, trate o erro aqui
                            Log.w("Firebase", "Erro ao obter documentos.", task.getException());
                        }
                    }
                });
    }

    private Boolean procurarTurma(String matricula) {
        Log.d("turmas",  "matricula " + matricula + " turmas alunos " + turmasAlunos.toString());

        // Obtém a turma associada à matrícula
        String turmaAssociada = turmasAlunos.get(matricula);

        // Verifica se a turma associada não é nula e é igual à turma selecionada
        if (turmaAssociada != null && turmaAssociada.equals(turmaSelecionado)) {
            return true;
        } else {
            return false;
        }
    }


    private String mesParaDia(){
        String diaMes =  "00";
        if (mesSelecionado != null && !mesSelecionado.equals("")){
            Map<String, String> mesesMap = new HashMap<>();
            mesesMap.put("Todos", "00");
            mesesMap.put("Janeiro", "01");
            mesesMap.put("Fevereiro", "02");
            mesesMap.put("Março", "03");
            mesesMap.put("Abril", "04");
            mesesMap.put("Maio", "05");
            mesesMap.put("Junho", "06");
            mesesMap.put("Julho", "07");
            mesesMap.put("Agosto", "08");
            mesesMap.put("Setembro", "09");
            mesesMap.put("Outubro", "10");
            mesesMap.put("Novembro", "11");
            mesesMap.put("Dezembro", "12");

            diaMes = mesesMap.get(mesSelecionado);
        }
        return diaMes;
    }

    private void setarSpinnerTurma(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.turmas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurmaMerendaGrafico.setAdapter(adapter);
        spinnerTurmaMerendaGrafico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTurma = parent.getItemAtPosition(position).toString();
                turmaSelecionado = selectedTurma;
                carregarDiasMerenda();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ação a ser tomada quando nada é selecionado (opcional)
            }
        });
    }

    private void setarSpinnerMeses(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.months_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMesesMerendaGrafico.setAdapter(adapter);
        spinnerMesesMerendaGrafico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTurma = parent.getItemAtPosition(position).toString();
                mesSelecionado = selectedTurma;
                carregarDiasMerenda();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ação a ser tomada quando nada é selecionado (opcional)
            }
        });
    }

    private void setarGrafico(ArrayList<String> xAxisLabels){
        // creating a new bar data set.
        barDataSet = new BarDataSet(barEntriesArrayList, "Controle de quantidade PNAE, do mês: " +  mesSelecionado + ", da turma: " + turmaSelecionado);

        // Obtenha o eixo X do seu gráfico de barras
        XAxis xAxis = barChart.getXAxis();

        // Configure o ValueFormatter personalizado no eixo X
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));

        barData = new BarData(barDataSet);

        // below line is to set data
        // to our bar chart.
        barChart.setData(barData);

        // adding color to our bar data set.
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);

        // setting text size
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
        progressBarGrafico.setVisibility(View.INVISIBLE);


    }


}

