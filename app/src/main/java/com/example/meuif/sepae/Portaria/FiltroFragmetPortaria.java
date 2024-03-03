package com.example.meuif.sepae.Portaria;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.meuif.R;
public class FiltroFragmetPortaria extends Fragment  {
    private ImageView imageViewSairFragmentPortaria;
    private Spinner spinnerTurmasPortaria;
    private String Tturma;
    private OnFiltroSelectedListener listener;
    private ModelFiltroPortaria obj;
    private Switch manha;
    private Switch tarde;
    private Switch noite;
    private EditText nomeOuMatricula;
    public FiltroFragmetPortaria(ModelFiltroPortaria modelFiltroPortaria) {
        // Required empty public constructor
        this.obj = modelFiltroPortaria;
        Tturma = modelFiltroPortaria.getTurma();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFiltroSelectedListener) {
            listener = (OnFiltroSelectedListener) context;
        } else {
            listener = null;
        }
    }


    private void removeFragment() {
        // Obtém o FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Remove o fragment atual com animação de saída
        Fragment fragment = fragmentManager.findFragmentById(R.id.frameFiltrosPortaria); // Substitua R.id.frameFiltros pelo ID do seu FrameLayout
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Defina a animação de saída do fragment
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);

            obj = new ModelFiltroPortaria(nomeOuMatricula.getText().toString(),
                    nomeOuMatricula.getText().toString(),
                    manha.isChecked(),tarde.isChecked(),noite.isChecked(),
                    Tturma);

            listener.onObjSelected(obj);
            transaction.remove(fragment).commit();
        }
    }

    private void setarSpinnerTurmas() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.turmas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurmasPortaria.setAdapter(adapter);
        int position = -1;

        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(Tturma)) {
                position = i;
                break; // Item encontrado, saia do loop
            }
        }
        if (position != -1) {
            spinnerTurmasPortaria.setSelection(position);
        } else {
            spinnerTurmasPortaria.setSelection(0);
        }
        spinnerTurmasPortaria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void setarSwitchTurmas(){
        if (obj != null){
            if (obj.getManha()){
                manha.setChecked(true);
            } else {
                manha.setChecked(false);
            }
            if (obj.getTarde()){
                tarde.setChecked(true);
            } else {
                tarde.setChecked(false);
            }
            if (obj.getNoite()){
                noite.setChecked(true);
            } else {
                noite.setChecked(false);
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filtro_fragmet_portaria, container, false);
        Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        // Aplica a animação à view do fragment
        view.startAnimation(slideUp);
        // Encontre o Spinner dentro do layout do fragment
        imageViewSairFragmentPortaria = view.findViewById(R.id.imageViewSairFragmentPortaria);
        spinnerTurmasPortaria = view.findViewById(R.id.spinnerTurmasPortaria);
        manha = view.findViewById(R.id.switchManha);
        tarde = view.findViewById(R.id.switchTarde);
        noite = view.findViewById(R.id.switchNoite);
        nomeOuMatricula = view.findViewById(R.id.buscaNomeMatriculaPortaria);

        setarSpinnerTurmas();
        setarSwitchTurmas();

        imageViewSairFragmentPortaria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });
        return view;
    }
}