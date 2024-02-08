package com.example.meuif.configs;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.meuif.R;
import com.example.meuif.Tela_Principal;

public class Tela_Config extends AppCompatActivity {
    private Switch switchAnimacaoAoEntrar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_config);

        ActionBar actionBar = getSupportActionBar();
        setTitle("Configurações");
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (actionBar != null) {
            // Defina a cor de background desejada (por exemplo, cor vermelha)
            ColorDrawable colorDrawable = new ColorDrawable(0xff23729a);
            actionBar.setBackgroundDrawable(colorDrawable);
        }

        switchAnimacaoAoEntrar = findViewById(R.id.switchAnimacaoAoEntrar);

        if (recuperarDados("animacao").equals("true") || recuperarDados("animacao").equals("")){
            switchAnimacaoAoEntrar.setChecked(true);
        }else {
            switchAnimacaoAoEntrar.setChecked(false);
        }

        switchAnimacaoAoEntrar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                salvarDados("animacao", isChecked ? "true" : "false");
            }
        });
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


    public String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }
    public void salvarDados(String chave, String valor){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(chave, valor);
        editor.commit();
    }
}