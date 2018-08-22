package com.yutao.netutils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.yutao.netutils.listener.OnNetworkChangeListener;
import com.yutao.netutils.receiver.NetworkChangedReceiver;

public class NetWorkUtils {
    private static NetWorkUtils instance;
    private Context mContext;
    private NetworkChangedReceiver networkChangedReceiver = new NetworkChangedReceiver();//网络变化广播
    private boolean isWifi,isMob,isConnect;
    private ConnectivityManager connectivityManager;
    private OnNetworkChangeListener onNetworkChangeListener;
    private Handler mHandler;
    /**
     * 网络变化的监听器，因为在android低版本的时候找不到NetworkCallback类，所以只能用Object
     */
    private Object networkCallback;

    private NetWorkUtils(Context mContext){
        this.mContext = mContext.getApplicationContext();
        connectivityManager = (ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public synchronized static NetWorkUtils getInstance(Context mContext) {
        synchronized (NetWorkUtils.class){
            if (instance == null)
                instance = new NetWorkUtils(mContext);
        }
        return instance;
    }

    /**
     * 注册网络监听器
     * @param onNetworkChangeListener
     */
    public void registerNetChangeListener(OnNetworkChangeListener onNetworkChangeListener){
        this.onNetworkChangeListener = onNetworkChangeListener;
        if (onNetworkChangeListener == null)
            return;
        registerNetChangeReceiver();
    }

    /**
     * 注册网络变化的广播以及监听器
     */
    private void registerNetChangeReceiver(){
        if (mContext==null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            networkCallback = new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (onNetworkChangeListener!=null){
                                onNetworkChangeListener.onChange(isNetConnection(),isWifi(),isMob(),getNetExtraInfo());
                            }
                        }
                    });
                }

                @Override
                public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                }

                @Override
                public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                    super.onLinkPropertiesChanged(network, linkProperties);
                }

                @Override
                public void onLosing(Network network, int maxMsToLive) {
                    super.onLosing(network, maxMsToLive);
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (onNetworkChangeListener!=null){
                                onNetworkChangeListener.onChange(isNetConnection(),isWifi(),isMob(),getNetExtraInfo());
                            }
                        }
                    });
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                }
            };

            if (!isNetConnection()){
                if (onNetworkChangeListener!=null){
                    onNetworkChangeListener.onChange(isNetConnection(),isWifi(),isMob(),getNetExtraInfo());
                }
            }
            connectivityManager.registerDefaultNetworkCallback((ConnectivityManager.NetworkCallback) networkCallback);
        }else {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            intentFilter.addAction("android.net.wifi.STATE_CHANGE");
            mContext.registerReceiver(networkChangedReceiver, intentFilter);
        }
    }

    /**
     * 取消注册网络监听器
     */
    public void unRegisterNetChangeReceiver(){
        if (mContext==null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.unregisterNetworkCallback((ConnectivityManager.NetworkCallback) networkCallback);
        }else{
            mContext.unregisterReceiver(networkChangedReceiver);
        }
    }

    /**
     * 网络是否连接
     * @return
     */
    public boolean isNetConnection(){
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo==null
                ||!networkInfo.isConnected()){
            isConnect = false;
        }else{
            isConnect = true;
        }
        return isConnect;
    }

    /**
     * 是否是wifi
     * @return
     */
    public boolean isWifi() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo==null
                ||!networkInfo.isConnected()){
            isWifi = false;
        }else if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()){
            isWifi = true;
        }else{
            isWifi = false;
        }
        return isWifi;
    }

    /**
     * 是否是数据流量
     * @return
     */
    public boolean isMob() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo==null
                ||!networkInfo.isConnected()){
            isMob = false;
        }else if (ConnectivityManager.TYPE_MOBILE == networkInfo.getType()){
            isMob = true;
        }else{
            isMob = false;
        }
        return isMob;
    }

    /**
     * 获得当前连接的额外信息
     * @return
     */
    public String getNetExtraInfo(){
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo==null
                ||!networkInfo.isConnected()){
            return null;
        }else{
            return networkInfo.getExtraInfo();
        }
    }

    public OnNetworkChangeListener getOnNetworkChangeListener() {
        return onNetworkChangeListener;
    }

}

