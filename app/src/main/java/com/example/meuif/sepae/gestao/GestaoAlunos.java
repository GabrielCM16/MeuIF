package com.example.meuif.sepae.gestao;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meuif.R;
import com.example.meuif.sepae.gestao.aluno.GerenciarAluno;

public class GestaoAlunos extends Fragment {
    private ConstraintLayout constraintLayoutGestaoLideres;
    private ConstraintLayout constraintLayoutGestaoUsuario;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_gestao_alunos, container, false);

        constraintLayoutGestaoLideres = root.findViewById(R.id.constraintLayoutGestaoLideres);
        constraintLayoutGestaoUsuario = root.findViewById(R.id.constraintLayoutGestaoUsuario);

        constraintLayoutGestaoLideres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestaoLideres();
            }
        });

        constraintLayoutGestaoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestaoAluno();
            }
        });

        return root;
    }

    private void gestaoAluno() {
        Intent intent = new Intent(getContext(), GerenciarAluno.class);
        startActivity(intent);
    }

    private void gestaoLideres(){
        Intent intent = new Intent(getContext(), GerenciarLideres.class);
        startActivity(intent);
    }
}