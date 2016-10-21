package com.leiyu.iboard.draw;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.leiyu.iboard.ScoreShowView;

/**
 * Created by leiyu on 2016/10/21.
 */

public abstract class AShape implements IShape {

    protected ScoreShowView showView;
    protected Paint paint;

    public AShape(ScoreShowView showView, int model) {
        this.showView = showView;
        //定义画笔
        paint = new Paint();
        //抗锯齿
        paint.setAntiAlias(true);
        //设置画笔颜色
        paint.setColor(showView.getColor());
        //设置画笔为空心
        paint.setStyle(Paint.Style.STROKE);
        //设置画笔宽度
        paint.setStrokeWidth(showView.getPenWidth());
        //颜色高精度
        paint.setDither(true);
        //结合处、画笔皆圆滑
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }
}
