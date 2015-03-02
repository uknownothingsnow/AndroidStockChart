package com.github.lzyzsd.androidstockchart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Bruce on 2/27/15.
 */
public class LineChartView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#2a2e36");
    private static final int GRID_LINE_COLOR = Color.WHITE;
    private static final int GRID_BORDER_COLOR = Color.RED;
    private static final int CHART_LINE_COLOR = Color.RED;
    private static final int AXIS_LABEL_COLOR = Color.GREEN;
    private static final int AXIS_LABEL_COLOR_RED = Color.parseColor("#ff2d19");
    private static final int AXIS_LABEL_COLOR_WHITE = Color.parseColor("#bdbec1");
    private static final int AXIS_LABEL_COLOR_GREEN = Color.parseColor("#33fd33");

    private SurfaceHolder holder;
    private Canvas canvas;
    private Paint paint;
    private Paint axisLabelPaint;
    private Paint bottomAxisLabelPaint;
    private Paint chartLinePaint;
    private Thread thread;
    private boolean isRunning;

    private int horizontalLinesNumber = 7;
    private int verticalLinesNumber = 12;
    private float preClose = 3257f;
    private float diff = 0;
    private final float PADDING_VALUE = 30f;
    private float[] leftAxisValues = new float[horizontalLinesNumber];
    private long[] bottomAxisValues = new long[verticalLinesNumber];
    private long xStepSize = 7200000;

    float min = preClose - diff - PADDING_VALUE;
    float max = preClose + diff + PADDING_VALUE;

    private List<Point> points = new ArrayList<>();

    private int axisLabelTextSize = 24;
    private float axisLabelAscent;
    private float axisLabelDecent;

    //不包含底部label的高度
    private float contentHeight;

    public LineChartView(Context context) {
        this(context, null);
    }

    public LineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        holder = getHolder();
        holder.addCallback(this);
    }

    public void setPreClose(float preClose) {
        this.preClose = preClose;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
        setMinMaxValue();
        generateLeftAxisValues();
        generateBottomAxisValues();
    }

    private void setMinMaxValue() {
        float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
        for (Point point : points) {
            if (point.y < min && point.y > 0) {
                min = point.y;
            }
            if (point.y > max) {
                max = point.y;
            }
        }

        diff = Math.max(Math.abs(min - preClose), Math.abs(max - preClose));

        this.min = preClose - diff - PADDING_VALUE;
        this.max = preClose + diff + PADDING_VALUE;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        paint = new Paint();
        paint.setColor(DEFAULT_BACKGROUND_COLOR);

        axisLabelPaint = new Paint();
        axisLabelPaint.setColor(AXIS_LABEL_COLOR);
        axisLabelPaint.setTextSize(axisLabelTextSize);
        axisLabelAscent = axisLabelPaint.ascent();
        axisLabelDecent = axisLabelPaint.descent();

        bottomAxisLabelPaint = new Paint();
        bottomAxisLabelPaint.setColor(AXIS_LABEL_COLOR);
        bottomAxisLabelPaint.setTextSize(axisLabelTextSize);

        chartLinePaint = new Paint();
        chartLinePaint.setColor(CHART_LINE_COLOR);
        chartLinePaint.setStyle(Paint.Style.STROKE);

        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        contentHeight = height - (-axisLabelAscent - axisLabelDecent);
    }

    @Override
    public void run() {
        while (isRunning )
        {
            if (points.size() == 0) {
                try {
                    thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            try
            {
                if (end - start < 50)
                {
                    Thread.sleep(50 - (end - start));
                }
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void draw()
    {
        try
        {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                paint.setColor(DEFAULT_BACKGROUND_COLOR);
                canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
                drawHorizontalLines(canvas);
                drawVerticalLines(canvas);
                drawPoints(canvas);
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawHorizontalLines(Canvas canvas) {
        float cellHeight = contentHeight / (horizontalLinesNumber - 1);

        paint.setColor(GRID_BORDER_COLOR);
        canvas.drawLine(0, 0, getWidth(), 0, paint);
        drawLeftAxisLabel(0, 0, -axisLabelAscent, canvas);
        drawRightAxisLabel(0, -axisLabelAscent, canvas);

        paint.setColor(GRID_LINE_COLOR);
        for (int i = 1; i <= horizontalLinesNumber - 2; i++) {
            canvas.drawLine(0, i * cellHeight, getWidth(), i * cellHeight, paint);
            float labelY = i * cellHeight - (axisLabelAscent + axisLabelDecent) / 2;
            drawLeftAxisLabel(i, 0, labelY, canvas);
            drawRightAxisLabel(i, labelY, canvas);
        }

        paint.setColor(GRID_BORDER_COLOR);
        canvas.drawLine(0, contentHeight - 1, getWidth(), contentHeight - 1, paint);
        drawLeftAxisLabel(horizontalLinesNumber - 1, 0, contentHeight - 1, canvas);
        drawRightAxisLabel(horizontalLinesNumber - 1, contentHeight - 1, canvas);
    }

    Rect textRect = new Rect();
    private void drawVerticalLines(Canvas canvas) {
        float cellWidth = getWidth() / (verticalLinesNumber - 1);

        paint.setColor(GRID_BORDER_COLOR);
        canvas.drawLine(0, 0, 0, contentHeight, paint);
        drawBottomAxisLabel(0, 0, getHeight(), canvas);

        paint.setColor(GRID_LINE_COLOR);
        for (int i = 1; i <= verticalLinesNumber - 2; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, contentHeight, paint);
            String bottomAxisValue = String.valueOf(bottomAxisValues[i]);
            axisLabelPaint.getTextBounds(bottomAxisValue, 0, bottomAxisValue.length(), textRect);
            drawBottomAxisLabel(i, i * cellWidth - axisLabelPaint.measureText("00:00", 0, 5) / 2, getHeight(), canvas);
        }

        paint.setColor(GRID_BORDER_COLOR);
        canvas.drawLine(getWidth() - 1, 0, getWidth() - 1, contentHeight, paint);
        String lastBottomAxisValue = String.valueOf(bottomAxisValues[verticalLinesNumber - 1]);
        axisLabelPaint.getTextBounds(lastBottomAxisValue, 0, lastBottomAxisValue.length(), textRect);
        drawBottomAxisLabel(verticalLinesNumber - 1, getWidth() - axisLabelPaint.measureText("00:00", 0, 5) - 1, getHeight(), canvas);
    }

    Path path = new Path();
    private void drawPoints(Canvas canvas) {
        path.moveTo(computeRawX(points.get(0).x), computeRawY(points.get(0).y));
        for (int i = 1; i < points.size(); i++) {
            path.lineTo(computeRawX(points.get(i).x), computeRawY(points.get(i).y));
        }

        canvas.drawPath(path, chartLinePaint);
        path.reset();
    }

    private float computeRawX(float x) {
        long start = points.get(0).x;
        return (x - start) / (float) xStepSize / (float) verticalLinesNumber * getWidth();
    }

    private float computeRawY(float y) {
        return (max - y) / (max - min) * contentHeight;
    }

    private void generateLeftAxisValues() {
        float step = (max - min) / horizontalLinesNumber;

        leftAxisValues[0] = max;
        leftAxisValues[leftAxisValues.length - 1] = min;
        int middle = leftAxisValues.length / 2;
        leftAxisValues[middle] = preClose;

        for (int i = 1; i < middle; i++) {
            leftAxisValues[middle - i] = max + i * step;
            leftAxisValues[middle + i] = max - i * step;
        }
    }

    private void generateBottomAxisValues() {
        long start = points.get(0).x;
        for (int i = 0; i < bottomAxisValues.length; i++) {
            bottomAxisValues[i] = start + xStepSize * i;
        }
    }

    private void drawLeftAxisLabel(int position, float x, float y, Canvas canvas) {
        int middle = horizontalLinesNumber / 2;
        if (position == middle) {
            axisLabelPaint.setColor(AXIS_LABEL_COLOR_WHITE);
        } else if (position > middle) {
            axisLabelPaint.setColor(AXIS_LABEL_COLOR_GREEN);
        } else {
            axisLabelPaint.setColor(AXIS_LABEL_COLOR_RED);
        }
        String text = String.format("%.02f", leftAxisValues[position]);
        canvas.drawText(text, x, y, axisLabelPaint);
    }

    private void drawRightAxisLabel(int position, float y, Canvas canvas) {
        int middle = horizontalLinesNumber / 2;
        if (position == middle) {
            axisLabelPaint.setColor(AXIS_LABEL_COLOR_WHITE);
        } else if (position > middle) {
            axisLabelPaint.setColor(AXIS_LABEL_COLOR_GREEN);
        } else {
            axisLabelPaint.setColor(AXIS_LABEL_COLOR_RED);
        }
        float percent = Math.abs((leftAxisValues[position] - preClose) / preClose * 100);
        String text = String.format("%.02f", percent) + "%";
        axisLabelPaint.getTextBounds(text, 0, text.length(), textRect);
        canvas.drawText(text, getWidth() - textRect.width(), y, axisLabelPaint);
    }

    private void drawBottomAxisLabel(int position, float x, float y, Canvas canvas) {
        canvas.drawText(DateUtil.format_hh_mm(bottomAxisValues[position]), x, y, axisLabelPaint);
    }
}
