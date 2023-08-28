package com.example.meuif.sepae.recyclerMerenda;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meuif.R;

import java.util.List;

public class AdapterMerenda extends RecyclerView.Adapter<AdapterMerenda.ViewHolder> {

    private List<AlunoMerenda> stringList;

    public AdapterMerenda(List<AlunoMerenda> stringList) {

        this.stringList = stringList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lister_alunos_merenda, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlunoMerenda aluno = stringList.get(position);
        holder.nomeMerenda.setText(aluno.getNome());
        holder.matriculaMerenda.setText(aluno.getMatricula());
        holder.horaMerenda.setText(aluno.getHora());
        holder.numeroMerenda.setText(aluno.getNumero());
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeMerenda;
        TextView matriculaMerenda;
        TextView horaMerenda;
        TextView numeroMerenda;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeMerenda = itemView.findViewById(R.id.nomeMerenda);
            matriculaMerenda = itemView.findViewById(R.id.matriculaMerenda);
            horaMerenda = itemView.findViewById(R.id.horaMerenda);
            numeroMerenda = itemView.findViewById(R.id.numeroMerenda);
        }
    }
}

