# netUtils
自己封装的一个网络请求框架，基于HttpURLConnection
1、有网络判断功能；
2、有网络监听功能；
3、网络请求失败后能够返回接口的最后一次请求数据；
4、自定义数据判断；
5、支持表单以及json传输；

//注册网络变化监听器
HttpUtils.getInstance(this).registerNetChaneListener(this);
HttpUtils.getInstance(this).setCustomCheckData(new HttpUtils.CustomCheckData() {
            @Override
            public boolean onCheckCustomData(String result,Object tag,String lastResult) {
                if (StringUtils.isNotBlank(result)){
                    JsonObject jsonObject = GsonUtils.fromJson(result, JsonObject.class);
                    int code = -1;
                    String error = null;
                    if (jsonObject.has("code")){
                        if (jsonObject.get("code")!=null
                                &&!jsonObject.get("error").isJsonNull())
                            code = jsonObject.get("code").getAsInt();
                    }
                    if (jsonObject.has("error")){
                        if (jsonObject.get("error")!=null
                                &&!jsonObject.get("error").isJsonNull())
                            error = jsonObject.get("error").getAsString();
                    }
                    if (code>0&&StringUtils.isNotBlank(error)){
                        onErrorOnUI(tag,code,error,lastResult);
                        return false;//表示不通过
                    }else{
                        return true;//表示通过
                    }
                }else{
                    return true;//表示通过
                }
            }
        });
HttpUtils.getInstance(this).startNet(
                new RequestParams()
                        .setUrl("http://192.168.0.140:54333/wlsh/user/dmuser/login1")
                        .setTag("tag")
                        .addParams("phone", "name")
                        .addParams("password", "password")
                , this);
  
