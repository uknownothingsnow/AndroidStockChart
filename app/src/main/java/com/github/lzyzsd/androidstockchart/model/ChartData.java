package com.github.lzyzsd.androidstockchart.model;

/**
 * Created by Bruce on 3/3/15.
 */
public class ChartData {
    protected Axis<Long> axisXBottom;
    protected Axis<Float> axisYLeft;
    protected Axis<Long> axisXTop;
    protected Axis<Float> axisYRight;

    public Axis<Long> getAxisXBottom() {
        return axisXBottom;
    }

    public void setAxisXBottom(Axis xAxisXBottom) {
        this.axisXBottom = xAxisXBottom;
    }

    public Axis<Float> getAxisYLeft() {
        return axisYLeft;
    }

    public void setAxisYLeft(Axis<Float> axisYLeft) {
        this.axisYLeft = axisYLeft;
    }

    public Axis<Long> getAxisXTop() {
        return axisXTop;
    }

    public void setAxisXTop(Axis<Long> xAxisXTop) {
        this.axisXTop = xAxisXTop;
    }

    public Axis<Float> getAxisYRight() {
        return axisYRight;
    }

    public void setAxisYRight(Axis<Float> axisYRight) {
        this.axisYRight = axisYRight;
    }
}
