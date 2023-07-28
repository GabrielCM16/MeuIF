package com.example.meuif;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.example.meuif.R;

public class Notification {

    private static final String CHANNEL_ID = "MyCh";
    private static final CharSequence CHANNEL_NAME = "My Channel";
    private static final String CHANNEL_DESCRIPTION = "MeuIF";

    private static int notificationId = 1;

    public static void showNotification(Context context, String title, String content) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Verifique se a versão do Android é igual ou superior a Oreo (26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Crie o canal de notificação para dispositivos com Android Oreo e superior
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }

        android.app.Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new android.app.Notification.Builder(context, CHANNEL_ID);
        } else {
            builder = new android.app.Notification.Builder(context);
        }

        builder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.baseline_arrow_back_ios_24)
                .setAutoCancel(true); // A notificação será cancelada automaticamente quando o usuário clicar nela

        notificationManager.notify(notificationId, builder.build());
    }
}
