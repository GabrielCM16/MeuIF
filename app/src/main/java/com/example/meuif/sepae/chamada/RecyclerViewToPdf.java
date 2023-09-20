package com.example.meuif.sepae.chamada;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.meuif.R;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewToPdf {
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static Map<String, Object> diasParaPdf = new HashMap<>();
    private static List<String> dias = new ArrayList<>();
    private static String mes = "";
    private static String mesTitulo = "";
    private static String turma = "";
    private static String diaGerada = "";

    public RecyclerViewToPdf(Map<String, Object> diasParaPdf, String mes, String mesTitulo, String turma, String diaGerada) {
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
        this.mesTitulo = mesTitulo;
        this.turma = turma;
        this.diaGerada = diaGerada;
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
            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);

            // Crie um Div para o título e a imagem
            Div titleWithImage = new Div();

// Crie uma tabela com duas colunas
            Table table1 = new Table(new float[]{1, 4}); // As proporções (1 e 4) podem ser ajustadas conforme necessário

// Adicione a imagem à primeira coluna da tabela
            int resourceId = R.drawable.logoapp;
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
            float desiredWidth = 30f;
            float desiredHeight = 30f;
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) desiredWidth, (int) desiredHeight, false);
            ImageData imageData = ImageDataFactory.create(toByteArray(bitmap));
            Image image = new Image(imageData);
            table1.addCell(new Cell().add(image));

// Crie um Paragraph para o título
            Paragraph title = new Paragraph("Planilha De Frequência Da Turma " + turma + " Do Mês " + mesTitulo);
            title.setFont(font); // Defina a fonte para o título
            title.setFontSize(14f); // Defina o tamanho da fonte para o título

// Adicione o título à segunda coluna da tabela
            table1.addCell(new Cell().add(title));

// Adicione a tabela ao Div
            titleWithImage.add(table1);

