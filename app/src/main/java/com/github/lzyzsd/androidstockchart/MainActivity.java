package com.github.lzyzsd.androidstockchart;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import org.joda.time.DateTime;

import java.util.ArrayList;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.functions.Func1;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        QuoteService quoteService = restAdapter.create(QuoteService.class);

        AndroidObservable.bindActivity(this, quoteService.getQuote("TPME.XAGUSD", 1, "20140701120000"))
                .subscribe(new Subscriber<QuoteDataList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(QuoteDataList quoteDataList) {
                        chartView.setPreClose(quoteDataList.info.preclose);
                        chartView.setPoints(convertQuotesToPoints(quoteDataList.data, quoteDataList.info.preclose));
                    }
                });
    }

    private ArrayList<Point> convertQuotesToPoints(ArrayList<QuoteData> quotes, float preClose) {
        ArrayList<Point> points = new ArrayList<>(quotes.size());
        boolean shouldThrowZero = false;
        for (QuoteData quoteData : quotes) {
            if (quoteData.open > 0) {
                shouldThrowZero = true;
                points.add(new Point(quoteData.updateTime.getMillis(), quoteData.open));
            } else if (!shouldThrowZero) {
                points.add(new Point(quoteData.updateTime.getMillis(), preClose));
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
