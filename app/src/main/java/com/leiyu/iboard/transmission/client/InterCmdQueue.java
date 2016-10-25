package com.leiyu.iboard.transmission.client;

import java.util.ArrayList;

/**
 * Created by leiyu on 2016/10/25.
 */

public class InterCmdQueue {
    private static ArrayList<String> cmdIn = new ArrayList<>();
    private static ArrayList<String> cmdOut = new ArrayList<>();

    private static final Integer cmdInSyn = 0;
    private static final Integer cmdOutSyn = 1;

    public static void addCmdIn(String cmd) {
        synchronized (cmdInSyn) {
            cmdIn.add(cmd);
        }
    }

    public static String getCmdIn() {
        String rtnStr = null;
        synchronized (cmdInSyn) {
            if (cmdIn.size() > 0) {
                rtnStr = cmdIn.remove(0);
            }
        }

        return rtnStr;
    }

    public static void addCmdOut(String cmd) {
        synchronized (cmdOutSyn) {
            cmdOut.add(cmd);
        }
    }

    public static String getCmdOut() {
        String rtnStr = null;
        synchronized (cmdOutSyn) {
            if (cmdOut.size() > 0) {
                rtnStr = cmdOut.remove(0);
            }
        }

        return rtnStr;
    }
}
