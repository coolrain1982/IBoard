package com.leiyu.iboard.Socket;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by leiyu on 2016/10/25.
 */

public class SocketHandler implements SocketStatusListener {

    private Socket socket = null;
    private ReadTask reader;
    private WriteTask writer;

    public SocketHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.socket.setTcpNoDelay(true);
        reader = new ReadTask(socket);
        writer = new WriteTask(socket);
        onSocketStatusChanged(socket, STATUS_OPEN, null);
    }

    public void sendMessage(String msg) {
        //writer.send(msg);
    }

    public void listen(boolean isListen) {
        reader.startListener(this);
    }

    public void shutDown() {
        try {
            //writer.finish();
        } catch (Exception e) {

        }

        try {
            reader.finish();
        } catch (Exception e) {

        }

        try {
            socket.close();
        } catch (Exception e) {

        }

        reader = null;
        writer = null;
    }

    @Override
    public void onSocketStatusChanged(Socket socket, int status, IOException e) {
        switch (status) {
            case SocketStatusListener.STATUS_CLOSE:
            case SocketStatusListener.STATUS_RESET:
            case SocketStatusListener.STATUS_PIP_BROKEN:
                shutDown();
                break;
            default:
                break;
        }
    }
}
