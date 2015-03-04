package com.github.lzyzsd.androidstockchart.model;

import android.graphics.Color;

import com.github.lzyzsd.androidstockchart.formatter.AxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruce on 3/3/15.
 */
public class Axis {
    private List<AxisValue> values = new ArrayList<>();
    private int lineColor = Color.LTGRAY;
    private float min, max, middle;
    private AxisValueFormatter axisValueFormatter;

    public void Axis() {

    }

    public void Axis(List<AxisValue> values) {
        if (values == null) {
            this.values = new ArrayList<>();
        } else {
            this.values = values;
        }
    }

    public List<AxisValue> getValues() {
        return values;
    }

    public void setValues(List<AxisValue> values) {
        this.values = values;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getMiddle() {
        return middle;
    }

    public void setMiddle(float middle) {
        this.middle = middle;
    }

    public AxisValueFormatter getAxisValueFormatter() {
        return axisValueFormatter;
    }

    public void setAxisValueFormatter(AxisValueFormatter axisValueFormatter) {
        this.axisValueFormatter = axisValueFormatter;
    }
}
