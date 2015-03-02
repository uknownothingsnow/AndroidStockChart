package com.github.lzyzsd.androidstockchart;

import java.util.ArrayList;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Bruce on 2/28/15.
 */
public interface QuoteService {
    @GET("/api/hq/mtdata.do")
    public Observable<QuoteDataList> getQuote(@Query("sid") String sid, @Query("quotationType") int quotationType, @Query("tradedate") String tradeDate);
}
