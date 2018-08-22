package com.yutao.netutils.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.yutao.netutils.HttpUtils;
import com.yutao.netutils.NetWorkUtils;
import com.yutao.netutils.R;
import com.yutao.netutils.beans.RequestParams;
import com.yutao.netutils.listener.OnNetworkChangeListener;
import com.yutao.netutils.listener.OnRequestListener;

public class MainActivity extends AppCompatActivity implements OnNetworkChangeListener, OnRequestListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetWorkUtils.getInstance(this).registerNetChangeListener(this);
        HttpUtils.getInstance(this).startNet(
                new RequestParams()
                        .setUrl("http://192.168.0.140:54333/wlsh/user/dmuser/login1")
                        .setTag("tag")
                        .addParams("phone", "name")
                        .addParams("password", "password")
                , this);
    }

    @Override
    public void onChange(boolean isConnect, boolean isWifi, boolean isMob, String extra) {

    }

    @Override
    public void onSuccess(Object tag, String result) {

    }

    @Override
    public void onError(Object tag, int code, String message, String lastResult) {

    }

    @Override
    public void onNetError(Object tag, String message, String lastResult) {

    }
}
