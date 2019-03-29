package com.benbaba.common.socket.callback;

import java.net.Socket;

public interface OnReceiveSocketCallBack {
    /**
     * 接收到客户端发送的消息
     *
     * @param key ip:port 存储的Key用于查找对应的TcpRunnable
     * @param msg
     */
    void receiveMsg(String key, String msg);

    /**
     * socket关闭
     *
     * @param key
     */
    void socketClose(String key);
}
