package com.sezr.focuspro;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private int seconds = 25 * 60;
    private TextView timerText;

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
        subtitle.setPadding(0, 12, 0, 42);
        root.addView(subtitle, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        timerText = new TextView(this);
        timerText.setText(format(seconds));
        timerText.setTextColor(Color.WHITE);
        timerText.setTextSize(58);
        timerText.setGravity(Gravity.CENTER);
        timerText.setTypeface(null, 1);
        root.addView(timerText, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView info = new TextView(this);
        info.setText("İlk native sürüm: Pomodoro, görevler, hedefler ve raporlar bu yapının üzerine eklenecek.");
        info.setTextColor(Color.parseColor("#dbeafe"));
        info.setTextSize(15);
        info.setGravity(Gravity.CENTER);
        info.setPadding(0, 34, 0, 26);
        root.addView(info, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button start = new Button(this);
        start.setText("Başlat");
        start.setTextColor(Color.parseColor("#111827"));
        start.setBackgroundColor(Color.parseColor("#facc15"));
        root.addView(start, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 120));

        setContentView(root);
    }

    private String format(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }
}
