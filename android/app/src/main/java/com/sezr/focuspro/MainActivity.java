package com.sezr.focuspro;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView title = new TextView(this);
        title.setText("SezR Focus");
        title.setTextSize(26);
        title.setPadding(40, 80, 40, 40);
        setContentView(title);
    }
}
