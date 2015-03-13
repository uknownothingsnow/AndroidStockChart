package com.github.lzyzsd.androidstockchart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.github.lzyzsd.androidstockchart.model.Line;
import com.github.lzyzsd.androidstockchart.model.LineChartData;

import java.util.List;

/**
 * Created by Bruce on 2/27/15.
 */
public class LineChartView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#2a2e36");
    public static final int DEFAULT_BORDER_COLOR = Color.parseColor("#333232");
    public static final int DEFAULT_GRID_LINE_COLOR = Color.parseColor("#3a3f48");

    private SurfaceHolder holder;
    private Canvas canvas;
    private Paint paint;
    private Paint chartLinePaint;
    private Thread thread;
    private boolean isRunning;

    private int horizontalLinesNumber = 7;
    private int verticalLinesNumber = 11;
    private float preClose = 0f;

    String bondCategory;

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
        axisRenderer.setBorderColor(DEFAULT_BORDER_COLOR);
        axisRenderer.setLineColor(DEFAULT_GRID_LINE_COLOR);
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

    public float getPreClose() {
        return preClose;
    }

    public List<Line> getLines() {
        return chartData.getLines();
    }

    public void setBondCategory(String bondCategory) {
        this.bondCategory = bondCategory;
    }

    public String getBondCategory() {
        return bondCategory;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        paint = new Paint();
        paint.setColor(DEFAULT_BACKGROUND_COLOR);

        chartLinePaint = new Paint();
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
            if (chartData.getLines().size() == 0 && TextUtils.isEmpty(bondCategory)) {
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
        return getWidth() / (float) (verticalLinesNumber - 1);
    }

    public float getCellHeight() {
        return getContentHeight() / (float) (horizontalLinesNumber - 1);
    }

    private void drawLines(Canvas canvas) {
        for (Line line : chartData.getLines()) {
            chartLinePaint.setColor(line.getColor());
            drawPoints(canvas, line.getPoints());
        }
    }

    Path path = new Path();
    private void drawPoints(Canvas canvas, List<Point> points) {
        path.moveTo(computeRawX(0), computeRawY(points.get(0).y));
        for (int i = 1; i < points.size(); i++) {
            path.lineTo(computeRawX(i), computeRawY(points.get(i).y));
        }

        canvas.drawPath(path, chartLinePaint);
        path.reset();
    }

    private float computeRawX(int position) {
        int totalPoints = DateUtil.getPoints4OneTradeDayFromBondCategory(bondCategory);
        return (getWidth() / (float) (totalPoints - 1)) * position;
    }

    private float computeRawY(float y) {
        return (axisRenderer.getMax() - y) / (axisRenderer.getMax() - axisRenderer.getMin()) * contentHeight;
    }
}
