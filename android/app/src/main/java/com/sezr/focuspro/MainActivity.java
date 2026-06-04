package com.sezr.focuspro;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    private static final String PREFS = "sezr_focus_pro_native";
    private static final String BG = "#F6F7FB";
    private static final String TEXT = "#171827";
    private static final String SUB = "#6B7280";
    private static final String BLUE = "#DDEBFF";
    private static final String BLUE2 = "#A7C7FF";
    private static final String YELLOW = "#FFF0B8";
    private static final String YELLOW2 = "#FFE17A";
    private static final String GREEN = "#DDF7EA";
    private static final String GREEN2 = "#A8E6CF";
    private static final String ORANGE = "#FFE2CA";
    private static final String LILAC = "#EEE4FF";
    private static final String RED = "#FFE1E1";

    private SharedPreferences prefs;
    private LinearLayout content, navBar, taskBox;
    private TextView timerText, statusText, profileText, musicText, examText, notesText, settingsText, fullTimer, fullStatus;
    private Button timerButton, fullButton;
    private final AmbientPlayer ambient = new AmbientPlayer();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private int activeTab = 0;
    private int focusDuration = 25 * 60;
    private int seconds = 25 * 60;
    private int minutes = 0;
    private int sessions = 0;
    private int target = 60;
    private int taskDuration = 25;
    private final int[] week = new int[7];
    private final int[] categories = new int[5];
    private boolean running = false;
    private boolean soundOn = true;
    private boolean strictMode = false;
    private String profile = "Misafir";
    private String focusCategory = "Matematik";
    private String musicMode = "Sessiz";
    private String examName = "YKS";
    private String examDate = "2026-06-20";
    private final List<FocusTask> tasks = new ArrayList<>();
    private final List<String> notes = new ArrayList<>();

    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (!running) return;
            if (seconds > 0) {
                seconds--;
                updateTimer();
                handler.postDelayed(this, 1000);
            } else {
                running = false;
                ambient.stop();
                int earned = Math.max(1, focusDuration / 60);
                minutes += earned;
                sessions++;
                week[todayIndex()] += earned;
                categories[categoryIndex(focusCategory)] += earned;
                save();
                if (soundOn) beep();
                showCompleteDialog(earned);
                show(activeTab);
            }
        }
    };

    @Override protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        load();
        applyDateReset();
        FocusNotificationHelper.ensureChannel(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(24), dp(18), dp(92));
        root.setBackgroundColor(Color.parseColor(BG));

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content, new ScrollView.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        navBar = navBar();
        root.addView(navBar, margins(0, 10, 0, 32));
        setContentView(root);
        show(0);
    }

    private void show(int tab) {
        activeTab = tab;
        refreshNav();
        content.removeAllViews();
        if (tab == 0) showHome();
        if (tab == 1) showFocus();
        if (tab == 2) showTasks();
        if (tab == 3) showReports();
        if (tab == 4) showAi();
    }

    private void showHome() {
        content.addView(title("Pano", todayText()), margins(0,0,0,14));
        content.addView(todayPanel(), margins(0,0,0,14));
        content.addView(flowCard(), margins(0,0,0,14));
        content.addView(motivationCard(), margins(0,0,0,14));
        content.addView(profileCard(), margins(0,0,0,14));
        content.addView(settingsCard(), wrap());
        updateAll();
    }

    private void showFocus() {
        content.addView(title("Odak", "Pomodoro, kategori, ses ve katı mod"), margins(0,0,0,14));
        content.addView(timerCard(), margins(0,0,0,14));
        content.addView(categoryCard(), margins(0,0,0,14));
        content.addView(musicCard(), margins(0,0,0,14));
        content.addView(strictCard(), wrap());
        updateTimer();
        updateMusic();
    }

    private void showTasks() {
        content.addView(title("Görevler", "Ders çalışma planını adım adım takip et"), margins(0,0,0,14));
        content.addView(tasksCard(), wrap());
        renderTasks();
    }

    private void showReports() {
        content.addView(title("Rapor", "Günlük, haftalık ve başarımlar"), margins(0,0,0,14));
        content.addView(reportCard(), margins(0,0,0,14));
        content.addView(weeklyCard(), margins(0,0,0,14));
        content.addView(categoryReportCard(), margins(0,0,0,14));
        content.addView(achievementsCard(), margins(0,0,0,14));
        content.addView(leaderboardCard(), wrap());
    }

    private void showAi() {
        content.addView(title("AI Rehber", "Yerel koçluk: odak, görev ve sınav önerisi"), margins(0,0,0,14));
        content.addView(aiCoachCard(), margins(0,0,0,14));
        content.addView(aiPlanCard(), margins(0,0,0,14));
        content.addView(examCard(), margins(0,0,0,14));
        content.addView(notesCard(), wrap());
        updateExam();
        renderNotes();
    }

    private LinearLayout title(String main, String sub) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.addView(text(sub, 15, SUB, false), wrap());
        box.addView(text(main, 34, TEXT, true), wrap());
        return box;
    }

    private LinearLayout todayPanel() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout top = row();
        LinearLayout left = column();
        left.addView(text("Bugün ne yapacağım?", 20, TEXT, true), wrap());
        left.addView(text("AI rehber, görev ve odak özetini kontrol et", 14, SUB, false), wrap());
        top.addView(left, new LinearLayout.LayoutParams(0, -2, 1));
        Button ai = button("AI Öner", LILAC);
        top.addView(ai, new LinearLayout.LayoutParams(dp(112), dp(48)));
        ai.setOnClickListener(v -> show(4));
        c.addView(top, margins(0,0,0,14));

        LinearLayout r1 = row();
        r1.addView(summaryBox("☀", "Bugün", minutes + " dk", BLUE), weightCell());
        r1.addView(summaryBox("✓", "Seans", String.valueOf(sessions), YELLOW), weightCell());
        r1.addView(summaryBox("🎯", "Hedef", "%" + progress(), RED), weightCell());
        c.addView(r1, margins(0,0,0,10));
        LinearLayout r2 = row();
        r2.addView(summaryWide("Görev", doneTaskCount() + " / " + tasks.size(), GREEN), weightCell());
        r2.addView(summaryWide(examName, daysToExam() + " gün", ORANGE), weightCell());
        c.addView(r2, wrap());
        return c;
    }

    private LinearLayout flowCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout top = row();
        top.addView(text("Bugünün akışı", 22, TEXT, true), new LinearLayout.LayoutParams(0, -2, 1));
        top.addView(pill(tasks.size() + " görev", BLUE), new LinearLayout.LayoutParams(dp(96), dp(42)));
        c.addView(top, margins(0,0,0,12));
        if (tasks.isEmpty()) {
            c.addView(text("Bugün görev yok. Görevler sekmesinden çalışma maddesi ekleyebilirsin.", 15, SUB, false), wrap());
        } else {
            int limit = Math.min(3, tasks.size());
            for (int i = 0; i < limit; i++) c.addView(taskPreview(tasks.get(i)), margins(0,0,0,8));
            if (tasks.size() > 3) c.addView(text("+" + (tasks.size() - 3) + " görev daha", 14, "#EF8A18", true), wrap());
        }
        return c;
    }

    private LinearLayout taskPreview(FocusTask task) {
        LinearLayout r = row();
        r.setGravity(Gravity.CENTER_VERTICAL);
        r.setPadding(dp(12), dp(10), dp(12), dp(10));
        r.setBackground(bg(task.done ? GREEN : "#F8FAFF", 18, "#E8ECF5"));
        r.addView(text((task.done ? "✓ " : "○ ") + task.text, 15, TEXT, true), new LinearLayout.LayoutParams(0, -2, 1));
        r.addView(pill(task.minutes + " dk", task.color()), new LinearLayout.LayoutParams(dp(82), dp(36)));
        return r;
    }

    private LinearLayout motivationCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("Motivasyon", 20, TEXT, true), wrap());
        c.addView(text("Yerel sıralama: #" + localRank() + " • Seviye: " + levelName(), 16, SUB, false), margins(0,8,0,10));
        LinearLayout r = row();
        r.addView(summaryWide("Rozet", badgeCount() + " / 8", YELLOW), weightCell());
        r.addView(summaryWide("Seri", activeDayCount() + " gün", GREEN), weightCell());
        c.addView(r, wrap());
        return c;
    }

    private LinearLayout profileCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("Profil / Giriş", 20, TEXT, true), wrap());
        profileText = text("", 15, SUB, false);
        profileText.setPadding(0, dp(8), 0, dp(10));
        c.addView(profileText, wrap());
        EditText input = input("Adını yaz veya misafir kal");
        if (!"Misafir".equals(profile)) input.setText(profile);
        c.addView(input, heightMargins(48, 0, 0, 0, 10));
        LinearLayout r = row();
        Button guest = softButton("Misafir");
        Button save = button("Kaydet", YELLOW2);
        r.addView(guest, weightCell());
        r.addView(save, weightCell());
        c.addView(r, wrap());
        guest.setOnClickListener(v -> { profile = "Misafir"; input.setText(""); save(); updateProfile(); });
        save.setOnClickListener(v -> { String s = input.getText().toString().trim(); profile = s.length() == 0 ? "Misafir" : s; save(); updateProfile(); });
        return c;
    }

    private LinearLayout timerCard() {
        LinearLayout c = card();
        c.setGravity(Gravity.CENTER);
        c.setPadding(dp(18), dp(22), dp(18), dp(22));
        c.addView(pill("ODAK SAYACI", YELLOW), center());
        timerText = text(format(seconds), 68, TEXT, true);
        timerText.setGravity(Gravity.CENTER);
        timerText.setPadding(0, dp(16), 0, 0);
        c.addView(timerText, wrap());
        statusText = text(running ? "Odak seansı çalışıyor" : "Hazır", 16, SUB, false);
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, dp(8), 0, dp(18));
        c.addView(statusText, wrap());
        LinearLayout modes = row();
        modes.addView(modeButton("25 dk", 25), weightCell());
        modes.addView(modeButton("45 dk", 45), weightCell());
        modes.addView(modeButton("60 dk", 60), weightCell());
        c.addView(modes, margins(0,0,0,12));
        timerButton = button(running ? "Duraklat" : "Başlat", GREEN2);
        c.addView(timerButton, height(52));
        Button reset = softButton("Sıfırla");
        Button full = button("Tam Ekran Odak", LILAC);
        c.addView(reset, heightMargins(50, 0, 8, 0, 0));
        c.addView(full, heightMargins(50, 0, 8, 0, 0));
        timerButton.setOnClickListener(v -> toggleTimer());
        reset.setOnClickListener(v -> resetTimer());
        full.setOnClickListener(v -> showFullscreen());
        return c;
    }

    private LinearLayout categoryCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("Odak Kategorisi", 20, TEXT, true), wrap());
        c.addView(text("Seçili: " + focusCategory, 14, SUB, false), margins(0,8,0,10));
        LinearLayout r1 = row();
        r1.addView(categoryButton("Matematik", GREEN2), weightCell());
        r1.addView(categoryButton("Problem", ORANGE), weightCell());
        c.addView(r1, margins(0,0,0,8));
        LinearLayout r2 = row();
        r2.addView(categoryButton("Okuma", BLUE2), weightCell());
        r2.addView(categoryButton("Sınav", RED), weightCell());
        c.addView(r2, wrap());
        return c;
    }

    private LinearLayout musicCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("Arka Plan Sesi", 20, TEXT, true), wrap());
        musicText = text("", 14, SUB, false);
        musicText.setPadding(0, dp(8), 0, dp(10));
        c.addView(musicText, wrap());
        LinearLayout r1 = row();
        r1.addView(musicButton("Sessiz"), weightCell());
        r1.addView(musicButton("Yağmur"), weightCell());
        r1.addView(musicButton("Kütüphane"), weightCell());
        c.addView(r1, margins(0,0,0,8));
        LinearLayout r2 = row();
        r2.addView(musicButton("Beyaz Gürültü"), weightCell());
        r2.addView(musicButton("Lo-fi"), weightCell());
        r2.addView(musicButton("Doğa"), weightCell());
        c.addView(r2, wrap());
        return c;
    }

    private LinearLayout strictCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("Katı Mod", 20, TEXT, true), wrap());
        c.addView(text(strictMode ? "Açık: seans sırasında dikkat uyarısı gösterilir." : "Kapalı: normal odak modu.", 14, SUB, false), margins(0,8,0,10));
        Button b = button(strictMode ? "Katı Modu Kapat" : "Katı Modu Aç", strictMode ? RED : GREEN2);
        c.addView(b, height(48));
        b.setOnClickListener(v -> { strictMode = !strictMode; save(); show(1); });
        return c;
    }

    private LinearLayout tasksCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("Bugünkü Görevler", 20, TEXT, true), wrap());
        EditText input = input("Örn. 30 problem çöz");
        c.addView(input, heightMargins(48, 0, 12, 0, 8));
        LinearLayout durs = row();
        durs.addView(durationButton("15 dk", 15), weightCell());
        durs.addView(durationButton("25 dk", 25), weightCell());
        durs.addView(durationButton("45 dk", 45), weightCell());
        c.addView(durs, margins(0,0,0,8));
        Button add = button("Görev Ekle", YELLOW2);
        c.addView(add, height(48));
        taskBox = column();
        taskBox.setPadding(0, dp(12), 0, 0);
        c.addView(taskBox, wrap());
        Button clear = softButton("Tamamlananları Temizle");
        c.addView(clear, heightMargins(46, 0, 10, 0, 0));
        add.setOnClickListener(v -> {
            String s = input.getText().toString().trim();
            if (s.length() == 0) return;
            tasks.add(new FocusTask(s, false, focusCategory, taskDuration));
            input.setText(""); save(); renderTasks();
        });
        clear.setOnClickListener(v -> { for (int i = tasks.size() - 1; i >= 0; i--) if (tasks.get(i).done) tasks.remove(i); save(); renderTasks(); });
        return c;
    }

    private LinearLayout reportCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("Günlük Rapor", 22, TEXT, true), wrap());
        TextView big = text(minutes + " dk", 42, TEXT, true);
        big.setGravity(Gravity.CENTER);
        c.addView(big, wrap());
        TextView sub = text("Hedef: " + target + " dk • Başarı: %" + progress() + " • Seans: " + sessions, 15, SUB, false);
        sub.setGravity(Gravity.CENTER);
        c.addView(sub, margins(0,4,0,14));
        c.addView(progressBar(progress(), GREEN2), height(14));
        return c;
    }

    private LinearLayout weeklyCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("Haftalık Grafik", 22, TEXT, true), wrap());
        String[] names = {"Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz"};
        int max = 1; for (int v : week) if (v > max) max = v;
        for (int i = 0; i < 7; i++) c.addView(chartRow(names[i], week[i], max, i == todayIndex() ? YELLOW2 : BLUE2), margins(0,8,0,0));
        return c;
    }

    private LinearLayout categoryReportCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("Kategori Dağılımı", 22, TEXT, true), wrap());
        String[] names = {"Matematik", "Problem", "Okuma", "Sınav", "Diğer"};
        String[] colors = {GREEN2, ORANGE, BLUE2, RED, LILAC};
        int total = 0; for (int v : categories) total += v;
        for (int i = 0; i < names.length; i++) {
            int pct = total == 0 ? 0 : Math.round(categories[i] * 100f / total);
            c.addView(chartRow(names[i] + " %" + pct, categories[i], Math.max(1, total), colors[i]), margins(0,8,0,0));
        }
        return c;
    }

    private LinearLayout achievementsCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("Başarımlar", 22, TEXT, true), wrap());
        c.addView(text(badgeCount() + " / 8 başarı açıldı", 16, SUB, false), margins(0,6,0,12));
        c.addView(achievement("🚀", "İlk Adım", "İlk seansı tamamla", sessions >= 1, YELLOW), margins(0,0,0,8));
        c.addView(achievement("🔥", "Ateş Serisi", "3 gün çalışma izi bırak", activeDayCount() >= 3, ORANGE), margins(0,0,0,8));
        c.addView(achievement("🏅", "Altın Odak", "200 dk toplam odak", minutes >= 200, YELLOW), margins(0,0,0,8));
        c.addView(achievement("🎯", "Hedefçi", "Günlük hedefi tamamla", minutes >= target, GREEN), wrap());
        return c;
    }

    private LinearLayout leaderboardCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("Liderlik Tablosu", 22, TEXT, true), wrap());
        c.addView(leaderRow("#1", profile, minutes + " dk", YELLOW), margins(0,10,0,8));
        c.addView(leaderRow("#2", "Hedef", target + " dk", GREEN), margins(0,0,0,8));
        c.addView(leaderRow("#3", "Haftalık", weeklyTotal() + " dk", BLUE), wrap());
        return c;
    }

    private LinearLayout aiCoachCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("Bugün neye odaklanmalı?", 22, TEXT, true), wrap());
        TextView advice = text(aiAdvice(), 16, TEXT, false);
        advice.setPadding(0, dp(10), 0, dp(12));
        c.addView(advice, wrap());
        LinearLayout r = row();
        r.addView(summaryBox("📅", "Bugün", minutes + " dk", BLUE), weightCell());
        r.addView(summaryBox("⚠", "Eksik", openTaskCount() + " görev", RED), weightCell());
        r.addView(summaryBox("✓", "Hedef", "%" + progress(), YELLOW), weightCell());
        c.addView(r, margins(0,0,0,12));
        Button refresh = button("Kısa takip özeti oluştur", GREEN);
        c.addView(refresh, height(48));
        refresh.setOnClickListener(v -> advice.setText(aiAdvice()));
        return c;
    }

    private LinearLayout aiPlanCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("AI Çalışma Planı", 22, TEXT, true), wrap());
        c.addView(text("1) 10 dk konu özeti\n2) 25 dk soru çözümü\n3) 5 dk yanlış analizi\n4) 25 dk tekrar seansı\n5) Gün sonu rapor kontrolü", 15, SUB, false), margins(0,8,0,12));
        LinearLayout r = row();
        Button add = button("Planı Görevlere Ekle", YELLOW2);
        Button start = button("25 dk Başlat", GREEN2);
        r.addView(add, weightCell());
        r.addView(start, weightCell());
        c.addView(r, wrap());
        add.setOnClickListener(v -> {
            tasks.add(new FocusTask("AI Plan: 10 dk konu özeti", false, "Matematik", 10));
            tasks.add(new FocusTask("AI Plan: 25 dk soru çözümü", false, "Problem", 25));
            tasks.add(new FocusTask("AI Plan: 5 dk yanlış analizi", false, "Sınav", 5));
            save(); show(2);
        });
        start.setOnClickListener(v -> { focusDuration = 25 * 60; seconds = focusDuration; focusCategory = "Matematik"; show(1); });
        return c;
    }

    private LinearLayout examCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("Sınav Sayacı", 20, TEXT, true), wrap());
        c.addView(text("Tarih formatı: 2026-06-20", 12, SUB, false), wrap());
        EditText n = input("Sınav adı"); n.setText(examName); c.addView(n, heightMargins(48,0,10,0,8));
        EditText d = input("yyyy-MM-dd"); d.setText(examDate); c.addView(d, heightMargins(48,0,0,0,8));
        Button saveExam = button("Sınavı Kaydet", YELLOW2);
        c.addView(saveExam, height(48));
        examText = text("", 16, SUB, false);
        examText.setPadding(0, dp(12), 0, 0);
        c.addView(examText, wrap());
        saveExam.setOnClickListener(v -> { examName = n.getText().toString().trim().length() == 0 ? "Sınav" : n.getText().toString().trim(); examDate = d.getText().toString().trim(); save(); updateExam(); });
        return c;
    }

    private LinearLayout notesCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("Koçluk Notları", 20, TEXT, true), wrap());
        EditText input = input("Kısa not ekle");
        c.addView(input, heightMargins(48,0,12,0,8));
        Button add = button("Not Ekle", YELLOW2);
        Button clear = softButton("Notları Temizle");
        c.addView(add, height(48));
        c.addView(clear, heightMargins(46,0,8,0,0));
        notesText = text("", 14, SUB, false);
        notesText.setPadding(0, dp(12), 0, 0);
        c.addView(notesText, wrap());
        add.setOnClickListener(v -> { String s = input.getText().toString().trim(); if (s.length() == 0) return; notes.add(s); input.setText(""); save(); renderNotes(); });
        clear.setOnClickListener(v -> { notes.clear(); save(); renderNotes(); });
        return c;
    }

    private LinearLayout settingsCard() {
        LinearLayout c = card();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(text("Ayarlar", 20, TEXT, true), wrap());
        settingsText = text("", 14, SUB, false);
        settingsText.setPadding(0, dp(8), 0, dp(10));
        c.addView(settingsText, wrap());
        Button sound = button(soundOn ? "Seans Sesi: Açık" : "Seans Sesi: Kapalı", BLUE);
        Button reset = button("Tüm Verileri Sıfırla", RED);
        c.addView(sound, height(46));
        c.addView(reset, heightMargins(46,0,8,0,0));
        sound.setOnClickListener(v -> { soundOn = !soundOn; save(); show(0); });
        reset.setOnClickListener(v -> { clearAll(); show(0); });
        return c;
    }

    private void toggleTimer() {
        running = !running;
        if (running) {
            ambient.play(musicMode);
            if (strictMode) strictDialog();
            if (timerButton != null) timerButton.setText("Duraklat");
            if (statusText != null) statusText.setText("Odak seansı çalışıyor");
            handler.post(tick);
        } else {
            ambient.stop();
            if (timerButton != null) timerButton.setText("Devam Et");
            if (statusText != null) statusText.setText("Duraklatıldı");
            handler.removeCallbacks(tick);
        }
        syncFull();
    }

    private void resetTimer() {
        running = false;
        ambient.stop();
        handler.removeCallbacks(tick);
        seconds = focusDuration;
        if (timerButton != null) timerButton.setText("Başlat");
        if (statusText != null) statusText.setText("Hazır");
        updateTimer();
    }

    private void showFullscreen() {
        Dialog dlg = new Dialog(this);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout c = card();
        c.setGravity(Gravity.CENTER);
        c.setPadding(dp(22), dp(22), dp(22), dp(22));
        TextView title = text(focusCategory, 26, TEXT, true);
        title.setGravity(Gravity.CENTER);
        c.addView(title, margins(0,0,0,10));
        TextView prog = text(minutes + " dk / " + target + " dk • %" + progress(), 16, SUB, false);
        prog.setGravity(Gravity.CENTER);
        c.addView(prog, margins(0,0,0,22));
        fullTimer = text(format(seconds), 84, TEXT, true);
        fullTimer.setGravity(Gravity.CENTER);
        c.addView(fullTimer, wrap());
        fullStatus = text(running ? "Odak seansı çalışıyor" : "Başlamak için hazır", 18, SUB, false);
        fullStatus.setGravity(Gravity.CENTER);
        fullStatus.setPadding(0, dp(12), 0, dp(20));
        c.addView(fullStatus, wrap());
        c.addView(text("Ses: " + musicMode + " • Katı mod: " + (strictMode ? "Açık" : "Kapalı"), 15, SUB, false), margins(0,0,0,18));
        fullButton = button(running ? "Duraklat" : "Başlat", GREEN2);
        Button close = softButton("Çık");
        c.addView(fullButton, height(56));
        c.addView(close, heightMargins(56,0,12,0,0));
        fullButton.setOnClickListener(v -> toggleTimer());
        close.setOnClickListener(v -> dlg.dismiss());
        dlg.setContentView(c);
        dlg.show();
        Window w = dlg.getWindow();
        if (w != null) w.setLayout(-1, -1);
    }

    private void showCompleteDialog(int earned) {
        Dialog dlg = simpleDialog("Seans Tamamlandı", earned + " dk odak süresi eklendi. AI rehber raporunu güncelledi.", "Tamam", YELLOW2);
        dlg.show();
    }

    private void strictDialog() {
        Dialog dlg = simpleDialog("Katı Mod Başladı", "Telefonu çevirmeden, uygulamadan çıkmadan ve bildirimlere bakmadan seansı tamamlamaya çalış.", "Odaklan", GREEN2);
        dlg.show();
    }

    private Dialog simpleDialog(String title, String message, String ok, String color) {
        Dialog dlg = new Dialog(this);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout c = card();
        c.setPadding(dp(24), dp(24), dp(24), dp(24));
        TextView t = text(title, 24, TEXT, true); t.setGravity(Gravity.CENTER); c.addView(t, wrap());
        TextView m = text(message, 16, SUB, false); m.setGravity(Gravity.CENTER); m.setPadding(0, dp(12), 0, dp(16)); c.addView(m, wrap());
        Button b = button(ok, color); c.addView(b, height(52));
        b.setOnClickListener(v -> dlg.dismiss());
        dlg.setContentView(c);
        return dlg;
    }

    private void renderTasks() {
        if (taskBox == null) return;
        taskBox.removeAllViews();
        if (tasks.isEmpty()) { taskBox.addView(text("Henüz görev eklenmedi.", 14, SUB, false), wrap()); return; }
        for (int i = 0; i < tasks.size(); i++) {
            final int index = i;
            FocusTask task = tasks.get(i);
            LinearLayout r = row();
            r.setGravity(Gravity.CENTER_VERTICAL);
            r.setPadding(dp(10), dp(8), dp(10), dp(8));
            r.setBackground(bg(task.done ? GREEN : "#FFFFFF", 16, "#E5E7EF"));
            TextView label = text(task.text + "\n" + task.category + " • " + task.minutes + " dk", 15, task.done ? "#147A44" : TEXT, false);
            if (task.done) label.setPaintFlags(label.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            r.addView(label, new LinearLayout.LayoutParams(0, -2, 1));
            Button play = button("▶", task.color());
            Button done = button(task.done ? "Geri" : "✓", task.done ? LILAC : GREEN);
            r.addView(play, new LinearLayout.LayoutParams(dp(50), dp(42)));
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dp(58), dp(42)); p.setMargins(dp(6),0,0,0); r.addView(done, p);
            play.setOnClickListener(v -> { focusCategory = tasks.get(index).category; focusDuration = tasks.get(index).minutes * 60; seconds = focusDuration; show(1); });
            done.setOnClickListener(v -> { tasks.get(index).done = !tasks.get(index).done; save(); renderTasks(); });
            taskBox.addView(r, margins(0,0,0,8));
        }
    }

    private void renderNotes() {
        if (notesText == null) return;
        if (notes.isEmpty()) { notesText.setText("Henüz koçluk notu yok."); return; }
        StringBuilder sb = new StringBuilder();
        for (String n : notes) sb.append("• ").append(n).append("\n");
        notesText.setText(sb.toString());
    }

    private void updateAll() {
        updateProfile();
        updateMusic();
        updateExam();
        if (settingsText != null) settingsText.setText("Hedef: " + target + " dk\nSeans sesi: " + (soundOn ? "Açık" : "Kapalı") + "\nKatı mod: " + (strictMode ? "Açık" : "Kapalı"));
    }

    private void updateTimer() {
        if (timerText != null) timerText.setText(format(seconds));
        syncFull();
    }

    private void syncFull() {
        if (fullTimer != null) fullTimer.setText(format(seconds));
        if (fullStatus != null) fullStatus.setText(running ? "Odak seansı çalışıyor" : "Duraklatıldı");
        if (fullButton != null) fullButton.setText(running ? "Duraklat" : "Devam Et");
    }

    private void updateProfile() { if (profileText != null) profileText.setText("Merhaba, " + profile + ". Bugünkü odak alanın hazır."); }
    private void updateMusic() { if (musicText != null) musicText.setText("Seçili ses: " + musicMode + "\nBaşlatınca seçili ses çalar; Sessiz seçilirse ses kapalıdır."); }
    private void updateExam() { if (examText != null) examText.setText(examName + " için kalan süre: " + daysToExam() + " gün"); }

    private Button modeButton(String label, int min) { Button b = softButton(label); b.setOnClickListener(v -> { if (running) return; focusDuration = min * 60; seconds = focusDuration; updateTimer(); if (statusText != null) statusText.setText(label + " modu seçildi"); }); return b; }
    private Button categoryButton(String label, String color) { Button b = button(label.equals(focusCategory) ? "✓ " + label : label, color); b.setOnClickListener(v -> { focusCategory = label; save(); show(1); }); return b; }
    private Button durationButton(String label, int min) { Button b = button(taskDuration == min ? "✓ " + label : label, taskDuration == min ? GREEN2 : BLUE); b.setOnClickListener(v -> { taskDuration = min; show(2); }); return b; }
    private Button musicButton(String label) { Button b = softButton(label); b.setTextSize(9); b.setOnClickListener(v -> { musicMode = label; if (running) ambient.play(musicMode); save(); updateMusic(); }); return b; }

    private String aiAdvice() {
        StringBuilder s = new StringBuilder();
        if (minutes == 0) s.append("İlk öncelik: 25 dakikalık kısa bir Matematik odak seansı başlat. ");
        else if (minutes < target) s.append("Hedefe ulaşmak için ").append(target - minutes).append(" dk daha çalışman yeterli. ");
        else s.append("Bugünkü hedef tamamlandı. Şimdi kısa tekrar ve yanlış analizi yap. ");
        if (openTaskCount() > 0) s.append("Açık görevlerden en kısa olanı seçip bitir. ");
        else s.append("Görev listen boş; konu özeti, soru çözümü ve yanlış analizi ekle. ");
        if (daysToExamInt() <= 30) s.append(examName).append(" yaklaşıyor; yeni konu yerine eksik kapatma ve deneme analizi öne alınmalı.");
        else s.append("Sınava zaman var; düzenli tekrar ve haftalık hedef yeterli.");
        return s.toString();
    }

    private int progress() { return target == 0 ? 0 : Math.min(100, Math.round(minutes * 100f / target)); }
    private int doneTaskCount() { int c = 0; for (FocusTask t : tasks) if (t.done) c++; return c; }
    private int openTaskCount() { return tasks.size() - doneTaskCount(); }
    private int weeklyTotal() { int t = 0; for (int v : week) t += v; return t; }
    private int activeDayCount() { int c = 0; for (int v : week) if (v > 0) c++; return c; }
    private int badgeCount() { int c = 0; if (sessions >= 1) c++; if (sessions >= 5) c++; if (minutes >= 60) c++; if (minutes >= 200) c++; if (progress() >= 100) c++; if (activeDayCount() >= 3) c++; if (tasks.size() > 0) c++; if (doneTaskCount() >= 3) c++; return c; }
    private int localRank() { if (minutes >= target) return 1; if (minutes >= target / 2) return 2; return 3; }
    private String levelName() { if (minutes >= 500) return "Usta"; if (minutes >= 200) return "Altın I"; if (minutes >= 60) return "Gümüş"; if (sessions >= 1) return "Bronz"; return "Yeni Başlangıç"; }
    private int categoryIndex(String c) { if ("Matematik".equals(c)) return 0; if ("Problem".equals(c)) return 1; if ("Okuma".equals(c)) return 2; if ("Sınav".equals(c)) return 3; return 4; }
    private int todayIndex() { int d = Calendar.getInstance().get(Calendar.DAY_OF_WEEK); return d == Calendar.SUNDAY ? 6 : d - 2; }
    private String todayText() { return new SimpleDateFormat("d MMMM, EEEE", new Locale("tr", "TR")).format(new Date()); }
    private int daysToExamInt() { try { Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(examDate); long diff = d.getTime() - System.currentTimeMillis(); return (int)Math.max(0, TimeUnit.MILLISECONDS.toDays(diff)); } catch (Exception e) { return 999; } }
    private String daysToExam() { int d = daysToExamInt(); return d == 999 ? "?" : String.valueOf(d); }
    private String dayKey() { return new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date()); }
    private String weekKey() { Calendar c = Calendar.getInstance(); return c.get(Calendar.YEAR) + "-" + c.get(Calendar.WEEK_OF_YEAR); }
    private String format(int s) { return String.format(Locale.US, "%02d:%02d", s / 60, s % 60); }
    private int toInt(String s, int fallback) { try { return Integer.parseInt(s); } catch (Exception e) { return fallback; } }
    private String clean(String s) { return s.replace("\n", " ").replace("|", " "); }
    private void beep() { try { ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80); tg.startTone(ToneGenerator.TONE_PROP_ACK, 300); } catch (Exception ignored) {} }

    private void applyDateReset() {
        String d = dayKey(), w = weekKey();
        String oldD = prefs.getString("last_day", d), oldW = prefs.getString("last_week", w);
        if (!d.equals(oldD)) { minutes = 0; sessions = 0; tasks.clear(); }
        if (!w.equals(oldW)) { for (int i = 0; i < 7; i++) week[i] = 0; }
        prefs.edit().putString("last_day", d).putString("last_week", w).apply();
        save();
    }

    private void load() {
        sessions = prefs.getInt("sessions", 0);
        minutes = prefs.getInt("minutes", 0);
        target = prefs.getInt("target", 60);
        soundOn = prefs.getBoolean("sound", true);
        strictMode = prefs.getBoolean("strict", false);
        profile = prefs.getString("profile_name", "Misafir");
        focusCategory = prefs.getString("focus_category", "Matematik");
        musicMode = prefs.getString("music_mode", "Sessiz");
        examName = prefs.getString("exam_name", "YKS");
        examDate = prefs.getString("exam_date", "2026-06-20");
        for (int i = 0; i < 7; i++) week[i] = prefs.getInt("week_" + i, 0);
        for (int i = 0; i < 5; i++) categories[i] = prefs.getInt("cat_" + i, 0);
        String savedTasks = prefs.getString("tasks", "");
        if (savedTasks != null) for (String line : savedTasks.split("\\n")) if (line.trim().length() > 0) {
            String[] p = line.split("\\|", 4);
            if (p.length >= 4) tasks.add(new FocusTask(p[1], "1".equals(p[0]), p[2], toInt(p[3], 25)));
        }
        String savedNotes = prefs.getString("notes", "");
        if (savedNotes != null) for (String line : savedNotes.split("\\n")) if (line.trim().length() > 0) notes.add(line.trim());
    }

    private void save() {
        StringBuilder tb = new StringBuilder();
        for (FocusTask t : tasks) tb.append(t.done ? "1" : "0").append("|").append(clean(t.text)).append("|").append(clean(t.category)).append("|").append(t.minutes).append("\n");
        StringBuilder nb = new StringBuilder();
        for (String n : notes) nb.append(clean(n)).append("\n");
        SharedPreferences.Editor e = prefs.edit()
            .putInt("sessions", sessions).putInt("minutes", minutes).putInt("target", target)
            .putBoolean("sound", soundOn).putBoolean("strict", strictMode)
            .putString("profile_name", profile).putString("focus_category", focusCategory).putString("music_mode", musicMode)
            .putString("exam_name", examName).putString("exam_date", examDate).putString("tasks", tb.toString()).putString("notes", nb.toString())
            .putString("last_day", dayKey()).putString("last_week", weekKey());
        for (int i = 0; i < 7; i++) e.putInt("week_" + i, week[i]);
        for (int i = 0; i < 5; i++) e.putInt("cat_" + i, categories[i]);
        e.apply();
    }

    private void clearAll() {
        running = false; ambient.stop(); handler.removeCallbacks(tick);
        minutes = 0; sessions = 0; target = 60; seconds = focusDuration; profile = "Misafir"; musicMode = "Sessiz"; strictMode = false;
        tasks.clear(); notes.clear();
        for (int i = 0; i < 7; i++) week[i] = 0;
        for (int i = 0; i < 5; i++) categories[i] = 0;
        save();
    }

    private LinearLayout navBar() {
        LinearLayout n = row();
        n.setPadding(dp(8), dp(7), dp(8), dp(7));
        n.setBackground(bg("#F2FFFFFF", 32, "#FFFFFF"));
        n.addView(tab("⌂\nPano", 0, YELLOW2), navCell());
        n.addView(tab("◷\nOdak", 1, BLUE2), navCell());
        n.addView(tab("✓\nGörev", 2, GREEN2), navCell());
        n.addView(tab("▥\nRapor", 3, ORANGE), navCell());
        n.addView(tab("✦\nAI", 4, LILAC), navCell());
        return n;
    }

    private void refreshNav() {
        if (navBar == null) return;
        navBar.removeAllViews();
        navBar.addView(tab("⌂\nPano", 0, YELLOW2), navCell());
        navBar.addView(tab("◷\nOdak", 1, BLUE2), navCell());
        navBar.addView(tab("✓\nGörev", 2, GREEN2), navCell());
        navBar.addView(tab("▥\nRapor", 3, ORANGE), navCell());
        navBar.addView(tab("✦\nAI", 4, LILAC), navCell());
    }

    private Button tab(String label, int index, String color) { Button b = activeTab == index ? button(label, color) : softButton(label); b.setTextSize(9); b.setAllCaps(false); b.setOnClickListener(v -> show(index)); return b; }
    private LinearLayout summaryBox(String icon, String label, String value, String color) { LinearLayout b = column(); b.setPadding(dp(12), dp(10), dp(12), dp(10)); b.setBackground(bg(color, 20, "#FFFFFF")); b.addView(text(icon, 22, TEXT, true), wrap()); b.addView(text(label, 13, SUB, true), wrap()); b.addView(text(value, 20, TEXT, true), wrap()); return b; }
    private LinearLayout summaryWide(String label, String value, String color) { LinearLayout b = column(); b.setPadding(dp(14), dp(12), dp(14), dp(12)); b.setBackground(bg(color, 20, "#FFFFFF")); b.addView(text(label, 14, SUB, true), wrap()); b.addView(text(value, 21, TEXT, true), wrap()); return b; }
    private LinearLayout progressBar(int percent, String color) { LinearLayout outer = row(); outer.setBackground(bg("#EDF1F7", 12, "#EDF1F7")); TextView in = new TextView(this); in.setBackground(bg(color, 12, color)); outer.addView(in, new LinearLayout.LayoutParams(0, -1, Math.max(1, percent))); TextView rest = new TextView(this); outer.addView(rest, new LinearLayout.LayoutParams(0, -1, Math.max(1, 100 - percent))); return outer; }
    private LinearLayout chartRow(String label, int value, int max, String color) { LinearLayout r = column(); r.addView(text(label + "  " + value + " dk", 14, TEXT, true), wrap()); int pct = Math.min(100, Math.round(value * 100f / max)); r.addView(progressBar(pct, color), heightMargins(12,0,4,0,0)); return r; }
    private LinearLayout achievement(String icon, String title, String desc, boolean done, String color) { LinearLayout r = row(); r.setGravity(Gravity.CENTER_VERTICAL); r.setPadding(dp(12), dp(10), dp(12), dp(10)); r.setBackground(bg(done ? color : "#F8FAFF", 18, "#E5E7EF")); r.addView(text(icon, 28, TEXT, true), new LinearLayout.LayoutParams(dp(48), -2)); LinearLayout t = column(); t.addView(text(title, 17, TEXT, true), wrap()); t.addView(text(desc, 13, SUB, false), wrap()); r.addView(t, new LinearLayout.LayoutParams(0, -2, 1)); r.addView(text(done ? "✓" : "○", 24, done ? "#0F9F6E" : SUB, true), new LinearLayout.LayoutParams(dp(34), -2)); return r; }
    private LinearLayout leaderRow(String rank, String name, String score, String color) { LinearLayout r = row(); r.setGravity(Gravity.CENTER_VERTICAL); r.setPadding(dp(12), dp(12), dp(12), dp(12)); r.setBackground(bg(color, 18, "#FFFFFF")); r.addView(text(rank, 20, TEXT, true), new LinearLayout.LayoutParams(dp(54), -2)); r.addView(text(name, 17, TEXT, true), new LinearLayout.LayoutParams(0, -2, 1)); r.addView(text(score, 17, TEXT, true), new LinearLayout.LayoutParams(dp(90), -2)); return r; }

    private LinearLayout card() { LinearLayout c = column(); c.setBackground(bg("#FFFFFF", 28, "#E5E7EF")); return c; }
    private LinearLayout column() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l; }
    private LinearLayout row() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); return l; }
    private TextView text(String value, int sp, String color, boolean bold) { TextView t = new TextView(this); t.setText(value); t.setTextSize(sp); t.setTextColor(Color.parseColor(color)); if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return t; }
    private TextView pill(String value, String color) { TextView t = text(value, 14, TEXT, true); t.setGravity(Gravity.CENTER); t.setPadding(dp(12), dp(8), dp(12), dp(8)); t.setBackground(bg(color, 24, "#FFFFFF")); return t; }
    private EditText input(String hint) { EditText e = new EditText(this); e.setHint(hint); e.setHintTextColor(Color.parseColor("#8A94A6")); e.setTextColor(Color.parseColor(TEXT)); e.setSingleLine(true); e.setBackground(bg("#FFFFFF", 18, "#D9E3F4")); e.setPadding(dp(14), 0, dp(14), 0); return e; }
    private Button button(String label, String color) { Button b = new Button(this); b.setText(label); b.setTextColor(Color.parseColor(TEXT)); b.setTextSize(12); b.setTypeface(Typeface.DEFAULT, Typeface.BOLD); b.setBackground(bg(color, 18, "#FFFFFF")); return b; }
    private Button softButton(String label) { Button b = button(label, "#FFFFFF"); b.setBackground(bg("#FFFFFF", 18, "#D9E3F4")); return b; }
    private GradientDrawable bg(String fill, int radius, String stroke) { GradientDrawable g = new GradientDrawable(); g.setColor(Color.parseColor(fill)); g.setCornerRadius(dp(radius)); g.setStroke(dp(1), Color.parseColor(stroke)); return g; }
    private LinearLayout.LayoutParams wrap() { return new LinearLayout.LayoutParams(-1, -2); }
    private LinearLayout.LayoutParams height(int h) { return new LinearLayout.LayoutParams(-1, dp(h)); }
    private LinearLayout.LayoutParams margins(int l, int t, int r, int b) { LinearLayout.LayoutParams p = wrap(); p.setMargins(dp(l), dp(t), dp(r), dp(b)); return p; }
    private LinearLayout.LayoutParams heightMargins(int h, int l, int t, int r, int b) { LinearLayout.LayoutParams p = height(h); p.setMargins(dp(l), dp(t), dp(r), dp(b)); return p; }
    private LinearLayout.LayoutParams weightCell() { LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, -2, 1); p.setMargins(dp(4), 0, dp(4), 0); return p; }
    private LinearLayout.LayoutParams navCell() { LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(48), 1); p.setMargins(dp(3), 0, dp(3), 0); return p; }
    private LinearLayout.LayoutParams center() { LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-2, -2); p.gravity = Gravity.CENTER_HORIZONTAL; return p; }
    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    @Override protected void onDestroy() {
        ambient.stop();
        handler.removeCallbacks(tick);
        save();
        super.onDestroy();
    }

    private static class FocusTask {
        String text; boolean done; String category; int minutes;
        FocusTask(String text, boolean done, String category, int minutes) { this.text = text; this.done = done; this.category = category; this.minutes = minutes; }
        String color() { if ("Matematik".equals(category)) return GREEN; if ("Problem".equals(category)) return ORANGE; if ("Okuma".equals(category)) return BLUE; if ("Sınav".equals(category)) return RED; return LILAC; }
    }
}
