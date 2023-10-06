package com.example.meuif.sepae.chamada;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DiasUteisDoMes {
    public static List<String> obterDiasUteisDoMes() {
        List<String> diasUteis = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Defina o dia para o primeiro dia do mês atual

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        int ultimoDiaDoMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        while (calendar.get(Calendar.DAY_OF_MONTH) <= ultimoDiaDoMes) {
            int diaDaSemana = calendar.get(Calendar.DAY_OF_WEEK);

            // 1 é Domingo, 7 é Sábado
            if (diaDaSemana != Calendar.SATURDAY && diaDaSemana != Calendar.SUNDAY) {
                diasUteis.add(dateFormat.format(calendar.getTime()));
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1); // Avança para o próximo dia
        }

        return diasUteis;
    }
}
