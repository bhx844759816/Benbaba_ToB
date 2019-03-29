package com.benbaba.common.socket.tcp;

import com.benbaba.common.socket.callback.OnReceiveSocketCallBack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 管理client连接得线程
 */
public class TcpRunnable implements Runnable {
    private Socket mClientSocket;
    private PrintWriter pw;
    private InputStream is = null;
    private OutputStream os = null;
    private byte[] buff = new byte[1024];
    private String key;//
    private OnReceiveSocketCallBack mCallBack;

    TcpRunnable(Socket socket, OnReceiveSocketCallBack callback) {
        try {
            mCallBack = callback;
            mClientSocket = socket;
            mClientSocket.setSoTimeout(5000);
            os = socket.getOutputStream();
            is = socket.getInputStream();
            pw = new PrintWriter(os, true);
            key = mClientSocket.getInetAddress().toString() + ":" + socket.getPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (mClientSocket != null) {
            try {
                mClientSocket.close();
                mClientSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (is != null) {
            try {
                is.close();
                is = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (pw != null) {
            pw.close();
            pw = null;
        }
        if (os != null) {
            try {
                os.close();
                os = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 往客户端发送消息
     *
     * @param msg
     * @return
     */
    public boolean send(String msg) {
        if (pw == null || pw.checkError()) {
            return false;
        }
        pw.println(msg);
        pw.flush(); //强制送出数据
        return true;
    }

    @Override
    public void run() {
        String rcvMsg;
        int rcvLen;
        while (mClientSocket != null && !mClientSocket.isClosed() &&
                mClientSocket.isConnected() && !mClientSocket.isInputShutdown()) {
            try {
                if ((rcvLen = is.read(buff)) != -1) {
                    rcvMsg = new String(buff, 0, rcvLen);
                    //接收到客户端发送过来得消息
                    if (mCallBack != null) {
                        mCallBack.receiveMsg(key, rcvMsg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mCallBack != null) {
            mCallBack.socketClose(key);
        }
        close();
    }
}
