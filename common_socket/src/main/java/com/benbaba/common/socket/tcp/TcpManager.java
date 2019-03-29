package com.benbaba.common.socket.tcp;

import com.benbaba.common.socket.ThreadPoolManager;
import com.benbaba.common.socket.callback.OnReceiveSocketCallBack;
import com.benbaba.common.socket.callback.OnSocketConnectCallBack;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Tcp管理类 创建和销毁Tcp服务端
 * 管理所有得连接线程
 */
public class TcpManager {

    private static volatile TcpManager INSTANCE;
    private Map<String, TcpRunnable> mConnectSocketMap;//连接到Tcp服务端得Socket集合
    private TcpServer mTcpServer;
    private OnReceiveSocketCallBack mReceiveSocketCallBack;

    private TcpManager() {
        mConnectSocketMap = new HashMap<>();
    }

    public static TcpManager getInstance() {
        if (INSTANCE == null) {
            synchronized (TcpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TcpManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 设置接收Socket消息的回调接口
     */
    public void setOnReceiveSocketCallBack(OnReceiveSocketCallBack callBack) {
           this.mReceiveSocketCallBack = callBack;
    }

    /**
     * 开启Tcp服务端接收线程
     */
    public void startTcpServer(int port) {
        if (mTcpServer != null) {
            return;
        }
        mTcpServer = new TcpServer(port, new OnSocketConnectCallBack() {
            @Override
            public void connect(Socket socket) {
                TcpRunnable tcpRunnable = new TcpRunnable(socket, mReceiveSocketCallBack);
                //ip:port为key 存储tcpRunnable到内存进行统一管理
                String key = socket.getInetAddress().toString() + ":" + socket.getPort();
                mConnectSocketMap.put(key, tcpRunnable);
                //放到线程池中执行
                ThreadPoolManager.getInstance().execute(tcpRunnable);
            }
        });
    }

    /**
     * 发送消息到指定的客户端
     *
     * @param key
     * @param message
     */
    public boolean sendMsg(String key, String message) {
        if (!mConnectSocketMap.containsKey(key)) {
            return false;
        }
        TcpRunnable runnable = mConnectSocketMap.get(key);
        if (runnable == null) {
            return false;
        }
        return runnable.send(message);
    }

    /**
     * 群发消息
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        for (Map.Entry<String, TcpRunnable> entry : mConnectSocketMap.entrySet()) {
            TcpRunnable runnable = entry.getValue();
            if (runnable != null) {
                runnable.send(msg);
            }
        }
    }

    /**
     * 断开指定的Socket连接
     *
     * @param key
     * @return
     */
    public boolean disConnectSocket(String key) {
        if (!mConnectSocketMap.containsKey(key)) {
            return false;
        }
        TcpRunnable runnable = mConnectSocketMap.get(key);
        if (runnable == null) {
            return false;
        }
        //关闭资源
        runnable.close();
        ThreadPoolManager.getInstance().remove(runnable);
        synchronized (TcpManager.this) {
            mConnectSocketMap.remove(key);
        }
        return true;
    }


}

