package com.example.meuif.events;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meuif.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TelaNovoEvento extends AppCompatActivity {

    private Spinner SpinnerCategoria;
    private String categoriaSelecionado;
    private TextView textDisciplina2;
    private Spinner SpinnerDisciplina;
    private FirebaseFirestore db;
    private ConstraintLayout entradaCoresEvento;
    private List<String> disciplinas = new ArrayList<String>();
    private Button selectedButton;
    private ProgressBar progressBarNovoEvento;
    private TextView TextoDescricao;
    private TextView entradaTituloNovoEvento;
    private String disciplinaSelecionada;
    private EditText editTextLocal;
    private Button buttonRed;
    private Button buttonGreen;
    private Button buttonPurple;
    private Button buttonOrange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        setContentView(R.layout.activity_tela_novo_evento);

        carregarComponentes();
        setarSpinnerCategoria();
        setarSpinnerDisciplinas();
    }

    private void carregarComponentes() {
        db = FirebaseFirestore.getInstance();
        SpinnerCategoria = findViewById(R.id.SpinnerCategoria);
        entradaCoresEvento = findViewById(R.id.entradaCoresEvento);
        textDisciplina2 = findViewById(R.id.textDisciplina2);
        entradaTituloNovoEvento = findViewById(R.id.entradaTituloNovoEvento);
        TextoDescricao = findViewById(R.id.TextoDescricao);
        editTextLocal = findViewById(R.id.editTextLocal);
        SpinnerDisciplina = findViewById(R.id.SpinnerDisciplina);
        progressBarNovoEvento = findViewById(R.id.progressBarNovoEvento);
        progressBarNovoEvento.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        entradaCoresEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirDialogCores();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_novo_evento);
        TextView cancelar = findViewById(R.id.TextCancelar);
        TextView salvar = findViewById(R.id.TextSalvar);

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarNovoEvento.setVisibility(View.VISIBLE);
                salvarEvento();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void salvarEvento() {
        // Obtém o Timestamp para o momento atual
        Timestamp timestampAtual = Timestamp.now();

// Define um período de 1 dia em segundos (24 horas x 60 minutos x 60 segundos)
        long umDiaEmSegundos = 24 * 60 * 60;

// Adiciona um dia (em segundos) ao timestamp atual
        Timestamp timestampAmanha = new Timestamp(timestampAtual.getSeconds() + umDiaEmSegundos, 0);

        List<Timestamp> modificacoes = new ArrayList<Timestamp>();

        String local = editTextLocal.getText().toString();
        String quemCriou = recuperarDados("nome");

        String cor = qualCorSelecionada();

        if (categoriaSelecionado.equals("Eventos Acadêmicos") || categoriaSelecionado.equals("Projeto de Extensão")){
            disciplinaSelecionada = "";
        }

        Events events = new Events(categoriaSelecionado,
                TextoDescricao.getText().toString(),
                entradaTituloNovoEvento.getText().toString(),
                timestampAtual,
                timestampAmanha,
                modificacoes,
                cor,
                quemCriou,
                disciplinaSelecionada,
                local);

        SalvarEvento salvarEvento = new SalvarEvento(events);
        salvarEvento.gravarEvent();
        progressBarNovoEvento.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), "Evento Salvo", Toast.LENGTH_SHORT).show();
        finish();
    }

    public String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }

    private String qualCorSelecionada(){
        String corHEXA = "";
        if (selectedButton.equals(buttonRed)) {
            corHEXA = "FF0000";
        } else if (selectedButton.equals(buttonGreen)) {
            corHEXA = "00FF00";
        } else if (selectedButton.equals(buttonPurple)){
            corHEXA = "A020F0";
        } else if (selectedButton.equals(buttonOrange)){
            corHEXA = "ffa500";
        } else {
            corHEXA = "000000";
        }
        return corHEXA;
    }

    private void abrirDialogCores(){
        // Infla o layout XML personalizado para o diálogo
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_cores_eventos, null);

// Cria o AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

// Encontre os botões no layout personalizado
        buttonRed = dialogView.findViewById(R.id.button_red);
         buttonGreen = dialogView.findViewById(R.id.button_green);
         buttonPurple = dialogView.findViewById(R.id.button_purple);
         buttonOrange = dialogView.findViewById(R.id.button_orange);

        // Crie o diálogo
        AlertDialog dialog = builder.create();

// Configurar um ouvinte de clique para cada botão
        buttonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Desmarcar o botão selecionado anteriormente (se houver)
                if (selectedButton != null) {
                    selectedButton.setSelected(false);
                }

                // Marcar o botão atual como selecionado
                buttonRed.setSelected(true);

                // Defina o botão atual como o botão selecionado
                selectedButton = buttonRed;

                // Registre o botão selecionado no log
                Log.d("botao", "Botão selecionado = vermelho" + selectedButton.getText().toString());
                // Fechar o diálogo após a seleção
                // Suponha que você tenha um recurso Drawable chamado "fundo_botao_cor_vermelho".
                Drawable drawable = getResources().getDrawable(R.drawable.fundo_botao_cor_vermelho);

