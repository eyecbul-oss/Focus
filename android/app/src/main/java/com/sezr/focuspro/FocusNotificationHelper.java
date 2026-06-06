package com.sezr.focuspro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public final class FocusNotificationHelper {
    public static final String CHANNEL_ID = "focus_pro_channel";

    private FocusNotificationHelper() {}

    public static void ensureChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "SezR Focus Pro",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("Odak seansı ve hedef hatırlatmaları");
        manager.createNotificationChannel(channel);
    }

    public static String reminderText(int todayMinutes, int targetMinutes, int daysToExam, int openTasks) {
        if (todayMinutes <= 0) return "Bugün henüz odak seansı yok. 25 dakika ile başla.";
        if (todayMinutes < targetMinutes) return "Günlük hedefe " + Math.max(0, targetMinutes - todayMinutes) + " dk kaldı.";
        if (openTasks > 0) return "Hedef tamam, açık görevleri kısa tekrar ile kapat.";
        if (daysToExam <= 30) return "Sınav yaklaşıyor; deneme ve yanlış analizi yap.";
        return "Hedef tamamlandı. Kısa tekrar iyi olur.";
    }

    public static String sessionDoneText(int minutes, String category) {
        return minutes + " dk " + category + " odak seansı tamamlandı.";
    }

    public static String breakDoneText() {
        return "Mola bitti. Yeni odak seansına dönebilirsin.";
    }
}
