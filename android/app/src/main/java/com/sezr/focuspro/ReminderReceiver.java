package com.sezr.focuspro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "sezr_focus_reminders";
    public static final String EXTRA_NOTE = "note";

    @Override
    public void onReceive(Context context, Intent intent) {
        createChannel(context);

        String note = intent != null ? intent.getStringExtra(EXTRA_NOTE) : null;
        if (note == null || note.trim().isEmpty()) {
            note = "Bugünkü çalışma hedefini unutma.";
        }

        Intent openIntent = new Intent(context, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                1001,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_focus_launcher)
                .setContentTitle("SezR Focus")
                .setContentText(note)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(note))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(1001, builder.build());
        }
    }

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "SezR Focus Hatırlatmaları",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Günlük çalışma hatırlatmaları");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
