package com.benbaba.common.wifi.utils;

/**
 * Created by code4a on 2017/5/16.
 */

public class NetUtil {

    /**
     * ip地址转换
     * @param address 需要转换的地址
     * @return ip字符串
     */
    public static String getIPAddress(int address) {
        StringBuilder sb = new StringBuilder();
        sb.append(address & 0x000000FF).append(".")
                .append((address & 0x0000FF00) >> 8).append(".")
                .append((address & 0x00FF0000) >> 16).append(".")
                .append((address & 0xFF000000L) >> 24);
        return sb.toString();
    }
}
