package com.example.meuif;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meuif.autorizacoes.AdapterAutorizacaoEntrada;
import com.example.meuif.autorizacoes.AlunoAutorizacaoEntrada;
import com.example.meuif.sepae.recyclerMerenda.AdapterMerenda;
import com.example.meuif.sepae.recyclerMerenda.AlunoMerenda;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class AutorizacoesEntradaAtrasada extends Fragment {
    private ConstraintLayout constraintDuvidaEntrada;
    private TextView textViewEntradaAtrasadaTextDia;
    private RecyclerView RecyclerDiasAtrasados;
    private FirebaseFirestore db;
    private List<Map<String, Timestamp>> diaAtrasado = new ArrayList<Map<String, Timestamp>>();
    private View binding;


    public AutorizacoesEntradaAtrasada() {
        // Required empty public constructor
    }

    public static AutorizacoesEntradaAtrasada newInstance() {
        AutorizacoesEntradaAtrasada fragment = new AutorizacoesEntradaAtrasada();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = inflater.inflate(R.layout.fragment_autorizacoes_entrada_atrasada, container, false);
        constraintDuvidaEntrada = binding.findViewById(R.id.constraintDuvidaEntrada);
        db = FirebaseFirestore.getInstance();
        textViewEntradaAtrasadaTextDia = binding.findViewById(R.id.textViewEntradaAtrasadaTextDia);
        RecyclerDiasAtrasados = binding.findViewById(R.id.RecyclerDiasAtrasados);

        String matricula = recuperarDados("matricula");
        mostrarDia();
        pegarDiasAtrasados(matricula, new Callback() {
            @Override
            public void onComplete() {
                mostarAtrasados();
            }
        });

        constraintDuvidaEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDuvida();
            }
        });

        return binding;
    }

    public String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }

    private void mostrarDia(){
        String dia = getDayAndMonth();
        textViewEntradaAtrasadaTextDia.setText("Pedidos de entrada: " + dia);
    }

    private void mostarAtrasados(){
        List<AlunoAutorizacaoEntrada> stringList = new ArrayList<>();
        TimeZone timeZone = TimeZone.getTimeZone("GMT-3");

        // Crie um SimpleDateFormat usando o fuso horário definido
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sdf.setTimeZone(timeZone);

        int auxNumero = 0;

        for (Map<String, Timestamp> mapa : diaAtrasado) {
            for (Map.Entry<String, Timestamp> entry : mapa.entrySet()) {
                auxNumero++;
                String chave = entry.getKey();       // Obtém a chave (String)
                Timestamp timestamp = entry.getValue(); // Obtém o timestamp (Timestamp)

                String dataFormatada = sdf.format(timestamp.toDate());

                AlunoAutorizacaoEntrada aluno = new AlunoAutorizacaoEntrada(chave, dataFormatada, String.valueOf(auxNumero));
                stringList.add(aluno);

            }
        }


        AdapterAutorizacaoEntrada adapter = new AdapterAutorizacaoEntrada(stringList);

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerDiasAtrasados.setLayoutManager(layoutManager);
        RecyclerDiasAtrasados.setHasFixedSize(true);
        RecyclerDiasAtrasados.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayout.VERTICAL));
        RecyclerDiasAtrasados.setAdapter(adapter); //criar adapter
    }

    private void pegarDiasAtrasados(String matricula, Callback callback){
        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(matricula).document("autorizacoes");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, List<Map<String, Timestamp>>> entradas = (Map<String, List<Map<String, Timestamp>>>) document.get("entradaAula");

                        String dia = getDayAndMonth();

                        if (entradas.containsKey(dia)){
                            List<Map<String, Timestamp>> aux = entradas.get(dia);
                            diaAtrasado = aux;
                        }
                        callback.onComplete();

                    } else {
                        Log.d("TAGG", "Documento de turma não encontrado");
                        callback.onComplete();
                    }
                } else {
                    Log.d("TAGG", "Falhou em ", task.getException());
                    callback.onComplete();

                }
            }
        });
    }

    private void mostrarDuvida(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        //configurar titulo e mensagem
        dialog.setTitle("Entrada Atrasada" );
        dialog.setMessage("Caso necessite de uma autorização para entrar na sala de aula, entre em contato com alguém da SEPAE e solicite a autorização. A autorização será fornecida em formato digital para ser apresentada ao professor, substituindo o papel físico.");

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

    private interface Callback {
        void onComplete();
    }

    private String getDayAndMonth() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3")); // Defina o fuso horário desejado
        Date date = new Date();
        return dateFormat.format(date);
    }
}