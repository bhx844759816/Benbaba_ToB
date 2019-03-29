package com.benbaba.common.socket.tcp;

import com.benbaba.common.socket.callback.OnSocketConnectCallBack;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Tcp得服务端
 * 接收到Socket连接将Socket保存到全局
 */
public class TcpServer implements Runnable {
    private int mPort; // 服务端绑定得端口号
    private OnSocketConnectCallBack mSocketConnectCallBack; //
    private ServerSocket mServerSocket;
    private boolean isRunning;

    TcpServer(int port, OnSocketConnectCallBack callBack) {
        mPort = port;
        this.isRunning = true;
    }

    @Override
    public void run() {
        try {
            mServerSocket = new ServerSocket();
            InetSocketAddress socketAddress = new InetSocketAddress(mPort);
            mServerSocket.bind(socketAddress);
            while (isRunning && !Thread.interrupted()) {
                Socket client = mServerSocket.accept();
                String clientIp = client.getInetAddress().getHostAddress();
                if (mSocketConnectCallBack != null) {
                    mSocketConnectCallBack.connect(client);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (mServerSocket != null)
                    mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
