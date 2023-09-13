package com.example.meuif.autorizacoes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meuif.R;
import com.example.meuif.sepae.recyclerMerenda.AlunoMerenda;

import java.util.List;

public class AdapterAutorizacaoEntrada extends RecyclerView.Adapter<AdapterAutorizacaoEntrada.ViewHolder>{
    private List<AlunoAutorizacaoEntrada> stringList;

    public AdapterAutorizacaoEntrada(List<AlunoAutorizacaoEntrada> stringList) {

        this.stringList = stringList;
    }

    @NonNull
    @Override
    public AdapterAutorizacaoEntrada.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_autorizacao_atrasada, parent, false);
        return new AdapterAutorizacaoEntrada.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAutorizacaoEntrada.ViewHolder holder, int position) {
        AlunoAutorizacaoEntrada aluno = stringList.get(position);
        holder.nomeAutorizou.setText(aluno.getNome());
        holder.horaAutorizacao.setText(aluno.getHora());
        holder.numeroAtrasado.setText(aluno.getNumero());
        holder.MotivoAtraso.setText(aluno.getMotivo());
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeAutorizou;
        TextView horaAutorizacao;
        TextView numeroAtrasado;
        TextView MotivoAtraso;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeAutorizou = itemView.findViewById(R.id.nomeAutorizou);
            horaAutorizacao = itemView.findViewById(R.id.horaAutorizacao);
            numeroAtrasado = itemView.findViewById(R.id.numeroAtrasado);
            MotivoAtraso = itemView.findViewById(R.id.MotivoAtraso);
        }
    }
}
