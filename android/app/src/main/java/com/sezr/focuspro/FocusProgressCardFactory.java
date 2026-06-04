package com.sezr.focuspro;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class FocusProgressCardFactory {
    private FocusProgressCardFactory() {}

    public static LinearLayout create(Context context, int completedMinutes, int completedSessions) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(context, 18), dp(context, 18), dp(context, 18), dp(context, 18));
        card.setBackground(bg(context, "#111827", 24, "#243041"));

        TextView title = text(context, "Seviye / Rozetler", 22, "#facc15", true);
        card.addView(title, matchWrap());

        TextView level = text(context, "Seviye: " + FocusLevelRules.levelName(completedMinutes), 18, "#ffffff", true);
        level.setPadding(0, dp(context, 10), 0, dp(context, 8));
        card.addView(level, matchWrap());

        TextView badges = text(context, FocusLevelRules.badges(completedMinutes, completedSessions), 15, "#dbeafe", false);
        card.addView(badges, matchWrap());

        return card;
    }

    private static TextView text(Context context, String value, int sp, String color, boolean bold) {
        TextView tv = new TextView(context);
        tv.setText(value);
        tv.setTextSize(sp);
        tv.setTextColor(Color.parseColor(color));
        if (bold) tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return tv;
    }

    private static LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private static GradientDrawable bg(Context context, String fill, int radiusDp, String stroke) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(fill));
        gd.setCornerRadius(dp(context, radiusDp));
        gd.setStroke(dp(context, 1), Color.parseColor(stroke));
        return gd;
    }

    private static int dp(Context context, int value) {
        return (int) (value * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
