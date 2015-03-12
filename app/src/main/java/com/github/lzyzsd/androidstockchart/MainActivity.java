package com.github.lzyzsd.androidstockchart;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;


public class MainActivity extends ActionBarActivity {

    final Category[] categories = new Category[] {
        new Category("TPME.XAGUSD", "现货白银", "360-1440;0-240"),
        new Category("INAU.XAU", "伦敦金", "360-1440;0-360"),
        new Category("INAU.XAG", "伦敦银", "360-1440;0-360"),
        new Category("SGE.AGT+D", "白银延期", "1260-1440;0-150;540-690;810-930")
    };
    ArrayAdapter<Category> spinnerArrayAdapter;

    private Category selectedCategory = categories[0];

    private AtomicBoolean isFetching = new AtomicBoolean(false);

    LineChartView chartView;
    ProgressBar progressBar;

    QuoteService quoteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        chartView = (LineChartView) findViewById(R.id.chart_view);
        chartView.setBondCategory(selectedCategory.bondCategory);

        initSpinner();
        initQuoteService();

        fetch();

//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(runnable);
//            }
//        }, 0, 10_000);
    }

    private void initQuoteService() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new QuoteData.DateTimeTypeAdapter())
                .create();
        RestAdapter.Builder builder = new RestAdapter.Builder();
        RestAdapter restAdapter = builder.setClient(new OkClient())
                .setEndpoint("http://api.baidao.com")
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setConverter(new GsonConverter(gson))
                .build();
        quoteService = restAdapter.create(QuoteService.class);
    }

    private void initSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories);
        spinner.setAdapter(spinnerArrayAdapter);
        if (Build.VERSION.SDK_INT >= 17) {
            spinner.setDropDownVerticalOffset(40);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category category = spinnerArrayAdapter.getItem(position);
                if (selectedCategory.id.equals(category.id)) {
                    return;
                }
                selectedCategory = category;
                chartView.setBondCategory(selectedCategory.bondCategory);
                fetch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fetch() {
        if (isFetching.compareAndSet(false, true)) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            return;
        }
        String dateStr = DateTime.now().toString("YYYYMMDDHHmmss");
        AndroidObservable.bindActivity(this, quoteService.getQuote(selectedCategory.id, 1, dateStr))
                .subscribe(new Subscriber<QuoteDataList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        isFetching.set(false);
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(QuoteDataList quoteDataList) {
                        isFetching.set(false);
                        progressBar.setVisibility(View.GONE);
                        chartView.setPreClose(quoteDataList.info.preclose);
                        LineChartData lineChartData = buildChartData(quoteDataList);
                        chartView.setChartData(lineChartData);
                    }
                });
    }

    private LineChartData buildChartData(QuoteDataList quoteDataList) {
        List<Line> lines = new ArrayList<>();
        List<Point> points = convertQuotesToPoints(quoteDataList.data);
        Line line = new Line(points);
        lines.add(line);
        Line avgLine = AvgComputator.getAvgLine(line);
        avgLine.setColor(Color.BLUE);
        lines.add(avgLine);

        LineChartData lineChartData = new LineChartData(lines);

        Axis<Float> leftYAxis = new Axis<>();
        Axis<Long> bottomAxis = new Axis<>();
        List<AxisValue<Long>> bottomAxisValues = new ArrayList<>();
        AxisValue<Long> start = new AxisValue<>((long) DateUtil.getTradeStartMinuteFromBondCategory(selectedCategory.bondCategory));
        AxisValue<Long> end = new AxisValue<>((long)DateUtil.getTradeEndMinuteFromBondCategory(selectedCategory.bondCategory));
        bottomAxisValues.add(start);
        bottomAxisValues.add(end);
        bottomAxis.setValues(bottomAxisValues);
        bottomAxis.setAxisValueFormatter(new AxisValueFormatter<AxisValue<Long>>() {
            @Override
            public String format(AxisValue<Long> axisValue) {
                int hour = axisValue.getValue().intValue() / 60;
                int minute = axisValue.getValue().intValue() % 60;

                DateTime  dateTime = DateTime.now().withTimeAtStartOfDay().withHourOfDay(hour).withMinuteOfHour(minute);
                return dateTime.toString("hh:mm");
            }
        });
        lineChartData.setAxisYLeft(leftYAxis);
        lineChartData.setAxisXBottom(bottomAxis);
        return lineChartData;
    }

    private ArrayList<Point> convertQuotesToPoints(ArrayList<QuoteData> quotes) {
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

        float lastNonZeroValue = firstNonZeroValue;
        for (int i = 0; i < quotes.size(); i++) {
            QuoteData quoteData = quotes.get(i);
            if (i > firstNonZeroPosition) {
                //数据中间的零值需要丢弃
                if (quoteData.open > 0) {
                    lastNonZeroValue = quoteData.open;
                    points.add(new Point(quoteData.updateTime.getMillis(), quoteData.open));
                } else {
                    points.add(new Point(quoteData.updateTime.getMillis(), lastNonZeroValue));
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

    public class Category {
        String id;
        String name;
        String bondCategory;

        public Category(String id, String name, String bondCategory) {
            this.id = id;
            this.name = name;
            this.bondCategory = bondCategory;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
