package com.leiyu.iboard.draw;

import android.graphics.Canvas;

import java.io.Serializable;
import java.util.List;


/**
 * Created by leiyu on 2016/10/21.
 */

public class Curve extends AShape implements Serializable {

    private float startX,startY = 0;
    private SerialPath serialPath;

    public Curve(int model) {
        super(model);
        serialPath = new SerialPath();
    }

    @Override
    public void touchDown(float x, float y) {
        this.startX = x;
        this.startY = y;
        serialPath.moveTo(startX, startY);
        serialPath.addPoint(new float[]{(float)Math.round(x*10)/10, (float)Math.round(y*10)/10});
    }

    @Override
    public void touchMove(float x, float y) {

        float endX = (x + startX) / 2;
        float endY = (y + startY) / 2;

        serialPath.addPoint(new float[]{(float)Math.round(x*10)/10, (float)Math.round(y*10)/10});

        serialPath.quadTo(startX, startY, endX, endY);
        this.startX = x;
        this.startY = y;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(serialPath, paint);
    }

    @Override
    public List<float[]> getPoints() {
        return serialPath.getPoints();
    }
}
