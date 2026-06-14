package com.sezr.focuspro;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Calendar;

public class MainActivity extends Activity {
    private static final int REMINDER_REQUEST_CODE = 1001;
    private WebView page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestNotificationPermissionIfNeeded();
        ReminderReceiver.createChannel(this);

        page = new WebView(this);
        page.setWebViewClient(new WebViewClient());
        WebSettings settings = page.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        page.addJavascriptInterface(new FocusBridge(), "FocusAndroid");
        page.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        setContentView(page);
        String asset = "file:///android_asset/focus.html";
        page.loadUrl(asset);
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2001);
        }
    }

    private PendingIntent reminderPendingIntent(String note) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra(ReminderReceiver.EXTRA_NOTE, note);
        return PendingIntent.getBroadcast(
                this,
                REMINDER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private void scheduleReminder(String time, String note) {
        if (time == null || !time.matches("^\\d{2}:\\d{2}$")) {
            return;
        }
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    reminderPendingIntent(note)
            );
        }
    }

    private void cancelReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(reminderPendingIntent(""));
        }
    }

    public class FocusBridge {
        @JavascriptInterface
        public void setReminder(String enabled, String time, String note) {
            runOnUiThread(() -> {
                if ("on".equals(enabled)) {
                    scheduleReminder(time, note);
                } else {
                    cancelReminder();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (page != null && page.canGoBack()) {
            page.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
