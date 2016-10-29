package com.leiyu.iboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    //角色，0-无效；1-教师；2-学生；9-admin
    public static int role = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //这里从strings.xml中获取角色，供调试用。正式运行中应从登陆信息中获取
        role = getResources().getInteger(R.integer.role);
        final ScoreShowView showView = (ScoreShowView) findViewById(R.id.scoreShowView);

        showView.showScore("s1");

        ContextInfo.setContext(getApplicationContext());
        ContextInfo.setActivity(MainActivity.this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {}

                showView.iboardStart();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        ScoreShowView showView = (ScoreShowView) findViewById(R.id.scoreShowView);
        showView.iboardEnd();

        super.onDestroy();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
