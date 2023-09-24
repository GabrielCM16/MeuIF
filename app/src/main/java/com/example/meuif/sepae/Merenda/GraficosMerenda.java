package com.example.meuif.sepae.Merenda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.meuif.R;
import com.example.meuif.sepae.telaMerendaEscolar;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GraficosMerenda extends AppCompatActivity {
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
    private String mesSelecionado = "";
    private String turmaSelecionado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficos_merenda);

        inicializarComponents();
        setarSpinnerMeses();
        setarSpinnerTurma();
    }

    private void inicializarComponents(){
        db = FirebaseFirestore.getInstance();
        barChart = findViewById(R.id.graficoMerenda);
        spinnerMesesMerendaGrafico = findViewById(R.id.spinnerMesesMerendaGrafico);
        spinnerTurmaMerendaGrafico = findViewById(R.id.spinnerTurmaMerendaGrafico);

    }

    private void carregarDiasMerenda(){
        barEntriesArrayList = new ArrayList<>();
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

                                if (mes.equals("00")){
                                    int quantity = (int) key.size();
                                    Log.d("Firebase", quantity + " dia + " + dia.substring(0,2));
                                    barEntriesArrayList.add(new BarEntry(i, quantity)); // Use o índice como valor no eixo X
                                    String mesAux = dia.substring(0, 2) + "/" + dia.substring(2, 4);
                                    xAxisLabels.add(mesAux); // Adicione o dia como rótulo no eixo X
                                    i++; // Incrementa o índice
                                }else {
                                    if (dia.substring(2,4).equals(mes)){
                                        int quantity = (int) key.size();
                                        Log.d("Firebase", quantity + " dia + " + dia.substring(0,2));
                                        barEntriesArrayList.add(new BarEntry(i, quantity)); // Use o índice como valor no eixo X
                                        xAxisLabels.add(dia.substring(0, 2)); // Adicione o dia como rótulo no eixo X
                                        i++; // Incrementa o índice
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
        barDataSet = new BarDataSet(barEntriesArrayList, "Controle de quantidade PNAE, Gerado por MeuIF");

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
        // Suponha que 'barChart' seja a instância do seu gráfico de barras.
        barChart.saveToGallery("nome_do_arquivo", 100); // O segundo parâmetro é a qualidade da imagem (0-100).

    }


}

