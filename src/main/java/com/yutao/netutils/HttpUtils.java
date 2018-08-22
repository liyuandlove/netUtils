package com.yutao.netutils;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.yutao.netutils.beans.RequestParams;
import com.yutao.netutils.listener.OnNetworkChangeListener;
import com.yutao.netutils.listener.OnRequestListener;
import com.yutao.netutils.receiver.NetworkChangedReceiver;

import java.util.concurrent.Future;

public class HttpUtils implements OnNetworkChangeListener {
    public static final String TAG = "HttpUtils";

    private static HttpUtils instance;

    public Context mContext;
    //必要的权限
    private String[] permissionNeed = new String[]{
            Manifest.permission.INTERNET
            ,Manifest.permission.ACCESS_NETWORK_STATE
    };

    private boolean isJsonHttp = true;//是否是通过json传后台数据的，默认为true
    private Handler mHandler;
    private CustomCheckData customCheckData;//自定义检查数据是否符合的类

    public synchronized static void init(Context mContext){
        if (mContext == null)
            return;
        synchronized (HttpUtils.class) {
            if (instance==null) {
                instance = new HttpUtils(mContext.getApplicationContext());
            }
        }
    }

    public static HttpUtils getInstance(Context mContext) {
        init(mContext);
        return instance;
    }

    public HttpUtils(Context mContext) {
        this.mContext = mContext;
        mHandler = new Handler(Looper.getMainLooper());
        CacheUtils.getInstance(mContext);

        String permissionResult = checkPermission(mContext);//检查必要的权限
        NetWorkUtils.getInstance(mContext).registerNetChangeListener(this);//注册网络变化监听器
    }

    /**
     * 注册网络变化监听器
     * @param onNetworkChangeListener
     */
    public void registerNetChaneListener(OnNetworkChangeListener onNetworkChangeListener){
        if (mContext == null)
            return;
        NetWorkUtils.getInstance(mContext).registerNetChangeListener(onNetworkChangeListener);
    }

    /**
     * 检查必要的权限
     */
    public String checkPermission(Context mContext){
        if (mContext==null)
            return "上下文为空";
        for (String permission : permissionNeed) {
            if ( ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED ){
                return "权限 "+permission+" 缺失";
            }
        }
        return null;
    }

    /**
     * 开始请求
     * @param requestParams
     */
    public void startNet(RequestParams requestParams,OnRequestListener onRequestListener){
        startNet(requestParams,null,onRequestListener);
    }

    /**
     * 开始请求 //TODO 在开始请求前应该加上网络判断,并且加上网络改变的监听器
     * @param requestParams
     * @param threadPoolTag
     * @param onRequestListener
     */
    public void startNet(RequestParams requestParams,String threadPoolTag,OnRequestListener onRequestListener){
        GetDataThread getDataThread = new GetDataThread(requestParams,onRequestListener);
        Future future = ThreadPoolUtils.getInstance().addThread(getDataThread,threadPoolTag,onRequestListener);
        getDataThread.setFuture(future);
    }

    @Override
    public void onChange(boolean isConnect, boolean isWifi, boolean isMob, String extra) {
        //默认网络变化什么都不做
    }

    /**
     * 设置自定义检查数据的类
     * @param customCheckData
     * @return
     */
    public HttpUtils setCustomCheckData(CustomCheckData customCheckData) {
        this.customCheckData = customCheckData;
        return this;
    }

    /**
     * 自定义检查数据的类
     */
    public abstract static class CustomCheckData{
        private OnRequestListener onRequestListener;
        private Handler mHandler;
        public CustomCheckData(){
            mHandler = new Handler(Looper.getMainLooper());
        }
        /**
         * 检查自定义数据，比如接口请求成功后，但是返回的却是错误，注意，这段代码运行在子线程中
         * @return
         */
        public abstract boolean onCheckCustomData(String result,Object tag,String lastResult);

        private CustomCheckData setOnRequestListener(OnRequestListener onRequestListener) {
            this.onRequestListener = onRequestListener;
            return this;
        }

        /**
         * 主动调用成功回调
         * @param result
         */
        public void onSuccessOnUI(final Object tag,final String result){
            if (mHandler!=null)
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (onRequestListener!=null)
                            onRequestListener.onSuccess(tag,result);
                    }
                });
        }

        /**
         * 主动调用错误回调
         * @param code
         * @param message
         */
        public void onErrorOnUI(final Object tag, final int code, final String message,final String lastResult){
            if (mHandler!=null)
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (onRequestListener!=null)
                            onRequestListener.onError(tag,code,message,lastResult);
                    }
                });
        }
    }
    /**
     * 获取资源的线程
     */
    public class GetDataThread extends Thread{
        protected RequestParams requestParams;
        protected Future future;
        protected OnRequestListener onRequestListener;
        protected String result,lastResult;

        public GetDataThread(RequestParams requestParams,OnRequestListener onRequestListener) {
            this.requestParams = requestParams;
            this.onRequestListener = onRequestListener;
        }

        @Override
        public void run() {
            try {
                lastResult = CacheUtils.getInstance(mContext).getDataFromSP(requestParams.getUrl());
                if (!NetWorkUtils.getInstance(mContext).isNetConnection()
                        &&onRequestListener!=null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onRequestListener.onNetError(requestParams.getTag(), "网络未连接", lastResult);
                        }
                    });
                    return;
                }
                result = RequestUtils.sendStrDataByPost(requestParams.getUrl()
                        ,requestParams.getDatas()==null?null:requestParams.getDatas().toString()
                        ,isJsonHttp);

                CacheUtils.getInstance(mContext).saveDatas(requestParams.getUrl(),result);

                if (customCheckData!=null
                        &&!customCheckData
                        .setOnRequestListener(onRequestListener)
                        .onCheckCustomData(result,requestParams.getTag(),lastResult)){
                    return;
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (onRequestListener!=null)
                            onRequestListener.onSuccess(requestParams.getTag(),result);
                    }
                });
            } catch (final Exception e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (onRequestListener != null) {
                            if (e instanceof RequestUtils.NetUtilsRequestException) {
                                onRequestListener.onError(requestParams.getTag()
                                        , ((RequestUtils.NetUtilsRequestException) e).getCode()
                                        , e.getMessage()
                                        , lastResult);
                            } else {
                                onRequestListener.onError(requestParams.getTag()
                                        , -1
                                        , e.getMessage()
                                        , lastResult);
                            }
                        }
                    }
                });
                e.printStackTrace();
            }
        }

        public Future getFuture() {
            return future;
        }

        public void setFuture(Future future) {
            this.future = future;
        }

        public RequestParams getRequestParams() {
            return requestParams;
        }

        public void setRequestParams(RequestParams requestParams) {
            this.requestParams = requestParams;
        }
    }

    public boolean isJsonHttp() {
        return isJsonHttp;
    }

    public void setJsonHttp(boolean jsonHttp) {
        isJsonHttp = jsonHttp;
    }
}
