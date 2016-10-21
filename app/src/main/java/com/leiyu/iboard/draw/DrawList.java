package com.leiyu.iboard.draw;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leiyu on 2016/10/21.
 */

public class DrawList {
    private List<DrawPath> drawPathList = new ArrayList<>() ;

    public void add(DrawPath path) {
        synchronized (this) {
            drawPathList.add(path);
        }
    }

    public List<DrawPath> getAllDrawPath() {

        List<DrawPath> rtnList = new ArrayList<>();

        synchronized (this) {
            for(int i = 0; i < rtnList.size(); i++) {
                rtnList.add(drawPathList.get(i));
            }
        }

        return rtnList;
    }
}
