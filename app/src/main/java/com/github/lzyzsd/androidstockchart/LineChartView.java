package com.github.lzyzsd.androidstockchart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Bruce on 2/27/15.
 */
public class LineChartView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#2a2e36");
    private static final int GRID_LINE_COLOR = Color.WHITE;
    private static final int GRID_BORDER_COLOR = Color.RED;
    private static final int CHART_LINE_COLOR = Color.RED;
    private static final int AXIS_LABEL_COLOR = Color.GREEN;

    private SurfaceHolder holder;
    private Canvas canvas;
    private Paint paint;
    private Paint axisLabelPaint;
    private Paint bottomAxisLabelPaint;
    private Paint chartLinePaint;
    private Thread thread;
    private boolean isRunning;

    private int horizontalLinesNumber = 6;
    private int verticalLinesNumber = 16;
    private float preClose = 6003.2f;
    private float diff = 103.2f;
    private final float PADDING_VALUE = 30f;
    private float[] leftAxisValues = new float[horizontalLinesNumber];
    private float[] bottomAxisValues = new float[verticalLinesNumber];

    private Point[] points = new Point[200];

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

    private void initPoints() {
        float base;
        for (int i = 0; i < points.length; i++) {
            if (i % 2 == 0) {
                base = -diff;
            } else {
                base = diff;
            }
            float randomY = (float) Math.random() * base + preClose;
            float randomX = (float) Math.random() * verticalLinesNumber;
            points[i] = new Point(randomX, randomY);
        }

        for (int i = 0; i < points.length; i++) {
            for (int j = i; j < points.length; j++) {
                if (points[j].x < points[i].x) {
                    Point temp = points[i];
                    points[i] = points[j];
                    points[j] = temp;
                }
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        generateLeftAxisValues();
        generateBottomAxisValues();
        initPoints();

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
        while (isRunning)
        {
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
        float cellHeight = contentHeight / horizontalLinesNumber;

        paint.setColor(GRID_BORDER_COLOR);
        canvas.drawLine(0, 0, getWidth(), 0, paint);
        drawLeftAxisLabel(0, 0, -axisLabelAscent, canvas);

        paint.setColor(GRID_LINE_COLOR);
        for (int i = 1; i < horizontalLinesNumber; i++) {
            canvas.drawLine(0, i * cellHeight, getWidth(), i * cellHeight, paint);
            drawLeftAxisLabel(i, 0, i * cellHeight - (axisLabelAscent + axisLabelDecent) / 2, canvas);
        }

        paint.setColor(GRID_BORDER_COLOR);
        canvas.drawLine(0, contentHeight - 1, getWidth(), contentHeight - 1, paint);
        drawLeftAxisLabel(horizontalLinesNumber - 1, 0, contentHeight - 1, canvas);
    }

    Rect textRect = new Rect();
    private void drawVerticalLines(Canvas canvas) {
        float cellWidth = getWidth() / verticalLinesNumber;

        paint.setColor(GRID_BORDER_COLOR);
        canvas.drawLine(0, 0, 0, contentHeight, paint);
        drawBottomAxisLabel(0, 0, getHeight(), canvas);

        paint.setColor(GRID_LINE_COLOR);
        for (int i = 1; i < verticalLinesNumber; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, contentHeight, paint);
            String bottomAxisValue = String.valueOf(bottomAxisValues[i]);
            axisLabelPaint.getTextBounds(bottomAxisValue, 0, bottomAxisValue.length(), textRect);
            drawBottomAxisLabel(i, i * cellWidth - textRect.width() / 2, getHeight(), canvas);
        }

        paint.setColor(GRID_BORDER_COLOR);
        canvas.drawLine(getWidth() - 1, 0, getWidth() - 1, contentHeight, paint);
        String lastBottomAxisValue = String.valueOf(bottomAxisValues[verticalLinesNumber - 1]);
        axisLabelPaint.getTextBounds(lastBottomAxisValue, 0, lastBottomAxisValue.length(), textRect);
        drawBottomAxisLabel(verticalLinesNumber - 1, getWidth() - textRect.width() - 1, getHeight(), canvas);
    }

    Path path = new Path();
    private void drawPoints(Canvas canvas) {
        path.moveTo(computeRawX(points[0].x), computeRawY(points[0].y));
        for (int i = 1; i < points.length; i++) {
            path.lineTo(computeRawX(points[i].x), computeRawY(points[i].y));
        }

        canvas.drawPath(path, chartLinePaint);
        path.reset();
    }

    private float computeRawX(float x) {
        return x / (float) verticalLinesNumber * getWidth();
    }

    private float computeRawY(float y) {
        return (y - min) / (max - min) * contentHeight;
    }

    float min = preClose - diff - PADDING_VALUE;
    float max = preClose + diff + PADDING_VALUE;

    private void generateLeftAxisValues() {
        float step = (max - min) / horizontalLinesNumber;
        for (int i = 0; i < leftAxisValues.length; i++) {
            leftAxisValues[i] = min + i * step;
        }
    }

    private void generateBottomAxisValues() {
        for (int i = 0; i < bottomAxisValues.length; i++) {
            bottomAxisValues[i] = i;
        }
    }

    private void drawLeftAxisLabel(int position, float x, float y, Canvas canvas) {
        canvas.drawText(String.valueOf(leftAxisValues[position]), x, y, axisLabelPaint);
    }

    private void drawBottomAxisLabel(int position, float x, float y, Canvas canvas) {
        canvas.drawText(String.valueOf(bottomAxisValues[position]), x, y, axisLabelPaint);
    }
}
