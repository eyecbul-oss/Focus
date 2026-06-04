package com.sezr.focuspro;

import java.util.ArrayList;
import java.util.List;

/**
 * Local rule-based study coach for SezR Focus Pro.
 * This class does not need internet and can later be connected to Firebase/OpenAI.
 */
public final class AiStudyCoach {
    private AiStudyCoach() {}

    public static String advice(int todayMinutes, int targetMinutes, int openTasks, int daysToExam, String examName) {
        StringBuilder s = new StringBuilder();
        if (todayMinutes <= 0) {
            s.append("Bugün sıfırdan başlıyorsun. İlk hedef 25 dakikalık kısa bir odak seansı olsun. ");
        } else if (todayMinutes < targetMinutes) {
            s.append("Bugünkü hedefe yaklaşmışsın. ")
             .append(Math.max(0, targetMinutes - todayMinutes))
             .append(" dk daha çalışırsan hedef tamamlanır. ");
        } else {
            s.append("Günlük hedef tamamlandı. Yeni konu açmak yerine tekrar ve yanlış analizi yap. ");
        }

        if (openTasks > 0) {
            s.append("Açık görevlerden en kısa ve en kritik olanı seçip bitir. ");
        } else {
            s.append("Görev listen boş; AI planını görevlere ekle. ");
        }

        if (daysToExam <= 7) {
            s.append(examName).append(" çok yaklaştı. Deneme, yanlış analizi ve eksik kapatma öncelikli olmalı.");
        } else if (daysToExam <= 30) {
            s.append(examName).append(" yaklaşıyor. Yeni konu yerine eksik kapatma, deneme ve tekrar ağırlıklı ilerle.");
        } else {
            s.append("Sınava zaman var. Konu özeti, soru çözümü ve haftalık tekrar dengeli gitmeli.");
        }
        return s.toString();
    }

    public static List<PlanItem> buildPlan(int todayMinutes, int targetMinutes, int daysToExam, int breakMinutes) {
        List<PlanItem> plan = new ArrayList<>();
        if (daysToExam <= 7) {
            plan.add(new PlanItem("Son hafta deneme yanlışlarını analiz et", "Sınav", 25, 5));
            plan.add(new PlanItem("En çok yanlış çıkan konudan 20 soru çöz", "Problem", 25, 5));
            plan.add(new PlanItem("Kısa formül ve not tekrarı yap", "Matematik", 15, 4));
            plan.add(new PlanItem(breakMinutes + " dk kontrollü mola", "Mola", breakMinutes, 2));
            return plan;
        }
        if (daysToExam <= 30) {
            plan.add(new PlanItem("Deneme yanlışlarını incele", "Sınav", 25, 5));
            plan.add(new PlanItem("Eksik konudan soru çöz", "Problem", 25, 4));
            plan.add(new PlanItem("Yanlış defterine 5 madde yaz", "Sınav", 10, 4));
            plan.add(new PlanItem(breakMinutes + " dk mola", "Mola", breakMinutes, 2));
            return plan;
        }
        if (todayMinutes < targetMinutes / 2) {
            plan.add(new PlanItem("10 dk konu özeti çıkar", "Matematik", 10, 3));
            plan.add(new PlanItem("25 dk soru çözümü yap", "Problem", 25, 4));
            plan.add(new PlanItem("5 dk yanlış analizi yap", "Sınav", 5, 4));
            plan.add(new PlanItem(breakMinutes + " dk mola", "Mola", breakMinutes, 2));
            plan.add(new PlanItem("25 dk tekrar seansı yap", "Matematik", 25, 3));
            return plan;
        }
        plan.add(new PlanItem("Kısa tekrar yap", "Matematik", 15, 3));
        plan.add(new PlanItem("Zor soruları işaretle", "Problem", 15, 4));
        plan.add(new PlanItem("Gün sonu rapor kontrolü yap", "Sınav", 5, 3));
        return plan;
    }

    public static String planText(List<PlanItem> items) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            PlanItem item = items.get(i);
            sb.append(i + 1).append(") ")
              .append(item.minutes).append(" dk ")
              .append(item.title);
            if (item.priority >= 5) sb.append("  ⭐");
            if (i < items.size() - 1) sb.append("\n");
        }
        return sb.toString();
    }

    public static String shortPlanTitle(int todayMinutes, int targetMinutes, int daysToExam) {
        if (daysToExam <= 7) return "Son hafta: deneme + yanlış analizi";
        if (daysToExam <= 30) return "Eksik kapatma ve deneme dönemi";
        if (todayMinutes < targetMinutes / 2) return "Hedefe başlama planı";
        if (todayMinutes >= targetMinutes) return "Hedef tamam: tekrar planı";
        return "Hedefi tamamlama planı";
    }

    public static String reportSummary(int todayMinutes, int targetMinutes, int sessions, int weeklyMinutes, int openTasks) {
        int progress = targetMinutes <= 0 ? 0 : Math.min(100, Math.round(todayMinutes * 100f / targetMinutes));
        StringBuilder sb = new StringBuilder();
        sb.append("Bugün ").append(todayMinutes).append(" dk çalıştın. ");
        sb.append("Hedef ilerlemesi: %").append(progress).append(". ");
        sb.append("Seans: ").append(sessions).append(", hafta toplamı: ").append(weeklyMinutes).append(" dk. ");
        if (openTasks > 0) sb.append(openTasks).append(" açık görev var; öncelikli görevden devam et.");
        else sb.append("Açık görev yok; yeni AI planı oluşturabilirsin.");
        return sb.toString();
    }

    public static String motivationLine(int todayMinutes, int targetMinutes, int sessions) {
        if (sessions <= 0) return "İlk seansı başlatmak bugünün en önemli adımı.";
        if (todayMinutes >= targetMinutes) return "Hedef tamamlandı. Bugünkü çalışma disiplinini korudun.";
        if (todayMinutes >= targetMinutes / 2) return "Yarıyı geçtin. Kısa bir seans daha hedefi yaklaştırır.";
        return "Küçük başla: 25 dakika bile günü toparlar.";
    }

    public static int priorityScore(int minutes, boolean done, String category, int daysToExam) {
        if (done) return -1000;
        int score = 100 - Math.min(90, minutes);
        if (daysToExam <= 30 && "Sınav".equals(category)) score += 30;
        if (daysToExam <= 30 && "Problem".equals(category)) score += 20;
        if ("Matematik".equals(category)) score += 10;
        if (minutes <= 15) score += 8;
        return score;
    }

    public static String priorityLabel(int minutes, boolean done, String category, int daysToExam) {
        int score = priorityScore(minutes, done, category, daysToExam);
        if (done) return "Tamamlandı";
        if (score >= 120) return "Çok öncelikli";
        if (score >= 100) return "Öncelikli";
        return "Normal";
    }

    public static final class PlanItem {
        public final String title;
        public final String category;
        public final int minutes;
        public final int priority;

        public PlanItem(String title, String category, int minutes) {
            this(title, category, minutes, 3);
        }

        public PlanItem(String title, String category, int minutes, int priority) {
            this.title = title;
            this.category = category;
            this.minutes = minutes;
            this.priority = priority;
        }
    }
}
