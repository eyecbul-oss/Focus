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
            s.append("Açık görevlerden en kısa olanı seçip bitir. ");
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
            plan.add(new PlanItem("Son hafta deneme yanlışlarını analiz et", "Sınav", 25));
            plan.add(new PlanItem("En çok yanlış çıkan konudan 20 soru çöz", "Problem", 25));
            plan.add(new PlanItem("Kısa formül ve not tekrarı yap", "Matematik", 15));
            plan.add(new PlanItem(breakMinutes + " dk kontrollü mola", "Mola", breakMinutes));
            return plan;
        }
        if (daysToExam <= 30) {
            plan.add(new PlanItem("Deneme yanlışlarını incele", "Sınav", 25));
            plan.add(new PlanItem("Eksik konudan soru çöz", "Problem", 25));
            plan.add(new PlanItem("Yanlış defterine 5 madde yaz", "Sınav", 10));
            plan.add(new PlanItem(breakMinutes + " dk mola", "Mola", breakMinutes));
            return plan;
        }
        if (todayMinutes < targetMinutes / 2) {
            plan.add(new PlanItem("10 dk konu özeti çıkar", "Matematik", 10));
            plan.add(new PlanItem("25 dk soru çözümü yap", "Problem", 25));
            plan.add(new PlanItem("5 dk yanlış analizi yap", "Sınav", 5));
            plan.add(new PlanItem(breakMinutes + " dk mola", "Mola", breakMinutes));
            plan.add(new PlanItem("25 dk tekrar seansı yap", "Matematik", 25));
            return plan;
        }
        plan.add(new PlanItem("Kısa tekrar yap", "Matematik", 15));
        plan.add(new PlanItem("Zor soruları işaretle", "Problem", 15));
        plan.add(new PlanItem("Gün sonu rapor kontrolü yap", "Sınav", 5));
        return plan;
    }

    public static String planText(List<PlanItem> items) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            PlanItem item = items.get(i);
            sb.append(i + 1).append(") ")
              .append(item.minutes).append(" dk ")
              .append(item.title);
            if (i < items.size() - 1) sb.append("\n");
        }
        return sb.toString();
    }

    public static int priorityScore(int minutes, boolean done, String category, int daysToExam) {
        if (done) return -1000;
        int score = 100 - Math.min(90, minutes);
        if (daysToExam <= 30 && "Sınav".equals(category)) score += 30;
        if (daysToExam <= 30 && "Problem".equals(category)) score += 20;
        if ("Matematik".equals(category)) score += 10;
        return score;
    }

    public static final class PlanItem {
        public final String title;
        public final String category;
        public final int minutes;

        public PlanItem(String title, String category, int minutes) {
            this.title = title;
            this.category = category;
            this.minutes = minutes;
        }
    }
}
