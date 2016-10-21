package com.leiyu.iboard.draw;

import android.graphics.Canvas;

/**
 * Created by leiyu on 2016/10/21.
 */

public interface IShape {

    public void touchMove(int startX, int startY, int x, int y);

    public void draw(Canvas canvas);
}
