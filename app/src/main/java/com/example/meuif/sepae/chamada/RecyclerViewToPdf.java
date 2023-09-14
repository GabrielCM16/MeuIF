package com.example.meuif.sepae.chamada;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewToPdf {
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static Map<String, Object> diasParaPdf = new HashMap<>();
    private static List<String> dias = new ArrayList<>();
    private static String mes = "";

    public RecyclerViewToPdf(Map<String, Object> diasParaPdf, String mes) {
        this.diasParaPdf = diasParaPdf;
        if (diasParaPdf != null) {
            // Iterando pelos campos e imprimindo seus nomes
            for (String fieldName : diasParaPdf.keySet()) {
                if (!fieldName.equals("Lider") && !fieldName.equals("ViceLider") && !fieldName.equals("nomesSala")){
                    dias.add(fieldName);
                }
            }
        }
        this.mes = mes;
        Log.d("pdf", mes + diasParaPdf.toString());
    }

    public static void createPdf(Context context, String pdfFileName) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("pdf", "tem permissao");
            generatePdf(context, pdfFileName);
        } else {
            Log.d("pdf", "sem permissao");
            Toast.makeText(context, "PERMISSÃO NEGADA DE ARQUIVOS", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Não foi possivel gerar o pdf", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, Context context, String pdfFileName) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generatePdf(context, pdfFileName);
            } else {
                // Permissão negada pelo usuário
            }
        }
    }


    private static void generatePdf(Context context, String pdfFileName) {
        // Nome do arquivo PDF
        Log.d("pdf", "chegou no gerar");
        Toast.makeText(context, "Gerando PDF e Salvando", Toast.LENGTH_SHORT).show();

        try {
            // Diretório onde o arquivo PDF será salvo
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MeuIF_Docs");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File pdfFile = new File(directory, pdfFileName);

            PdfWriter pdfWriter = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            pdfDocument.setDefaultPageSize(PageSize.A4.rotate()); // Isso define a orientação como paisagem (horizontal)

            Document document = new Document(pdfDocument);

            // Configurar fonte
            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);

            // Configurações de estilo da tabela
            float[] columnWidths = {150f, 80f, 150f}; // Larguras das colunas
            Color backgroundColor = new DeviceGray(0.9f); // Cor de fundo das células
            Color borderColor = new DeviceGray(0.7f); // Cor da borda das células

            //verifica o  tamanho da tabela
            int contT = 0;
            if (diasParaPdf != null) {
                for (String fieldName : diasParaPdf.keySet()) {
                    if (!fieldName.equals("Lider") && !fieldName.equals("ViceLider") && !fieldName.equals("nomesSala") && fieldName.substring(2,4).equals(mes)){
                        contT++;
                    }
                }
            }

            // Preencha a tabela com dados
            Table table = new Table(contT + 1); // Tabela com 6 colunas (rótulos e valores)

            if (diasParaPdf != null) { //adiciona os dias
                // Iterando pelos campos e imprimindo seus nomes
                table.addCell(createCell("Dias: ", font, 8f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));
                for (String fieldName : diasParaPdf.keySet()) {
                    if (!fieldName.equals("Lider") && !fieldName.equals("ViceLider") && !fieldName.equals("nomesSala") && fieldName.substring(2,4).equals(mes)){
                        table.addCell(createCell(fieldName, font, 8f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));
                    }
                }
            }

            Map<String, List<Boolean>> nomesChamada = new HashMap<>();

            if (diasParaPdf != null) {
                for (String fieldName : diasParaPdf.keySet()) {
                    if (fieldName.substring(2,4).equals(mes)){
                        Object value = diasParaPdf.get(fieldName);

                        if (value instanceof Map) {
                            // É um mapa, faça o cast e processe os valores
                            Map<String, Object> diaAtual = (Map<String, Object>) value;
                            Log.d("TAG", "cada dia  " + fieldName + " " + diaAtual.toString());

                            for (Object aux : diaAtual.keySet()) {
                                //table.addCell(createCell(String.valueOf((Boolean) aux), font, 8f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));
                                if (nomesChamada.containsKey(aux)) {
                                    Log.d("TAG", "lista" + diaAtual.get(aux) + "aux" + aux);
                                    Boolean a = (Boolean) diaAtual.get(aux);
                                    List<Boolean> b = nomesChamada.get(aux);
                                    b.add((Boolean) diaAtual.get(aux));
                                    nomesChamada.put((String) aux, b);
                                } else {
                                    List<Boolean> a = new ArrayList<>();
                                    a.add((Boolean) diaAtual.get(aux));
                                    nomesChamada.put((String) aux, a);
                                }
                            }
                            Log.d("TAG", nomesChamada.toString());
                        } else {
                            // Não é um mapa, faça o que for necessário para lidar com outros tipos de valores
                        }
                    }
                }
            }

            // Iterar sobre as chaves do Map
            for (String chave : nomesChamada.keySet()) {
                table.addCell(createCell(chave, font, 8f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));

                // Obter a lista associada a essa chave
                List<Boolean> lista = nomesChamada.get(chave);

                // Iterar sobre os elementos da lista
                for (Boolean valor : lista) {
                    table.addCell(createCell(String.valueOf(valor), font, 8f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));
                }
            }




//            for (int i = 0; i < content.length; i++) {
//                table.addCell(createCell("Nome:", font, 10f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));
//                table.addCell(createCell(content[i][0], font, 10f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));
//
//                table.addCell(createCell("Idade:", font, 10f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));
//                table.addCell(createCell(content[i][1], font, 10f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));
//
//                table.addCell(createCell("Cidade:", font, 10f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));
//                table.addCell(createCell(content[i][2], font, 10f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));
//            }

            document.add(table);

            // Feche o documento PDF
            document.close();

            Log.d("PDF", "PDF gerado com sucesso: " + pdfFile.getAbsolutePath());
            Toast.makeText(context, "PDF gerado", Toast.LENGTH_SHORT).show();
            openPdf(pdfFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Cell createCell(String text, PdfFont font, float fontSize, TextAlignment alignment, VerticalAlignment verticalAlignment) {
        return new Cell()
                .setFont(font)
                .setFontSize(fontSize)
                .setTextAlignment(alignment)
                .setVerticalAlignment(verticalAlignment)
                .add(new Paragraph(text));
    }


    public static void openPdf(String filePath) {
        try {
            File pdfFile = new File(filePath);
            if (pdfFile.exists()) {
                Process process = Runtime.getRuntime().exec("cmd.exe /c start " + filePath);
                process.waitFor();
            } else {
                Log.d("PDF", "Arquivo PDF não encontrado: " + filePath);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
