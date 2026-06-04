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
import android.view.ViewGroup;
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
    private static final String PINK = "#FFD6E7";

    private int focusDuration = 1500, seconds = 1500, sessions = 0, minutes = 0, target = 60, activeTab = 0;
    private int selectedTaskMinutes = 25;
    private boolean running = false, soundOn = true, strictMode = false;
    private String examName = "YKS", examDate = "2026-06-20", profile = "Misafir", musicMode = "Sessiz", focusCategory = "Matematik";
    private final int[] week = new int[7];
    private final int[] categoryMinutes = new int[5];
    private final List<FocusTask> tasks = new ArrayList<>();
    private final List<String> notes = new ArrayList<>();
    private final AmbientPlayer ambient = new AmbientPlayer();
    private SharedPreferences prefs;
    private LinearLayout content, navBar, taskBox;
    private TextView timer, status, weeklyText, examText, noteText, profileText, musicText, fullTimer, fullStatus, settingsText, aiText, reportText;
    private Button toggle, fullToggle;
    private final Handler handler = new Handler(Looper.getMainLooper());

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
                int earned = focusDuration / 60;
                sessions++;
                minutes += earned;
                week[today()] += earned;
                categoryMinutes[categoryIndex(focusCategory)] += earned;
                save();
                if (soundOn) beep();
                showCompleteDialog(earned);
                show(activeTab);
            }
        }
    };

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
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

        navBar = nav();
        root.addView(navBar, m(0, 10, 0, 32));
        setContentView(root);
        show(0);
    }

    private void show(int tab) {
        activeTab = tab;
        refreshNav();
        content.removeAllViews();
        if (tab == 0) home();
        if (tab == 1) focus();
        if (tab == 2) tasks();
        if (tab == 3) reports();
        if (tab == 4) aiCoach();
    }

    private void home() {
        content.addView(screenTitle("Pano", todayText()), m(0,0,0,14));
        content.addView(todayPanel(), m(0,0,0,14));
        content.addView(flowCard(), m(0,0,0,14));
        content.addView(leaderMiniCard(), m(0,0,0,14));
        content.addView(profileCard(), m(0,0,0,14));
        content.addView(settingsCard(), wrap());
        updateAll();
    }

    private void focus() {
        content.addView(screenTitle("Odak", "Pomodoro, kategori, ses ve katı mod"), m(0,0,0,14));
        content.addView(timerCard(), m(0,0,0,14));
        content.addView(categoryCard(), m(0,0,0,14));
        content.addView(musicCard(), m(0,0,0,14));
        content.addView(strictModeCard(), wrap());
        updateTimer();
        updateMusic();
    }

    private void tasks() {
        content.addView(screenTitle("Görevler", "Ders çalışma planını adım adım takip et"), m(0,0,0,14));
        content.addView(tasksCard(), wrap());
        renderTasks();
    }

    private void reports() {
        content.addView(screenTitle("Rapor", "Günlük, haftalık ve başarımlar"), m(0,0,0,14));
        content.addView(reportSummaryCard(), m(0,0,0,14));
        content.addView(weeklyChartCard(), m(0,0,0,14));
        content.addView(categoryChartCard(), m(0,0,0,14));
        content.addView(achievementsCard(), m(0,0,0,14));
        content.addView(leaderboardCard(), wrap());
    }

    private void aiCoach() {
        content.addView(screenTitle("AI Rehber", "Yerel koçluk: odak, görev ve sınav önerisi"), m(0,0,0,14));
        content.addView(aiCard(), m(0,0,0,14));
        content.addView(aiPlanCard(), m(0,0,0,14));
        content.addView(examCard(), m(0,0,0,14));
        content.addView(notesCard(), wrap());
        renderNotes();
        updateExam();
    }

    private LinearLayout screenTitle(String title, String sub) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(2), 0, dp(2), 0);
        box.addView(txt(sub, 15, SUB, false), wrap());
        box.addView(txt(title, 34, TEXT, true), wrap());
        return box;
    }

    private LinearLayout todayPanel() {
        LinearLayout c = whiteCard();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout top = row();
        LinearLayout texts = new LinearLayout(this); texts.setOrientation(LinearLayout.VERTICAL);
        texts.addView(txt("Bugün ne yapacağım?", 20, TEXT, true), wrap());
        texts.addView(txt("AI rehber, görev ve odak özetini kontrol et", 14, SUB, false), wrap());
        top.addView(texts, new LinearLayout.LayoutParams(0, -2, 1));
        Button ai = smallButton("AI Öner", LILAC);
        top.addView(ai, new LinearLayout.LayoutParams(dp(112), dp(48)));
        ai.setOnClickListener(v -> show(4));
        c.addView(top, m(0,0,0,14));

        LinearLayout r1 = row();
        r1.addView(summaryBox("☀", "Bugün", minutes + " dk", BLUE), wc());
        r1.addView(summaryBox("✓", "Seans", String.valueOf(sessions), YELLOW), wc());
        r1.addView(summaryBox("🎯", "Hedef", "%" + progress(), RED), wc());
        c.addView(r1, m(0,0,0,10));
        LinearLayout r2 = row();
        r2.addView(summaryWide("Görev", doneTaskCount() + " / " + tasks.size(), GREEN), wc());
        r2.addView(summaryWide(examName, daysToExam() + " gün", ORANGE), wc());
        c.addView(r2, wrap());
        return c;
    }

    private LinearLayout flowCard() {
        LinearLayout c = whiteCard();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout top = row();
        top.addView(txt("Bugünün akışı", 22, TEXT, true), new LinearLayout.LayoutParams(0, -2, 1));
        TextView count = pillText(tasks.size() + " görev", BLUE);
        top.addView(count, new LinearLayout.LayoutParams(dp(96), dp(42)));
        c.addView(top, m(0,0,0,12));
        if (tasks.isEmpty()) {
            c.addView(txt("Bugün görev yok. Görevler sekmesinden çalışma maddesi ekleyebilirsin.", 15, SUB, false), wrap());
        } else {
            int limit = Math.min(3, tasks.size());
            for (int i = 0; i < limit; i++) c.addView(taskPreview(tasks.get(i)), m(0,0,0,8));
            if (tasks.size() > 3) c.addView(txt("+" + (tasks.size() - 3) + " görev daha", 14, "#EF8A18", true), wrap());
        }
        return c;
    }

    private LinearLayout taskPreview(FocusTask t) {
        LinearLayout r = row();
        r.setGravity(Gravity.CENTER_VERTICAL);
        r.setPadding(dp(12), dp(10), dp(12), dp(10));
        r.setBackground(bg(t.done ? GREEN : "#F8FAFF", 18, "#E8ECF5"));
        TextView label = txt((t.done ? "✓ " : "○ ") + t.text, 15, TEXT, true);
        r.addView(label, new LinearLayout.LayoutParams(0, -2, 1));
        r.addView(pillText(t.minutes + " dk", t.color()), new LinearLayout.LayoutParams(dp(82), dp(36)));
        return r;
    }

    private LinearLayout leaderMiniCard() {
        LinearLayout c = whiteCard();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(txt("Motivasyon", 20, TEXT, true), wrap());
        TextView rank = txt("Bugünkü yerel sıralama: #" + localRank() + " • " + levelName(), 16, SUB, false);
        rank.setPadding(0, dp(8), 0, dp(10));
        c.addView(rank, wrap());
        LinearLayout row = row();
        row.addView(summaryWide("Rozet", badgeCount() + " / 8", YELLOW), wc());
        row.addView(summaryWide("Seri", activeDayCount() + " gün", GREEN), wc());
        c.addView(row, wrap());
        return c;
    }

    private LinearLayout timerCard(){
        LinearLayout c=whiteCard();
        c.setGravity(Gravity.CENTER);
        c.setPadding(dp(18),dp(22),dp(18),dp(22));
        c.addView(pillText("ODAK SAYACI",YELLOW),center());
        timer=txt(format(seconds),68,TEXT,true);
        timer.setGravity(Gravity.CENTER);
        timer.setPadding(0,dp(16),0,0);
        c.addView(timer,wrap());
        status=txt(running?"Odak seansı çalışıyor":"Hazır",16,SUB,false);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0,dp(8),0,dp(18));
        c.addView(status,wrap());
        LinearLayout modes=row();
        modes.addView(mode("25 dk",25),wc());
        modes.addView(mode("45 dk",45),wc());
        modes.addView(mode("60 dk",60),wc());
        c.addView(modes,m(0,0,0,12));
        toggle=colored(running?"Duraklat":"Başlat",GREEN2);
        c.addView(toggle,height(52));
        Button reset=soft("Sıfırla"), full=colored("Tam Ekran Odak",LILAC);
        c.addView(reset,h(50,0,8,0,0));
        c.addView(full,h(50,0,8,0,0));
        toggle.setOnClickListener(v->toggleTimer());
        reset.setOnClickListener(v->resetTimer());
        full.setOnClickListener(v->fullscreen());
        return c;
    }

    private LinearLayout categoryCard() {
        LinearLayout c = whiteCard();
        c.setPadding(dp(16),dp(16),dp(16),dp(16));
        c.addView(txt("Odak Kategorisi",20,TEXT,true), wrap());
        TextView selected = txt("Seçili: " + focusCategory, 14, SUB, false);
        selected.setPadding(0, dp(8), 0, dp(10));
        c.addView(selected, wrap());
        LinearLayout r1 = row();
        r1.addView(categoryButton("Matematik", GREEN2), wc());
        r1.addView(categoryButton("Problem", ORANGE), wc());
        c.addView(r1, m(0,0,0,8));
        LinearLayout r2 = row();
        r2.addView(categoryButton("Okuma", BLUE2), wc());
        r2.addView(categoryButton("Sınav", RED), wc());
        c.addView(r2, wrap());
        return c;
    }

    private LinearLayout strictModeCard() {
        LinearLayout c = whiteCard();
        c.setPadding(dp(16),dp(16),dp(16),dp(16));
        c.addView(txt("Katı Mod",20,TEXT,true), wrap());
        TextView info = txt(strictMode ? "Açık: Seans sırasında çıkmama, sessiz çalışma ve dikkat uyarıları gösterilir." : "Kapalı: Normal odak modu.", 14, SUB, false);
        info.setPadding(0, dp(8), 0, dp(10));
        c.addView(info, wrap());
        Button b = colored(strictMode ? "Katı Modu Kapat" : "Katı Modu Aç", strictMode ? RED : GREEN2);
        c.addView(b, height(48));
        b.setOnClickListener(v -> { strictMode = !strictMode; save(); show(1); });
        return c;
    }

    private LinearLayout musicCard(){
        LinearLayout c=whiteCard();
        c.setPadding(dp(16),dp(16),dp(16),dp(16));
        c.addView(txt("Arka Plan Sesi",20,TEXT,true),wrap());
        musicText=txt("",14,SUB,false);
        musicText.setPadding(0,dp(8),0,dp(10));
        c.addView(musicText,wrap());
        LinearLayout a=row(), b=row();
        a.addView(music("Sessiz"),wc());
        a.addView(music("Yağmur"),wc());
        a.addView(music("Kütüphane"),wc());
        b.addView(music("Beyaz Gürültü"),wc());
        b.addView(music("Lo-fi"),wc());
        b.addView(music("Doğa"),wc());
        c.addView(a,m(0,0,0,8));
        c.addView(b,wrap());
        return c;
    }

    private LinearLayout tasksCard(){
        LinearLayout c=whiteCard();
        c.setPadding(dp(16),dp(16),dp(16),dp(16));
        c.addView(txt("Bugünkü Görevler",20,TEXT,true),wrap());
        EditText in=input("Örn. 30 problem çöz");
        c.addView(in,h(46,0,12,0,8));
        LinearLayout durations = row();
        durations.addView(durationButton("15 dk",15), wc());
        durations.addView(durationButton("25 dk",25), wc());
        durations.addView(durationButton("45 dk",45), wc());
        c.addView(durations, m(0,0,0,8));
        Button add=colored("Görev Ekle",YELLOW2);
        c.addView(add,height(48));
        taskBox=new LinearLayout(this);
        taskBox.setOrientation(LinearLayout.VERTICAL);
        taskBox.setPadding(0,dp(12),0,0);
        c.addView(taskBox,wrap());
        Button clear=soft("Tamamlananları Temizle");
        c.addView(clear,h(46,0,10,0,0));
        add.setOnClickListener(v->{
            String x=in.getText().toString().trim();
            if(x.length()==0)return;
            tasks.add(new FocusTask(x,false,focusCategory, selectedTaskMinutes));
            in.setText(""); save(); renderTasks();
        });
        clear.setOnClickListener(v->{for(int i=tasks.size()-1;i>=0;i--) if(tasks.get(i).done) tasks.remove(i); save(); renderTasks();});
        return c;
    }

    private LinearLayout reportSummaryCard() {
        LinearLayout c = whiteCard();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(txt("Günlük Rapor", 22, TEXT, true), wrap());
        TextView big = txt(minutes + " dk", 42, TEXT, true);
        big.setGravity(Gravity.CENTER);
        c.addView(big, wrap());
        TextView sub = txt("Hedef: " + target + " dk • Başarı: %" + progress() + " • Seans: " + sessions, 15, SUB, false);
        sub.setGravity(Gravity.CENTER);
        c.addView(sub, m(0,4,0,14));
        c.addView(progressBar(progress(), GREEN2), height(14));
        return c;
    }

    private LinearLayout weeklyChartCard() {
        LinearLayout c = whiteCard();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(txt("Haftalık Grafik", 22, TEXT, true), wrap());
        String[] names={"Pzt","Sal","Çar","Per","Cum","Cmt","Paz"};
        int max = 1; for (int v: week) if(v>max) max=v;
        for(int i=0;i<7;i++) c.addView(chartRow(names[i], week[i], max, i==today()?YELLOW2:BLUE2), m(0,8,0,0));
        return c;
    }

    private LinearLayout categoryChartCard() {
        LinearLayout c = whiteCard();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(txt("Kategori Dağılımı", 22, TEXT, true), wrap());
        int total = 0; for(int v: categoryMinutes) total += v;
        String[] names={"Matematik","Problem","Okuma","Sınav","Diğer"};
        String[] colors={GREEN2,ORANGE,BLUE2,RED,LILAC};
        for(int i=0;i<names.length;i++) {
            int pct = total == 0 ? 0 : Math.round(categoryMinutes[i] * 100f / total);
            c.addView(chartRow(names[i] + " %" + pct, categoryMinutes[i], Math.max(1,total), colors[i]), m(0,8,0,0));
        }
        return c;
    }

    private LinearLayout achievementsCard() {
        LinearLayout c = whiteCard();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(txt("Başarımlar", 22, TEXT, true), wrap());
        c.addView(txt(badgeCount() + " / 8 başarı açıldı", 16, SUB, false), m(0,6,0,12));
        c.addView(achievement("🚀", "İlk Adım", "İlk seansı tamamla", sessions >= 1, YELLOW), m(0,0,0,8));
        c.addView(achievement("🔥", "Ateş Serisi", "3 gün çalışma izi bırak", activeDayCount() >= 3, ORANGE), m(0,0,0,8));
        c.addView(achievement("🏅", "Altın Odak", "200 dk toplam odak", minutes >= 200, YELLOW), m(0,0,0,8));
        c.addView(achievement("🎯", "Hedefçi", "Günlük hedefi tamamla", minutes >= target, GREEN), wrap());
        return c;
    }

    private LinearLayout leaderboardCard() {
        LinearLayout c = whiteCard();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(txt("Liderlik Tablosu", 22, TEXT, true), wrap());
        c.addView(leaderRow("#1", profile, minutes + " dk", YELLOW), m(0,10,0,8));
        c.addView(leaderRow("#2", "Hedef", target + " dk", GREEN), m(0,0,0,8));
        c.addView(leaderRow("#3", "Haftalık", weeklyTotal() + " dk", BLUE), wrap());
        return c;
    }

    private LinearLayout aiCard() {
        LinearLayout c = whiteCard();
        c.setPadding(dp(16),dp(16),dp(16),dp(16));
        c.addView(txt("Bugün neye odaklanmalı?", 22, TEXT, true), wrap());
        aiText = txt(aiAdvice(), 16, TEXT, false);
        aiText.setPadding(0, dp(10), 0, dp(12));
        c.addView(aiText, wrap());
        LinearLayout r = row();
        r.addView(summaryBox("📅", "Bugün", minutes + " dk", BLUE), wc());
        r.addView(summaryBox("⚠", "Eksik", openTaskCount() + " görev", RED), wc());
        r.addView(summaryBox("✓", "Hedef", "%" + progress(), YELLOW), wc());
        c.addView(r, m(0,0,0,12));
        Button refresh = colored("Kısa takip özeti oluştur", GREEN);
        c.addView(refresh, height(48));
        refresh.setOnClickListener(v -> { if(aiText != null) aiText.setText(aiAdvice()); });
        return c;
    }

    private LinearLayout aiPlanCard() {
        LinearLayout c = whiteCard();
        c.setPadding(dp(16),dp(16),dp(16),dp(16));
        c.addView(txt("AI Çalışma Planı", 22, TEXT, true), wrap());
        TextView plan = txt(aiPlan(), 15, SUB, false);
        plan.setPadding(0, dp(8), 0, dp(12));
        c.addView(plan, wrap());
        LinearLayout r = row();
        Button addPlan = colored("Planı Görevlere Ekle", YELLOW2);
        Button start = colored("25 dk Başlat", GREEN2);
        r.addView(addPlan, wc());
        r.addView(start, wc());
        c.addView(r, wrap());
        addPlan.setOnClickListener(v -> {
            tasks.add(new FocusTask("AI Plan: 10 dk konu özeti", false, "Matematik", 10));
            tasks.add(new FocusTask("AI Plan: 25 dk soru çözümü", false, "Problem", 25));
            tasks.add(new FocusTask("AI Plan: 5 dk yanlış analizi", false, "Sınav", 5));
            save(); show(2);
        });
        start.setOnClickListener(v -> { focusDuration = 25*60; seconds = focusDuration; focusCategory = "Matematik"; show(1); });
        return c;
    }

    private LinearLayout examCard(){
        LinearLayout c=whiteCard();
        c.setPadding(dp(16),dp(16),dp(16),dp(16));
        c.addView(txt("Sınav Sayacı",20,TEXT,true),wrap());
        c.addView(txt("Tarih formatı: 2026-06-20",12,SUB,false),wrap());
        EditText n=input("Sınav adı"); n.setText(examName); c.addView(n,h(46,0,10,0,8));
        EditText d=input("yyyy-MM-dd"); d.setText(examDate); c.addView(d,h(46,0,0,0,8));
        Button saveBtn=colored("Sınavı Kaydet",YELLOW2); c.addView(saveBtn,height(48));
        examText=txt("",16,SUB,false); examText.setPadding(0,dp(12),0,0); c.addView(examText,wrap());
        saveBtn.setOnClickListener(v->{examName=n.getText().toString().trim(); if(examName.length()==0) examName="Sınav"; examDate=d.getText().toString().trim(); save(); updateExam();});
        return c;
    }

    private LinearLayout notesCard(){
        LinearLayout c=whiteCard();
        c.setPadding(dp(16),dp(16),dp(16),dp(16));
        c.addView(txt("Koçluk Notları",20,TEXT,true),wrap());
        EditText in=input("Kısa not ekle"); c.addView(in,h(46,0,12,0,8));
        Button add=colored("Not Ekle",YELLOW2), clear=soft("Notları Temizle");
        c.addView(add,height(48)); c.addView(clear,h(46,0,8,0,0));
        noteText=txt("",14,SUB,false); noteText.setPadding(0,dp(12),0,0); c.addView(noteText,wrap());
        add.setOnClickListener(v->{String x=in.getText().toString().trim(); if(x.length()==0)return; notes.add(x); in.setText(""); save(); renderNotes();});
        clear.setOnClickListener(v->{notes.clear(); save(); renderNotes();});
        return c;
    }

    private LinearLayout settingsCard(){
        LinearLayout c=whiteCard();
        c.setPadding(dp(16),dp(16),dp(16),dp(16));
        c.addView(txt("Ayarlar",20,TEXT,true),wrap());
        settingsText=txt("",14,SUB,false); settingsText.setPadding(0,dp(8),0,dp(10)); c.addView(settingsText,wrap());
        Button sound=colored(soundOn?"Seans Sesi: Açık":"Seans Sesi: Kapalı",BLUE), reset=colored("Tüm Verileri Sıfırla",RED);
        c.addView(sound,height(46)); c.addView(reset,h(46,0,8,0,0));
        sound.setOnClickListener(v->{soundOn=!soundOn;save();show(0);});
        reset.setOnClickListener(v->{clearAll();show(0);});
        return c;
    }

    private void toggleTimer(){
        running=!running;
        if(running){
            ambient.play(musicMode);
            if(strictMode) showStrictReminder();
            if(toggle!=null)toggle.setText("Duraklat");
            if(status!=null)status.setText("Odak seansı çalışıyor");
            handler.post(tick);
        } else {
            ambient.stop();
            if(toggle!=null)toggle.setText("Devam Et");
            if(status!=null)status.setText("Duraklatıldı");
            handler.removeCallbacks(tick);
        }
        syncFull();
    }

    private void resetTimer(){ running=false; ambient.stop(); seconds=focusDuration; handler.removeCallbacks(tick); if(toggle!=null)toggle.setText("Başlat"); if(status!=null)status.setText("Hazır"); updateTimer(); }
    private Button mode(String label,int min){ Button b=soft(label); b.setOnClickListener(v->{ if(running)return; focusDuration=min*60; seconds=focusDuration; if(status!=null)status.setText(label+" modu seçildi"); updateTimer();}); return b; }
    private Button music(String label){ Button b=soft(label); b.setTextSize(9); b.setOnClickListener(v->{musicMode=label; if(running) ambient.play(musicMode); save(); updateMusic();}); return b; }
    private Button categoryButton(String label, String color){ Button b=colored(label.equals(focusCategory)?"✓ "+label:label, color); b.setOnClickListener(v->{focusCategory=label; save(); show(1);}); return b; }
    private Button durationButton(String label, int min){ Button b=colored(selectedTaskMinutes==min?"✓ "+label:label, selectedTaskMinutes==min?GREEN2:BLUE); b.setOnClickListener(v->{selectedTaskMinutes=min; show(2);}); return b; }

    private void fullscreen(){
        Dialog dlg=new Dialog(this); dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout c=whiteCard(); c.setOrientation(LinearLayout.VERTICAL); c.setGravity(Gravity.CENTER); c.setPadding(dp(22),dp(22),dp(22),dp(22));
        TextView brand=txt(focusCategory,26,TEXT,true); brand.setGravity(Gravity.CENTER); c.addView(brand,m(0,0,0,10));
        TextView prog=txt(minutes+" dk / "+target+" dk • %"+progress(),16,SUB,false); prog.setGravity(Gravity.CENTER); c.addView(prog,m(0,0,0,22));
        fullTimer=txt(format(seconds),84,TEXT,true); fullTimer.setGravity(Gravity.CENTER); c.addView(fullTimer,wrap());
        fullStatus=txt(running?"Odak seansı çalışıyor":"Başlamak için hazır",18,SUB,false); fullStatus.setGravity(Gravity.CENTER); fullStatus.setPadding(0,dp(12),0,dp(20)); c.addView(fullStatus,wrap());
        TextView ms=txt("Ses: "+musicMode+" • Katı mod: "+(strictMode?"Açık":"Kapalı"),15,SUB,false); ms.setGravity(Gravity.CENTER); c.addView(ms,m(0,0,0,18));
        fullToggle=colored(running?"Duraklat":"Başlat",GREEN2); c.addView(fullToggle,height(56));
        Button close=soft("Çık"); c.addView(close,h(56,0,12,0,0));
        fullToggle.setOnClickListener(v->toggleTimer()); close.setOnClickListener(v->dlg.dismiss());
        dlg.setContentView(c); dlg.show(); Window w=dlg.getWindow(); if(w!=null) w.setLayout(-1,-1);
    }

    private void showCompleteDialog(int earned){
        Dialog dlg=new Dialog(this); dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout c=whiteCard(); c.setPadding(dp(24),dp(24),dp(24),dp(24));
        TextView title=txt("Seans Tamamlandı",24,TEXT,true); title.setGravity(Gravity.CENTER); c.addView(title,wrap());
        TextView msg=txt(earned+" dk odak süresi eklendi. AI rehber raporunu güncelledi.",16,SUB,false); msg.setGravity(Gravity.CENTER); msg.setPadding(0,dp(12),0,dp(16)); c.addView(msg,wrap());
        Button ok=colored("Tamam",YELLOW2); c.addView(ok,height(52)); ok.setOnClickListener(v->dlg.dismiss());
        dlg.setContentView(c); dlg.show(); Window w=dlg.getWindow(); if(w!=null) w.setLayout(-1,-2);
    }

    private void showStrictReminder(){
        Dialog dlg=new Dialog(this); dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout c=whiteCard(); c.setPadding(dp(22),dp(22),dp(22),dp(22));
        TextView title=txt("Katı Mod Başladı",22,TEXT,true); title.setGravity(Gravity.CENTER); c.addView(title,wrap());
        TextView msg=txt("Telefonu çevirmeden, uygulamadan çıkmadan ve bildirimlere bakmadan seansı tamamlamaya çalış.",15,SUB,false); msg.setGravity(Gravity.CENTER); msg.setPadding(0,dp(10),0,dp(14)); c.addView(msg,wrap());
        Button ok=colored("Odaklan",GREEN2); c.addView(ok,height(48)); ok.setOnClickListener(v->dlg.dismiss());
        dlg.setContentView(c); dlg.show(); Window w=dlg.getWindow(); if(w!=null) w.setLayout(-1,-2);
    }

    private void renderTasks(){
        taskBox.removeAllViews();
        if(tasks.isEmpty()){taskBox.addView(txt("Henüz görev eklenmedi.",14,SUB,false),wrap()); return;}
        for(int i=0;i<tasks.size();i++){
            final int ix=i; FocusTask t=tasks.get(i);
            LinearLayout r=row(); r.setGravity(Gravity.CENTER_VERTICAL); r.setPadding(dp(10),dp(8),dp(10),dp(8)); r.setBackground(bg(t.done?GREEN:"#FFFFFF",16,"#E5E7EF"));
            TextView label=txt(t.text+"\n"+t.category+" • "+t.minutes+" dk",15,t.done?"#147A44":TEXT,false);
            if(t.done) label.setPaintFlags(label.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            r.addView(label,new LinearLayout.LayoutParams(0,-2,1));
            Button play=colored("▶",t.color()), done=colored(t.done?"Geri":"✓",t.done?LILAC:GREEN);
            r.addView(play,new LinearLayout.LayoutParams(dp(48),dp(42)));
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(dp(48),dp(42)); p.setMargins(dp(6),0,0,0); r.addView(done,p);
            play.setOnClickListener(v->{ focusCategory=tasks.get(ix).category; focusDuration=tasks.get(ix).minutes*60; seconds=focusDuration; show(1); });
            done.setOnClickListener(v->{tasks.get(ix).done=!tasks.get(ix).done; save(); renderTasks();});
            taskBox.addView(r,m(0,0,0,8));
        }
    }

    private void renderNotes(){ if(noteText==null)return; if(notes.isEmpty()){noteText.setText("Henüz koçluk notu yok.");return;} StringBuilder sb=new StringBuilder(); for(String n:notes) sb.append("• ").append(n).append("\n"); noteText.setText(sb.toString()); }
    private void updateAll(){ if(settingsText!=null)settingsText.setText("Hedef: "+target+" dk\nSeans sesi: "+(soundOn?"Açık":"Kapalı")+"\nKatı mod: "+(strictMode?"Açık":"Kapalı")); updateProfile(); updateMusic(); updateExam(); }
    private void updateTimer(){ if(timer!=null)timer.setText(format(seconds)); syncFull(); }
    private void syncFull(){ if(fullTimer!=null) fullTimer.setText(format(seconds)); if(fullStatus!=null) fullStatus.setText(running?"Odak seansı çalışıyor":"Duraklatıldı"); if(fullToggle!=null) fullToggle.setText(running?"Duraklat":"Devam Et"); }
    private void updateProfile(){ if(profileText!=null) profileText.setText("Merhaba, "+profile+". Bugünkü odak alanın hazır."); }
    private void updateMusic(){ if(musicText!=null) musicText.setText("Seçili ses: "+musicMode+"\nBaşlatınca seçili ses çalar; Sessiz seçilirse ses kapalıdır."); }
    private void updateExam(){ if(examText==null)return; examText.setText(examName+" için kalan süre: "+daysToExam()+" gün"); }
    private void beep(){ try{ ToneGenerator tg=new ToneGenerator(AudioManager.STREAM_NOTIFICATION,80); tg.startTone(ToneGenerator.TONE_PROP_ACK,300); }catch(Exception ignored){} }

    private String aiAdvice(){
        StringBuilder s = new StringBuilder();
        if (minutes == 0) s.append("İlk öncelik: 25 dakikalık kısa bir Matematik odak seansı başlat. ");
        else if (minutes < target) s.append("Hedefe ulaşmak için ").append(target-minutes).append(" dk daha çalışman yeterli. ");
        else s.append("Bugünkü hedef tamamlandı. Şimdi kısa tekrar ve yanlış analizi yap. ");
        if (openTaskCount() > 0) s.append("Açık görevlerden en kısa olanı seçip bitir. ");
        else s.append("Görev listen boş; 3 küçük görev ekle: konu özeti, soru çözümü, yanlış analizi. ");
        if (daysToExamInt() <= 30) s.append(examName).append(" yaklaşıyor; yeni konu yerine eksik kapatma ve deneme analizi öne alınmalı.");
        else s.append("Sınava zaman var; düzenli tekrar ve haftalık hedef yeterli.");
        return s.toString();
    }

    private String aiPlan(){ return "1) 10 dk: konu özeti oku\n2) 25 dk: seçili konudan soru çöz\n3) 5 dk: yanlışları not al\n4) 25 dk: ikinci odak seansı\n5) Gün sonunda rapor ve rozet kontrolü"; }
    private int categoryIndex(String c){ if(c.equals("Matematik"))return 0; if(c.equals("Problem"))return 1; if(c.equals("Okuma"))return 2; if(c.equals("Sınav"))return 3; return 4; }
    private int progress(){ return target==0?0:Math.min(100,Math.round(minutes*100f/target)); }
    private int doneTaskCount(){ int c=0; for(FocusTask t:tasks) if(t.done)c++; return c; }
    private int openTaskCount(){ return tasks.size()-doneTaskCount(); }
    private int weeklyTotal(){ int t=0; for(int v:week)t+=v; return t; }
    private int activeDayCount(){ int c=0; for(int v:week) if(v>0)c++; return c; }
    private int badgeCount(){ int c=0; if(sessions>=1)c++; if(sessions>=5)c++; if(minutes>=60)c++; if(minutes>=200)c++; if(progress()>=100)c++; if(activeDayCount()>=3)c++; if(tasks.size()>0)c++; if(doneTaskCount()>=3)c++; return c; }
    private int localRank(){ if(minutes>=target) return 1; if(minutes>=target/2) return 2; return 3; }
    private String levelName(){ if(minutes>=500)return "Usta"; if(minutes>=200)return "Altın I"; if(minutes>=60)return "Gümüş"; if(sessions>=1)return "Bronz"; return "Yeni Başlangıç"; }
    private int today(){ int d= Calendar.getInstance().get(Calendar.DAY_OF_WEEK); return d==Calendar.SUNDAY?6:d-2; }
    private String todayText(){ return new SimpleDateFormat("d MMMM, EEEE", new Locale("tr", "TR")).format(new Date()); }
    private int daysToExamInt(){ try{ Date d=new SimpleDateFormat("yyyy-MM-dd",Locale.US).parse(examDate); long diff=d.getTime()-System.currentTimeMillis(); return (int)Math.max(0,TimeUnit.MILLISECONDS.toDays(diff)); }catch(Exception e){ return 999; } }
    private String daysToExam(){ int d=daysToExamInt(); return d==999?"?":String.valueOf(d); }
    private String dayKey(){ return new SimpleDateFormat("yyyyMMdd",Locale.US).format(new Date()); }
    private String weekKey(){ Calendar c=Calendar.getInstance(); return c.get(Calendar.YEAR)+"-"+c.get(Calendar.WEEK_OF_YEAR); }
    private void applyDateReset(){ String d=dayKey(), w=weekKey(); String oldD=prefs.getString("last_day",d), oldW=prefs.getString("last_week",w); if(!d.equals(oldD)){ minutes=0; sessions=0; tasks.clear(); } if(!w.equals(oldW)){ for(int i=0;i<7;i++)week[i]=0; } prefs.edit().putString("last_day",d).putString("last_week",w).apply(); save(); }
    private void clearAll(){ running=false; ambient.stop(); handler.removeCallbacks(tick); sessions=0; minutes=0; target=60; seconds=focusDuration; profile="Misafir"; musicMode="Sessiz"; soundOn=true; strictMode=false; tasks.clear(); notes.clear(); for(int i=0;i<7;i++)week[i]=0; for(int i=0;i<5;i++)categoryMinutes[i]=0; save(); }

    private void load(){
        sessions=prefs.getInt("sessions",0); minutes=prefs.getInt("minutes",0); target=prefs.getInt("target",60); soundOn=prefs.getBoolean("sound",true); strictMode=prefs.getBoolean("strict",false);
        examName=prefs.getString("exam_name","YKS"); examDate=prefs.getString("exam_date","2026-06-20"); profile=prefs.getString("profile_name","Misafir"); musicMode=prefs.getString("music_mode","Sessiz"); focusCategory=prefs.getString("focus_category","Matematik");
        for(int i=0;i<7;i++)week[i]=prefs.getInt("week_"+i,0); for(int i=0;i<5;i++)categoryMinutes[i]=prefs.getInt("cat_"+i,0);
        String rt=prefs.getString("tasks",""); if(rt!=null) for(String row:rt.split("\\n")) if(row.trim().length()>0){ String[] p=row.split("\\|",4); if(p.length>=4) tasks.add(new FocusTask(p[1],"1".equals(p[0]),p[2],toInt(p[3],25))); else tasks.add(new FocusTask(row.length()>2?row.substring(2):"Görev",row.startsWith("1|"),"Matematik",25)); }
        String rn=prefs.getString("notes",""); if(rn!=null) for(String row:rn.split("\\n")) if(row.trim().length()>0) notes.add(row.trim());
    }

    private void save(){
        StringBuilder tb=new StringBuilder(), nb=new StringBuilder();
        for(FocusTask t:tasks) tb.append(t.done?"1":"0").append("|").append(clean(t.text)).append("|").append(clean(t.category)).append("|").append(t.minutes).append("\n");
        for(String n:notes) nb.append(clean(n)).append("\n");
        SharedPreferences.Editor e=prefs.edit().putInt("sessions",sessions).putInt("minutes",minutes).putInt("target",target).putBoolean("sound",soundOn).putBoolean("strict",strictMode).putString("exam_name",examName).putString("exam_date",examDate).putString("profile_name",profile).putString("music_mode",musicMode).putString("focus_category",focusCategory).putString("tasks",tb.toString()).putString("notes",nb.toString()).putString("last_day",dayKey()).putString("last_week",weekKey());
        for(int i=0;i<7;i++)e.putInt("week_"+i,week[i]); for(int i=0;i<5;i++)e.putInt("cat_"+i,categoryMinutes[i]); e.apply();
    }

    private int toInt(String s,int fallback){ try{return Integer.parseInt(s);}catch(Exception e){return fallback;} }
    private String clean(String v){ return v.replace("\n"," ").replace("|"," "); }
    private String format(int s){ return String.format(Locale.US,"%02d:%02d",s/60,s%60); }

    private LinearLayout nav(){ LinearLayout n=new LinearLayout(this); n.setOrientation(LinearLayout.HORIZONTAL); n.setPadding(dp(8),dp(7),dp(8),dp(7)); n.setBackground(bg("#F2FFFFFF",32,"#FFFFFF")); n.addView(tab("⌂\nPano",0,YELLOW2),wb()); n.addView(tab("◷\nOdak",1,BLUE2),wb()); n.addView(tab("✓\nGörev",2,GREEN2),wb()); n.addView(tab("▥\nRapor",3,ORANGE),wb()); n.addView(tab("✦\nAI",4,LILAC),wb()); return n; }
    private Button tab(String label,int index,String color){ Button b=activeTab==index?colored(label,color):navSoft(label); b.setTextSize(9); b.setAllCaps(false); b.setOnClickListener(v->show(index)); return b; }
    private void refreshNav(){ if(navBar==null)return; navBar.removeAllViews(); navBar.addView(tab("⌂\nPano",0,YELLOW2),wb()); navBar.addView(tab("◷\nOdak",1,BLUE2),wb()); navBar.addView(tab("✓\nGörev",2,GREEN2),wb()); navBar.addView(tab("▥\nRapor",3,ORANGE),wb()); navBar.addView(tab("✦\nAI",4,LILAC),wb()); }

    private LinearLayout summaryBox(String icon,String label,String value,String color){ LinearLayout b=new LinearLayout(this); b.setOrientation(LinearLayout.VERTICAL); b.setPadding(dp(12),dp(10),dp(12),dp(10)); b.setBackground(bg(color,20,"#FFFFFF")); b.addView(txt(icon,22,TEXT,true),wrap()); b.addView(txt(label,13,SUB,true),wrap()); b.addView(txt(value,20,TEXT,true),wrap()); return b; }
    private LinearLayout summaryWide(String label,String value,String color){ LinearLayout b=new LinearLayout(this); b.setOrientation(LinearLayout.VERTICAL); b.setPadding(dp(14),dp(12),dp(14),dp(12)); b.setBackground(bg(color,20,"#FFFFFF")); b.addView(txt(label,14,SUB,true),wrap()); b.addView(txt(value,21,TEXT,true),wrap()); return b; }
    private LinearLayout progressBar(int pct,String color){ LinearLayout outer=new LinearLayout(this); outer.setPadding(0,0,0,0); outer.setBackground(bg("#EDF1F7",12,"#EDF1F7")); TextView inner=new TextView(this); inner.setBackground(bg(color,12,color)); outer.addView(inner,new LinearLayout.LayoutParams(0,-1,Math.max(1,pct))); TextView rest=new TextView(this); outer.addView(rest,new LinearLayout.LayoutParams(0,-1,Math.max(1,100-pct))); return outer; }
    private LinearLayout chartRow(String label,int value,int max,String color){ LinearLayout r=new LinearLayout(this); r.setOrientation(LinearLayout.VERTICAL); TextView top=txt(label+"  " + value + " dk",14,TEXT,true); r.addView(top,wrap()); int pct=Math.min(100,Math.round(value*100f/max)); r.addView(progressBar(pct,color),h(12,0,4,0,0)); return r; }
    private LinearLayout achievement(String icon,String title,String desc,boolean done,String color){ LinearLayout r=row(); r.setGravity(Gravity.CENTER_VERTICAL); r.setPadding(dp(12),dp(10),dp(12),dp(10)); r.setBackground(bg(done?color:"#F8FAFF",18,"#E5E7EF")); r.addView(txt(icon,28,TEXT,true),new LinearLayout.LayoutParams(dp(48),-2)); LinearLayout texts=new LinearLayout(this); texts.setOrientation(LinearLayout.VERTICAL); texts.addView(txt(title,17,TEXT,true),wrap()); texts.addView(txt(desc,13,SUB,false),wrap()); r.addView(texts,new LinearLayout.LayoutParams(0,-2,1)); r.addView(txt(done?"✓":"○",24,done?"#0F9F6E":SUB,true),new LinearLayout.LayoutParams(dp(34),-2)); return r; }
    private LinearLayout leaderRow(String rank,String name,String score,String color){ LinearLayout r=row(); r.setGravity(Gravity.CENTER_VERTICAL); r.setPadding(dp(12),dp(12),dp(12),dp(12)); r.setBackground(bg(color,18,"#FFFFFF")); r.addView(txt(rank,20,TEXT,true),new LinearLayout.LayoutParams(dp(54),-2)); r.addView(txt(name,17,TEXT,true),new LinearLayout.LayoutParams(0,-2,1)); r.addView(txt(score,17,TEXT,true),new LinearLayout.LayoutParams(dp(90),-2)); return r; }

    private LinearLayout whiteCard(){ LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setBackground(bg("#FFFFFF",28,"#E5E7EF")); return c; }
    private LinearLayout row(){ LinearLayout r=new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL); return r; }
    private TextView txt(String v,int sp,String color,boolean bold){ TextView t=new TextView(this); t.setText(v); t.setTextSize(sp); t.setTextColor(Color.parseColor(color)); if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD); return t; }
    private TextView pillText(String v,String color){ TextView t=txt(v,14,TEXT,true); t.setGravity(Gravity.CENTER); t.setPadding(dp(12),dp(8),dp(12),dp(8)); t.setBackground(bg(color,24,"#FFFFFF")); return t; }
    private EditText input(String hint){ EditText e=new EditText(this); e.setHint(hint); e.setHintTextColor(Color.parseColor("#8A94A6")); e.setTextColor(Color.parseColor(TEXT)); e.setSingleLine(true); e.setBackground(bg("#FFFFFF",18,"#D9E3F4")); e.setPadding(dp(14),0,dp(14),0); return e; }
    private Button colored(String v,String color){ Button b=new Button(this); b.setText(v); b.setTextColor(Color.parseColor(TEXT)); b.setTextSize(12); b.setTypeface(Typeface.DEFAULT,Typeface.BOLD); b.setBackground(bg(color,18,"#FFFFFF")); return b; }
    private Button smallButton(String v,String color){ Button b=colored(v,color); b.setTextSize(11); return b; }
    private Button soft(String v){ Button b=new Button(this); b.setText(v); b.setTextColor(Color.parseColor(TEXT)); b.setTextSize(11); b.setTypeface(Typeface.DEFAULT,Typeface.BOLD); b.setBackground(bg("#FFFFFF",18,"#D9E3F4")); return b; }
    private Button navSoft(String v){ Button b=soft(v); b.setBackground(bg("#FFFFFF",20,"#E7EAF3")); return b; }
    private GradientDrawable bg(String fill,int radius,String stroke){ GradientDrawable g=new GradientDrawable(); g.setColor(Color.parseColor(fill)); g.setCornerRadius(dp(radius)); g.setStroke(dp(1),Color.parseColor(stroke)); return g; }
    private LinearLayout.LayoutParams wrap(){ return new LinearLayout.LayoutParams(-1,-2); }
    private LinearLayout.LayoutParams m(int l,int t,int r,int b){ LinearLayout.LayoutParams p=wrap(); p.setMargins(dp(l),dp(t),dp(r),dp(b)); return p; }
    private LinearLayout.LayoutParams height(int h){ return new LinearLayout.LayoutParams(-1,dp(h)); }
    private LinearLayout.LayoutParams h(int h,int l,int t,int r,int b){ LinearLayout.LayoutParams p=height(h); p.setMargins(dp(l),dp(t),dp(r),dp(b)); return p; }
    private LinearLayout.LayoutParams wb(){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(48),1); p.setMargins(dp(3),0,dp(3),0); return p; }
    private LinearLayout.LayoutParams wc(){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1); p.setMargins(dp(4),0,dp(4),0); return p; }
    private LinearLayout.LayoutParams center(){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-2,-2); p.gravity=Gravity.CENTER_HORIZONTAL; return p; }
    private int dp(int v){ return (int)(v*getResources().getDisplayMetrics().density+0.5f); }
    @Override protected void onDestroy(){ ambient.stop(); handler.removeCallbacks(tick); save(); super.onDestroy(); }

    private static class FocusTask{
        String text; boolean done; String category; int minutes;
        FocusTask(String t,boolean d,String c,int m){ text=t; done=d; category=c; minutes=m; }
        String color(){ if(category.equals("Matematik")) return GREEN; if(category.equals("Problem")) return ORANGE; if(category.equals("Okuma")) return BLUE; if(category.equals("Sınav")) return RED; return LILAC; }
    }
}
