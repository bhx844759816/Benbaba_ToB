package com.benbaba.tob.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.bhx.common.utils.LogUtils;

public class SocketService extends Service {
    public SocketService() {
    }

    //client 可以通过Binder获取Service实例
    public class SocketBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }

    private SocketBinder binder = new SocketBinder();

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.e("onBIND");
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
