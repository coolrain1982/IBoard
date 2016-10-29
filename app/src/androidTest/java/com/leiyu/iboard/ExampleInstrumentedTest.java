package com.leiyu.iboard;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.leiyu.iboard.draw.AShape;
import com.leiyu.iboard.draw.Curve;
import com.leiyu.iboard.transmission.SerializeTool;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        Curve curve = new Curve(0);
        curve.setIboardID(12345);
        //先序列化对象
        String objSerial = SerializeTool.object2String(curve);

        AShape aShape = SerializeTool.getObjectFromString(objSerial, AShape.class);

        assertEquals("com.leiyu.iboard", appContext.getPackageName());
    }
}
