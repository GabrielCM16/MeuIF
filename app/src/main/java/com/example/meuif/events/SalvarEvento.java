package com.example.meuif.events;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SalvarEvento {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Events evento;

    public SalvarEvento(Events events){
        this.evento = events;
    }

    public void gravarEvent(){
        String turma = evento.getDiscipline();
        Map<String, Object> objetosMap = new HashMap<>();
        String identificador1 = UUID.randomUUID().toString();
        objetosMap.put(identificador1, evento);

        db.collection("Agenda")
                .document(turma)
                .update(objetosMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Documento salvo com sucesso.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ocorreu um erro ao salvar o documento.
                    }
                });
    }
}
