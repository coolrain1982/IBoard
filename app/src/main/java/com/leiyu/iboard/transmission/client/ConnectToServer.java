package com.leiyu.iboard.transmission.client;


import android.app.AlertDialog;
import android.content.DialogInterface;

import com.leiyu.iboard.ContextInfo;
import com.leiyu.iboard.MainActivity;
import com.leiyu.iboard.R;

import java.net.Socket;

/**
 * Created by leiyu on 2016/10/25.
 */

public class ConnectToServer {

    private String serverDNS="";
    private int port=0;
    private Socket socket = null;

    public Socket connect() throws ConnectToServerException {
        try {
            serverDNS = ContextInfo.getContext().getResources().getString(R.string.server_dns);
            port = ContextInfo.getContext().getResources().getInteger(R.integer.iboard_port);
            socket = new Socket(serverDNS, port);

            return socket;
        } catch (Exception e) {
            throw new ConnectToServerException(e.getMessage(), serverDNS, Integer.toString(port));
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void showFailedDialog() {
        final AlertDialog.Builder dia = new AlertDialog.Builder(ContextInfo.getActivity());
        dia.setTitle("Connect to board server failed!");
        dia.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dia.show();
    }
}
