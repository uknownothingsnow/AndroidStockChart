package com.github.lzyzsd.androidstockchart.model;

/**
 * Created by Bruce on 3/3/15.
 */
public class ChartData {
    protected Axis axisXBottom;
    protected Axis axisYLeft;
    protected Axis axisXTop;
    protected Axis axisYRight;

    public Axis getAxisXBottom() {
        return axisXBottom;
    }

    public void setAxisXBottom(Axis axisXBottom) {
        this.axisXBottom = axisXBottom;
    }

    public Axis getAxisYLeft() {
        return axisYLeft;
    }

    public void setAxisYLeft(Axis axisYLeft) {
        this.axisYLeft = axisYLeft;
    }

    public Axis getAxisXTop() {
        return axisXTop;
    }

    public void setAxisXTop(Axis axisXTop) {
        this.axisXTop = axisXTop;
    }

    public Axis getAxisYRight() {
        return axisYRight;
    }

    public void setAxisYRight(Axis axisYRight) {
        this.axisYRight = axisYRight;
    }
}
