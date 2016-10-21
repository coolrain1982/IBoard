package com.leiyu.iboard.draw;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by leiyu on 2016/10/21.
 */

public class DrawPath {
    private Path path;
    private Paint paint;

    public Path getPath() {
        return path;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
