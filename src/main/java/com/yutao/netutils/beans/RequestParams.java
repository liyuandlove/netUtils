package com.yutao.netutils.beans;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static com.yutao.netutils.HttpUtils.TAG;


public class RequestParams {
    private String url;
    private Object tag;
    private JSONObject datas;

    public RequestParams addParams(String key,String value){
        synchronized (RequestParams.class) {
            if (datas == null) {
                datas = new JSONObject();
            }
        }
        try {
            datas.put(key,value);
        } catch (JSONException e) {
            Log.e(TAG, "RequestParams->addParams: ", e);
            e.printStackTrace();
        }
        return this;
    }



    public String getUrl() {
        return url;
    }

    public RequestParams setUrl(String url) {
        this.url = url;
        return this;
    }

    public Object getTag() {
        return tag;
    }

    public RequestParams setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public JSONObject getDatas() {
        return datas;
    }

    public String getRequestDatas(){
//        HashMap hashMap = new HashMap();
//        hashMap.put("params",getDatas()==null?null:getDatas().toString());

//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("params",(getDatas()==null?null:getDatas().toString()));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return
//                jsonObject.toString();
                    "params="+
                            (getDatas()==null?null:getDatas().toString());
//        return hashMap.toString();
    }

    public RequestParams setDatas(JSONObject datas) {
        this.datas = datas;
        return this;
    }
}
