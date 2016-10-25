package com.leiyu.iboard;

import android.app.Activity;
import android.content.Context;

/**
 * Created by leiyu on 2016/10/25.
 */

public class ContextInfo {

    private static Context context = null;
    private static Activity activity = null;

    public static void setContext(Context context) {
        ContextInfo.context = context;
    }

    public static void setActivity(Activity activity) {
        ContextInfo.activity = activity;
    }

    public static Context getContext() {
        return context;
    }

    public static Activity getActivity() {return activity;}
}
