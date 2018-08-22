package com.yutao.netutils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 缓存地址，保留对应url的最后一次请求数据
 */
public class CacheUtils {
    private Context mContext;
    private static CacheUtils instance;

    public synchronized static CacheUtils getInstance(Context mContext) {
        synchronized (CacheUtils.class){
            if (instance==null)
                instance = new CacheUtils(mContext);
        }
        return instance;
    }

    private CacheUtils(Context mContext){
        init(mContext);
    }

    private void init(Context mContext){
        if (mContext == null)
            return;
        this.mContext = mContext.getApplicationContext();
    }

    public void saveDatas(String tag,String datas){
        if (mContext == null||tag == null)
            return;
        tag = formatTag(tag);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(tag,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("data",datas);
        editor.apply();
    }

    public String getDataFromSP(String tag){
        if (mContext==null)
            return null;
        tag = formatTag(tag);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(tag,Context.MODE_PRIVATE);
        String lastResult = sharedPreferences.getString("data",null);
        return lastResult;
    }

    /**
     * 格式化tag
     * @param tag
     * @return
     */
    private String formatTag(String tag){
        if (tag==null)
            return null;
        String formatTag =  tag.replaceAll("\\/","")
                .replaceAll("\\//","")
                .replaceAll(".","");
        return formatTag;
    }
}
