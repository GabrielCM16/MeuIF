package com.example.meuif.sepae.gestao;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meuif.R;
import com.example.meuif.databinding.FragmentInformacoesPessoaisBinding;
import com.example.meuif.events.TelaNovoEvento;

public class GestaoSEPAE extends Fragment {
    private ConstraintLayout constraintLayoutGestaosepaeNovoCadastro;
    private FragmentInformacoesPessoaisBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_gestao_s_e_p_a_e, container, false);
        constraintLayoutGestaosepaeNovoCadastro = root.findViewById(R.id.constraintLayoutGestaosepaeNovoCadastro);

        constraintLayoutGestaosepaeNovoCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TelaCadastrarNovoUsuario.class);
                startActivity(intent);
            }
        });
        return root;
    }
}