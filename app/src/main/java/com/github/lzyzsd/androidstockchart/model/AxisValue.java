package com.github.lzyzsd.androidstockchart.model;

import android.graphics.Color;

/**
 * Created by Bruce on 3/4/15.
 */
public class AxisValue<T> {
    private static final int DEFAULT_LABEL_COLOR = Color.WHITE;

    private T value;
    private int labelColor;
    //对应坐标
    private float position;

    public AxisValue(T value) {
        setValue(value);
        setLabelColor(DEFAULT_LABEL_COLOR);
    }

    public AxisValue(T value, int lineColor, float position) {
        setValue(value);
        setLabelColor(lineColor);
        setPosition(position);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public int getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
    }

    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        this.position = position;
    }
}
