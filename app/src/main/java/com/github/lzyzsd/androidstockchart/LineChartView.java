package com.github.lzyzsd.androidstockchart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.github.lzyzsd.androidstockchart.model.Line;
import com.github.lzyzsd.androidstockchart.model.LineChartData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruce on 2/27/15.
 */
public class LineChartView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#2a2e36");
    private static final int CHART_LINE_COLOR = Color.RED;

    private SurfaceHolder holder;
    private Canvas canvas;
    private Paint paint;
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

    private LineChartData chartData = new LineChartData();

    //不包含底部label的高度
    private float contentHeight;

    private AxisRenderer axisRenderer;

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

        axisRenderer = new AxisRenderer(this);
    }

    public void setPreClose(float preClose) {
        this.preClose = preClose;
    }

    public LineChartData getChartData() {
        return chartData;
    }

    public void setChartData(LineChartData chartData) {
        this.chartData = chartData;
        axisRenderer.setMinMaxValue();
        axisRenderer.generateLeftAxisValues();
        axisRenderer.generateBottomAxisValues();
    }

    public float getContentHeight() {
        return contentHeight;
    }

    public int getHorizontalLinesNumber() {
        return horizontalLinesNumber;
    }

    public int getVerticalLinesNumber() {
        return verticalLinesNumber;
    }

    public float[] getLeftAxisValues() {
        return leftAxisValues;
    }

    public long[] getBottomAxisValues() {
        return bottomAxisValues;
    }

    public float getPreClose() {
        return preClose;
    }

    public List<Line> getLines() {
        return chartData.getLines();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        paint = new Paint();
        paint.setColor(DEFAULT_BACKGROUND_COLOR);

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
        contentHeight = height - axisRenderer.getBottomLabelHeight();
    }

    @Override
    public void run() {
        while (isRunning )
        {
            if (chartData.getLines().size() == 0) {
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
                axisRenderer.drawHorizontalLines(canvas);
                axisRenderer.drawVerticalLines(canvas);
                drawLines(canvas);
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public float getCellWidth() {
        return getWidth() / (verticalLinesNumber - 1);
    }

    public float getCellHeight() {
        return getContentHeight() / (horizontalLinesNumber - 1);
    }

    private void drawLines(Canvas canvas) {
        for (Line line : chartData.getLines()) {
            chartLinePaint.setColor(line.getColor());
            drawPoints(canvas, line.getPoints());
        }
    }

    Path path = new Path();
    private void drawPoints(Canvas canvas, List<Point> points) {
        path.moveTo(computeRawX(points.get(0).x), computeRawY(points.get(0).y));
        for (int i = 1; i < points.size(); i++) {
            path.lineTo(computeRawX(points.get(i).x), computeRawY(points.get(i).y));
        }

        canvas.drawPath(path, chartLinePaint);
        path.reset();
    }

    private float computeRawX(long x) {
        long start = chartData.getLines().get(0).getPoints().get(0).x;
        float cellPosition =  (x - start) / (float) axisRenderer.getxStepSize();
        return cellPosition * getCellWidth();
    }

    private float computeRawY(float y) {
        return (axisRenderer.getMax() - y) / (axisRenderer.getMax() - axisRenderer.getMin()) * contentHeight;
    }


}
