package com.leiyu.iboard.score;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by leiyu on 2016/10/23.
 */

public class LoadScore {
     public static Bitmap loadScore(Context context, String scoreName, int width, int height) {
         int resourceID = context.getResources().getIdentifier(scoreName, "drawable", "com.leiyu.iboard");
         try {

             return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), resourceID),
                     width, height, false);
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }
     }
}
