package com.github.lzyzsd.androidstockchart;

import com.github.lzyzsd.androidstockchart.model.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruce on 3/2/15.
 */
public class AvgComputator {
    public static Line getAvgLine(Line line) {
        List<Point> points = line.getPoints();
        List<Point> newPoints = new ArrayList<>(points.size() * 2);
        float y = 0;
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            y += point.y;
            Point newPoint = new Point(point.x, y/(i+1));
            newPoints.add(newPoint);
        }

        return new Line(newPoints);
    }
}
