package com.leiyu.iboard;

import android.content.Context;

/**
 * Created by leiyu on 2016/10/25.
 */

public class ContextInfo {

    private static Context context = null;

    public static void setContext(Context context) {
        ContextInfo.context = context;
    }

    public static Context getContext() {
        return context;
    }
}
