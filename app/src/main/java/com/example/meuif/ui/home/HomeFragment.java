package com.example.meuif.ui.home;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meuif.Aulas;
import com.example.meuif.AulasAdapter;
import com.example.meuif.R;
import com.example.meuif.TelaMerendaPessoal;
import com.example.meuif.Tela_Principal;
import com.example.meuif.databinding.FragmentHomeBinding;
import com.example.meuif.faltasPessoais.telaVerFaltasPessoais;
import com.example.meuif.tela_chamada_dia;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.meuif.MostrarAtualizacoes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerAulas;
    private AulasAdapter adapter;
    private ArrayList<Aulas> itens;
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
    private ConstraintLayout botaoChamada;
    private String turma;
    private String matricula;
    private String diaSemana;
    private TextView saidaSemana;
    private String versao = "";
    private boolean LiderDeTurma = false;
    private ConstraintLayout layoutAulas;
    private ConstraintLayout layoutFaltas;
    private ConstraintLayout layoutMerenda;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        progressBarCentral = root.findViewById(R.id.progressBarCentral);

        FloatingActionButton fabFragmentB = root.findViewById(R.id.fab_fragment_b);
        fabFragmentB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarCentral.setVisibility(View.VISIBLE);
                mostrarPresenca();
                setarBemVindo();
                atualizarGrafico(presencas, ausencias);
                atualizaPresenca();
                botaoLider();
                diaSemana = diaAtual();
                //getAulas(turma, "quinta");
                progressBarCentral.setVisibility(View.INVISIBLE);
            }
        });



        progressBarCentral.setVisibility(View.VISIBLE);

        //muda a cor do progressBar pra preto
        progressBarCentral.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        db = FirebaseFirestore.getInstance();
        textViewBemVindo = root.findViewById(R.id.textViewBemVindo);
        saidaNumAulas = root.findViewById(R.id.saidaNumAulas);
        layoutAulas = root.findViewById(R.id.constraintVerAulas);
        layoutFaltas = root.findViewById(R.id.constraintVerFaltas);
        layoutMerenda = root.findViewById(R.id.constraintMerendaAluno);
        saidaNumAusencias = root.findViewById(R.id.saidaNumAusencias);
        saidaNumPresencas = root.findViewById(R.id.saidaNumPresencas);
        botaoChamada = root.findViewById(R.id.constraintChamadaAluno);
        nomeCompleto = recuperarDados("nome");
        pieChart = root.findViewById(R.id.pie_chart);
        //saidaSemana = root.findViewById(R.id.saidaSemana);
        //recyclerAulas = root.findViewById(R.id.recycleAulas);

        //botaoChamada.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        //botaoChamada.setPadding(30, botaoChamada.getPaddingTop(), 15, botaoChamada.getPaddingBottom());

        turma = recuperarDados("turma");
        matricula = recuperarDados("matricula");

        mostrarPresenca();
        setarBemVindo();
        atualizarGrafico(presencas, ausencias);
        atualizaPresenca();
        diaSemana = diaAtual();
        botaoLider();
        //getAulas(turma, "quinta");

        progressBarCentral.setVisibility(View.INVISIBLE);

        layoutAulas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Função em desenvolvimento", Toast.LENGTH_LONG).show();
            }
        });

        layoutFaltas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getContext(), telaVerFaltasPessoais.class);
                startActivity(intent);
            }
        });

        layoutMerenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaMerendaPessoal();
            }
        });

        return root;
    }

    private void telaMerendaPessoal(){
        // Criar a Intent
        Intent intent = new Intent(getContext(), TelaMerendaPessoal.class);

        // Iniciar a atividade de destino
        startActivity(intent);
    }

    private interface Callback {
        void onComplete();
    }

    public void onStart() {

        super.onStart();

        String vc = recuperarDados("versao");

        botaoLider();
        Log.d("TAGLER", "APOS BOTAO LIDER");
        mostrarPresenca();
        Log.d("TAGLER", "APOS MOSTRAR PRESENCA");
        setarBemVindo();
        Log.d("TAGLER", "APOS SETAR");
        atualizaPresenca();
        Log.d("TAGLER", "APOS ATUALIZAR PRESENCA 1");
        atualizarGrafico(presencas, ausencias);
        Log.d("TAGLER", "APOS ATUALIZAR GRAFICO");
        atualizaPresenca();
        Log.d("TAGLER", "APOS ATUALIZAR PRESENCA 2");
        diaSemana = diaAtual();
        botaoLider();
        Log.d("TAGLER", "APOS BOTAO LIDER");
        getAulas(turma, "quinta");
        Log.d("TAGLER", "APOS GETAULAS");

        getVersao(new Callback() {
            @Override
            public void onComplete() {
                Log.d("TAGG", "versao app = " + versao + "versao versao " + vc);
                if (!versao.equals(vc)){
                    String[] primeiroNome = nomeCompleto.split(" ");
                    MostrarAtualizacoes mostrarAtualizacoes = new MostrarAtualizacoes();
                    mostrarAtualizacoes.abrirDialogAtualizacoes(getContext(), primeiroNome[0]);
                }
            }
        });


        progressBarCentral.setVisibility(View.INVISIBLE);


    }

    public void getVersao(Callback callback){
        DocumentReference docRef = db.collection("MaisInformacoes").document("atualizacoes");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String versaoAux = document.getString("versao");
                        Log.d("TAGG", "versao no obj = " + versaoAux);
                        versao = versaoAux;
                        salvarDados("versao", versaoAux);
                        callback.onComplete();
                    } else {
                        Log.d("TAGG", "Documento de turma não encontrado");
                        callback.onComplete();
                    }
                } else {
                    Log.d("TAGG", "Falhou em ", task.getException());
                    callback.onComplete();
                }
            }
        });
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setarrecylerView(Map<String, Object> aulas){
        itens = new ArrayList<Aulas>();

        if (aulas != null){
            for (String key : aulas.keySet()) {

                if (aulas.get(key) instanceof ArrayList) {

                    ArrayList<Object> array = (ArrayList<Object>) aulas.get(key);

                    String materia = (String) array.get(0);
                    String horaComeco = (String) array.get(1);
                    String horaFim = (String) array.get(2);
                    String professor = (String) array.get(3);

                    itens.add(new Aulas(materia, professor, horaComeco, horaFim));
                }
            }
        }


        adapter = new AulasAdapter(getContext() , itens);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerAulas.setLayoutManager(layoutManager);
        recyclerAulas.setItemAnimator(new DefaultItemAnimator());
        recyclerAulas.setAdapter(adapter);
    }

    private void getAulas(String turma, String dia){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("HorarioAulas").document(turma);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains(dia)) {
                        Map<String, Object> mapAulas = (Map<String, Object>) documentSnapshot.getData().get(dia);
                        //setarrecylerView(mapAulas);


                    } else {
                        Log.d("TAG", "O campo dia não existe no documento!");
                    }
                } else {
                    Log.d("TAG", "O documento da turma das aulas não existe!");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "Erro ao obter o documento: " + e.toString());
            }
        });
    }

    private String diaAtual(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-3"));

        int diaSemanaint = calendar.get(Calendar.DAY_OF_WEEK);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        String data;
        switch (diaSemanaint){
            case 2:
                data = "segunda";
                break;
            case 3:
                data = "terca";
                break;
            case 4:
                data = "quarta";
                break;
            case 5:
                data = "quinta";
                break;
            case 6:
                data = "sexta";
                break;
            case 7:
                data = "sabado";
                break;
            default:
                data = "domingo";
                break;
        }

        diaSemana = data;
        //saidaSemana.setText(dia + ", " + data);
        return data;
    }

    private void botaoLider(){
        if (!matricula.isEmpty()){
            liderDeSala(matricula);
        }

        if (LiderDeTurma){
            botaoChamada.setVisibility(View.VISIBLE);
            botaoChamada.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    telaChamada();
                }
            });
        } else {
            botaoChamada.setVisibility(View.GONE);
        }
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


                        if (nMatricula.equals(matriculaLider) || nMatricula.equals(matriculaViceLider)){
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
            pieChart.animateY(1500);
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

    private void telaChamada(){
        // Criar a Intent
        Intent intent = new Intent(getActivity(), tela_chamada_dia.class);
        // Iniciar a atividade de destino
        startActivity(intent);
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH");
        Date date = new Date();
        return dateFormat.format(date);
    }

}