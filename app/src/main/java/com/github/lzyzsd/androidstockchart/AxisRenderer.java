package com.github.lzyzsd.androidstockchart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.lzyzsd.androidstockchart.model.Axis;
import com.github.lzyzsd.androidstockchart.model.AxisValue;
import com.github.lzyzsd.androidstockchart.model.Line;
import com.github.lzyzsd.androidstockchart.model.LineChartData;

import java.util.List;

/**
 * Created by Bruce on 3/3/15.
 */
public class AxisRenderer {
    private static final int TOP = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int BOTTOM = 3;

    private static final int AXIS_LINE_COLOR = Color.WHITE;
    private static final int AXIS_BORDER_COLOR = Color.RED;
    private static final int AXIS_LABEL_COLOR_RED = Color.parseColor("#ff2d19");
    private static final int AXIS_LABEL_COLOR_WHITE = Color.parseColor("#bdbec1");
    private static final int AXIS_LABEL_COLOR_GREEN = Color.parseColor("#33fd33");
    private static final int LABEL_SIZE = 24;

    private LineChartView chartView;
    private int lineColor = AXIS_LINE_COLOR;
    private int borderColor = AXIS_BORDER_COLOR;
    private int labelTextSize = LABEL_SIZE;
    private float axisLabelAscent;
    private float axisLabelDecent;

    private Paint[] linePaints = new Paint[] {new Paint(), new Paint(), new Paint(), new Paint()};
    private Paint[] labelPaints = new Paint[] {new Paint(), new Paint(), new Paint(), new Paint()};

    private float diff = 0;
    private final float PADDING_VALUE = 30f;
    float min;// = preClose - diff - PADDING_VALUE;
    float max;// = preClose + diff + PADDING_VALUE;
    private long xStepSize = 7200000;

    public AxisRenderer(LineChartView chartView) {
        this.chartView = chartView;

        for (int i = 0; i < 4; i++) {
            linePaints[i].setColor(lineColor);
            labelPaints[i].setColor(AXIS_LABEL_COLOR_WHITE);
            labelPaints[i].setTextSize(labelTextSize);
        }

        axisLabelAscent = labelPaints[0].ascent();
        axisLabelDecent = labelPaints[0].descent();
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public void setMinMaxValue() {
        float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
        for (Line line: chartView.getLines()) {
            for (Point point : line.getPoints()) {
                if (point.y < min && point.y > 0) {
                    min = point.y;
                }
                if (point.y > max) {
                    max = point.y;
                }
            }
        }

        float preClose = chartView.getPreClose();
        diff = Math.max(Math.abs(min - preClose), Math.abs(max - preClose));

        this.min = preClose - diff - PADDING_VALUE;
        this.max = preClose + diff + PADDING_VALUE;
    }

    public void generateLeftAxisValues() {
        Axis<Float> axis = chartView.getChartData().getAxisYLeft();
        List<AxisValue<Float>> values = axis.getValues();
        float step = (max - min) / chartView.getHorizontalLinesNumber();
        int middle = chartView.getHorizontalLinesNumber() / 2;

        for (int i = 0; i < chartView.getHorizontalLinesNumber(); i++) {
            int color = AXIS_LABEL_COLOR_WHITE;
            if (i !=  middle) {
                if (i < middle) {
                    color = AXIS_LABEL_COLOR_RED;
                } else {
                    color = AXIS_LABEL_COLOR_GREEN;
                }
            }
            values.add(new AxisValue<>(max - i * step, color, chartView.getCellHeight() * i));
        }
    }

    public void drawHorizontalLines(Canvas canvas) {
        Axis<Float> axis = chartView.getChartData().getAxisYLeft();

        Paint paint = linePaints[LEFT];

        AxisValue<Float> firstValue = axis.getValues().get(0);
        paint.setColor(borderColor);
        canvas.drawLine(0, firstValue.getPosition(), chartView.getWidth(), firstValue.getPosition(), linePaints[LEFT]);
        drawLeftAxisLabel(firstValue.getPosition() - axisLabelAscent, firstValue.getValue(), firstValue.getLabelColor(), canvas);
        drawRightAxisLabel(firstValue.getPosition() - axisLabelAscent, firstValue.getValue(), firstValue.getLabelColor(), canvas);

        for (int i = 1; i < axis.getValues().size() - 1; i++) {
            AxisValue<Float> YAxisValue = axis.getValues().get(i);
            float labelY = YAxisValue.getPosition() - (axisLabelAscent + axisLabelDecent) / 2;
            drawLeftAxisLabel(labelY, YAxisValue.getValue(), YAxisValue.getLabelColor(), canvas);
            drawRightAxisLabel(labelY, YAxisValue.getValue(), YAxisValue.getLabelColor(), canvas);
        }

        AxisValue<Float> lastValue = axis.getValues().get(axis.getValues().size() - 1);
        canvas.drawLine(0, lastValue.getPosition(), chartView.getWidth(), lastValue.getPosition(), paint);
        drawLeftAxisLabel(lastValue.getPosition(), lastValue.getValue(), lastValue.getLabelColor(), canvas);
        drawRightAxisLabel(lastValue.getPosition(), lastValue.getValue(), lastValue.getLabelColor(), canvas);
    }

    private void drawLeftAxisLabel(float y, float value, int color, Canvas canvas) {
        Paint axisLabelPaint = labelPaints[LEFT];
        axisLabelPaint.setColor(color);

        String text = String.format("%.02f", value);
        canvas.drawText(text, 0, y, axisLabelPaint);
    }

    private void drawRightAxisLabel(float y, float value, int color, Canvas canvas) {
        Paint axisLabelPaint = labelPaints[LEFT];
        axisLabelPaint.setColor(color);
        float percent = Math.abs((value - chartView.getPreClose()) / chartView.getPreClose() * 100);
        String text = String.format("%.02f", percent) + "%";
        canvas.drawText(text, chartView.getWidth() - axisLabelPaint.measureText(text, 0, text.length()), y, axisLabelPaint);
    }

    public void drawVerticalLines(Canvas canvas) {
        Paint labelPaint = labelPaints[BOTTOM];
        Paint linePaint = linePaints[BOTTOM];

        linePaint.setColor(borderColor);
        canvas.drawLine(0, 0, 0, chartView.getContentHeight(), linePaint);
        String text = chartView.getStartLabel();
        drawBottomAxisLabel(text, 0, chartView.getHeight(), canvas);

        canvas.drawLine(chartView.getWidth() - 1, 0, chartView.getWidth() - 1, chartView.getContentHeight(), linePaint);
        text = chartView.getEndLabel();
        drawBottomAxisLabel(text, chartView.getWidth() - labelPaint.measureText(text, 0, text.length()), chartView.getHeight(), canvas);
    }

    private void drawBottomAxisLabel(String text, float x, float y, Canvas canvas) {
        canvas.drawText(text, x, y, labelPaints[BOTTOM]);
    }

    public float getBottomLabelHeight() {
        Paint paint = labelPaints[BOTTOM];
        return paint.descent() - paint.ascent();
    }
}
