package com.leiyu.iboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.leiyu.iboard.draw.DrawList;
import com.leiyu.iboard.draw.DrawPath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leiyu on 2016/10/20.
 */

public class ScoreShowView extends View {

    private int scoreID = 0;
    private String scoreName = null;
    private int width, height = 0;
    private ScoreShowView self = null;
    private Map<String, DrawList> drawListMap = new HashMap<>();

    public ScoreShowView(Context context, AttributeSet as) {
        super(context, as);
        self = this;
        this.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        width = self.getWidth();
                        height = self.getHeight();
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (scoreID != 0) {
            canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), scoreID),
                    width, height, false), 0, 0, null);

            if (drawListMap.containsKey(scoreName)) {
                //开始画记号
                List<DrawPath> drawPathList = drawListMap.get(scoreName).getAllDrawPath();
                if (drawPathList.size() == 0) {
                    return;
                }

                canvas.save();

                for(DrawPath drawPath : drawPathList) {
                    canvas.drawPath(drawPath.getPath(), drawPath.getPaint());
                }

                canvas.restore();
            }
        }
    }

    public void showScore(String scoreName) {

        //
        int resourceID = getResources().getIdentifier(scoreName, "drawable", "com.leiyu.iboard");

        if (resourceID!=0) {
            scoreID = resourceID;
            this.scoreName = scoreName;
            invalidate();
        }
    }

    /**
     * 获取画笔颜色
     * @return
     */
    public int getColor() {
        return Color.BLACK;
    }

    public  float getPenWidth() {
        return 4.0f;
    }
}
