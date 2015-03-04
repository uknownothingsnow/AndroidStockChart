package com.github.lzyzsd.androidstockchart.model;

import android.graphics.Color;

import com.github.lzyzsd.androidstockchart.formatter.AxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruce on 3/4/15.
 */
public class Axis<T> {
    private List<AxisValue<T>> values = new ArrayList<>();
    private int lineColor = Color.LTGRAY;
    private long min, max, step;
    private AxisValueFormatter axisValueFormatter;

    public void Axis() {

    }

    public void Axis(List<AxisValue<T>> values) {
        if (values == null) {
            this.values = new ArrayList<>();
        } else {
            this.values = values;
        }
    }

    public List<AxisValue<T>> getValues() {
        return values;
    }

    public void setValues(List<AxisValue<T>> values) {
        this.values = values;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }

    public int getCellNumber() {
        return (int) ((max - min) / step);
    }

    public AxisValueFormatter getAxisValueFormatter() {
        return axisValueFormatter;
    }

    public void setAxisValueFormatter(AxisValueFormatter axisValueFormatter) {
        this.axisValueFormatter = axisValueFormatter;
    }
}
