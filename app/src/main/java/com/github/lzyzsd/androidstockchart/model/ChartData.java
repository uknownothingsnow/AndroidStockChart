package com.github.lzyzsd.androidstockchart.model;

/**
 * Created by Bruce on 3/3/15.
 */
public class ChartData {
    protected XAxis axisXBottom;
    protected YAxis axisYLeft;
    protected XAxis axisXTop;
    protected YAxis axisYRight;

    public XAxis getAxisXBottom() {
        return axisXBottom;
    }

    public void setAxisXBottom(XAxis xAxisXBottom) {
        this.axisXBottom = xAxisXBottom;
    }

    public YAxis getAxisYLeft() {
        return axisYLeft;
    }

    public void setAxisYLeft(YAxis YAxisYLeft) {
        this.axisYLeft = YAxisYLeft;
    }

    public XAxis getAxisXTop() {
        return axisXTop;
    }

    public void setAxisXTop(XAxis xAxisXTop) {
        this.axisXTop = xAxisXTop;
    }

    public YAxis getAxisYRight() {
        return axisYRight;
    }

    public void setAxisYRight(YAxis YAxisYRight) {
        this.axisYRight = YAxisYRight;
    }
}
