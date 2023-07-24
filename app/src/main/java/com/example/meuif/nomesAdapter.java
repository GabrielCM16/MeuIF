package com.example.meuif;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class nomesAdapter extends RecyclerView.Adapter<ChamadaViewHolder> {

    private Context context;
    private ArrayList<nomes> itens;

    public nomesAdapter(Context context, ArrayList<nomes> itens) {
        this.context = context;
        this.itens = itens;
    }


    @NonNull
    @Override
    public ChamadaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.linha_nomes, parent, false);
        ChamadaViewHolder viewHolder = new ChamadaViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChamadaViewHolder holder, int position) {
        nomes nomee = itens.get(position);
        holder.nome.setText(nomee.getNome());

        if (nomee.getPresencaOUausencia()) {
            holder.saidaPresenca.setImageResource(R.drawable.presenca);
        } else {
            holder.saidaPresenca.setImageResource(R.drawable.falta);
        }

        holder.saidaPresenca.setOnClickListener(v -> {
            // Inverte o estado da imagem ao clicar
            nomee.setPresencaOUausencia(!nomee.getPresencaOUausencia());
            notifyDataSetChanged(); // Notifica a RecyclerView para atualizar as imagens
        });

    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}
