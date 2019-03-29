package com.benbaba.tob;

import com.bhx.common.BaseApplication;
import com.bhx.common.utils.LogUtils;

public class App extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.init();
    }
}
