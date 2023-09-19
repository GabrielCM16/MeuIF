package com.example.meuif.chamadaLideres;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meuif.AlunoChamada;
import com.example.meuif.R;
import com.example.meuif.sepae.recyclerMerenda.AlunoMerenda;

import java.util.List;

public class AdapterChamadaLideres extends RecyclerView.Adapter<AdapterChamadaLideres.ViewHolder>{
    private List<AlunoChamada> stringList;
    private Boolean tercaQuinta;

    public AdapterChamadaLideres(List<AlunoChamada> stringList, Boolean tercaQuinta) {
        this.stringList = stringList;
        this.tercaQuinta = tercaQuinta;
    }

    @NonNull
    @Override
    public AdapterChamadaLideres.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_chamada_listview, parent, false);
        return new AdapterChamadaLideres.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChamadaLideres.ViewHolder holder, int position) {
        AlunoChamada aluno = stringList.get(position);
        holder.imageViewChamada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aluno.setChamadaTurno1();
                Log.d("aluno", "click em 1 " + aluno.getNome() + "cham " + aluno.getChamadaTurno1());
                notifyDataSetChanged();
            }
        });

        if (tercaQuinta){
            holder.imageViewChamada2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aluno.setChamadaTurno2();
                    Log.d("aluno", "click em 1 " + aluno.getNome() + "cham " + aluno.getChamadaTurno2());
                    notifyDataSetChanged();
                }
            });
        }

        holder.textViewNomes.setText(aluno.getNome());

        if (aluno.getChamadaTurno1() != null && aluno.getChamadaTurno1()) {
            holder.imageViewChamada.setImageResource(R.drawable.presenca);
        } else {
            holder.imageViewChamada.setImageResource(R.drawable.falta);
        }
        if (tercaQuinta){
            if (aluno.getChamadaTurno2() != null && aluno.getChamadaTurno2()) {
                holder.imageViewChamada2.setVisibility(View.VISIBLE);
                holder.imageViewChamada2.setImageResource(R.drawable.presenca);
            } else {
                holder.imageViewChamada2.setVisibility(View.VISIBLE);
                holder.imageViewChamada2.setImageResource(R.drawable.falta);
            }
        } else {
            holder.imageViewChamada2.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewChamada;
        TextView textViewNomes;
        ImageView imageViewChamada2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewChamada = itemView.findViewById(R.id.imageViewChamada);
            imageViewChamada2 = itemView.findViewById(R.id.imageViewChamada2);
            textViewNomes = itemView.findViewById(R.id.textViewNomes);
        }
    }
}
