package com.example.meuif.ui.gallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.meuif.R;
import com.example.meuif.Tela_Principal;
import com.example.meuif.databinding.FragmentGalleryBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private TextView saidaTempo;
    private ImageView qrCode;
    private Button botao;
    private long finalContador = 15000; //milisegundos (15 segundos)
    private long intervaloContador = 1000; //milisegundos (1 segundo)
    private boolean apertado = false;
    private TextView textNome;
    private TextView textMatricula;
    private TextView textCurso;
    private Context contex;
    public String matricula;
    public String nome;
    public String curso;
    public Tela_Principal tela_principal;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);

        tela_principal = new Tela_Principal();

        View root = binding.getRoot();


        saidaTempo = root.findViewById(R.id.saidaTempo);
        qrCode = root.findViewById(R.id.qrcode);
        textNome = root.findViewById(R.id.textNome);
        textCurso = root.findViewById(R.id.textCurso);
        textMatricula = root.findViewById(R.id.textMatricula);
        botao = root.findViewById(R.id.button);
        qrCode.setVisibility(View.INVISIBLE);
        botao.setVisibility(View.VISIBLE);


        contex = root.getContext();

        nome = recuperarDados("nome");
        matricula = recuperarDados("matricula");
        curso = recuperarDados("curso");

        textNome.setText("Nome: " + nome);
        textMatricula.setText("Matrícula: " + matricula);
        textCurso.setText("Curso: Ensino Médio Integrado ao Técnico em " + curso);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarTempo(view);
            }
        });


        return root;
    }

    public String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }



    private void contador(long finalCont, long intervaloCont){
        new CountDownTimer(finalCont, intervaloCont) {

            public void onTick(long millisUntilFinished) {
                long segundos = millisUntilFinished / 1000;
                long minutos = millisUntilFinished / 60000;
                saidaTempo.setText("tempo restante: " + minutos + ":" + segundos);
            }

            public void onFinish() {
                saidaTempo.setText("Fim!");
                qrCode.setVisibility(View.INVISIBLE);
                botao.setVisibility(View.VISIBLE);
                apertado = false;
            }
        }.start();
    }

    public void iniciarTempo(View view){
        if (!apertado) {
            apertado = true;
            botao.setVisibility(View.INVISIBLE);
            contador(finalContador, intervaloContador);
            String qr = stringQRcode();
            gerarQR(qr);
        } else {
            abrirToast("QR code já gerado!");
        }
    }

    public String stringQRcode(){
        long currentAtual = System.currentTimeMillis();
        String saidaQR = matricula + "/" + String.valueOf(currentAtual);
        return saidaQR;
    }

    public void gerarQR(String gerar){
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(gerar, BarcodeFormat.QR_CODE, 500, 500);
            ImageView imageViewQrCode = (ImageView) qrCode;
            imageViewQrCode.setImageBitmap(bitmap);
            qrCode.setVisibility(View.VISIBLE);
        } catch(Exception e) {

        }
    }

    public void abrirToast(String texto){
        Toast.makeText(
                contex,
                texto,
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}