// Defina o Drawable como plano de fundo do seu ConstraintLayout
                entradaCoresEvento.setBackground(drawable);

                dialog.dismiss();
            }
        });

        buttonPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Desmarcar o botão selecionado anteriormente (se houver)
                if (selectedButton != null) {
                    selectedButton.setSelected(false);
                }

                // Marcar o botão atual como selecionado
                buttonPurple.setSelected(true);

                // Defina o botão atual como o botão selecionado
                selectedButton = buttonPurple;

                // Registre o botão selecionado no log
                Log.d("botao", "Botão selecionado = roxo" + selectedButton.getText().toString());
                // Fechar o diálogo após a seleção
                // Suponha que você tenha um recurso Drawable chamado "fundo_botao_cor_vermelho".
                Drawable drawable = getResources().getDrawable(R.drawable.fundo_botao_cor_purple);

// Defina o Drawable como plano de fundo do seu ConstraintLayout
                entradaCoresEvento.setBackground(drawable);

                dialog.dismiss();
            }
        });

        buttonOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Desmarcar o botão selecionado anteriormente (se houver)
                if (selectedButton != null) {
                    selectedButton.setSelected(false);
                }

                // Marcar o botão atual como selecionado
                buttonOrange.setSelected(true);

                // Defina o botão atual como o botão selecionado
                selectedButton = buttonOrange;

                // Registre o botão selecionado no log
                Log.d("botao", "Botão selecionado = laranja" + selectedButton.getText().toString());
                // Fechar o diálogo após a seleção
                // Suponha que você tenha um recurso Drawable chamado "fundo_botao_cor_vermelho".
                Drawable drawable = getResources().getDrawable(R.drawable.fundo_botao_cor_laranja);

// Defina o Drawable como plano de fundo do seu ConstraintLayout
                entradaCoresEvento.setBackground(drawable);

                dialog.dismiss();
            }
        });


        buttonGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Desmarcar o botão selecionado anteriormente (se houver)
                if (selectedButton != null) {
                    selectedButton.setSelected(false);
                }

                // Marcar o botão atual como selecionado
                buttonGreen.setSelected(true);

                // Defina o botão atual como o botão selecionado
                selectedButton = buttonGreen;

                // Registre o botão selecionado no log
                Log.d("botao", "Botão selecionado = verde" + selectedButton.getText().toString());
                // Fechar o diálogo após a seleção
                Drawable drawable = getResources().getDrawable(R.drawable.fundo_botao_cor_verde);

// Defina o Drawable como plano de fundo do seu ConstraintLayout
                entradaCoresEvento.setBackground(drawable);
                dialog.dismiss();
            }
        });

// Mostre o diálogo
        dialog.show();



    }
    private void setarSpinnerDisciplinas(){
        // Referência para o documento que contém o array "componentes"
        //String turma = recuperarDados("turma");
        String turma = "3INF";
        DocumentReference docRef = db.collection("Componentes").document(turma);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtém o array "componentes" do documento
                        List<Map<String, String>> componentes = (List<Map<String, String>>) document.get("componentes");

                        for (Map<String, String> component : componentes) {
                            for (String key : component.keySet()) {
                                disciplinas.add(key);
                            }
                        }

                        // Converter a List<String> em um array de strings simples
                        String[] dataArray = new String[disciplinas.size()];
                        disciplinas.toArray(dataArray);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        SpinnerDisciplina.setAdapter(adapter);

                        SpinnerDisciplina.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                //dias.clear();
                                String selectedDisciplina = parent.getItemAtPosition(position).toString();
                                disciplinaSelecionada = selectedDisciplina;
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Ação a ser tomada quando nada é selecionado (opcional)
                            }
                        });
                    } else {
                        // O documento não existe
                    }
                } else {
                    // Falha ao obter o documento
                }
            }
        });

    }

    private void setarSpinnerCategoria(){
        String[] dataArray = new String[]{"Prova", "Atividade", "Trabalho", "Seminário", "Eventos Acadêmicos", "Projeto de Extensão"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerCategoria.setAdapter(adapter);

        SpinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDia = parent.getItemAtPosition(position).toString();
                categoriaSelecionado = selectedDia;
                if (selectedDia.equals("Eventos Acadêmicos") || selectedDia.equals("Projeto de Extensão")){
                    textDisciplina2.setVisibility(View.VISIBLE);
                    SpinnerDisciplina.setVisibility(View.INVISIBLE);
                    textDisciplina2.setText("-------------------------");
                } else {
                    textDisciplina2.setVisibility(View.INVISIBLE);
                    SpinnerDisciplina.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ação a ser tomada quando nada é selecionado (opcional)
            }
        });
    }
}