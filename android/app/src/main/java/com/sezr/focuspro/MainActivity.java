package com.sezr.focuspro;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    private static final String PREFS = "sezr_focus_pro_native";
    private int focusDuration = 25 * 60;
    private int seconds = focusDuration;
    private boolean running = false;
    private int completedSessions = 0;
    private int completedMinutes = 0;
    private int dailyTarget = 60;
    private String examName = "YKS";
    private String examDate = "2026-06-20";
    private String profileName = "Misafir";
    private String musicMode = "Sessiz";
    private final int[] weekMinutes = new int[7];
    private final List<FocusTask> tasks = new ArrayList<>();
    private final List<String> notes = new ArrayList<>();
    private SharedPreferences prefs;

    private TextView timerText, statusText, statMinutes, statSessions, targetText, targetPercentText;
    private TextView examInfoText, noteListText, weeklyText, profileText, musicText;
    private TextView fullscreenTimerText, fullscreenStatusText;
    private LinearLayout taskContainer;
    private Button toggleButton, fullscreenToggleButton;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            if (!running) return;
            if (seconds > 0) {
                seconds--;
                updateTimer();
                handler.postDelayed(this, 1000);
            } else {
                running = false;
                int earned = focusDuration / 60;
                completedSessions++;
                completedMinutes += earned;
                weekMinutes[todayIndex()] += earned;
                toggleButton.setText("Başlat");
                statusText.setText("Seans tamamlandı. Kısa bir mola ver.");
                saveState();
                updateStats();
            }
        }
    };

    private final Runnable examTicker = new Runnable() {
        @Override public void run() {
            updateExamCountdown();
            handler.postDelayed(this, 60000);
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        loadState();

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.parseColor("#020617"));
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(26), dp(18), dp(26));
        scroll.addView(root, new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        root.addView(buildHeader(), margin(0, 0, 0, 14));
        root.addView(buildProfileCard(), margin(0, 0, 0, 14));
        root.addView(buildTimerCard(), margin(0, 0, 0, 14));
        root.addView(buildStatsRow(), margin(0, 0, 0, 14));
        root.addView(buildTargetCard(), margin(0, 0, 0, 14));
        root.addView(buildWeeklyCard(), margin(0, 0, 0, 14));
        root.addView(buildMusicCard(), margin(0, 0, 0, 14));
        root.addView(buildTaskCard(), margin(0, 0, 0, 14));
        root.addView(buildExamCard(), margin(0, 0, 0, 14));
        root.addView(buildNotesCard(), wrap());

        updateTimer();
        updateStats();
        renderTasks();
        renderNotes();
        updateExamCountdown();
        updateProfileText();
        updateMusicText();
        handler.post(examTicker);
        setContentView(scroll);
    }

    private LinearLayout buildHeader() {
        LinearLayout header = card();
        header.setGravity(Gravity.CENTER_HORIZONTAL);
        header.setPadding(dp(18), dp(18), dp(18), dp(18));
        TextView title = text("SezR Focus Pro", 32, "#facc15", true);
        title.setGravity(Gravity.CENTER);
        header.addView(title, wrap());
        TextView sub = text("Bağımsız Android odak uygulaması", 15, "#cbd5e1", false);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0, dp(8), 0, 0);
        header.addView(sub, wrap());
        return header;
    }

    private LinearLayout buildProfileCard() {
        LinearLayout box = card();
        box.setPadding(dp(18), dp(18), dp(18), dp(18));
        box.addView(text("Profil / Giriş", 22, "#facc15", true), wrap());
        profileText = text("", 16, "#dbeafe", false);
        profileText.setPadding(0, dp(8), 0, dp(12));
        box.addView(profileText, wrap());
        EditText nameInput = input("Adını yaz veya misafir kal");
        nameInput.setText("Misafir".equals(profileName) ? "" : profileName);
        box.addView(nameInput, heightMargin(52, 0, 0, 0, 10));
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        Button guest = darkButton("Misafir");
        Button save = primaryButton("Kaydet");
        row.addView(guest, weightButton());
        row.addView(save, weightButton());
        box.addView(row, wrap());
        guest.setOnClickListener(v -> { profileName = "Misafir"; nameInput.setText(""); saveState(); updateProfileText(); });
        save.setOnClickListener(v -> { String value = nameInput.getText().toString().trim(); profileName = value.length() == 0 ? "Misafir" : value; saveState(); updateProfileText(); });
        return box;
    }

    private LinearLayout buildTimerCard() {
        LinearLayout box = card();
        box.setGravity(Gravity.CENTER_HORIZONTAL);
        box.setPadding(dp(18), dp(22), dp(18), dp(22));
        box.addView(pill("ODAK SAYACI"), centerWrap());
        timerText = text(format(seconds), 64, "#ffffff", true);
        timerText.setGravity(Gravity.CENTER);
        timerText.setPadding(0, dp(18), 0, 0);
        box.addView(timerText, wrap());
        statusText = text("Hazır", 16, "#cbd5e1", false);
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, dp(8), 0, dp(18));
        box.addView(statusText, wrap());
        LinearLayout modes = new LinearLayout(this);
        modes.setOrientation(LinearLayout.HORIZONTAL);
        modes.addView(modeButton("25 dk", 25), weightButton());
        modes.addView(modeButton("45 dk", 45), weightButton());
        modes.addView(modeButton("60 dk", 60), weightButton());
        box.addView(modes, margin(0, 0, 0, 14));
        toggleButton = primaryButton("Başlat");
        box.addView(toggleButton, height(54));
        Button reset = darkButton("Sıfırla");
        box.addView(reset, heightMargin(54, 0, 10, 0, 0));
        Button full = darkButton("Tam Ekran Odak Modu");
        box.addView(full, heightMargin(54, 0, 10, 0, 0));
        toggleButton.setOnClickListener(v -> toggleTimer());
        reset.setOnClickListener(v -> resetTimer());
        full.setOnClickListener(v -> showFullscreenFocus());
        return box;
    }

    private LinearLayout buildStatsRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        statMinutes = statCard("0 dk", "Bugünkü çalışma");
        statSessions = statCard("0", "Tamamlanan seans");
        row.addView((LinearLayout) statMinutes.getParent(), weightCard());
        row.addView((LinearLayout) statSessions.getParent(), weightCard());
        return row;
    }

    private LinearLayout buildTargetCard() {
        LinearLayout box = card();
        box.setPadding(dp(18), dp(18), dp(18), dp(18));
        box.addView(text("Günlük Hedef", 22, "#facc15", true), wrap());
        targetText = text("", 16, "#dbeafe", false);
        targetText.setPadding(0, dp(8), 0, dp(8));
        box.addView(targetText, wrap());
        targetPercentText = text("%0", 34, "#ffffff", true);
        box.addView(targetPercentText, wrap());
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(12), 0, 0);
        row.addView(targetButton("30 dk", 30), weightButton());
        row.addView(targetButton("60 dk", 60), weightButton());
        row.addView(targetButton("90 dk", 90), weightButton());
        box.addView(row, wrap());
        return box;
    }

    private LinearLayout buildWeeklyCard() {
        LinearLayout box = card();
        box.setPadding(dp(18), dp(18), dp(18), dp(18));
        box.addView(text("Haftalık Özet", 22, "#facc15", true), wrap());
        weeklyText = text("", 15, "#dbeafe", false);
        weeklyText.setPadding(0, dp(10), 0, 0);
        box.addView(weeklyText, wrap());
        return box;
    }

    private LinearLayout buildMusicCard() {
        LinearLayout box = card();
        box.setPadding(dp(18), dp(18), dp(18), dp(18));
        box.addView(text("Müzik / Odak Atmosferi", 22, "#facc15", true), wrap());
        musicText = text("", 15, "#dbeafe", false);
        musicText.setPadding(0, dp(8), 0, dp(12));
        box.addView(musicText, wrap());
        LinearLayout row1 = new LinearLayout(this); row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.addView(musicButton("Sessiz"), weightButton());
        row1.addView(musicButton("Yağmur"), weightButton());
        row1.addView(musicButton("Kütüphane"), weightButton());
        box.addView(row1, margin(0, 0, 0, 8));
        LinearLayout row2 = new LinearLayout(this); row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.addView(musicButton("Beyaz Gürültü"), weightButton());
        row2.addView(musicButton("Lo-fi"), weightButton());
        row2.addView(musicButton("Doğa"), weightButton());
        box.addView(row2, wrap());
        return box;
    }

    private LinearLayout buildTaskCard() {
        LinearLayout box = card();
        box.setPadding(dp(18), dp(18), dp(18), dp(18));
        box.addView(text("Bugünkü görevler", 22, "#facc15", true), wrap());
        EditText taskInput = input("Örn. 30 problem çöz");
        box.addView(taskInput, heightMargin(52, 0, 14, 0, 10));
        Button add = primaryButton("Görev Ekle");
        box.addView(add, height(52));
        taskContainer = new LinearLayout(this);
        taskContainer.setOrientation(LinearLayout.VERTICAL);
        taskContainer.setPadding(0, dp(14), 0, 0);
        box.addView(taskContainer, wrap());
        Button clear = darkButton("Tamamlananları Temizle");
        box.addView(clear, heightMargin(52, 0, 12, 0, 0));
        add.setOnClickListener(v -> { String value = taskInput.getText().toString().trim(); if (value.length() == 0) return; tasks.add(new FocusTask(value, false)); taskInput.setText(""); saveState(); renderTasks(); });
        clear.setOnClickListener(v -> { for (int i = tasks.size() - 1; i >= 0; i--) if (tasks.get(i).done) tasks.remove(i); saveState(); renderTasks(); });
        return box;
    }

    private LinearLayout buildExamCard() {
        LinearLayout box = card();
        box.setPadding(dp(18), dp(18), dp(18), dp(18));
        box.addView(text("Sınav Sayacı", 22, "#facc15", true), wrap());
        TextView helper = text("Tarih formatı: 2026-06-20", 13, "#94a3b8", false);
        helper.setPadding(0, dp(6), 0, dp(12));
        box.addView(helper, wrap());
        EditText nameInput = input("Sınav adı: YKS / LGS / KPSS");
        nameInput.setText(examName);
        box.addView(nameInput, heightMargin(52, 0, 0, 0, 10));
        EditText dateInput = input("Tarih: yyyy-MM-dd");
        dateInput.setText(examDate);
        box.addView(dateInput, heightMargin(52, 0, 0, 0, 10));
        Button save = primaryButton("Sınavı Kaydet");
        box.addView(save, height(52));
        examInfoText = text("", 17, "#dbeafe", false);
        examInfoText.setPadding(0, dp(14), 0, 0);
        box.addView(examInfoText, wrap());
        save.setOnClickListener(v -> { examName = nameInput.getText().toString().trim().length() == 0 ? "Sınav" : nameInput.getText().toString().trim(); examDate = dateInput.getText().toString().trim(); saveState(); updateExamCountdown(); });
        return box;
    }

    private LinearLayout buildNotesCard() {
        LinearLayout box = card();
        box.setPadding(dp(18), dp(18), dp(18), dp(18));
        box.addView(text("Notlar", 22, "#facc15", true), wrap());
        EditText noteInput = input("Kısa not ekle");
        box.addView(noteInput, heightMargin(52, 0, 14, 0, 10));
        Button add = primaryButton("Not Ekle");
        box.addView(add, height(52));
        Button clear = darkButton("Notları Temizle");
        box.addView(clear, heightMargin(52, 0, 10, 0, 0));
        noteListText = text("", 15, "#cbd5e1", false);
        noteListText.setPadding(0, dp(14), 0, 0);
        box.addView(noteListText, wrap());
        add.setOnClickListener(v -> { String value = noteInput.getText().toString().trim(); if (value.length() == 0) return; notes.add(value); noteInput.setText(""); saveState(); renderNotes(); });
        clear.setOnClickListener(v -> { notes.clear(); saveState(); renderNotes(); });
        return box;
    }

    private void toggleTimer() {
        running = !running;
        if (running) { toggleButton.setText("Duraklat"); statusText.setText("Odak seansı çalışıyor"); handler.post(ticker); }
        else { toggleButton.setText("Devam Et"); statusText.setText("Duraklatıldı"); handler.removeCallbacks(ticker); }
        syncFullscreenText();
    }

    private void resetTimer() {
        running = false; seconds = focusDuration; handler.removeCallbacks(ticker); toggleButton.setText("Başlat"); statusText.setText("Hazır"); updateTimer();
    }

    private Button modeButton(String label, int minutes) {
        Button b = darkButton(label);
        b.setOnClickListener(v -> { if (running) return; focusDuration = minutes * 60; seconds = focusDuration; statusText.setText(label + " modu seçildi"); updateTimer(); });
        return b;
    }

    private Button targetButton(String label, int minutes) {
        Button b = darkButton(label);
        b.setOnClickListener(v -> { dailyTarget = minutes; saveState(); updateStats(); });
        return b;
    }

    private Button musicButton(String label) {
        Button b = darkButton(label);
        b.setTextSize(12);
        b.setOnClickListener(v -> { musicMode = label; saveState(); updateMusicText(); });
        return b;
    }

    private void showFullscreenFocus() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(dp(22), dp(22), dp(22), dp(22));
        box.setBackgroundColor(Color.parseColor("#020617"));
        TextView brand = text("SezR Focus Pro", 26, "#facc15", true);
        brand.setGravity(Gravity.CENTER);
        box.addView(brand, margin(0, 0, 0, 28));
        fullscreenTimerText = text(format(seconds), 84, "#ffffff", true);
        fullscreenTimerText.setGravity(Gravity.CENTER);
        box.addView(fullscreenTimerText, wrap());
        fullscreenStatusText = text(running ? "Odak seansı çalışıyor" : "Başlamak için hazır", 18, "#cbd5e1", false);
        fullscreenStatusText.setGravity(Gravity.CENTER);
        fullscreenStatusText.setPadding(0, dp(12), 0, dp(28));
        box.addView(fullscreenStatusText, wrap());
        TextView music = text("Atmosfer: " + musicMode, 15, "#94a3b8", false);
        music.setGravity(Gravity.CENTER);
        music.setPadding(0, 0, 0, dp(18));
        box.addView(music, wrap());
        fullscreenToggleButton = primaryButton(running ? "Duraklat" : "Başlat");
        box.addView(fullscreenToggleButton, height(56));
        Button close = darkButton("Çık");
        box.addView(close, heightMargin(56, 0, 12, 0, 0));
        fullscreenToggleButton.setOnClickListener(v -> toggleTimer());
        close.setOnClickListener(v -> dialog.dismiss());
        dialog.setContentView(box);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void syncFullscreenText() {
        if (fullscreenTimerText != null) fullscreenTimerText.setText(format(seconds));
        if (fullscreenStatusText != null) fullscreenStatusText.setText(running ? "Odak seansı çalışıyor" : "Duraklatıldı");
        if (fullscreenToggleButton != null) fullscreenToggleButton.setText(running ? "Duraklat" : "Devam Et");
    }

    private void renderTasks() {
        taskContainer.removeAllViews();
        if (tasks.isEmpty()) { taskContainer.addView(text("Henüz görev eklenmedi.", 15, "#cbd5e1", false), wrap()); return; }
        for (int i = 0; i < tasks.size(); i++) {
            final int index = i;
            FocusTask task = tasks.get(i);
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(10), dp(8), dp(10), dp(8));
            row.setBackground(panelBg(task.done ? "#10251a" : "#0f172a", dp(14), task.done ? "#1f6f3a" : "#263244"));
            TextView label = text(task.text, 15, task.done ? "#86efac" : "#e5e7eb", false);
            if (task.done) label.setPaintFlags(label.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            row.addView(label, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            Button done = darkButton(task.done ? "Geri" : "Tamam"); done.setTextSize(12); row.addView(done, new LinearLayout.LayoutParams(dp(82), dp(42)));
            Button del = darkButton("Sil"); del.setTextSize(12); LinearLayout.LayoutParams delParams = new LinearLayout.LayoutParams(dp(58), dp(42)); delParams.setMargins(dp(6), 0, 0, 0); row.addView(del, delParams);
            done.setOnClickListener(v -> { tasks.get(index).done = !tasks.get(index).done; saveState(); renderTasks(); });
            del.setOnClickListener(v -> { tasks.remove(index); saveState(); renderTasks(); });
            taskContainer.addView(row, margin(0, 0, 0, 8));
        }
    }

    private void renderNotes() {
        if (notes.isEmpty()) { noteListText.setText("Henüz not eklenmedi."); return; }
        StringBuilder sb = new StringBuilder(); for (String n : notes) sb.append("• ").append(n).append("\n"); noteListText.setText(sb.toString());
    }

    private void updateProfileText() {
        if (profileText != null) profileText.setText("Merhaba, " + profileName + ". Bugünkü odak alanın hazır.");
    }

    private void updateMusicText() {
        if (musicText != null) musicText.setText("Seçili atmosfer: " + musicMode + "\nSes dosyası desteği sonraki sürümde eklenecek; bu panel odak modu tercihini kaydeder.");
    }

    private void updateExamCountdown() {
        if (examInfoText == null) return;
        try {
            Date target = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(examDate);
            if (target == null) throw new ParseException("empty", 0);
            long diff = target.getTime() - System.currentTimeMillis();
            if (diff <= 0) { examInfoText.setText(examName + " tarihi geçti veya bugün."); return; }
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            long hours = TimeUnit.MILLISECONDS.toHours(diff) % 24;
            examInfoText.setText(examName + " için kalan süre: " + days + " gün " + hours + " saat");
        } catch (Exception e) { examInfoText.setText("Tarih okunamadı. Örnek: 2026-06-20"); }
    }

    private void updateTimer() { timerText.setText(format(seconds)); syncFullscreenText(); }

    private void updateStats() {
        statMinutes.setText(completedMinutes + " dk");
        statSessions.setText(String.valueOf(completedSessions));
        targetText.setText(completedMinutes + " / " + dailyTarget + " dk");
        int pct = dailyTarget == 0 ? 0 : Math.min(100, Math.round(completedMinutes * 100f / dailyTarget));
        targetPercentText.setText("%" + pct);
        updateWeeklyText();
    }

    private void updateWeeklyText() {
        if (weeklyText == null) return;
        String[] names = {"Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz"};
        int total = 0; StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) { total += weekMinutes[i]; sb.append(names[i]).append(": ").append(weekMinutes[i]).append(" dk"); if (i < 6) sb.append("\n"); }
        sb.append("\n\nHaftalık toplam: ").append(total).append(" dk");
        weeklyText.setText(sb.toString());
    }

    private int todayIndex() { int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK); return day == Calendar.SUNDAY ? 6 : day - 2; }

    private void loadState() {
        completedSessions = prefs.getInt("sessions", 0); completedMinutes = prefs.getInt("minutes", 0); dailyTarget = prefs.getInt("target", 60);
        examName = prefs.getString("exam_name", "YKS"); examDate = prefs.getString("exam_date", "2026-06-20");
        profileName = prefs.getString("profile_name", "Misafir"); musicMode = prefs.getString("music_mode", "Sessiz");
        for (int i = 0; i < 7; i++) weekMinutes[i] = prefs.getInt("week_" + i, 0);
        tasks.clear(); String rawTasks = prefs.getString("tasks", "");
        if (rawTasks != null && rawTasks.length() > 0) for (String row : rawTasks.split("\\n")) if (row.trim().length() > 0) tasks.add(new FocusTask(row.length() > 2 ? row.substring(2) : "Görev", row.startsWith("1|")));
        notes.clear(); String rawNotes = prefs.getString("notes", "");
        if (rawNotes != null && rawNotes.length() > 0) for (String row : rawNotes.split("\\n")) if (row.trim().length() > 0) notes.add(row.trim());
    }

    private void saveState() {
        StringBuilder taskBuilder = new StringBuilder(); for (FocusTask t : tasks) taskBuilder.append(t.done ? "1|" : "0|").append(clean(t.text)).append("\n");
        StringBuilder noteBuilder = new StringBuilder(); for (String n : notes) noteBuilder.append(clean(n)).append("\n");
        SharedPreferences.Editor editor = prefs.edit().putInt("sessions", completedSessions).putInt("minutes", completedMinutes).putInt("target", dailyTarget)
                .putString("exam_name", examName).putString("exam_date", examDate).putString("profile_name", profileName).putString("music_mode", musicMode)
                .putString("tasks", taskBuilder.toString()).putString("notes", noteBuilder.toString());
        for (int i = 0; i < 7; i++) editor.putInt("week_" + i, weekMinutes[i]); editor.apply();
    }

    private String clean(String v) { return v.replace("\n", " ").replace("|", " "); }
    private String format(int t) { return String.format(Locale.US, "%02d:%02d", t / 60, t % 60); }

    private LinearLayout card() { LinearLayout c = new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setBackground(panelBg("#111827", dp(24), "#243041")); return c; }
    private TextView text(String v, int sp, String color, boolean bold) { TextView tv = new TextView(this); tv.setText(v); tv.setTextSize(sp); tv.setTextColor(Color.parseColor(color)); if (bold) tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return tv; }
    private EditText input(String hint) { EditText e = new EditText(this); e.setHint(hint); e.setHintTextColor(Color.parseColor("#64748b")); e.setTextColor(Color.WHITE); e.setSingleLine(true); e.setBackground(panelBg("#0f172a", dp(14), "#263244")); e.setPadding(dp(14), 0, dp(14), 0); return e; }
    private TextView pill(String v) { TextView tv = text(v, 13, "#facc15", true); tv.setGravity(Gravity.CENTER); tv.setPadding(dp(14), dp(8), dp(14), dp(8)); tv.setBackground(panelBg("#241a08", dp(22), "#5f4712")); return tv; }
    private Button primaryButton(String label) { Button b = new Button(this); b.setText(label); b.setTextColor(Color.parseColor("#111827")); b.setTextSize(15); b.setTypeface(Typeface.DEFAULT, Typeface.BOLD); b.setBackground(panelBg("#facc15", dp(16), "#facc15")); return b; }
    private Button darkButton(String label) { Button b = new Button(this); b.setText(label); b.setTextColor(Color.WHITE); b.setTextSize(14); b.setTypeface(Typeface.DEFAULT, Typeface.BOLD); b.setBackground(panelBg("#1e293b", dp(16), "#334155")); return b; }
    private TextView statCard(String value, String label) { LinearLayout box = card(); box.setPadding(dp(14), dp(14), dp(14), dp(14)); TextView number = text(value, 24, "#facc15", true); TextView caption = text(label, 12, "#cbd5e1", false); box.addView(number, wrap()); box.addView(caption, wrap()); return number; }
    private GradientDrawable panelBg(String fill, int radius, String stroke) { GradientDrawable gd = new GradientDrawable(); gd.setColor(Color.parseColor(fill)); gd.setCornerRadius(radius); gd.setStroke(dp(1), Color.parseColor(stroke)); return gd; }
    private LinearLayout.LayoutParams wrap() { return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); }
    private LinearLayout.LayoutParams margin(int l, int t, int r, int b) { LinearLayout.LayoutParams p = wrap(); p.setMargins(dp(l), dp(t), dp(r), dp(b)); return p; }
    private LinearLayout.LayoutParams height(int h) { return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(h)); }
    private LinearLayout.LayoutParams heightMargin(int h, int l, int t, int r, int b) { LinearLayout.LayoutParams p = height(h); p.setMargins(dp(l), dp(t), dp(r), dp(b)); return p; }
    private LinearLayout.LayoutParams weightButton() { LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(46), 1); p.setMargins(dp(4), 0, dp(4), 0); return p; }
    private LinearLayout.LayoutParams weightCard() { LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1); p.setMargins(dp(4), 0, dp(4), 0); return p; }
    private LinearLayout.LayoutParams centerWrap() { LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); p.gravity = Gravity.CENTER_HORIZONTAL; return p; }
    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density + 0.5f); }

    @Override protected void onDestroy() { handler.removeCallbacks(ticker); handler.removeCallbacks(examTicker); saveState(); super.onDestroy(); }
    private static class FocusTask { String text; boolean done; FocusTask(String text, boolean done) { this.text = text; this.done = done; } }
}
