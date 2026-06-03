package com.sezr.focuspro;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String PREFS = "sezr_focus_pro_native";
    private int focusDuration = 25 * 60;
    private int seconds = focusDuration;
    private boolean running = false;
    private int completedSessions = 0;
    private int completedMinutes = 0;
    private int dailyTarget = 60;

    private final List<FocusTask> tasks = new ArrayList<>();
    private SharedPreferences prefs;

    private TextView timerText;
    private TextView statusText;
    private TextView statMinutes;
    private TextView statSessions;
    private TextView targetText;
    private TextView targetPercentText;
    private LinearLayout taskContainer;
    private Button toggleButton;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            if (!running) return;
            if (seconds > 0) {
                seconds--;
                updateTimer();
                handler.postDelayed(this, 1000);
            } else {
                running = false;
                completedSessions++;
                completedMinutes += focusDuration / 60;
                toggleButton.setText("Başlat");
                statusText.setText("Seans tamamlandı. Kısa bir mola ver.");
                saveState();
                updateStats();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        LinearLayout header = card();
        header.setGravity(Gravity.CENTER_HORIZONTAL);
        header.setPadding(dp(18), dp(18), dp(18), dp(18));
        TextView title = text("SezR Focus Pro", 32, "#facc15", true);
        title.setGravity(Gravity.CENTER);
        header.addView(title, matchWrap());
        TextView subtitle = text("Android için bağımsız odak uygulaması", 15, "#cbd5e1", false);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(8), 0, 0);
        header.addView(subtitle, matchWrap());
        root.addView(header, matchWrapMargin(0, 0, 0, dp(14)));

        LinearLayout timerCard = card();
        timerCard.setGravity(Gravity.CENTER_HORIZONTAL);
        timerCard.setPadding(dp(18), dp(22), dp(18), dp(22));
        timerCard.addView(pill("ODAK SAYACI"), wrapCenter());

        timerText = text(format(seconds), 64, "#ffffff", true);
        timerText.setGravity(Gravity.CENTER);
        timerText.setPadding(0, dp(18), 0, 0);
        timerCard.addView(timerText, matchWrap());

        statusText = text("Hazır", 16, "#cbd5e1", false);
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, dp(8), 0, dp(18));
        timerCard.addView(statusText, matchWrap());

        LinearLayout modeRow = new LinearLayout(this);
        modeRow.setOrientation(LinearLayout.HORIZONTAL);
        modeRow.setGravity(Gravity.CENTER);
        modeRow.addView(modeButton("25 dk", 25), weightButton());
        modeRow.addView(modeButton("45 dk", 45), weightButton());
        modeRow.addView(modeButton("60 dk", 60), weightButton());
        timerCard.addView(modeRow, matchWrapMargin(0, 0, 0, dp(14)));

        toggleButton = primaryButton("Başlat");
        timerCard.addView(toggleButton, matchHeight(dp(54)));
        Button resetButton = darkButton("Sıfırla");
        timerCard.addView(resetButton, matchHeightMargin(dp(54), 0, dp(10), 0, 0));
        root.addView(timerCard, matchWrapMargin(0, 0, 0, dp(14)));

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        statMinutes = statCard("0 dk", "Bugünkü çalışma");
        statSessions = statCard("0", "Tamamlanan seans");
        stats.addView((LinearLayout) statMinutes.getParent(), weightCard());
        stats.addView((LinearLayout) statSessions.getParent(), weightCard());
        root.addView(stats, matchWrapMargin(0, 0, 0, dp(14)));

        root.addView(buildTargetCard(), matchWrapMargin(0, 0, 0, dp(14)));
        root.addView(buildTaskCard(), matchWrap());

        toggleButton.setOnClickListener(v -> toggleTimer());
        resetButton.setOnClickListener(v -> resetTimer());

        updateTimer();
        updateStats();
        renderTasks();
        setContentView(scroll);
    }

    private LinearLayout buildTargetCard() {
        LinearLayout targetCard = card();
        targetCard.setPadding(dp(18), dp(18), dp(18), dp(18));
        targetCard.addView(text("Günlük Hedef", 22, "#facc15", true), matchWrap());
        targetText = text("0 / 60 dk", 16, "#dbeafe", false);
        targetText.setPadding(0, dp(8), 0, dp(8));
        targetCard.addView(targetText, matchWrap());
        targetPercentText = text("%0", 34, "#ffffff", true);
        targetCard.addView(targetPercentText, matchWrap());

        LinearLayout targetButtons = new LinearLayout(this);
        targetButtons.setOrientation(LinearLayout.HORIZONTAL);
        targetButtons.setPadding(0, dp(12), 0, 0);
        targetButtons.addView(targetButton("30 dk", 30), weightButton());
        targetButtons.addView(targetButton("60 dk", 60), weightButton());
        targetButtons.addView(targetButton("90 dk", 90), weightButton());
        targetCard.addView(targetButtons, matchWrap());
        return targetCard;
    }

    private LinearLayout buildTaskCard() {
        LinearLayout taskCard = card();
        taskCard.setPadding(dp(18), dp(18), dp(18), dp(18));
        taskCard.addView(text("Bugünkü görevler", 22, "#facc15", true), matchWrap());

        EditText taskInput = new EditText(this);
        taskInput.setHint("Örn. 30 problem çöz");
        taskInput.setHintTextColor(Color.parseColor("#64748b"));
        taskInput.setTextColor(Color.WHITE);
        taskInput.setSingleLine(true);
        taskInput.setBackground(panelBg("#0f172a", dp(14), "#263244"));
        taskInput.setPadding(dp(14), 0, dp(14), 0);
        taskCard.addView(taskInput, matchHeightMargin(dp(52), 0, dp(14), 0, dp(10)));

        Button addTask = primaryButton("Görev Ekle");
        taskCard.addView(addTask, matchHeight(dp(52)));
        taskContainer = new LinearLayout(this);
        taskContainer.setOrientation(LinearLayout.VERTICAL);
        taskContainer.setPadding(0, dp(14), 0, 0);
        taskCard.addView(taskContainer, matchWrap());

        Button clearDone = darkButton("Tamamlananları Temizle");
        taskCard.addView(clearDone, matchHeightMargin(dp(52), 0, dp(12), 0, 0));

        addTask.setOnClickListener(v -> {
            String value = taskInput.getText().toString().trim();
            if (value.length() == 0) return;
            tasks.add(new FocusTask(value, false));
            taskInput.setText("");
            saveState();
            renderTasks();
        });
        clearDone.setOnClickListener(v -> {
            for (int i = tasks.size() - 1; i >= 0; i--) if (tasks.get(i).done) tasks.remove(i);
            saveState();
            renderTasks();
        });
        return taskCard;
    }

    private void toggleTimer() {
        running = !running;
        if (running) {
            toggleButton.setText("Duraklat");
            statusText.setText("Odak seansı çalışıyor");
            handler.post(ticker);
        } else {
            toggleButton.setText("Devam Et");
            statusText.setText("Duraklatıldı");
            handler.removeCallbacks(ticker);
        }
    }

    private void resetTimer() {
        running = false;
        seconds = focusDuration;
        handler.removeCallbacks(ticker);
        toggleButton.setText("Başlat");
        statusText.setText("Hazır");
        updateTimer();
    }

    private Button modeButton(String label, int minutes) {
        Button button = darkButton(label);
        button.setOnClickListener(v -> {
            if (running) return;
            focusDuration = minutes * 60;
            seconds = focusDuration;
            statusText.setText(label + " modu seçildi");
            updateTimer();
        });
        return button;
    }

    private Button targetButton(String label, int minutes) {
        Button button = darkButton(label);
        button.setOnClickListener(v -> {
            dailyTarget = minutes;
            saveState();
            updateStats();
        });
        return button;
    }

    private void renderTasks() {
        taskContainer.removeAllViews();
        if (tasks.isEmpty()) {
            TextView empty = text("Henüz görev eklenmedi.", 15, "#cbd5e1", false);
            taskContainer.addView(empty, matchWrap());
            return;
        }
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

            Button done = darkButton(task.done ? "Geri" : "Tamam");
            done.setTextSize(12);
            row.addView(done, new LinearLayout.LayoutParams(dp(82), dp(42)));

            Button del = darkButton("Sil");
            del.setTextSize(12);
            LinearLayout.LayoutParams delParams = new LinearLayout.LayoutParams(dp(58), dp(42));
            delParams.setMargins(dp(6), 0, 0, 0);
            row.addView(del, delParams);

            done.setOnClickListener(v -> {
                tasks.get(index).done = !tasks.get(index).done;
                saveState();
                renderTasks();
            });
            del.setOnClickListener(v -> {
                tasks.remove(index);
                saveState();
                renderTasks();
            });

            taskContainer.addView(row, matchWrapMargin(0, 0, 0, dp(8)));
        }
    }

    private void updateTimer() {
        timerText.setText(format(seconds));
    }

    private void updateStats() {
        statMinutes.setText(completedMinutes + " dk");
        statSessions.setText(String.valueOf(completedSessions));
        targetText.setText(completedMinutes + " / " + dailyTarget + " dk");
        int pct = dailyTarget == 0 ? 0 : Math.min(100, Math.round(completedMinutes * 100f / dailyTarget));
        targetPercentText.setText("%" + pct);
    }

    private void loadState() {
        completedSessions = prefs.getInt("sessions", 0);
        completedMinutes = prefs.getInt("minutes", 0);
        dailyTarget = prefs.getInt("target", 60);
        String raw = prefs.getString("tasks", "");
        tasks.clear();
        if (raw != null && raw.length() > 0) {
            String[] rows = raw.split("\\n");
            for (String row : rows) {
                if (row.trim().length() == 0) continue;
                boolean done = row.startsWith("1|");
                String text = row.length() > 2 ? row.substring(2) : "Görev";
                tasks.add(new FocusTask(text, done));
            }
        }
    }

    private void saveState() {
        StringBuilder sb = new StringBuilder();
        for (FocusTask task : tasks) {
            sb.append(task.done ? "1|" : "0|").append(task.text.replace("\n", " ")).append("\n");
        }
        prefs.edit()
                .putInt("sessions", completedSessions)
                .putInt("minutes", completedMinutes)
                .putInt("target", dailyTarget)
                .putString("tasks", sb.toString())
                .apply();
    }

    private String format(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(panelBg("#111827", dp(24), "#243041"));
        return card;
    }

    private TextView text(String value, int sp, String color, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(value);
        tv.setTextSize(sp);
        tv.setTextColor(Color.parseColor(color));
        if (bold) tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return tv;
    }

    private TextView pill(String value) {
        TextView tv = text(value, 13, "#facc15", true);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(dp(14), dp(8), dp(14), dp(8));
        tv.setBackground(panelBg("#241a08", dp(22), "#5f4712"));
        return tv;
    }

    private Button primaryButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.parseColor("#111827"));
        b.setTextSize(15);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setBackground(panelBg("#facc15", dp(16), "#facc15"));
        return b;
    }

    private Button darkButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setTextSize(14);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setBackground(panelBg("#1e293b", dp(16), "#334155"));
        return b;
    }

    private TextView statCard(String value, String label) {
        LinearLayout box = card();
        box.setPadding(dp(14), dp(14), dp(14), dp(14));
        TextView number = text(value, 24, "#facc15", true);
        TextView caption = text(label, 12, "#cbd5e1", false);
        box.addView(number, matchWrap());
        box.addView(caption, matchWrap());
        return number;
    }

    private GradientDrawable panelBg(String fill, int radius, String stroke) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(fill));
        gd.setCornerRadius(radius);
        gd.setStroke(dp(1), Color.parseColor(stroke));
        return gd;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams matchWrapMargin(int l, int t, int r, int b) {
        LinearLayout.LayoutParams p = matchWrap();
        p.setMargins(l, t, r, b);
        return p;
    }

    private LinearLayout.LayoutParams matchHeight(int height) {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
    }

    private LinearLayout.LayoutParams matchHeightMargin(int height, int l, int t, int r, int b) {
        LinearLayout.LayoutParams p = matchHeight(height);
        p.setMargins(l, t, r, b);
        return p;
    }

    private LinearLayout.LayoutParams weightButton() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(46), 1);
        p.setMargins(dp(4), 0, dp(4), 0);
        return p;
    }

    private LinearLayout.LayoutParams weightCard() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        p.setMargins(dp(4), 0, dp(4), 0);
        return p;
    }

    private LinearLayout.LayoutParams wrapCenter() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.CENTER_HORIZONTAL;
        return p;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(ticker);
        saveState();
        super.onDestroy();
    }

    private static class FocusTask {
        String text;
        boolean done;
        FocusTask(String text, boolean done) {
            this.text = text;
            this.done = done;
        }
    }
}
