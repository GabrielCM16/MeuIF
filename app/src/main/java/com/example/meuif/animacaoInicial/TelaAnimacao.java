package com.example.meuif.animacaoInicial;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.meuif.MainActivity;
import com.example.meuif.R;

public class TelaAnimacao extends AppCompatActivity {
    VideoView vv;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (recuperarDados("animacao").equals("true") || recuperarDados("animacao").equals("")){
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            setContentView(R.layout.activity_tela_animacao);

            vv = findViewById(R.id.vv);
            vv.setBackgroundColor(Color.TRANSPARENT);
            vv.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.introducao);

            // Desativa os controles padrão do VideoView
            vv.setMediaController(null);

            // Define um listener para detectar o término do vídeo
            vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    irParaOutraTela();
                }
            });

            // Desativa a interação com o VideoView
            vv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true; // Impede que o usuário toque no VideoView
                }
            });

            vv.setVisibility(View.VISIBLE);
            vv.start();

        } else {
            irParaOutraTela();
        }


    }

    public String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }

    private void irParaOutraTela() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}