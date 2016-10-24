package com.leiyu.iboard.draw;

import android.graphics.Canvas;

/**
 * Created by leiyu on 2016/10/21.
 */

public interface IShape {

    public void touchMove(float x, float y);

    public void touchDown(float x, float y);

    public void draw(Canvas canvas);
}
