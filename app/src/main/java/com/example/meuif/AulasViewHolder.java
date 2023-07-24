package com.example.meuif;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AulasViewHolder extends RecyclerView.ViewHolder{

    static TextView saidaComeco;
    static TextView saidaFim;
    static TextView saidaAula;
    static TextView saidaProfessor;

    public AulasViewHolder(@NonNull View itemView) {
        super(itemView);
        saidaComeco = itemView.findViewById(R.id.saidaComeco);
        saidaFim = itemView.findViewById(R.id.saidaFim);
        saidaAula = itemView.findViewById(R.id.saidaAula);
        saidaProfessor = itemView.findViewById(R.id.saidaProfessor);
    }
}
