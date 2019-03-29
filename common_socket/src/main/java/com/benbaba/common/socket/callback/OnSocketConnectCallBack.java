package com.benbaba.common.socket.callback;

import java.net.Socket;

/**
 * 连接到服务器中得
 */
public interface OnSocketConnectCallBack {

    /**
     * 连接成功得回调
     *
     * @param ip
     * @param socket
     */
    void connect(Socket socket);



}
