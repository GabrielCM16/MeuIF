package com.example.meuif.sepae.chamada;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meuif.R;

import java.util.List;

public class AdapterChamadaSepae extends RecyclerView.Adapter<AdapterChamadaSepae.ViewHolder> {

    private List<String> nomesChamada;
    private List<Integer> chamadaImages;

    public AdapterChamadaSepae(List<String> nomeschamada, List<Integer> chamadaImages) {

        this.nomesChamada = nomeschamada;;
        this.chamadaImages = chamadaImages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_chamada_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = nomesChamada.get(position);
        holder.textViewNomes.setText(text);
        holder.imageViewChamada.setImageResource(chamadaImages.get(position));
    }

    @Override
    public int getItemCount() {
        return nomesChamada.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNomes;
        ImageView imageViewChamada;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNomes = itemView.findViewById(R.id.textViewNomes);
            imageViewChamada = itemView.findViewById(R.id.imageViewChamada);
        }
    }
}

