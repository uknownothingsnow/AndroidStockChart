package com.github.lzyzsd.androidstockchart.formatter;

import com.github.lzyzsd.androidstockchart.model.YAxisValue;

/**
 * Created by Bruce on 3/4/15.
 */
public interface AxisValueFormatter<T> {
    public String format(T value);
}
