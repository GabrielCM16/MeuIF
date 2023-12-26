package com.example.meuif.sepae;


import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meuif.CaptureAct;
import com.example.meuif.R;
import com.example.meuif.sepae.Merenda.FiltroFragmentPNAE;
import com.example.meuif.sepae.Merenda.GraficosMerenda;
import com.example.meuif.sepae.Merenda.OnTurmaSelectedListener;
import com.example.meuif.sepae.recyclerMerenda.AdapterMerenda;
import com.example.meuif.sepae.recyclerMerenda.AlunoMerenda;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class telaMerendaEscolar extends AppCompatActivity implements OnTurmaSelectedListener {

    private FirebaseFirestore db;
    private ConstraintLayout botao;
    private AdapterMerenda adapter;
    private TextView saidaNumero;
    private List<String> stringList = new ArrayList<>();
    private long tempoValidade = 30000;
    private MediaPlayer somErro;
    private static Pattern p = Pattern.compile("[0-9]+");
    private MediaPlayer somSucess;
    private String ultimaMatricula = " ";
    private Boolean isNumeric;
    private String turma = "";
    private Map<String, Object> dataGlobal = new HashMap<>();
    private String diaSelecionado;
    private List<String> listDias = new ArrayList<>();
    private Map<String, Object> listar = new HashMap<>();
    private RecyclerView listardiasMerenda;
    private List<AlunoMerenda> alunosList = new ArrayList<>();
    private final Map<String, String> nomesAlunos = new HashMap<>();
    private final Map<String, String> turmasAlunos = new HashMap<>();
    private TextView textViewSaidaDiaRegistrosPNAESEPAE2;
    private ImageView imageViewEsquerdaPNAESEPAE2;
    private ImageView imageViewDireitaPNAESEPAE2;
    private EditText entradaMatriculaRegistroMerenda;
    private ConstraintLayout constraintRegistrarRegistroPNAE;
    private ConstraintLayout botaoPassePNAEFrontal;
    private ConstraintLayout ConstraintFiltros;
    private final List<String> matriculasPassadas = new ArrayList<String>();
    private FrameLayout frameFiltros;
    private String turmaSelect = "Todas";
    private int NFiltros = 0;
    private TextView saidaNumeroFiltros;
    private ImageView imageViewCalendarioPNAESEPAE;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_merenda_escolar);

        db = FirebaseFirestore.getInstance();
        pegarNomesAlunos();
        pegarTurmaAlunos();
        carregarComponentes();
        listarDiasMerendados(new Callback() {
            @Override
            public void onComplete() {
                Log.d("obj", "obj " + dataGlobal.toString());
                Log.d("obj", "obj " + listar.get(diaAtual()));
                atualizarRecycler(diaAtual());
                atualizarRecycler(diaAtual());
            }
        });
    }

    private void carregarComponentes(){
        botao = findViewById(R.id.botaoPassePNAE);
        saidaNumero = findViewById(R.id.saidaNumero);
        listardiasMerenda = findViewById(R.id.listardiasMerenda);
        textViewSaidaDiaRegistrosPNAESEPAE2 = findViewById(R.id.textViewSaidaDiaRegistrosPNAESEPAE2);
        textViewSaidaDiaRegistrosPNAESEPAE2.setText(diaAtualB());
        imageViewEsquerdaPNAESEPAE2 = findViewById(R.id.imageViewEsquerdaPNAESEPAE2);
        imageViewDireitaPNAESEPAE2 = findViewById(R.id.imageViewDireitaPNAESEPAE2);
        imageViewCalendarioPNAESEPAE = findViewById(R.id.imageViewCalendarioPNAESEPAE);
        entradaMatriculaRegistroMerenda = findViewById(R.id.entradaMatriculaRegistroMerenda);
        saidaNumeroFiltros = findViewById(R.id.saidaNumeroFiltros);
        constraintRegistrarRegistroPNAE = findViewById(R.id.constraintRegistrarRegistroPNAE);
        ConstraintFiltros = findViewById(R.id.ConstraintFiltros);
        botaoPassePNAEFrontal = findViewById(R.id.botaoPassePNAEFrontal);
        // Inicialize o MediaPlayer com o arquivo de som do sucesso (success_sound.mp3 ou success_sound.wav)
        somErro = MediaPlayer.create(getApplicationContext(), R.raw.error);
        somSucess = MediaPlayer.create(getApplicationContext(), R.raw.sucess);

        imageViewCalendarioPNAESEPAE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarDialog(v);
            }
        });

        constraintRegistrarRegistroPNAE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procurarDadosAluno(entradaMatriculaRegistroMerenda.getText().toString());
                atualizarRecycler(diaAtual());
                entradaMatriculaRegistroMerenda.setText("");
            }
        });
        ConstraintFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarFragmentFiltros();
            }
        });

        botaoPassePNAEFrontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCodeFrontal();
            }
        });

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sCanCode();
            }
        });

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        if (actionBar != null) {
            // Defina a cor de background desejada (por exemplo, cor vermelha)
            ColorDrawable colorDrawable = new ColorDrawable(0xff23729a);
            actionBar.setBackgroundDrawable(colorDrawable);
        }

        actionBar.setCustomView(R.layout.custom_actionbar);
        TextView titleText = findViewById(R.id.titleText);
        ImageView leftImage = findViewById(R.id.leftImage);
        ImageView rightImage = findViewById(R.id.rightImage);

        rightImage.setImageResource(R.drawable.baseline_insert_chart_outlined_24);

        titleText.setText("PNAE");



        leftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaVoltar();
            }
        });

        rightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { telaGraficos();
                            }
        });
        imageViewEsquerdaPNAESEPAE2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menosUmDia();
            }
        });
        imageViewDireitaPNAESEPAE2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maisUmDia();
            }
        });
    }

    public void calendarDialog(View view) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new
                DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month,
                                          int dayOfMonth) {
                       String dateText = dayOfMonth + "/" + (month + 1) + "/" + year;
                       // openConfirmationDialog(view);
                        Log.d("calendario", "cal " + dateText);
                        textViewSaidaDiaRegistrosPNAESEPAE2.setText(dateText);
                        atualizarRecycler(formatarData(textViewSaidaDiaRegistrosPNAESEPAE2.getText().toString()));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void mostrarFragmentFiltros(){
        // Crie uma instância do Fragment que deseja adicionar
        FiltroFragmentPNAE fragment = new FiltroFragmentPNAE(turmaSelect);

// Use um FragmentManager para começar uma transação
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

// Adicione o Fragment ao FrameLayout (substituindo qualquer Fragment existente, se houver)
        transaction.replace(R.id.frameFiltros, fragment); // R.id.frameLayoutContainer é o ID do FrameLayout no seu layout XML

// Finalize a transação
        transaction.commit();
    }
    @Override
    public void onTurmaSelected(String turma) {
        // Faça o que desejar com a turma recebida do Fragment
        // Por exemplo, exiba-a em um TextView ou use-a de alguma outra forma na atividade
        Log.d("turma", "turma ac " + turma);
        turmaSelect = turma;
        if (!turmaSelect.equals("Todas")){
            NFiltros = 1;
        } else {
            NFiltros = 0;
        }
        saidaNumeroFiltros.setText(String.valueOf(NFiltros));
        atualizarRecycler(formatarData(textViewSaidaDiaRegistrosPNAESEPAE2.getText().toString()));
    }

    protected void onStart() {
        super.onStart();

    }
    private void scanCodeFrontal(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCameraId(1);
        options.setCaptureActivity(CaptureAct.class);
        barLaucherFrontal.launch(options);
    }
    ActivityResultLauncher<ScanOptions> barLaucherFrontal = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
            String aux = result.getContents();
            isNumeric = (aux != null && p.matcher(aux).find());

            if ( aux.length() == 11 && isNumeric){
                if (!ultimaMatricula.equals(aux) && !matriculasPassadas.contains(aux)){
                    String data = diaAtual();
                    atualizarMerenda(aux, data, new Callback() {
                        @Override
                        public void onComplete() {
                            playSucessSound();
                            ultimaMatricula = aux;
                        }
                    });
                } else{
                    playErrorSound();
                }
            } else {
                playErrorSound();
            }

            scanCodeFrontal();

        }
    });
    private void menosUmDia(){
        String diaHj = textViewSaidaDiaRegistrosPNAESEPAE2.getText().toString();
        String dia = diaHj.substring(0,2);
        if (!dia.contains("/")){
            int i = Integer.valueOf(dia);
            if (i > 1){
                i -= 1;
                String mesAno = diaHj.substring(2);
                String aux = String.valueOf(i) + mesAno;
                textViewSaidaDiaRegistrosPNAESEPAE2.setText(aux);
            }
        } else {
            dia = diaHj.substring(0,1);
            int i = Integer.valueOf(dia);
            if (i > 1){
                i -= 1;
                String mesAno = diaHj.substring(1);
                String aux = String.valueOf(i) + mesAno;
                textViewSaidaDiaRegistrosPNAESEPAE2.setText(aux);
            }
        }
        atualizarRecycler(formatarData(textViewSaidaDiaRegistrosPNAESEPAE2.getText().toString()));
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

        String diaHj = textViewSaidaDiaRegistrosPNAESEPAE2.getText().toString();
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
                        textViewSaidaDiaRegistrosPNAESEPAE2.setText(aux);
                    }
                } else {
                    dia = diaHj.substring(0,1);
                    int i = Integer.valueOf(dia);
                    if (i < num){
                        i += 1;
                        String mesAno = diaHj.substring(1);
                        String aux = String.valueOf(i) + mesAno;
                        textViewSaidaDiaRegistrosPNAESEPAE2.setText(aux);
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
                        textViewSaidaDiaRegistrosPNAESEPAE2.setText(aux);
                    }
                } else {
                    dia = diaHj.substring(0,1);
                    int i = Integer.valueOf(dia);
                    if (i < num){
                        i += 1;
                        String mesAno = diaHj.substring(1);
                        String aux = String.valueOf(i) + mesAno;
                        textViewSaidaDiaRegistrosPNAESEPAE2.setText(aux);
                    }
                }
            }
        }
        atualizarRecycler(formatarData(textViewSaidaDiaRegistrosPNAESEPAE2.getText().toString()));
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

    private void procurarDadosAluno(String matricula){
        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(matricula).document("dados");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nomeAluno = (String) document.get("nome");
                        String turma = (String) document.get("turma");
                       // .setVisibility(View.INVISIBLE);
                        abrirDialogInfAluno(nomeAluno, turma, matricula);

                    } else {
                        // O documento não existe
                        Toast.makeText(getApplicationContext(), "Erro ao procurar matrícula, matrícula inexistente", Toast.LENGTH_SHORT).show();
                      //  progressBarRegistrarEntrada.setVisibility(View.INVISIBLE);
                    }
                } else {
                    // Falha ao obter o documento
                    Toast.makeText(getApplicationContext(), "Falha inesperada + " + task.getException(), Toast.LENGTH_SHORT).show();
                   // progressBarRegistrarEntrada.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void abrirDialogInfAluno(String nomeAluno, String turma, String matricula) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        //configurar titulo e mensagem
        dialog.setTitle("Confira os dados" );
        dialog.setMessage("O nome é: " + nomeAluno + "\nA turma é: " + turma);

        //configurar cancelamento do alert dialog
        dialog.setCancelable(false);

        //configurar icone
        //dialog.setIcon(android.R.drawable.ic_btn_speak_now);

        //configurar açoes para sim e nâo
        dialog.setPositiveButton("Correto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              //  progressBarRegistrarEntrada.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Processando...", Toast.LENGTH_SHORT).show();
              //  motivoDoAtrasado(matricula);
                atualizarMerenda(matricula, diaAtual(), new Callback() {
                    @Override
                    public void onComplete() {
                        playSucessSound();
                        Toast.makeText(getApplicationContext(), "Registro Realizado com sucesso", Toast.LENGTH_SHORT).show();
                        atualizarRecycler(formatarData(textViewSaidaDiaRegistrosPNAESEPAE2.getText().toString()));
                    }
                });
            }
        });
        dialog.setNegativeButton("Incorreto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Confira os dados e tente novamente", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.create();
        dialog.show();
    }

    private void telaGraficos(){
        // Criar a Intent
        Intent intent = new Intent(getApplicationContext(), GraficosMerenda.class);
        // Iniciar a atividade de destino
        startActivity(intent);
        finish();
    }

    private void pegarNomesAlunos(){
        DocumentReference docRef = db.collection("Usuarios").document("Alunos");

// Obtém os dados do documento
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtém o campo "NomesAlunos" como um Map<String, String>
                        Map<String, String> nomesaux = (Map<String, String>) document.get("NomesAlunos");

                        // Agora você pode iterar sobre o Map e acessar os nomes dos alunos
                        for (Map.Entry<String, String> entry : nomesaux.entrySet()) {
                            String matricula = entry.getKey();
                            String nome = entry.getValue();
                            // Faça algo com as informações...
                            nomesAlunos.put(matricula, nome);
                        }
                        Log.d("TAG", "nomes alunos ==== " + nomesAlunos.toString());

                    } else {
                        // O documento não existe
                    }
                } else {
                    // Falha ao obter o documento
                }
            }
        });
    }
    private void pegarTurmaAlunos(){
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
                        Log.d("TAG", "turmas alunos ==== " + turmasAlunos.toString());

                    } else {
                        // O documento não existe
                    }
                } else {
                    // Falha ao obter o documento
                }
            }
        });
    }

    private void atualizarRecycler(String dia){
        alunosList.clear();
        Object objeto = listar.get(dia);
        if (objeto != null) {
            Log.d("obj", "obj " + objeto.toString());
            if (objeto instanceof List) {
                // Defina o fuso horário UTC-3
                TimeZone timeZone = TimeZone.getTimeZone("GMT-3");

                // Crie um SimpleDateFormat usando o fuso horário definido
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                sdf.setTimeZone(timeZone);
                List<Map<String, Timestamp>> listaDeMapas = (List<Map<String, Timestamp>>) objeto;

                int auxNumero = 0;

                for (Map<String, Timestamp> mapa : listaDeMapas) {
                    for (Map.Entry<String, Timestamp> entry : mapa.entrySet()) {
                        String chave = entry.getKey();
                        Timestamp timestamp = entry.getValue();
                        String dataFormatada = sdf.format(timestamp.toDate());

                        auxNumero += 1;

                        Log.d("TAG", "Chave obj: " + chave + "Timestamp obj: " + dataFormatada + " nome na lista " + nomesAlunos.get(chave));
                        String nome = nomesAlunos.getOrDefault(chave, "Erro em nome");
                        String turmaAux = turmasAlunos.getOrDefault(chave, "Erro Turma");
                        String aux = chave + " - " + turmaAux;
                       // AlunoMerenda aluno = new AlunoMerenda(nome, aux, dataFormatada, String.valueOf(auxNumero));
                        //alunosList.add(aluno);
                        if (turmaSelect.equals("Todas")) {
                            AlunoMerenda aluno = new AlunoMerenda(nome, aux, dataFormatada, String.valueOf(auxNumero));
                            alunosList.add(aluno);
                        } else if (turmaSelect.equals(turmaAux)){
                            AlunoMerenda aluno = new AlunoMerenda(nome, aux, dataFormatada, String.valueOf(auxNumero));
                            alunosList.add(aluno);
                        } else {
                            continue;
                        }
                    }
                }
                saidaNumero.setText(String.valueOf("Total de registros: " + alunosList.size()));
            } else {
                Log.d("TAG", "Nao é um obj");
            }

            List<AlunoMerenda> novaList = new ArrayList<>();

            if (alunosList.size() > 0) {
                for (int i = alunosList.size() - 1; i >= 0; i--) {
                    AlunoMerenda aluno = alunosList.get(i);
                    novaList.add(aluno);
                }
            }

            saidaNumero.setText(String.valueOf("Total de registros: " + alunosList.size()));
            AdapterMerenda adapter = new AdapterMerenda(novaList);


            //Configurar RecyclerView
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            listardiasMerenda.setLayoutManager(layoutManager);
            listardiasMerenda.setHasFixedSize(true);
            listardiasMerenda.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
            listardiasMerenda.setAdapter(adapter);

        } else{
            Log.d("TAG", "nullo");
            saidaNumero.setText(String.valueOf("Total de registros: " + alunosList.size()));
            AdapterMerenda adapter = new AdapterMerenda(alunosList);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            listardiasMerenda.setLayoutManager(layoutManager);
            listardiasMerenda.setHasFixedSize(true);
            listardiasMerenda.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
            listardiasMerenda.setAdapter(adapter);
        }
    }

    private String diaAtualB(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-3"));

        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int ano = calendar.get(Calendar.YEAR);

        String data = String.format("%02d/%02d/%d", dia, mes, ano);
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
        Intent intent = new Intent(getApplicationContext(), telaPrincipalSepae.class);
        // Iniciar a atividade de destino
        startActivity(intent);
        finish();
    }

    private interface Callback {
        void onComplete();
    }



    private void playErrorSound() {
        if (somErro != null) {
            somErro.start();
        }
    }

    private void playSucessSound() {
        if (somSucess != null) {
            somSucess.start();
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        // Libere os recursos do MediaPlayer ao encerrar a atividade
        if (somErro != null) {
            somErro.release();
            somErro = null;
        }

        if (somSucess != null) {
            somSucess.release();
            somSucess = null;
        }
    }

    private void sCanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
                String aux = result.getContents();
                isNumeric = (aux != null && p.matcher(aux).find());

                if ( aux.length() == 11 && isNumeric){
                    if (!ultimaMatricula.equals(aux) && !matriculasPassadas.contains(aux)){
                        String data = diaAtual();
                        atualizarMerenda(aux, data, new Callback() {
                            @Override
                            public void onComplete() {
                                playSucessSound();
                                ultimaMatricula = aux;
                                atualizarRecycler(formatarData(textViewSaidaDiaRegistrosPNAESEPAE2.getText().toString()));
                            }
                        });
                    } else{
                        playErrorSound();
                    }
                } else {
                    playErrorSound();
                }

                sCanCode();

        }
    });



    private String diaAtual(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-3"));

        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int ano = calendar.get(Calendar.YEAR);

        String data = String.format("%02d%02d%d", dia, mes, ano);
        return data;
    }

    private void atualizarMerenda(String matricula, String data, Callback callback){

        Timestamp novoTimestamp = Timestamp.now();

        DocumentReference docRef = db.collection("MerendaEscolar").document(data);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // O documento existe, atualize um campo nele
                    String turma = recuperarDados("turma");
                    List<Map<String, Timestamp>> existingList = (List<Map<String, Timestamp>>) document.get("todos");

                    Log.d("TAG", existingList.toString());

                    Map<String, Timestamp> aux = new HashMap<String, Timestamp>();
                    aux.put(matricula, novoTimestamp);

                    matriculasPassadas.add(matricula);

                    existingList.add(aux);

                    docRef.update("todos", existingList).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            listarDiasMerendados(new Callback() {
                                @Override
                                public void onComplete() {
                                    atualizarRecycler(formatarData(textViewSaidaDiaRegistrosPNAESEPAE2.getText().toString()));
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            playErrorSound();
                            atualizarRecycler(formatarData(textViewSaidaDiaRegistrosPNAESEPAE2.getText().toString()));
                        }
                    });
                    callback.onComplete();
                } else {
                    // O documento não existe, crie-o
                    Map<String, Object> dados = new HashMap<>();
                    dados.put(matricula, novoTimestamp); // Insere o timestamp atual

// Cria um array com um mapa dentro dele
                    List<Map<String, Object>> array = new ArrayList<>();
                    array.add(dados);

// Cria um mapa para o campo "todos"
                    Map<String, Object> mapaTodos = new HashMap<>();
                    mapaTodos.put("todos", array);

// Insere os dados no documento
                    docRef.set(mapaTodos)
                            .addOnSuccessListener(aVoid -> {
                                // Sucesso ao criar o documento
                                listarDiasMerendados(new Callback() {
                                    @Override
                                    public void onComplete() {
                                        atualizarRecycler(formatarData(textViewSaidaDiaRegistrosPNAESEPAE2.getText().toString()));
                                    }
                                });
                            })
                            .addOnFailureListener(e -> {
                                // Falha ao criar o documento
                            });
                    callback.onComplete();
                }
            } else {
                // Houve um erro ao buscar o documento
                Log.d("Firestore", "Erro: " + task.getException());
                callback.onComplete();
            }
        });


        //atualizar aluno agora
        DocumentReference usuarioDocRef = db.collection("Usuarios").document("Alunos").collection(matricula).document("merendaPessoal");

        usuarioDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    boolean flagAtual = document.getBoolean("flag");

                    // Criar um novo valor para a flag (inverter o valor booleano)
                    boolean novoValorFlag = !flagAtual;

                    // Atualizar a flag no documento
                    usuarioDocRef.update("flag", novoValorFlag)
                            .addOnSuccessListener(aVoid -> {
                                // Sucesso ao atualizar a flag
                                Log.d("Firestore", "Flag atualizada com sucesso");

                            })
                            .addOnFailureListener(e -> {
                                // Falha ao atualizar a flag
                                Log.e("Firestore", "Erro ao atualizar a flag", e);
                            });

                    // Adicionar um timestamp ao array "passes"
                    List<String> passes = (List<String>) document.get("passes");
                    passes.add(String.valueOf(novoTimestamp)); // Adiciona um timestamp em milissegundos
                    usuarioDocRef.update("passes", passes)
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

    private String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }

    private void listarDiasMerendados(Callback callback){

        listDias.clear();

        db.collection("MerendaEscolar")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obter um mapa de campos e valores do documento
                            Map<String, Object> data = document.getData();

                            dataGlobal = data;

                            String diaCompleto = document.getId();
                            listDias.add(diaCompleto);

                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                String chave = entry.getKey();
                                Object valor = entry.getValue();

                                listar.put(document.getId(), valor);
                            }
                        }
                        callback.onComplete();
                    } else {
                        // Tratar erro aqui, se necessário
                        callback.onComplete();
                    }
                });
    }
}