// Adicione o Div ao documento
            document.add(titleWithImage);


            // Configurar fonte


            // Configurações de estilo da tabela
            float[] columnWidths = {150f, 80f, 150f}; // Larguras das colunas
            Color backgroundColor = new DeviceGray(0.9f); // Cor de fundo das células
            Color borderColor = new DeviceGray(0.7f); // Cor da borda das células

            //verifica o  tamanho da tabela
            int contT = 0;
            if (diasParaPdf != null) {
                for (String fieldName : diasParaPdf.keySet()) {
                    if (!fieldName.equals("Lider") &&
                            !fieldName.equals("ViceLider") &&
                            !fieldName.equals("nomesSala") &&
                            fieldName.substring(fieldName.length() - 1).charAt(0) != 'T' &&
                            fieldName.substring(2,4).equals(mes)){
                        contT++;
                    }
                }
            }

            // Preencha a tabela com dados
            Table table = new Table(contT + 1); // Tabela com 6 colunas (rótulos e valores)
            Log.d("sim", String.valueOf(contT));

            List<String> dias = new ArrayList<String>();

            if (diasParaPdf != null) { //adiciona os dias
                // Iterando pelos campos e imprimindo seus nomes
                table.addCell(createCell("Dias: ", font, 8f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));
                for (String fieldName : diasParaPdf.keySet()) {
                    if (!fieldName.equals("Lider") &&
                            !fieldName.equals("ViceLider") &&
                            !fieldName.equals("nomesSala") &&
                            fieldName.substring(fieldName.length() - 1).charAt(0) != 'T' &&
                            fieldName.substring(2,4).equals(mes)){
                        dias.add(fieldName.substring(0,2));
                    }
                }
            }

            Log.d("dias", dias.toString());
            Collections.sort(dias, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    // Converta as strings em inteiros e compare
                    int numero1 = Integer.parseInt(s1);
                    int numero2 = Integer.parseInt(s2);
                    return Integer.compare(numero1, numero2);
                }
            });

            for (String dia: dias) {
                table.addCell(createCell(dia, font, 8f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));
            }
            Log.d("dias", "dias ordenados = " + dias.toString());


            Map<String, List<List<Boolean>>> nomesChamada = new HashMap<>();

            if (diasParaPdf != null) {
                for (String dia: dias){
                    for (String fieldName : diasParaPdf.keySet()) {

                        Log.d("dias", "filedname" + fieldName.substring(0,2));
                        if (fieldName.substring(2,4).equals(mes) &&
                                fieldName.substring(fieldName.length() - 1).charAt(0) != 'T' &&
                                dia.equals(fieldName.substring(0,2))){
                            Object value = diasParaPdf.get(fieldName);

                            if (value instanceof Map) {
                                if (diasParaPdf.containsKey(fieldName + "T")){
                                    Object value2 = diasParaPdf.get(fieldName + "T");
                                    // É um mapa, faça o cast e processe os valores
                                    Map<String, Object> diaAtualT = (Map<String, Object>) value2;
                                    Map<String, Object> diaAtualM = (Map<String, Object>) value;

                                    for (Object aux : diaAtualM.keySet()) {
                                        //table.addCell(createCell(String.valueOf((Boolean) aux), font, 8f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));
                                        if (nomesChamada.containsKey(aux)) {

                                            Boolean a = (Boolean) diaAtualM.get(aux);
                                            Boolean b = (Boolean) diaAtualT.get(aux);

                                            List<List<Boolean>> listaAuxPrincipal = nomesChamada.get(aux);
                                            List<Boolean> listaAux = new ArrayList<>();
                                            listaAux.add(a);
                                            listaAux.add(b);
                                            listaAuxPrincipal.add(listaAux);

                                            nomesChamada.put((String) aux, listaAuxPrincipal);

                                        } else {
                                            Boolean a = (Boolean) diaAtualM.get(aux);
                                            Boolean b = (Boolean) diaAtualT.get(aux);

                                            List<List<Boolean>> listaAuxPrincipal = new ArrayList<>();
                                            List<Boolean> listaAux = new ArrayList<>();
                                            listaAux.add(a);
                                            listaAux.add(b);
                                            listaAuxPrincipal.add(listaAux);

                                            nomesChamada.put((String) aux, listaAuxPrincipal);
                                        }
                                    }
                                } else {
                                    Map<String, Object> diaAtualM = (Map<String, Object>) value;

                                    for (Object aux : diaAtualM.keySet()) {
                                        //table.addCell(createCell(String.valueOf((Boolean) aux), font, 8f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));
                                        if (nomesChamada.containsKey(aux)) {

                                            Boolean a = (Boolean) diaAtualM.get(aux);

                                            List<List<Boolean>> listaAuxPrincipal = nomesChamada.get(aux);
                                            List<Boolean> listaAux = new ArrayList<>();
                                            listaAux.add(a);
                                            listaAux.add(null);
                                            listaAuxPrincipal.add(listaAux);


                                            nomesChamada.put((String) aux, listaAuxPrincipal);
                                        } else {
                                            Boolean a = (Boolean) diaAtualM.get(aux);

                                            List<List<Boolean>> listaAuxPrincipal = new ArrayList<>();
                                            List<Boolean> listaAux = new ArrayList<>();
                                            listaAux.add(a);
                                            listaAux.add(null);
                                            listaAuxPrincipal.add(listaAux);

                                            nomesChamada.put((String) aux, listaAuxPrincipal);
                                        }
                                    }

                                }
                            } else {

                            }


                        }
                    }
                }
            }

            //vamos ordenar
            // Converter o mapa em uma lista de entradas (chave-valor)
            List<Map.Entry<String, List<List<Boolean>>>> listaEntradas = new ArrayList<>(nomesChamada.entrySet());

            // Classificar a lista com base nas chaves (ordem alfabética)
            Collections.sort(listaEntradas, new Comparator<Map.Entry<String, List<List<Boolean>>>>() {
                @Override
                public int compare(Map.Entry<String, List<List<Boolean>>> entrada1, Map.Entry<String, List<List<Boolean>>> entrada2) {
                    return entrada1.getKey().compareTo(entrada2.getKey());
                }
            });

            // Criar um novo mapa ordenado a partir da lista classificada
            Map<String, List<List<Boolean>>> mapaOrdenado = new LinkedHashMap<>();
            for (Map.Entry<String, List<List<Boolean>>> entrada : listaEntradas) {
                mapaOrdenado.put(entrada.getKey(), entrada.getValue());
            }

            // Iterar sobre as chaves do Map
            for (String chave : mapaOrdenado.keySet()) {
                table.addCell(createCell(chave, font, 8f, TextAlignment.LEFT, VerticalAlignment.MIDDLE));

                // Obter a lista associada a essa chave
                List<List<Boolean>> lista = nomesChamada.get(chave);
                Log.d("aluno", "nome: " + chave);
                Log.d("aluno", "lista: " + lista.toString());

                // Iterar sobre os elementos da lista
                for (List<Boolean> valor : lista) {
                    if (valor.get(0) != null && valor.get(1) != null){
                        if (valor.get(0) && valor.get(1)){
                            Cell cell = createCustomCell("P", "P");
                            table.addCell(cell);
                        }else if(!valor.get(0) && !valor.get(1)) {
                            Cell cell = createCustomCell("F", "F");
                            table.addCell(cell);
                        } else if (valor.get(0) && !valor.get(1)){
                            Cell cell = createCustomCell("P", "F");
                            table.addCell(cell);
                        } else if (!valor.get(0) && valor.get(1)) {
                            Cell cell = createCustomCell("F", "P");
                            table.addCell(cell);
                        }
                    }else {
                        if (valor.get(0) != null && !valor.get(0) && valor.get(1) == null) {
                            Cell cell = createCustomCell("F", "");
                            table.addCell(cell);
                        }else if (valor.get(0) != null && valor.get(0) && valor.get(1) == null) {
                            Cell cell = createCustomCell("P", "");
                            table.addCell(cell);
                        } else {
                            Cell cell = createCustomCell("X", "X");
                            table.addCell(cell);
                        }
                    }


                }
            }

            document.add(table);

            // Crie um parágrafo para o rodapé
            Paragraph footer = new Paragraph("Planilha MeuIF Gerada Em " + diaGerada );
            footer.setFont(font); // Defina a fonte para o rodapé
            footer.setFontSize(10f); // Defina o tamanho da fonte para o rodapé
            footer.setTextAlignment(TextAlignment.CENTER); // Centralize o rodapé

            // Adicione espaço em branco após o rodapé (opcional)
            //footer.setMarginTop(10);

