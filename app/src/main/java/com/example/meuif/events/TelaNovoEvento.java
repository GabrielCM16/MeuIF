package com.example.meuif.events;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.meuif.R;

public class TelaNovoEvento extends AppCompatActivity {

    private Spinner SpinnerCategoria;
    private String categoriaSelecionado;
    private TextView textDisciplina2;
    private Spinner SpinnerDisciplina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_novo_evento);

        carregarComponentes();
        setarSpinnerCategoria();
    }

    private void carregarComponentes() {
        SpinnerCategoria = findViewById(R.id.SpinnerCategoria);
        textDisciplina2 = findViewById(R.id.textDisciplina2);
        SpinnerDisciplina = findViewById(R.id.SpinnerDisciplina);
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