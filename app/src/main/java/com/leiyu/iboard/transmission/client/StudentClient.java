package com.leiyu.iboard.transmission.client;

/**
 * Created by leiyu on 2016/10/24.
 */

public class StudentClient implements Runnable {

    //和服务器的连接是否完好
    private boolean isAlive = true;

    public StudentClient() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            //连接服务器
            InterWithServer interWithServer = new InterWithServer();
            interWithServer.connet();
        }
        catch (ConnectToServerException ctse) {

        }

        isAlive = false;
    }
}
