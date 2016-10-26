package com.leiyu.iboard.draw;

import android.graphics.Paint;
import com.leiyu.iboard.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by leiyu on 2016/10/21.
 */

public abstract class AShape implements IShape, Serializable {

    protected static final long serialVersionID = 1L;

    public static final int STATUS_MOVE = 1;
    public static final int STATUS_END = 2;

    protected Paint paint;
    protected int status = 0; //0-初始；1-移动；2-结束
    protected long time = 0;
    protected long iboardID = 0;

    public AShape(int model) {
        time = System.currentTimeMillis();
        //定义画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        //抗锯齿
        //设置画笔颜色
        paint.setColor(ScoreShowView.getColor());
        //设置画笔为空心
        paint.setStyle(Paint.Style.STROKE);
        //设置画笔宽度
        paint.setStrokeWidth(ScoreShowView.getPenWidth());
        //颜色高精度
        paint.setDither(true);
        //结合处、画笔皆圆滑
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setStatusMove() {
        this.status = STATUS_MOVE;
    }

    public void setStatusEnd() {
        this.status = STATUS_END;
    }

    public long getTime() {
        return time;
    }

    public void setIboardID(long iboardID) { this.iboardID = iboardID;}
}
