package com.yutao.netutils.listener;

public interface OnNetworkChangeListener {
    /**
     * 网络变化监听器
     * @param isConnect 是否连接
     * @param isWifi 是否是wifi
     * @param isMob 是否是移动数据流量
     * @param extra 额外的信息
     */
    void onChange(boolean isConnect,boolean isWifi,boolean isMob,String extra);
}