// Adicione o rodapé ao documento
            document.add(footer);

            // Feche o documento PDF
            document.close();

            Log.d("PDF", "PDF gerado com sucesso: " + pdfFile.getAbsolutePath());
            Toast.makeText(context, "PDF gerado", Toast.LENGTH_SHORT).show();
            openPdf(pdfFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Cell createCustomCell(String a1, String a2) {
        Color vermelho = new DeviceRgb(255, 0, 0);
        Color verde = new DeviceRgb(0, 255, 0);
        // Crie um Paragraph para a célula
        Paragraph paragraph = new Paragraph();

        if (a1.equals("P")){
            // Adicione o "F" em vermelho
            Text textoF = new Text(a1)
                    .setFontColor(verde)
                    .setFontSize(8f)
                    .setTextAlignment(TextAlignment.LEFT);
            paragraph.add(textoF);
        } else {
            // Adicione o "F" em vermelho
            Text textoF = new Text(a1)
                    .setFontColor(vermelho)
                    .setFontSize(8f)
                    .setTextAlignment(TextAlignment.LEFT);
            paragraph.add(textoF);
        }

        if (!a2.equals("")){
            // Adicione a barra ("/") em preto
            Text barra = new Text("/")
                    .setFontSize(8f)
                    .setTextAlignment(TextAlignment.LEFT);
            paragraph.add(barra);
        }

        if (a2.equals("P")){
            // Adicione o "P" em verde
            Text textoP = new Text(a2)
                    .setFontColor(verde)
                    .setFontSize(8f)
                    .setTextAlignment(TextAlignment.LEFT);
            paragraph.add(textoP);
        } else {
            // Adicione o "P" em verde
            Text textoP = new Text(a2)
                    .setFontColor(vermelho)
                    .setFontSize(8f)
                    .setTextAlignment(TextAlignment.LEFT);
            paragraph.add(textoP);
        }

        // Crie a célula e adicione o Paragraph
        Cell cell = new Cell()
                .add(paragraph)
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        return cell;
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
