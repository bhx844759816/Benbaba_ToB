package com.benbaba.common.socket.udp;

import android.os.Looper;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.benbaba.common.socket.ThreadPoolManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class UdpManager {
    private static UdpManager mInstance;

    /**
     * 获取单例对象
     *
     * @return
     */
    public static UdpManager getInstance() {
        if (mInstance == null) {
            synchronized (UdpManager.class) {
                if (mInstance == null) {
                    mInstance = new UdpManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 发送udp消息并接收消息
     *
     * @param sendMsg     发送得消息
     * @param sendIp      发送到服务端得IP地址
     * @param sendPort    发送到服务端得端口号
     * @param receivePort 本地接收端得端口号
     * @return
     */
    public Pair<String, String> sendMsg(final byte[] sendData, final String sendIp, final int sendPort, final int receivePort) {
        Future<Pair<String, String>> resultFuture = ThreadPoolManager.getInstance().submit(new Callable<Pair<String, String>>() {
            @Override
            public Pair<String, String> call() throws Exception {
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket(null);
                    socket.setReuseAddress(true); // 允许多个DatagramSocket 绑定到同一个端口号
                    socket.setBroadcast(true);
                    socket.setSoTimeout(1000); // 1s得接收延迟
                    socket.bind(new InetSocketAddress(receivePort));
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, 0, receiveData.length);
                    DatagramPacket sendPacket = new DatagramPacket(sendData, 0, sendData.length);
                    sendPacket.setAddress(InetAddress.getByName(sendIp));
                    sendPacket.setPort(sendPort);
                    socket.send(sendPacket);
                    socket.receive(receivePacket);
                    String receiveMsg = new String(receiveData, 0, receivePacket.getLength());
                    String receiveAddress = receivePacket.getAddress().toString().substring(1);
                    if (!TextUtils.isEmpty(receiveMsg)) {
                        return new Pair<>(receiveAddress, receiveMsg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (socket != null) {
                        socket.close();
                    }
                }
                return null;
            }
        });
        try {
            return resultFuture.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 在当前局域网发送广播
     */
    public void sendBroadcast(final byte[] sendData, final  int sendPort,final int receivePort) {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket(null);
                    socket.setReuseAddress(true); // 允许多个DatagramSocket 绑定到同一个端口号
                    socket.setBroadcast(true);
                    socket.setSoTimeout(1000);
                    socket.bind(new InetSocketAddress(receivePort));
                    DatagramPacket sendPacket = new DatagramPacket(sendData, 0, sendData.length);
                    sendPacket.setAddress(InetAddress.getByName("255.255.255.255"));
                    sendPacket.setPort(sendPort);
                    socket.send(sendPacket);
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(socket != null){
                        socket.close();
                    }
                }
            }
        });
    }


    /**
     * 发送消息并接收消息
     *
     * @param sendMsg     发送得消息
     * @param serverIp    服务端得IP
     * @param serverPort  服务端得address
     * @param receivePort 接收端得端口号
     * @return Pair 得key是接收得IP地址 value是接收得消息
     * @throws SocketException
     * @throws UnknownHostException
     */
    @WorkerThread
    public Pair<String, String> sendMsgForReceive(String sendMsg, String serverIp, int serverPort, int receivePort)
            throws SocketException, UnknownHostException {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            throw new RuntimeException("请在子线程运行");
        }
        DatagramSocket socket = new DatagramSocket(null);
        socket.setReuseAddress(true); // 允许多个DatagramSocket 绑定到同一个端口号
        socket.setBroadcast(true);
        socket.setSoTimeout(1000);
        socket.bind(new InetSocketAddress(receivePort));
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, 0, receiveData.length);
        byte[] data = sendMsg.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, 0, data.length);
        sendPacket.setAddress(InetAddress.getByName(serverIp));
        sendPacket.setPort(serverPort);
        try {
            socket.send(sendPacket);
            Log.i("TAG", "sendMsg:" + sendMsg);
            socket.receive(receivePacket);
            String receiveMsg = new String(receiveData, 0, receivePacket.getLength());
            Log.i("TAG", "receiveMsg:" + receiveMsg);
            String receiveAddress = receivePacket.getAddress().toString().substring(1);
            if (!TextUtils.isEmpty(receiveMsg) && !sendMsg.equals(receiveMsg)) {
                return new Pair<>(receiveAddress, receiveMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
        return null;
    }


}

