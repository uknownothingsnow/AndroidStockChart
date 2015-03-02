package com.github.lzyzsd.androidstockchart;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by Bruce on 2/28/15.
 */
public class QuoteData {
    public float open;
    public int index;
    public String percent;
    @SerializedName("updatetime")
    public DateTime updateTime;
    public float updrop;
    public float high;
    public float low;
    public float close;

    public static class DateTimeTypeAdapter extends TypeAdapter<DateTime> {
        DateTimeFormatter fmt1 = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
        DateTimeFormatter fmt2 = DateTimeFormat.forPattern("yyyyMMdd");

        @Override
        public void write(JsonWriter out, DateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }

            out.value(value.getMillis());
        }

        @Override
        public DateTime read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return deserializeToDate(in.nextString());
        }

        private synchronized DateTime deserializeToDate(String json) {
            try {
                return DateUtil.parse_yyyyddMM_hhmmss(json);
            } catch (Exception e){}

            try {
                return DateUtil.parse_yyyyddMM(json);
            } catch (Exception e){}

            return null;
        }
    }
}
