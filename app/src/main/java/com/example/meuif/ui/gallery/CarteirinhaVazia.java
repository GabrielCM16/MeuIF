package com.example.meuif.ui.gallery;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.meuif.R;
import com.example.meuif.Tela_Principal;

public class CarteirinhaVazia extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carteirinha_vazia);

        ActionBar actionBar = getSupportActionBar();
        setTitle("Carteirinha");
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (actionBar != null) {
            // Defina a cor de background desejada (por exemplo, cor vermelha)
            ColorDrawable colorDrawable = new ColorDrawable(0xff23729a);
            actionBar.setBackgroundDrawable(colorDrawable);
        }

        // Obter o FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Iniciar a transação do fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Adicionar o Fragment ao contêiner
        GalleryFragment seuFragment = new GalleryFragment();
        fragmentTransaction.replace(R.id.fragment_container_carteirinha, seuFragment);

        // Confirmar a transação
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Verifica se o item clicado é o botão do ActionBar
        if (item.getItemId() == android.R.id.home) {
            // Chame o método que você deseja executar quando o ActionBar for clicado
            Intent intent = new Intent(this, Tela_Principal.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}