package com.benbaba.tob.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.benbaba.common.socket.udp.UdpManager;
import com.benbaba.common.wifi.WiFiConnectManager;
import com.benbaba.common.wifi.utils.WifiUtils;
import com.benbaba.tob.Constants;
import com.benbaba.tob.model.DrumBean;
import com.bhx.common.utils.LogUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 操作鼓设备得管理类
 */
@SuppressWarnings("checkresult")
public class DeviceManager {
    public static final int GPS_SETTING_REQUEST_CODE = 0x021;
    private static final int WIFI_CONNECT_TIMEOUT = 6; //连接WIFI的超时时间
    private WifiUtils mWifiUtils;
    private WiFiConnectManager mWifiManager;
    private Disposable mSearchDisposable;
    private Activity mContext;
    private OnSearchResultListener mListener;
    private Condition mCondition;
    private Lock mLock;
    private Gson mGson;
    private boolean isConnectSuccess;// 连接成功
    private String wifiName;
    private String wifiPsd;

    public DeviceManager(Activity context) {
        mContext = context;
        mLock = new ReentrantLock();
        mCondition = mLock.newCondition();
        mWifiUtils = new WifiUtils(context);
        mGson = new Gson();
        mWifiManager = new WiFiConnectManager(context);
    }


    /**
     * 搜索局域网wifi
     */
    public void startSearch(OnSearchResultListener listener) {
        if (mSearchDisposable != null && !mSearchDisposable.isDisposed()) {
            return;
        }
        mSearchDisposable = Observable.interval(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    List<ScanResult> list = mWifiUtils.startScan();
                    listener.searchResult(list);
                });
    }

    /**
     * 停止搜索附件得wifi
     */
    public void stopSearchWifi() {
        if (mSearchDisposable != null) {
            mSearchDisposable.dispose();
            mSearchDisposable = null;
        }
    }

    /**
     * 设置发送到玩具鼓得wifi信息
     *
     * @param name wifi名称
     * @param psw  wifi密码
     */
    public void setSendDrumWifiMsg(String name, String psw) {
        this.wifiName = name;
        this.wifiPsd = psw;
    }

    private int mCurrentConnectWifiNum;//当前连接的次数

    public void setUpScanResult(final ScanResult scanResult, final String inputPsd,
                                final OnSendWifiToDeviceCallBack callBack) {
        isConnectSuccess = false;
        mWifiManager.connectWiFi(scanResult, inputPsd, new WiFiConnectManager.WiFiConnectListener() {
            @Override
            public void connectStart() {

            }

            @Override
            public void connectResult(String ssid, boolean isSuccess) {
                //连接成功
                if (callBack != null) {
                    if (isSuccess) {
                        connectAndSaveConfig(scanResult, callBack);
                    } else {
                        if (mCurrentConnectWifiNum < 3) {
                            mCurrentConnectWifiNum++;
                            setUpScanResult(scanResult, inputPsd, callBack);
                        }
                    }
                }

            }

            @Override
            public void connectEnd() {

            }
        });
    }

    /**
     * 连接并将wifi信息保存到本地
     */
    private void connectAndSaveConfig(final ScanResult result, final OnSendWifiToDeviceCallBack callBack) {
        Observable.create((ObservableOnSubscribe<DrumBean>) e -> {
            DrumBean drumBean = sendSocketMsg();
            if (drumBean != null) {
                e.onNext(drumBean);
            } else {
                LogUtils.i("connectAndSaveConfig error");
                e.onError(new Exception("send socket error"));
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bean -> callBack.settingResult(result, bean), throwable -> {
                    callBack.settingResult(result, null);
                });
    }


    /**
     * 发送家庭wifi和密码 重试4次
     *
     * @param ssid 需要配置得wifi得SSID
     * @param psd  需要配置得wifi得密码
     * @return socket得
     */
    private DrumBean sendSocketMsg() {
        int counts = 0;
        DrumBean drumBean = null;
        do {
            String sendMsg = DeviceUdpUtils.getWifiSettingJson(wifiName, wifiPsd);
            try {
                String ip = getBroadcastIpAddress();
                if (!ip.startsWith("192.168")) {
                    Thread.sleep(2000);
                    continue;
                }
                ip = getBroadcastIpAddress();
                Pair<String, String> receiveResult = sendMsgForReceive(sendMsg, ip,
                        Constants.DEVICE_PORT, Constants.RECEIVE_PORT);
                if (receiveResult != null) {
                    drumBean = mGson.fromJson(receiveResult.second, DrumBean.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                counts++;
                LogUtils.i("sendSocketMsg count:%s", counts);
            }
        } while (counts < 4 && drumBean == null);
        return drumBean;

    }

    /**
     * 发送消息
     *
     * @param sendMsg     消息内容
     * @param serverIp    发送到服务端得Ip
     * @param serverPort  发送到服务端得端口
     * @param receivePort 接收端得端口
     * @return 第一个参数表示服务端得IP地址 第二个参数表示服务端得返回得消息
     * @throws SocketException
     * @throws UnknownHostException
     */
    public Pair<String, String> sendMsgForReceive(String sendMsg, String serverIp, int serverPort, int receivePort)
            throws SocketException, UnknownHostException {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            throw new RuntimeException("请在子线程运行");
        }
        DatagramSocket socket = new DatagramSocket(null);
        socket.setReuseAddress(true); // 允许多个DatagramSocket 绑定到同一个端口号
        socket.setBroadcast(true);
        socket.setSoTimeout(3000);
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


    /**
     * 搜索结果得回调接口
     */
    public interface OnSearchResultListener {
        void searchResult(List<ScanResult> results);
    }


    public interface OnSendWifiToDeviceCallBack {
        /**
         * 连接设备结果
         *
         * @param result
         * @param state
         */
        void connectResult(ScanResult result, boolean isConnectSuccess);

        /**
         * 设置设备结果
         *
         * @param result
         * @param bean
         * @param state
         */
        void settingResult(ScanResult result, DrumBean bean);
    }

    private String getBroadcastIpAddress() {
        String ip = mWifiUtils.getLocalIpAddress();
        String ip_ = ip.substring(0, ip.lastIndexOf("."));
//        return String.valueOf("192.168.43" + "." + "255");
        return String.valueOf(ip_ + "." + "255");
    }

    public enum DeviceState {
        CONNECT_DEVICE_WIFI, CONNECT_DEVICE_WIFI_SUCCESS, CONNECT_DEVICE_WIFI_ERROR,// 连接设备wifi 连接成功 连接失败
        SEND_WIFI_INFO, SEND_WIFI_INFO_SUCCESS, SEND_WIFI_INFO_ERROR// 发送设备wifi 发送设备成功 发送设备失败

    }

}
