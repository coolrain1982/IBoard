package com.leiyu.iboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.leiyu.iboard.draw.AShape;
import com.leiyu.iboard.draw.Curve;
import com.leiyu.iboard.draw.DrawList;
import com.leiyu.iboard.draw.IShape;
import com.leiyu.iboard.score.LoadScore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leiyu on 2016/10/20.
 */

public class ScoreShowView extends View {

    private String scoreName = null;
    private int width, height = 0;
    private ScoreShowView self = null;
    private Map<String, DrawList> drawListMap = new HashMap<>();
    private final  Boolean drawListSyn = Boolean.FALSE;
    private Bitmap scoreBitmap = null;
    private boolean isInit = true;
    private AShape curShape = null;

    public ScoreShowView(Context context, AttributeSet as) {
        super(context, as);
        self = this;
        //取view的长度和宽度
        this.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        width = self.getWidth();
                        height = self.getHeight();
                    }
                });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInit) {
            this.scoreBitmap = LoadScore.loadScore(getContext(), scoreName, width, height);
            isInit = false;
        }

        if (scoreBitmap != null) {
            canvas.drawBitmap(scoreBitmap, 0, 0, null);

            if (drawListMap.containsKey(scoreName)) {
                //开始画记号
                List<IShape> drawPathList = getDrawList();
                if (drawPathList.size() > 0) {
                    canvas.save();

                    for (IShape drawPath : drawPathList) {
                        drawPath.draw(canvas);
                    }

                    canvas.restore();
                }
            }
        }

        if (curShape != null) {
            canvas.save();
            curShape.draw(canvas);
            canvas.restore();
        }

    }

    public void showScore(String scoreName) {

        this.scoreName = scoreName;

        Bitmap bitmap = LoadScore.loadScore(getContext(), scoreName, width, height);

        if (bitmap != null) {

            this.scoreBitmap = bitmap;
            invalidate();
        }
    }

    /**
     *增加画图路径
     */
    public void addDrawList(IShape drawPath) {

        DrawList drawList = null;
        synchronized (drawListSyn) {
            if (!drawListMap.containsKey(scoreName)) {
                drawListMap.put(scoreName, new DrawList());
            }

            drawList = drawListMap.get(scoreName);
            drawList.add(drawPath);
        }
    }

    public List<IShape> getDrawList() {

        synchronized (drawListSyn) {
            if (drawListMap.containsKey(scoreName)) {
                return drawListMap.get(scoreName).getAllshape();
            }
        }

        return null;
    }

    /**
     * 获取画笔颜色
     * @return
     */
    public static int getColor() {
        return Color.RED;
    }

    public  static float getPenWidth() {
        return 3.0f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                curShape = new Curve(0);
                curShape.touchDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE :
                curShape.touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                invalidate();
                addDrawList(curShape);
                break;
        }

        return true;
    }

}
