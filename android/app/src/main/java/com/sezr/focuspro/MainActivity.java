package com.sezr.focuspro;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private final int focusDuration = 25 * 60;
    private int seconds = focusDuration;
    private boolean running = false;
    private TextView timerText;
    private TextView statusText;
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
                toggleButton.setText("Başlat");
                statusText.setText("Seans tamamlandı. Kısa bir mola ver.");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(32, 56, 32, 32);
        root.setBackgroundColor(Color.parseColor("#020617"));

        TextView title = new TextView(this);
        title.setText("SezR Focus Pro");
        title.setTextColor(Color.parseColor("#facc15"));
        title.setTextSize(30);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, 1);
        root.addView(title, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView subtitle = new TextView(this);
        subtitle.setText("Bağımsız Android odak uygulaması");
        subtitle.setTextColor(Color.parseColor("#cbd5e1"));
        subtitle.setTextSize(16);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 12, 0, 38);
        root.addView(subtitle, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        timerText = new TextView(this);
        timerText.setText(format(seconds));
        timerText.setTextColor(Color.WHITE);
        timerText.setTextSize(60);
        timerText.setGravity(Gravity.CENTER);
        timerText.setTypeface(null, 1);
        root.addView(timerText, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        statusText = new TextView(this);
        statusText.setText("Hazır");
        statusText.setTextColor(Color.parseColor("#cbd5e1"));
        statusText.setTextSize(16);
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, 12, 0, 30);
        root.addView(statusText, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        toggleButton = new Button(this);
        toggleButton.setText("Başlat");
        toggleButton.setTextColor(Color.parseColor("#111827"));
        toggleButton.setBackgroundColor(Color.parseColor("#facc15"));
        root.addView(toggleButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 112));

        Button resetButton = new Button(this);
        resetButton.setText("Sıfırla");
        resetButton.setTextColor(Color.WHITE);
        resetButton.setBackgroundColor(Color.parseColor("#1e293b"));
        LinearLayout.LayoutParams resetParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 112);
        resetParams.setMargins(0, 16, 0, 0);
        root.addView(resetButton, resetParams);

        TextView info = new TextView(this);
        info.setText("İlk native sürüm: çalışan Pomodoro sayacı hazır. Sonraki adımda görevler ve günlük hedef eklenecek.");
        info.setTextColor(Color.parseColor("#dbeafe"));
        info.setTextSize(15);
        info.setGravity(Gravity.CENTER);
        info.setPadding(0, 28, 0, 0);
        root.addView(info, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        toggleButton.setOnClickListener(v -> toggleTimer());
        resetButton.setOnClickListener(v -> resetTimer());

        setContentView(root);
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

    private void updateTimer() {
        timerText.setText(format(seconds));
    }

    private String format(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(ticker);
        super.onDestroy();
    }
}
