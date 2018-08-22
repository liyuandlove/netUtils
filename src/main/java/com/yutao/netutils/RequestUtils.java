package com.yutao.netutils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.yutao.netutils.HttpUtils.TAG;

/**
 * 请求工具类
 */
public class RequestUtils {

    public static int connetcTimeOut = 2000;//连接超时时间
    public static int readTimeOut = 2000;//读取超时时间

    public static String sendStrDataByPost(String urlStr,String data,boolean isJson) throws Exception {
        URL url;
        String result = null;//要返回的结果
        OutputStream os = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try{
            url = new URL(urlStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(connetcTimeOut);
            httpURLConnection.setReadTimeout(readTimeOut);
            //设置是否向httpURLConnection输出，因为post请求参数要放在http正文内，所以要设置为true
            httpURLConnection.setDoOutput(true);
            //设置是否从httpURLConnection读入，默认是false
            httpURLConnection.setDoInput(true);

            //是否用json传数据的框架
            if (isJson) {
                httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                httpURLConnection.setRequestProperty("Accept", "application/json");
            }else{//表单方式传数据
                httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            }
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            httpURLConnection.setRequestProperty("Charset", "UTF-8");

            httpURLConnection.setRequestMethod("POST");

            httpURLConnection.setDefaultUseCaches(false);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setAllowUserInteraction(true);
            httpURLConnection.addRequestProperty("device","android");

            httpURLConnection.connect();

            os = httpURLConnection.getOutputStream();
            if (data!=null) {
                os.write(data.getBytes("utf-8"));
                Log.d(TAG, "sendStrDataByPost: put-->"+data);
            }
            os.flush();
            os.close();

            if (HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode()){

                is = httpURLConnection.getInputStream();

                baos = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int len;
                while (-1 != (len = is.read(buffer))){
                    baos.write(buffer,0,len);
                }

                result = baos.toString("UTF-8");

                Log.d(TAG, "sendDataByPost: get-->"+result+"\n"+httpURLConnection.getContentLength());

                baos.flush();
                baos.close();
                is.close();
            }else{
                Log.e(TAG, "sendStrDataByPost: 连接失败 "+httpURLConnection.getResponseCode());
                String errorMessage = "连接失败 "+httpURLConnection.getResponseCode();
                throw  new NetUtilsRequestException(httpURLConnection.getResponseCode(),errorMessage);
            }

        } catch (Exception e) {
            Log.e(TAG, "sendStrDataByPost: ", e);
            throw e;
        } finally {
            if (os!=null)
                os.close();
            if (is!=null)
                is.close();
            if (baos!=null)
                baos.close();
        }
        return result;
    }

    public static class NetUtilsRequestException extends Exception{
        private int code;
        public NetUtilsRequestException(int code,String message){
            super(message);
            this.code =code;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}
