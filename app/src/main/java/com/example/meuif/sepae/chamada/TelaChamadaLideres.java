package com.example.meuif.sepae.chamada;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.meuif.R;
import com.example.meuif.sepae.telaPrincipalSepae;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class TelaChamadaLideres extends AppCompatActivity {
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private Spinner spinnerMeses;
    private Spinner spinnerDias;
    private Spinner spinnerTurmas;
    private FirebaseFirestore db;
    private List<String> dias = new ArrayList<>();
    private List<String> meses = new ArrayList<>();
    private String turma = "";
    private Map<String, Object> dataGlobal = new HashMap<>();
    private Map<String, String> mesesAno = new HashMap<>();
    private RecyclerView recyclerViewChamadaSepae;
    private String diaSelecionado;
    private List<String> nomesChamadas;
    private List<Integer> chamdaImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_chamada_lideres);

        carregarComponentes();
        setupPermissionLauncher();
    }


    private void telaVoltar(){
        // Criar a Intent
        Intent intent = new Intent(getApplicationContext(), telaPrincipalSepae.class);
        // Iniciar a atividade de destino
        startActivity(intent);
        finish();
    }

    protected void onStart() {
        super.onStart();
        atribuirMesesAno();
        carregarComponentes();
        setarSpinnerTurmas(new Callback() {
            @Override
            public void onComplete() {
                pegarDadosDias(new Callback() {
                    @Override
                    public void onComplete() {
                        setarSpinnerMeses();
                        setarSpinnerDias();
                        setarRecyclerView(diaSelecionado);
                    }
                });
            }
        });

    }

    private void setarRecyclerView(String diaSelecionado){
        nomesChamadas.clear();
        chamdaImages.clear();

        Log.d("TAG", "dataglobal " + dataGlobal.toString());

        Map<String, Object> aux = (Map<String, Object>) dataGlobal.get(diaSelecionado);

        Log.d("TAG", "sla get" + aux);

        if (aux != null){

            List<Map.Entry<String, Object>> sortedEntries = new ArrayList<>(aux.entrySet());

            Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Object>>() {
                @Override
                public int compare(Map.Entry<String, Object> entry1, Map.Entry<String, Object> entry2) {
                    return entry1.getKey().compareTo(entry2.getKey());
                }
            });

            Map<String, Object> sortedMap = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : sortedEntries) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }

            int cont = 1;
            for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
                String chave = entry.getKey();
                Object valor = entry.getValue();

                nomesChamadas.add(cont + " - " + chave);
                if ((boolean) valor){
                    chamdaImages.add(R.drawable.presenca);
                } else if (!(boolean) valor){
                    chamdaImages.add(R.drawable.falta);
                }

                cont += 1;


                Log.d("TAG", "Chave: " + chave + ", Valor: " + valor);
            }

        }



        AdapterChamadaSepae adapter = new AdapterChamadaSepae(nomesChamadas, chamdaImages);

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewChamadaSepae.setLayoutManager(layoutManager);
        recyclerViewChamadaSepae.setHasFixedSize(true);
        recyclerViewChamadaSepae.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerViewChamadaSepae.setAdapter(adapter); //criar adapter

    }

    private void atribuirMesesAno(){
        mesesAno.put("01", "Janeiro");
        mesesAno.put("02", "Fevereiro");
        mesesAno.put("03", "Março");
        mesesAno.put("04", "Abril");
        mesesAno.put("05", "Maio");
        mesesAno.put("06", "Junho");
        mesesAno.put("07", "Julho");
        mesesAno.put("08", "Agosto");
        mesesAno.put("09", "Setembro");
        mesesAno.put("10", "Outubro");
        mesesAno.put("11", "Novembro");
        mesesAno.put("12", "Dezembro");
    }

    private void carregarComponentes(){
        db = FirebaseFirestore.getInstance();
        spinnerMeses = findViewById(R.id.spinnerMeses);
        spinnerDias = findViewById(R.id.spinnerDias);
        spinnerTurmas = findViewById(R.id.spinnerTurmas);
        recyclerViewChamadaSepae = findViewById(R.id.recyclerViewChamadaSepae);

        nomesChamadas = new ArrayList<>();
        chamdaImages = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_actionbar);
        ImageView leftImage = findViewById(R.id.leftImage);
        ImageView rightImage = findViewById(R.id.rightImage); //baixar pdf

        leftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaVoltar();
            }
        });

        rightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirDialogPDF();
            }
        });
    }

    private void abrirDialogPDF(){
        AlertDialog.Builder dialog2 = new AlertDialog.Builder(this);
        dialog2.setTitle("Baixar PDF de Chamada");
        dialog2.setMessage("Escolha a turma e mês");
        dialog2.setCancelable(false);

// Inflar o layout customizado que contém os Spinners
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        dialog2.setView(customLayout);

// Obtenha referências para os Spinners
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Spinner spinnerTurma = customLayout.findViewById(R.id.spinnerTurmaDialog);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Spinner spinnerMes = customLayout.findViewById(R.id.spinnerMesDialog);



// Crie um ArrayAdapter para preencher os Spinners com os itens desejados
        ArrayAdapter<CharSequence> turmaAdapter = ArrayAdapter.createFromResource(this, R.array.turmas, android.R.layout.simple_spinner_item);
        turmaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurma.setAdapter(turmaAdapter);

        ArrayAdapter<CharSequence> mesAdapter = ArrayAdapter.createFromResource(this, R.array.months_array, android.R.layout.simple_spinner_item);
        mesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMes.setAdapter(mesAdapter);

        dialog2.setPositiveButton("Baixar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Processando...", Toast.LENGTH_SHORT).show();

                String turmaSelecionada = spinnerTurma.getSelectedItem().toString();
                String mesSelecionado = spinnerMes.getSelectedItem().toString();

                // Agora você pode usar turmaSelecionada e mesSelecionado como desejado

                requestWriteExternalStoragePermission(turmaSelecionada, mesSelecionado);
            }
        });

        dialog2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Confira os dados e tente novamente", Toast.LENGTH_SHORT).show();
            }
        });

        dialog2.create();
        dialog2.show();

    }

    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(new RequestPermission(), isGranted -> {
            if (isGranted) {
                // A permissão foi concedida, você pode prosseguir com a operação que exigia a permissão.

            } else {
                //requestWriteExternalStoragePermission();
                // A permissão foi negada pelo usuário.

            }
        });
    }
    // Para solicitar a permissão em algum lugar do seu código
    private void requestWriteExternalStoragePermission(String turmaSelecionada, String mesSelecionado) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // A permissão já foi concedida, prossiga com a operação.
            baixarPDF(turmaSelecionada, mesSelecionado);
        } else {
            // Solicitar a permissão usando ActivityCompat.requestPermissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }


    private void baixarPDF(String turma, String mes){
        Context context = this;

        String diaGerado = getDayAndMonth();

                Map<String, String> mesesMap = new HashMap<>();
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

                String diaMes = mesesMap.get(mes);

                String nome = turma + mes + ".pdf";
                Toast.makeText(getApplicationContext(), "Processando Turmas", Toast.LENGTH_SHORT).show();
                pegarDadosDias(turma, new Callback() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(getApplicationContext(), "Processando PDF", Toast.LENGTH_SHORT).show();
                        Log.d("pdf", nome + "dia" + diaMes + "data" + dataGlobal);
                        //antes de criar o pdf precisa adicoonar os dias q n foram realizados a chamada

                        RecyclerViewToPdf recyclerViewToPdf = new RecyclerViewToPdf(dataGlobal, diaMes, mes, turma, diaGerado);
                        recyclerViewToPdf.createPdf(context, nome);
                    }
                });

    }
    public static List<String> obterDiasUteisDoMes() {
        List<String> diasUteis = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Defina o dia para o primeiro dia do mês atual

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        int ultimoDiaDoMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        while (calendar.get(Calendar.DAY_OF_MONTH) <= ultimoDiaDoMes) {
            int diaDaSemana = calendar.get(Calendar.DAY_OF_WEEK);

            // 1 é Domingo, 7 é Sábado
            if (diaDaSemana != Calendar.SATURDAY && diaDaSemana != Calendar.SUNDAY) {
                diasUteis.add(dateFormat.format(calendar.getTime()));
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1); // Avança para o próximo dia
        }

        return diasUteis;
    }

    private String getDayAndMonth() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3")); // Defina o fuso horário desejado
        Date date = new Date();
        return dateFormat.format(date);
    }


    private void pegarDadosDias(String turma, Callback callback){

        DocumentReference docRef = db.collection("ChamadaTurma").document(turma);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtendo todos os campos do documento
                        Map<String, Object> data = document.getData();

                        dataGlobal = data;

                        Log.d("TAG", "dataGlobal = " + dataGlobal.toString());

                        Log.d("TAG", "completo "+data.toString());
                        callback.onComplete();

                    } else {
                        Log.d("TAG", "O documento não existe.");
                        callback.onComplete();
                    }
                } else {
                    Log.d("TAG", "Erro ao obter o documento: " + task.getException());
                    callback.onComplete();
                }
            }
        });


    }

    private void setarSpinnerTurmas(Callback callback){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.turmas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurmas.setAdapter(adapter);

        spinnerTurmas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dias.clear();
                String selectedTurma = parent.getItemAtPosition(position).toString();

                turma = selectedTurma;
                pegarDadosDias(new Callback() {
                    @Override
                    public void onComplete() {
                        setarSpinnerMeses();
                        setarSpinnerDias();
                    }
                });
                callback.onComplete();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ação a ser tomada quando nada é selecionado (opcional)
            }
        });
    }

    private void setarSpinnerMeses(){
        meses.clear();
        for (int i = 0; i < dias.size(); i++){
            String dia = dias.get(i);
            String subString = dia.substring(2, 4);
            if (!meses.contains(mesesAno.getOrDefault(subString, "00"))){
                meses.add(mesesAno.getOrDefault(subString, "00"));
            }

        }
        Log.d("TAG", meses.toString());

        // Converter a List<String> em um array de strings simples
        String[] dataArray = new String[meses.size()];
        meses.toArray(dataArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeses.setAdapter(adapter);

    }

    private void pegarDadosDias(Callback callback){

        DocumentReference docRef = db.collection("ChamadaTurma").document(turma);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtendo todos os campos do documento
                        Map<String, Object> data = document.getData();

                        dataGlobal = data;

                        Log.d("TAG", "dataGlobal = " + dataGlobal.toString());

                        Log.d("TAG", "completo "+data.toString());

                        if (data != null) {
                            // Iterando pelos campos e imprimindo seus nomes
                            for (String fieldName : data.keySet()) {
                                Log.d("TAG", "Campo: " + fieldName);
                                if (!fieldName.equals("Lider") && !fieldName.equals("nomesSala") && !fieldName.equals("ViceLider")){
                                    if (!dias.contains(fieldName)){
                                        dias.add(fieldName);
                                    }
                                }
                            }
                            callback.onComplete();
                        }
                    } else {
                        Log.d("TAG", "O documento não existe.");
                        callback.onComplete();
                    }
                } else {
                    Log.d("TAG", "Erro ao obter o documento: " + task.getException());
                    callback.onComplete();
                }
            }
        });


    }

    private void setarSpinnerDias(){
        Log.d("TAG", " dias = " + dias.toString());

        // Converter a List<String> em um array de strings simples
        String[] dataArray = new String[dias.size()];
        for(int i = 0; i< dias.size(); i++){
            dataArray[i] = dias.get(i).substring(0, 2);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDias.setAdapter(adapter);

        spinnerDias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDia = parent.getItemAtPosition(position).toString();

                for(int i = 0; i< dias.size(); i++){
                    if (selectedDia.equals(dias.get(i).substring(0, 2))){
                        diaSelecionado = dias.get(i);
                        setarRecyclerView(diaSelecionado);
                        break;
                    }


                }

                Log.d("TAG", "dia selecionado" + diaSelecionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ação a ser tomada quando nada é selecionado (opcional)
            }
        });
    }

    private interface Callback {
        void onComplete();
    }


}