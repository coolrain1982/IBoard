package com.leiyu.iboard;

import com.leiyu.iboard.draw.AShape;
import com.leiyu.iboard.draw.Curve;
import com.leiyu.iboard.transmission.Command;
import com.leiyu.iboard.transmission.CommandProcess;
import com.leiyu.iboard.transmission.SerializeTool;

import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
//        StringBuffer sb = new StringBuffer();
//        sb.append("erfd");
//        sb.append("45445444");
//        CommandProcess cp3 = new CommandProcess(sb);
//        Command cd3 = cp3.getCommand();
//
//        String cmd = "abcdefghijlm";
//        sb.append("0001");
//        sb.append(String.format("%08d", cmd.length()));
//        sb.append(cmd);
//        sb.append("0002");
//        sb.append("00000005");
//        sb.append("no");
//
//        CommandProcess cp = new CommandProcess(sb);
//        Command cd = cp.getCommand();
//
//        CommandProcess cp2 = new CommandProcess(sb);
//        Command cd2 = cp2.getCommand();



        assertEquals(4, 2 + 2);
    }
}