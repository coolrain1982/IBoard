package com.leiyu.iboard.draw;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leiyu on 2016/10/21.
 */

public class DrawList {
    private List<IShape> shapeList = new ArrayList<>() ;

    public void add(IShape shape) {
        synchronized (this) {
            shapeList.add(shape);
        }
    }

    public List<IShape> getAllshape() {

        List<IShape> rtnList = new ArrayList<>();

        synchronized (this) {
            for(int i = 0; i < shapeList.size(); i++) {
                rtnList.add(shapeList.get(i));
            }
        }

        return rtnList;
    }
}
