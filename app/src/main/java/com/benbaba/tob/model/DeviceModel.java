package com.benbaba.tob.model;

import android.net.wifi.ScanResult;

public class DeviceModel {
    private ScanResult result;
    private boolean isSelect;
    private boolean isStartSetting;
    private int settingResult = -1;

    public int getSettingResult() {
        return settingResult;
    }

    public void setSettingResult(int settingResult) {
        this.settingResult = settingResult;
    }

    public boolean isVisibility() {
        return isVisibility;
    }

    public void setVisibility(boolean visibility) {
        isVisibility = visibility;
    }

    private boolean isVisibility;

    public ScanResult getResult() {
        return result;
    }

    public void setResult(ScanResult result) {
        this.result = result;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isStartSetting() {
        return isStartSetting;
    }

    public void setStartSetting(boolean startSetting) {
        isStartSetting = startSetting;
    }
}
