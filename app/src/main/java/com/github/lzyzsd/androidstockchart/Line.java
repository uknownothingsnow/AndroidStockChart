package com.github.lzyzsd.androidstockchart;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruce on 3/2/15.
 */
public class Line {
    private static final int DEFAULT_LINE_COLOR = Color.RED;
    private int color = DEFAULT_LINE_COLOR;
    private List<Point> points = new ArrayList<>();

    public Line() {
    }

    public Line(List<Point> points) {
        if (null == points) {
            this.points = new ArrayList<>();
        } else {
            this.points = points;
        }
    }

    public List<Point> getPoints() {
        return points;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
