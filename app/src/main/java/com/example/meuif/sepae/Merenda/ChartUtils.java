package com.example.meuif.sepae.Merenda;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ChartUtils {

    public static void saveChartToGallery(Context context, BarChart barChart, String chartName) {
        if (barChart != null) {
            // Salvar o gráfico como uma imagem
            Bitmap chartBitmap = getChartBitmap(barChart);

            if (chartBitmap != null) {
                // Salvar a imagem na galeria
                saveImageToGallery(context, chartBitmap, chartName);
            } else {
                showToast(context, "Falha ao criar a imagem do gráfico.");
            }
        } else {
            showToast(context, "O gráfico é nulo.");
        }
    }

    private static Bitmap getChartBitmap(BarChart barChart) {
        // Configurar o gráfico para renderizar em um bitmap
        barChart.setDrawingCacheEnabled(true);
        barChart.buildDrawingCache(true);
        Bitmap chartBitmap = Bitmap.createBitmap(barChart.getDrawingCache());
        barChart.setDrawingCacheEnabled(false);
        return chartBitmap;
    }

    private static void saveImageToGallery(Context context, Bitmap bitmap, String chartName) {
        // Salvar a imagem na pasta Pictures do dispositivo
        File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File chartImageFile = new File(picturesDirectory, chartName + ".png");

        try {
            OutputStream fOut = new FileOutputStream(chartImageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.close();

            // Adicionar a imagem à galeria
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.DATA, chartImageFile.getAbsolutePath());
            context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            showToast(context, "Gráfico salvo na galeria com sucesso.");
        } catch (Exception e) {
            showToast(context, "Erro ao salvar o gráfico na galeria.");
            e.printStackTrace();
        }
    }

    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
