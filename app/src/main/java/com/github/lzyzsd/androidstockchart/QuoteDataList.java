package com.github.lzyzsd.androidstockchart;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Bruce on 2/28/15.
 */
public class QuoteDataList {
    @SerializedName("datas")
    public ArrayList<QuoteData> data;
    public CategoryInfo info;
}
