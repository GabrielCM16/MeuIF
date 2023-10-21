package com.example.meuif.faltasPessoais;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meuif.R;
import com.example.meuif.sepae.recyclerMerenda.AlunoMerenda;

import java.util.List;

public class AdapterAcessosAluno extends RecyclerView.Adapter<AdapterAcessosAluno.ViewHolder>{
    private List<ModelAcessoAluno> stringList;

    public AdapterAcessosAluno(List<ModelAcessoAluno> stringList) {
        this.stringList = stringList;
    }

    @NonNull
    @Override
    public AdapterAcessosAluno.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_acessos_portaria, parent, false);
        return new AdapterAcessosAluno.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAcessosAluno.ViewHolder holder, int position) {
        ModelAcessoAluno aluno = stringList.get(position);
        holder.numeroAcesso.setText(aluno.getNumero());
        holder.textSaidaNome.setText(aluno.getNome());
        holder.textSaidaFlag.setText(aluno.getFlag());
        holder.horaAutorizacao.setText(aluno.getHora());
        if (aluno.getTurma() != null && !aluno.getTurma().equals("")){
            holder.textSaidaTurmaAcesso.setText(aluno.getTurma());
            holder.textSaidaTurmaAcesso.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView numeroAcesso;
        TextView textSaidaNome;
        TextView textSaidaFlag;
        TextView horaAutorizacao;
        TextView textSaidaTurmaAcesso;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            numeroAcesso = itemView.findViewById(R.id.numeroAcesso);
            textSaidaNome = itemView.findViewById(R.id.textSaidaNome);
            textSaidaFlag = itemView.findViewById(R.id.textSaidaFlag);
            horaAutorizacao = itemView.findViewById(R.id.horaAutorizacao);
            textSaidaTurmaAcesso = itemView.findViewById(R.id.textSaidaTurmaAcesso);
        }
    }
}
