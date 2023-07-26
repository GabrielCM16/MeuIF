package com.example.meuif;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.meuif.databinding.FragmentInformacoesPessoaisBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Informacoes_pessoais#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Informacoes_pessoais extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView teste;
    private FragmentInformacoesPessoaisBinding binding;



    public Informacoes_pessoais() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Informacoes_pessoais newInstance(String param1, String param2) {
        Informacoes_pessoais fragment = new Informacoes_pessoais();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentInformacoesPessoaisBinding.inflate(inflater, container, false);


        View root = binding.getRoot();

        teste = root.findViewById(R.id.testeaaa);
        teste.setText("ola");

        return root;

    }
}