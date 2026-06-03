package com.sezr.focuspro;

import android.app.Activity;
import android.graphics.Color;
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

public class MainActivity extends Activity {
    private int focusDuration = 25 * 60;
    private int seconds = focusDuration;
    private boolean running = false;
    private int completedSessions = 0;
    private int completedMinutes = 0;

    private TextView timerText;
    private TextView statusText;
    private TextView statMinutes;
    private TextView statSessions;
    private TextView taskList;
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
                updateStats();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        TextView badge = pill("ODAK SAYACI");
        timerCard.addView(badge, wrapCenter());

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

        LinearLayout taskCard = card();
        taskCard.setPadding(dp(18), dp(18), dp(18), dp(18));
        TextView taskTitle = text("Bugünkü görevler", 22, "#facc15", true);
        taskCard.addView(taskTitle, matchWrap());

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

        taskList = text("Henüz görev eklenmedi.", 15, "#cbd5e1", false);
        taskList.setPadding(0, dp(16), 0, 0);
        taskCard.addView(taskList, matchWrap());
        root.addView(taskCard, matchWrap());

        toggleButton.setOnClickListener(v -> toggleTimer());
        resetButton.setOnClickListener(v -> resetTimer());
        addTask.setOnClickListener(v -> {
            String value = taskInput.getText().toString().trim();
            if (value.length() == 0) return;
            String current = taskList.getText().toString();
            if (current.startsWith("Henüz")) current = "";
            taskList.setText(current + "• " + value + "\n");
            taskInput.setText("");
        });

        setContentView(scroll);
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

    private void updateTimer() {
        timerText.setText(format(seconds));
    }

    private void updateStats() {
        statMinutes.setText(completedMinutes + " dk");
        statSessions.setText(String.valueOf(completedSessions));
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
        super.onDestroy();
    }
}
