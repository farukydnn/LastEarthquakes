package com.farukydnn.lastearthquakes.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.ListView;

import java.lang.reflect.Field;

public class FadedListView extends ListView {

    private int mFadeColor = 0xFF000000;

    public FadedListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FadedListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setEdgeGlowColor(mFadeColor);
    }

    @Override
    public final int getSolidColor() {
        return mFadeColor;
    }

    public final void setFadeColor(int fadeColor) {
        mFadeColor = fadeColor;
    }

    public final void setEdgeGlowColor(int color) {
        try {
            Class<?> clazz = AbsListView.class;
            Field fEdgeGlowTop = clazz.getDeclaredField("mEdgeGlowTop");
            Field fEdgeGlowBottom = clazz.getDeclaredField("mEdgeGlowBottom");
            fEdgeGlowTop.setAccessible(true);
            fEdgeGlowBottom.setAccessible(true);
            setEdgeEffectColor((EdgeEffect) fEdgeGlowTop.get(this), color);
            setEdgeEffectColor((EdgeEffect) fEdgeGlowBottom.get(this), color);
        } catch (Exception ignored) {
        }
    }

    public final void setEdgeEffectColor(EdgeEffect edgeEffect, int color) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                edgeEffect.setColor(color);
                return;
            }
            Field edgeField = EdgeEffect.class.getDeclaredField("mEdge");
            Field glowField = EdgeEffect.class.getDeclaredField("mGlow");
            edgeField.setAccessible(true);
            glowField.setAccessible(true);
            Drawable mEdge = (Drawable) edgeField.get(edgeEffect);
            Drawable mGlow = (Drawable) glowField.get(edgeEffect);
            mEdge.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            mGlow.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            mEdge.setCallback(null);
            mGlow.setCallback(null);
        } catch (Exception ignored) {
        }
    }
}