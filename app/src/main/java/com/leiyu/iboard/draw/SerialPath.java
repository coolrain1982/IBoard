package com.leiyu.iboard.draw;

import android.graphics.Path;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leiyu on 2016/10/23.
 */

public class SerialPath extends Path implements Serializable {

    private static final long serialVersionID = 2016102315581501L;

    private List<float[]> points = new ArrayList<>();

    public void addPoint(float[] point) {
        points.add(point);
    }

    public List<float[]> getPoints() {
        return points;
    }

}
