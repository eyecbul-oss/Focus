package com.sezr.focuspro;

public final class FocusLevelRules {
    private FocusLevelRules() {}

    public static String levelName(int completedMinutes) {
        if (completedMinutes >= 1000) return "Usta Odakçı";
        if (completedMinutes >= 500) return "Kararlı Çalışan";
        if (completedMinutes >= 180) return "Ritim Kazanan";
        if (completedMinutes >= 60) return "Başlayan";
        return "Yeni Başlangıç";
    }

    public static String badges(int completedMinutes, int completedSessions) {
        StringBuilder builder = new StringBuilder();
        if (completedSessions >= 1) builder.append("• İlk Seans\n");
        if (completedSessions >= 5) builder.append("• 5 Seans\n");
        if (completedSessions >= 10) builder.append("• 10 Seans\n");
        if (completedMinutes >= 60) builder.append("• 1 Saat Odak\n");
        if (completedMinutes >= 180) builder.append("• 3 Saat Odak\n");
        if (completedMinutes >= 500) builder.append("• 500 Dakika\n");
        if (builder.length() == 0) return "Henüz rozet yok. İlk seansı tamamla.";
        return builder.toString().trim();
    }
}
