package com.example.meuif.sepae;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meuif.CRUD;
import com.example.meuif.R;

public class telaPrincipalSepae extends AppCompatActivity {

    private CRUD crud = new CRUD();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal_sepae);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button a = findViewById(R.id.a);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crud.abrirSnakbar("aperto", view);
            }
        });

    }
}