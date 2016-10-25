package com.leiyu.iboard.transmission.client;


import com.leiyu.iboard.ContextInfo;
import com.leiyu.iboard.R;

import java.net.Socket;

/**
 * Created by leiyu on 2016/10/25.
 */

public class InterWithServer {

    private String serverDNS="";
    private int port=0;
    private Socket socket = null;

    public void connet() throws ConnectToServerException {
        try {
            serverDNS = ContextInfo.getContext().getResources().getString(R.string.server_dns);
            port = ContextInfo.getContext().getResources().getInteger(R.integer.iboard_port);

            socket = new Socket(serverDNS, port);
        } catch (Exception e) {
            throw new ConnectToServerException(e.getMessage(), serverDNS, Integer.toString(port));
        }
    }

    public void getInfoFromServer() {

    }
}
