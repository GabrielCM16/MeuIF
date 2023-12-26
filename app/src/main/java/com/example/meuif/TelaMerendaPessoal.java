package com.example.meuif;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.meuif.sepae.recyclerMerenda.AdapterMerenda;
import com.example.meuif.sepae.recyclerMerenda.AlunoMerenda;
import com.example.meuif.ui.gallery.GalleryFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelaMerendaPessoal extends AppCompatActivity {

    private ConstraintLayout layoutCarteirinha;
    private FirebaseFirestore db;
    private ConstraintLayout layoutCardapio;
    private RecyclerView recyclerViewPessoalMerenda;
    private String matricula;
    private String nome;
    private TextView textViewMostrar;
    private List<AlunoMerenda> diasMerendados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_merenda_pessoal);


        matricula = recuperarDados("matricula");
        nome = recuperarDados("nome");
        carregarComponentes();
        mostrarDiasMerenda(matricula);
    }

    private void carregarComponentes(){
        ActionBar actionBar = getSupportActionBar();
        setTitle("Minhas retiradas PNAE");
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (actionBar != null) {
            // Defina a cor de background desejada (por exemplo, cor vermelha)
            ColorDrawable colorDrawable = new ColorDrawable(0xff23729a);
            actionBar.setBackgroundDrawable(colorDrawable);
        }

        layoutCarteirinha = findViewById(R.id.constraintIrCarteirinha);
        db = FirebaseFirestore.getInstance();
        layoutCardapio = findViewById(R.id.constraintVerCardapio);
        textViewMostrar = findViewById(R.id.textViewMostrar);
        recyclerViewPessoalMerenda = findViewById(R.id.RecyclerPessoalMerenda);


        layoutCarteirinha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaCarteirinha();
            }
        });

        layoutCardapio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCardapio();
            }
        });
    }

    private String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }

    private void mostrarDiasMerenda(String nMatricula){

        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(nMatricula).document("merendaPessoal");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int auxNumero = 0;
                       List<String> aux = (List<String>) document.get("passes");
                        for (String item : aux) {
                            // Definir padrões de expressões regulares para extrair segundos e nanossegundos
                            Pattern secondsPattern = Pattern.compile("seconds=(\\d+)");
                            Pattern nanosPattern = Pattern.compile("nanoseconds=(\\d+)");

// Criar objetos Matcher para encontrar correspondências nos padrões
                            Matcher secondsMatcher = secondsPattern.matcher(item);
                            Matcher nanosMatcher = nanosPattern.matcher(item);

// Verificar se encontrou correspondências e extrair os valores
                            if (secondsMatcher.find() && nanosMatcher.find()) {
                                long seconds = Long.parseLong(secondsMatcher.group(1));
                                int nanoseconds = Integer.parseInt(nanosMatcher.group(1));

                                // Criar um objeto Date usando os segundos
                                Date date = new Date(seconds * 1000L + nanoseconds / 1000000L);

                                // Criar um SimpleDateFormat para formatar a data
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                sdf.setTimeZone(TimeZone.getTimeZone("GMT-3"));

                                auxNumero += 1;

                                // Formatar a data para exibição
                                String dataFormatada = sdf.format(date);
                                AlunoMerenda eu = new AlunoMerenda(nome, matricula, dataFormatada, String.valueOf(auxNumero));
                                diasMerendados.add(eu);
                            }
                        }
                        Log.d("TAGLER", diasMerendados.toString());
                        atualizarRecycler(diasMerendados);
                    } else {
                        Log.d("TAGLER", "Documento não encontrado");
                    }
                } else {
                    Log.d("TAGLER", "Falhou em ", task.getException());
                }
            }
        });
    }

    private void atualizarRecycler(List<AlunoMerenda> dias){
        List<AlunoMerenda> diasInvertidos = new ArrayList<>();
        for (int i = dias.size() - 1; i >= 0; i--) {
            AlunoMerenda elemento = dias.get(i);
            diasInvertidos.add(elemento);
        }
        AdapterMerenda adapter = new AdapterMerenda(diasInvertidos);


        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewPessoalMerenda.setLayoutManager(layoutManager);
        recyclerViewPessoalMerenda.setHasFixedSize(true);
        recyclerViewPessoalMerenda.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerViewPessoalMerenda.setAdapter(adapter); //criar adapter
    }

    private void telaCarteirinha(){
        ScrollView aux = findViewById(R.id.scroll);
        aux.setVisibility(View.GONE);
        recyclerViewPessoalMerenda.setVisibility(View.GONE);
        // Criar uma instância do fragmento
        GalleryFragment meuFragment = new GalleryFragment();

        // Obter o FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Iniciar a transação de fragmento
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Substituir o conteúdo do LinearLayout pelo fragmento
        fragmentTransaction.replace(R.id.principalConstraint, meuFragment);

        // Confirmar a transação
        fragmentTransaction.commit();
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

    private void mostrarCardapio(){
        String dia = getDayAndMonth();
        MostrarAtualizacoes atualizacoes = new MostrarAtualizacoes();
        atualizacoes.abrirDialogCardapio(this, dia);
    }

    private String getDayAndMonth() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3")); // Defina o fuso horário desejado
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void telaVoltar(){
        // Criar a Intent
        Intent intent = new Intent(this, Tela_Principal.class);

        // Iniciar a atividade de destino
        startActivity(intent);
    }
}