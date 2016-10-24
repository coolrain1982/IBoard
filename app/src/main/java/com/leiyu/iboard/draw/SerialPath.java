package com.leiyu.iboard.draw;

import android.graphics.Path;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by leiyu on 2016/10/23.
 */

public class SerialPath extends Path implements Serializable {

    private static final long serialVersionID = 2016102315581501L;
    private ArrayList<float[]> pathPoint;

    public SerialPath() {
        super();
        pathPoint = new ArrayList<>();
    }

    public SerialPath(SerialPath serialPath) {
        super();
        pathPoint = serialPath.pathPoint;
    }

    public void addPathPoint(float[] points) {
        pathPoint.add(points);
    }

    public void loadPathPointsAdQuadTo() {
        float[] startPointsArray = pathPoint.remove(0);
        moveTo(startPointsArray[0], startPointsArray[1]);

        for(float[] pointsArray:pathPoint) {
            quadTo(pointsArray[0], pointsArray[1],pointsArray[2],pointsArray[3]);
        }
    }
}
