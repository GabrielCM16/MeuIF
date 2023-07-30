package com.example.meuif;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomChamadaAdapter extends BaseAdapter {

    Context context;
    List<String> nomesChamda;
    List<Integer> presencasChamada;
    LayoutInflater inflater;

    public CustomChamadaAdapter(Context ctx, List<String> nomes, List<Integer> images){
        this.context = ctx;
        this.presencasChamada = images;
        this.nomesChamda = nomes;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return nomesChamda.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_chamada_listview, null);
        TextView textView = (TextView) view.findViewById(R.id.textViewNomes);
        ImageView chamada = (ImageView) view.findViewById(R.id.imageViewChamada);
        textView.setText(nomesChamda.get(position));
        chamada.setImageResource(presencasChamada.get(position));
        return view;
    }
}
