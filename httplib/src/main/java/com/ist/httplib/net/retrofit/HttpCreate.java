package com.ist.httplib.net.retrofit;




import android.util.Log;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by zhengshaorui
 * Time on 2018/8/14
 */

public class HttpCreate {

    private static final int TIME_OUT = 300;

    /**
     * 获取retrofit服务
     * @return
     */
    public static HttpServer getService(){
        Log.d("TIME_OUT", "getService TIME_OUT: " + TIME_OUT);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.baidu.com/")
                //转字符串
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .client(getOkhttpClient())
                .build();
        return retrofit.create(HttpServer.class);
    }

    public static OkHttpClient getOkhttpClient(){
        return OkHttpHolder.BUILDER;
    }

    /**
     * 配置okhttp3 client
     */
    private static class OkHttpHolder{
         static OkHttpClient BUILDER = new OkHttpClient.Builder()
                 .retryOnConnectionFailure(true)
                 .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                 .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                 .writeTimeout(TIME_OUT+TIME_OUT,TimeUnit.SECONDS)
                 .build();
    }
}
