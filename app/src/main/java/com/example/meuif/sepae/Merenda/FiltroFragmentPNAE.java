package com.example.meuif.sepae.Merenda;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.meuif.R;
import com.example.meuif.sepae.chamada.TelaChamadaLideres;

public class FiltroFragmentPNAE extends Fragment {

    private Spinner spinnerTurmas;
    private OnTurmaSelectedListener listener;
    private ImageView imageViewSairFragment;
    private String Tturma;
    public FiltroFragmentPNAE(String Tturma) {
        // Required empty public const
        this.Tturma = Tturma;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTurmaSelectedListener) {
            listener = (OnTurmaSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnTurmaSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private void setarSpinnerTurmas() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.turmas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurmas.setAdapter(adapter);
        int position = -1;

        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(Tturma)) {
                position = i;
                break; // Item encontrado, saia do loop
            }
        }
        if (position != -1) {
            spinnerTurmas.setSelection(position);
        } else {
            // O código não foi encontrado, faça algo apropriado, como exibir uma mensagem de erro.
        }
        spinnerTurmas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTurma = parent.getItemAtPosition(position).toString();

                Tturma = selectedTurma;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ação a ser tomada quando nada é selecionado (opcional)
            }
        });
    }


    private void removeFragment() {
        // Obtém o FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Remove o fragment atual com animação de saída
        Fragment fragment = fragmentManager.findFragmentById(R.id.frameFiltros); // Substitua R.id.frameFiltros pelo ID do seu FrameLayout
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Defina a animação de saída do fragment
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);

            listener.onTurmaSelected(Tturma);
            transaction.remove(fragment).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filtro_p_n_a_e, container, false);
        Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        // Aplica a animação à view do fragment
        view.startAnimation(slideUp);
        // Encontre o Spinner dentro do layout do fragment
        spinnerTurmas = view.findViewById(R.id.spinnerTurmas2);
        imageViewSairFragment = view.findViewById(R.id.imageViewSairFragment);

        imageViewSairFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });

        // Configure o adaptador e o ouvinte de seleção
        setarSpinnerTurmas();

        return view;
    }
}