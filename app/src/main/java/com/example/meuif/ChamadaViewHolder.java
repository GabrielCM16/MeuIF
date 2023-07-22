package com.example.meuif;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChamadaViewHolder extends RecyclerView.ViewHolder {

    static TextView nome;
    static ImageView saidaPresenca;

    public ChamadaViewHolder(@NonNull View itemView) {
        super(itemView);
        nome = itemView.findViewById(R.id.nome);
        saidaPresenca = itemView.findViewById(R.id.saidaPresenca);
    }
}
