package com.example.meuif.sepae.chamada;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class RequestPermission extends ActivityResultContract<String, Boolean> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String permission) {
        // Esta classe não exige a criação de um Intent.
        return new Intent();
    }

    @Override
    public Boolean parseResult(int resultCode, @Nullable Intent intent) {
        // Verifica se a permissão foi concedida pelo usuário.
        return resultCode == PackageManager.PERMISSION_GRANTED;
    }
}
