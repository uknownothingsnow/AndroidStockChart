package com.github.lzyzsd.androidstockchart.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruce on 3/3/15.
 */
public class LineChartData extends ChartData {
    private List<Line> lines = new ArrayList<>();

    public LineChartData() {

    }

    public LineChartData(List<Line> lines) {
        setLines(lines);
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        if (lines == null) {
            this.lines = new ArrayList<>();
        } else {
            this.lines = lines;
        }
    }
}
