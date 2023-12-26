package com.example.meuif.ui.gallery;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;

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
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import com.example.meuif.Notification;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private ImageView qrCode;
    private FirebaseFirestore db;
    private TextView textNome;
    private TextView textMatricula;
    private TextView textCurso;
    private Context contex;
    private String possivelStatus;
    private Boolean flag = true;
    private Notification notification;
    public String matricula;
    public String nome;
    public String curso;
    public Tela_Principal tela_principal;
    private Switch switchPrimeiraTela;
    private int startDestinationId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);

        tela_principal = new Tela_Principal();

        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        qrCode = root.findViewById(R.id.qrcode);
        textNome = root.findViewById(R.id.textNome);
        textCurso = root.findViewById(R.id.textCurso);
        textMatricula = root.findViewById(R.id.textMatricula);
        qrCode.setVisibility(View.INVISIBLE);
        switchPrimeiraTela = root.findViewById(R.id.switchPrimeiraTela);

        contex = root.getContext();

        nome = recuperarDados("nome");
        matricula = recuperarDados("matricula");
        curso = recuperarDados("curso");

        String qualTelaIniciar = recuperarDados("primeiraTela");

        if (qualTelaIniciar.equals("carteirinha")){
            switchPrimeiraTela.setChecked(true);
        }else {
            switchPrimeiraTela.setChecked(false);
        }

        switchPrimeiraTela.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    salvarDados("primeiraTela", "carteirinha");
                    Log.d("telachamada", "carteirinha");
                    startDestinationId = R.id.nav_gallery;
                } else{
                    salvarDados("primeiraTela", "home");
                    startDestinationId = R.id.nav_home;
                }


            }
        });



//        atualizarStatus(new Callback() {
//            @Override
//            public void onComplete() {
//                possivelStatus = recuperarDados("possivelStatus");
//
//            }
//        });
//
//        atualizarFlag(matricula ,new Callback() {
//            @Override
//            public void onComplete() {
//                flag = Boolean.parseBoolean(recuperarDados("flag"));
//            }
//        });

        textNome.setText("Nome: " + nome);
        textMatricula.setText("Matrícula: " + matricula);
        textCurso.setText("Curso: Ensino Médio Integrado ao Técnico em " + curso);

//        botao.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                atualizarStatus(new Callback() {
//                    @Override
//                    public void onComplete() {
//                        atualizarFlag(matricula ,new Callback() {
//                            @Override
//                            public void onComplete() {
//                                possivelStatus = recuperarDados("possivelStatus");
//                                iniciarTempo(view);
//                            }
//                        });
//                    }
//                });
//
//            }
//        });

        gerarEExibirOuSalvarQR();

        View view = getView();

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
    private interface Callback {
        void onComplete();
    }
    @Override
    public void onPause() {
        super.onPause();
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

    public void gerarEExibirOuSalvarQR() {
        matricula = recuperarDados("matricula");

        String nomeDoArquivo = "QRCode_" + matricula + ".png";

        // Tenta carregar a imagem do armazenamento interno
        Bitmap imagemCarregada = carregarImagemDoArmazenamentoInterno(nomeDoArquivo);

        if (imagemCarregada == null) {
            // Se a imagem não existe, gera uma nova e salva
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(matricula, BarcodeFormat.QR_CODE, 500, 500);

                // Salvar a imagem internamente no diretório de arquivos privados
                salvarImagemNoArmazenamentoInterno(bitmap, nomeDoArquivo);

                // Exibir a imagem no ImageView
                ImageView imageViewQrCode = (ImageView) qrCode;
                imageViewQrCode.setImageBitmap(bitmap);
                qrCode.setVisibility(View.VISIBLE);

                // Notificar o usuário sobre o salvamento
                Toast.makeText(getContext(), "QR Code gerado e salvo como " + nomeDoArquivo, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Ocorreu um Erro ao gerar o QR Code", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Se a imagem já existe, exibe no ImageView
            ImageView imageViewQrCode = (ImageView) qrCode;
            imageViewQrCode.setImageBitmap(imagemCarregada);
            qrCode.setVisibility(View.VISIBLE);
        }
    }

    private void salvarImagemNoArmazenamentoInterno(Bitmap bitmap, String nomeDoArquivo) {
        try {
            FileOutputStream outputStream = requireContext().openFileOutput(nomeDoArquivo, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap carregarImagemDoArmazenamentoInterno(String nomeDoArquivo) {
        try {
            FileInputStream inputStream = getContext().openFileInput(nomeDoArquivo);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            // Se ocorrer uma exceção, a imagem não existe
            return null;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}