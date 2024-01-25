package com.example.meuif.sepae.gestao.aluno;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meuif.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterGerenciarAluno extends RecyclerView.Adapter<AdapterGerenciarAluno.MyViewHolder>{
    private List<ModelGerenciarAluno> lista;


    public AdapterGerenciarAluno(List<ModelGerenciarAluno> lista){
        this.lista = lista;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itenLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.lister_alunos_merenda, parent, false) ;

        return new MyViewHolder(itenLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.numeroMerenda.setVisibility(View.INVISIBLE);
        holder.horaMerenda.setVisibility(View.INVISIBLE);

        ModelGerenciarAluno aluno = lista.get(position);
        holder.nome.setText(aluno.getNome());
        holder.matricula.setText(aluno.getMatricula());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome;
        TextView matricula;
        TextView numeroMerenda;
        TextView horaMerenda;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.nomeMerenda);
            matricula = itemView.findViewById(R.id.matriculaMerenda);
            numeroMerenda = itemView.findViewById(R.id.numeroMerenda);
            horaMerenda = itemView.findViewById(R.id.horaMerenda);
        }
    }

}

