package com.example.meuif.ui.gallery;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.DocumentsContract;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import com.example.meuif.Notification;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private TextView saidaTempo;
    private ImageView qrCode;
    private Button botao;
    private FirebaseFirestore db;
    private long finalContador = 20000; //milisegundos (15 segundos)
    private long intervaloContador = 1000; //milisegundos (1 segundo)
    private boolean apertado = false;
    private TextView textNome;
    private TextView textMatricula;
    private TextView textCurso;
    private Context contex;
    private CountDownTimer countDownTimer;
    private long auxVerificarQR;
    private String possivelStatus;
    private Boolean flag = true;
    private Notification notification;
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

        db = FirebaseFirestore.getInstance();
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
        atualizarStatus(new Callback() {
            @Override
            public void onComplete() {
                possivelStatus = recuperarDados("possivelStatus");

            }
        });

        atualizarFlag(matricula ,new Callback() {
            @Override
            public void onComplete() {
                flag = Boolean.parseBoolean(recuperarDados("flag"));
            }
        });

        textNome.setText("Nome: " + nome);
        textMatricula.setText("Matrícula: " + matricula);
        textCurso.setText("Curso: Ensino Médio Integrado ao Técnico em " + curso);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atualizarStatus(new Callback() {
                    @Override
                    public void onComplete() {
                        atualizarFlag(matricula ,new Callback() {
                            @Override
                            public void onComplete() {
                                possivelStatus = recuperarDados("possivelStatus");
                                iniciarTempo(view);
                            }
                        });
                    }
                });

            }
        });


        return root;
    }



    public String recuperarDados(String chave){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String aux = sharedPreferences.getString(chave, "");
        return aux;
    }
    public void salvarDados(String chave, String valor){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(chave, valor);
        editor.commit();
    }



    private void contador(long finalCont, long intervaloCont){
        countDownTimer = new CountDownTimer(finalCont, intervaloCont) {

            public void onTick(long millisUntilFinished) {
                long segundos = millisUntilFinished / 1000;
                long minutos = millisUntilFinished / 60000;
                auxVerificarQR = segundos;
                if (auxVerificarQR >= 1){
                    String auxStatus = possivelStatus;
                    Boolean auxFlag = flag;
                    atualizarStatus(new Callback() {
                        @Override
                        public void onComplete() {
                            possivelStatus = recuperarDados("possivelStatus");
                            if (!possivelStatus.equals(auxStatus)){
                                notification.showNotification(getContext(), "Ola! " + nome, "Passe Realizado");
                            }
                        }
                    });

                    atualizarFlag(matricula ,new Callback() {
                        @Override
                        public void onComplete() {
                            flag = Boolean.parseBoolean(recuperarDados("flag"));
                            Log.d("flag", "a" + flag + " b "+ auxFlag);
                            if (flag != auxFlag){
                                notification.showNotification(getContext(), "Ola! " + nome, "Passe da Merenda Realizado, Bom Lanche!");
                            }
                        }
                    });
                }
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

    private interface Callback {
        void onComplete();
    }

    private void stopContador() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Interrompa o contador quando o fragmento estiver em pausa
        stopContador();
    }



    private void atualizarStatus(Callback callback){
        String nMatricula = recuperarDados("matricula");

        DocumentReference docRef = db.collection("Usuarios").document("Alunos").collection(nMatricula).document("chamadaPessoal");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String possivelStatus = document.getString("possivelStatus");
                        salvarDados("possivelStatus", possivelStatus);
                        callback.onComplete();
                    } else {
                        Log.d("TAGLER", "Documento não encontrado");
                        callback.onComplete();
                    }
                } else {
                    Log.d("TAGLER", "Falhou em ", task.getException());
                    callback.onComplete();
                }
            }
        });


    }

    private void atualizarFlag(String nMatricula, Callback callback ){
        DocumentReference docRefec = db.collection("Usuarios").document("Alunos").collection(nMatricula).document("merendaPessoal");
        docRefec.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Boolean flag = document.getBoolean("flag");
                        salvarDados("flag", String.valueOf(flag));
                        callback.onComplete();
                    } else {
                        Log.d("TAGLER", "Documento não encontrado");
                        callback.onComplete();
                    }
                } else {
                    Log.d("TAGLER", "Falhou em ", task.getException());
                    callback.onComplete();
                }
            }
        });
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
        return matricula;
    }

    public void gerarQR(String textoAGerar){
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(textoAGerar, BarcodeFormat.QR_CODE, 500, 500);
            ImageView imageViewQrCode = (ImageView) qrCode;
            imageViewQrCode.setImageBitmap(bitmap);
            qrCode.setVisibility(View.VISIBLE);
        } catch(Exception e) {
            Toast.makeText(getContext(), "Ocorreu um Erro ao gerar o QR Code", Toast.LENGTH_SHORT).show();
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