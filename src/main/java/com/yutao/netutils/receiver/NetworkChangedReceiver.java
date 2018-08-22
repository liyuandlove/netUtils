package com.yutao.netutils.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.yutao.netutils.NetWorkUtils;

/**
 * 监听网络状态的receiver
 */
public class NetworkChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//
//        if (activeNetworkInfo!=null
//                &&activeNetworkInfo.isConnected()){
//            Toast.makeText(context,"网络连接-广播",Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(context,"网络断开-广播",Toast.LENGTH_SHORT).show();
//        }

        if (NetWorkUtils.getInstance(context).getOnNetworkChangeListener()!=null){
            NetWorkUtils.getInstance(context).getOnNetworkChangeListener().onChange(
                    NetWorkUtils.getInstance(context).isNetConnection()
                    ,NetWorkUtils.getInstance(context).isWifi()
                    ,NetWorkUtils.getInstance(context).isMob()
                    ,NetWorkUtils.getInstance(context).getNetExtraInfo()
            );
        }
    }
}
