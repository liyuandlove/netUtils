package com.yutao.netutils.listener;

/**
 * 请求的监听器
 */
public interface OnRequestListener {
    void onSuccess(Object tag,String result);
    void onError(Object tag,int code,String message,String lastResult);

    /**
     * 网络错误
     * @param tag
     * @param message
     * @param lastResult
     */
    void onNetError(Object tag,String message,String lastResult);
}
