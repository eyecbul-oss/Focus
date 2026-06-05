package com.sezr.focuspro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class FocusRingView extends View {
    private final Paint track = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progress = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF oval = new RectF();
    private int percent = 0;
    private boolean breakMode = false;

    public FocusRingView(Context context) {
        super(context);
        track.setStyle(Paint.Style.STROKE);
        track.setStrokeCap(Paint.Cap.ROUND);
        track.setColor(Color.parseColor("#EAF0F8"));
        progress.setStyle(Paint.Style.STROKE);
        progress.setStrokeCap(Paint.Cap.ROUND);
        progress.setColor(Color.parseColor("#A8E6CF"));
    }

    public void setState(int percent, boolean breakMode) {
        this.percent = Math.max(0, Math.min(100, percent));
        this.breakMode = breakMode;
        progress.setColor(Color.parseColor(breakMode ? "#A7C7FF" : "#A8E6CF"));
        invalidate();
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float size = Math.min(getWidth(), getHeight());
        float stroke = Math.max(14f, size * 0.075f);
        track.setStrokeWidth(stroke);
        progress.setStrokeWidth(stroke);
        float pad = stroke / 2f + 8f;
        oval.set((getWidth() - size) / 2f + pad, (getHeight() - size) / 2f + pad, (getWidth() + size) / 2f - pad, (getHeight() + size) / 2f - pad);
        canvas.drawArc(oval, 135, 270, false, track);
        canvas.drawArc(oval, 135, 270f * percent / 100f, false, progress);
    }
}
