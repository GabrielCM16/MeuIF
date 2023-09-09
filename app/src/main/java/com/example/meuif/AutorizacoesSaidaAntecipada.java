package com.example.meuif;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AutorizacoesSaidaAntecipada extends Fragment {
    private ConstraintLayout constraintDuvidaSaidaAntecipada;
    private TextView textViewSaidaAntecipadaTextDia;


    public AutorizacoesSaidaAntecipada() {
        // Required empty public constructor
    }

    public static AutorizacoesSaidaAntecipada newInstance() {
        AutorizacoesSaidaAntecipada fragment = new AutorizacoesSaidaAntecipada();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View bindng =  inflater.inflate(R.layout.fragment_autorizacoes_saida_antecipada, container, false);
        constraintDuvidaSaidaAntecipada = bindng.findViewById(R.id.constraintDuvidaSaidaAntecipada);
        textViewSaidaAntecipadaTextDia = bindng.findViewById(R.id.textViewSaidaAntecipadaTextDia);

        mostrarDia();

        constraintDuvidaSaidaAntecipada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDuvida();
            }
        });
        return bindng;
    }

    private String getDayAndMonth() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3")); // Defina o fuso horário desejado
        Date date = new Date();
        return dateFormat.format(date);
    }
    private void mostrarDia(){
        String dia = getDayAndMonth();
        textViewSaidaAntecipadaTextDia.setText("Pedidos de entrada: " + dia);
    }

    private void mostrarDuvida(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        //configurar titulo e mensagem
        dialog.setTitle("Saida Antecipada" );
        dialog.setMessage("Caso necessite de uma autorização de saida antecipada do colégio, entre em contato com alguém da SEPAE e solicite a autorização. A autorização será fornecida em formato digital para ser apresentada na portaria no momento da saída, substituindo o papel físico.");

        //configurar cancelamento do alert dialog
        dialog.setCancelable(false);

        //configurar icone
        //dialog.setIcon(android.R.drawable.ic_btn_speak_now);

        //configurar açoes para sim e nâo
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "Duvidas, procure a SEPAE", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.create();
        dialog.show();
    }
}