package com.github.lzyzsd.androidstockchart;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.github.lzyzsd.androidstockchart.formatter.AxisValueFormatter;
import com.github.lzyzsd.androidstockchart.model.Axis;
import com.github.lzyzsd.androidstockchart.model.AxisValue;
import com.github.lzyzsd.androidstockchart.model.Line;
import com.github.lzyzsd.androidstockchart.model.LineChartData;
import com.github.lzyzsd.androidstockchart.util.XAxisUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;


public class MainActivity extends ActionBarActivity {

    ProgressBar progressBar;

    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        final LineChartView chartView = (LineChartView) findViewById(R.id.chart_view);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new QuoteData.DateTimeTypeAdapter())
                .create();
        RestAdapter.Builder builder = new RestAdapter.Builder();
        RestAdapter restAdapter = builder.setClient(new OkClient())
                .setEndpoint("http://api.baidao.com")
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setConverter(new GsonConverter(gson))
                .build();
        final QuoteService quoteService = restAdapter.create(QuoteService.class);

        runnable = new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                fetch(chartView, quoteService);
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(runnable);
            }
        }, 0, 10_000);
    }

    private void fetch(final LineChartView chartView, QuoteService quoteService) {
        String dateStr = DateTime.now().toString("YYYYMMDDHHmmss");
        AndroidObservable.bindActivity(this, quoteService.getQuote("TPME.XAGUSD", 1, dateStr))
                .subscribe(new Subscriber<QuoteDataList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(QuoteDataList quoteDataList) {
                        progressBar.setVisibility(View.GONE);
                        chartView.setPreClose(quoteDataList.info.preclose);
                        List<Line> lines = new ArrayList<>();
                        List<Point> points = convertQuotesToPoints(quoteDataList.data, quoteDataList.info.preclose);
                        Line line = new Line(points);
                        lines.add(line);
                        Line avgLine = AvgComputator.getAvgLine(line);
                        avgLine.setColor(Color.BLUE);
                        lines.add(avgLine);
                        LineChartData lineChartData = new LineChartData(lines);
                        Axis<Float> leftYAxis = new Axis<>();
                        Axis<Long> bottomAxis = new Axis<>();
                        bottomAxis.setMin(XAxisUtil.getStartValue());
                        bottomAxis.setMax(XAxisUtil.getEndValue());
                        bottomAxis.setStep(XAxisUtil.getCellSize());
                        bottomAxis.setAxisValueFormatter(new AxisValueFormatter<AxisValue<Long>>() {
                            @Override
                            public String format(AxisValue<Long> axisValue) {
                                return DateUtil.format_hh_mm(axisValue.getValue());
                            }
                        });
                        lineChartData.setAxisYLeft(leftYAxis);
                        lineChartData.setAxisXBottom(bottomAxis);
                        chartView.setChartData(lineChartData);
                    }
                });
    }

    private ArrayList<Point> convertQuotesToPoints(ArrayList<QuoteData> quotes, float preClose) {
        ArrayList<Point> points = new ArrayList<>(quotes.size());
        //数据最前面的零值需要设置为第一个非零值6
        int firstNonZeroPosition = 0;
        float firstNonZeroValue = -1;

        for (int i = 0; i < quotes.size(); i++) {
            if (quotes.get(i).open > 0) {
                firstNonZeroPosition = i;
                firstNonZeroValue = quotes.get(i).open;
                break;
            }
        }

        for (int i = 0; i < quotes.size(); i++) {
            QuoteData quoteData = quotes.get(i);
            if (i > firstNonZeroPosition) {
                //数据中间的零值需要丢弃
                if (quoteData.open > 0) {
                    points.add(new Point(quoteData.updateTime.getMillis(), quoteData.open));
                }
            } else {
                points.add(new Point(quoteData.updateTime.getMillis(), firstNonZeroValue));
            }
        }

        return points;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
