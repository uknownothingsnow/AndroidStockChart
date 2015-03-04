package com.github.lzyzsd.androidstockchart.model;

/**
 * Created by Bruce on 3/4/15.
 */
public class XAxisValue {
    private long value;
    private int labelColor;
    //对应坐标
    private float position;

    public XAxisValue(long value, int lineColor, float position) {
        setValue(value);
        setLabelColor(lineColor);
        setPosition(position);
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
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
