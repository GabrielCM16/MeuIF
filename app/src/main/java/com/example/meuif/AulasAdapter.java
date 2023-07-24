package com.example.meuif;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AulasAdapter extends RecyclerView.Adapter<AulasViewHolder>{

    private Context context;
    private ArrayList<Aulas> itens;

    public AulasAdapter(Context context, ArrayList<Aulas> itens) {
        this.context = context;
        this.itens = itens;
    }

    @NonNull
    @Override
    public AulasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_aulas, parent, false);
        AulasViewHolder viewHolder = new AulasViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AulasViewHolder holder, int position) {
        Aulas aulaAtual = itens.get(position);

        AulasViewHolder.saidaComeco.setText(aulaAtual.getComecoAula());
        AulasViewHolder.saidaFim.setText(aulaAtual.getFimAula());
        AulasViewHolder.saidaAula.setText(aulaAtual.getNomeAula());
        AulasViewHolder.saidaProfessor.setText(aulaAtual.getNomeProfessor());
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}
