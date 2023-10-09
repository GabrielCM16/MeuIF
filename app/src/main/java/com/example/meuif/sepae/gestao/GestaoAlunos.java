package com.example.meuif.sepae.gestao;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meuif.R;

public class GestaoAlunos extends Fragment {
    private ConstraintLayout constraintLayoutGestaoLideres;
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

        constraintLayoutGestaoLideres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestaoLideres();
            }
        });

        return root;
    }

    private void gestaoLideres(){
        Intent intent = new Intent(getContext(), GerenciarLideres.class);
        startActivity(intent);
    }
}