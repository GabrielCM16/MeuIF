package com.example.meuif.sepae.autorizacoes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meuif.R;
import com.example.meuif.sepae.recyclerMerenda.AlunoMerenda;

import java.util.List;

public class AdapterSEPAEautorizacoesHoje extends RecyclerView.Adapter<AdapterSEPAEautorizacoesHoje.ViewHolder>{
    private List<SepaeAutorizacoesEntradaAtrasada> stringList;

    public AdapterSEPAEautorizacoesHoje(List<SepaeAutorizacoesEntradaAtrasada> stringList) {

        this.stringList = stringList;
    }

    @NonNull
    @Override
    public AdapterSEPAEautorizacoesHoje.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_autorizacao_atrasada_sepae, parent, false);
        return new AdapterSEPAEautorizacoesHoje.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSEPAEautorizacoesHoje.ViewHolder holder, int position) {
        SepaeAutorizacoesEntradaAtrasada aluno = stringList.get(position);
        holder.quemAutorizouSepaeEntrada.setText("Autorizado por: " + aluno.getNomeSEPAE());
        holder.horaAutorizacaoSepae.setText(aluno.getHora());
        holder.numeroAtrasado.setText(aluno.getNumero());
        holder.nomeAlunoAutorizacao.setText(aluno.getNome());
        holder.turmaAluno.setText(aluno.getTurma());
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView quemAutorizouSepaeEntrada;
        TextView horaAutorizacaoSepae;
        TextView numeroAtrasado;
        TextView nomeAlunoAutorizacao;
        TextView turmaAluno;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            quemAutorizouSepaeEntrada = itemView.findViewById(R.id.quemAutorizouSepaeEntrada);
            horaAutorizacaoSepae = itemView.findViewById(R.id.horaAutorizacaoSepae);
            numeroAtrasado = itemView.findViewById(R.id.numeroAtrasado);
            nomeAlunoAutorizacao = itemView.findViewById(R.id.nomeAlunoAutorizacao);
            turmaAluno = itemView.findViewById(R.id.turmaAluno);
        }
    }
}
