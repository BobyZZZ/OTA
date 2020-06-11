package com.ist.otaservice;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.ist.httplib.OtaLibConfig;

import org.litepal.LitePal;

import okhttp3.OkHttpClient;

/**
 * Created by zhengshaorui
 * Time on 2018/9/12
 */

public class MainApplication extends Application {
    public static Context CONTEXT;
    public static Handler HANDLER = new Handler(Looper.getMainLooper());
    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;
        LitePal.initialize(this);

        //查看数据库
        Stetho.initializeWithDefaults(this);
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        //配置 ota 属性
        OtaLibConfig.config()
                .setContext(CONTEXT)
                .setHandler(HANDLER)
                .setUrl(CustomerConfig.getBaseUrl())
                .setFilePath(Constant.SAVE_PATH)
                .setFileName(Constant.FILE_NAME)
                .setThreadCount(Constant.THREAD_COUNT)
                .setUpdateTime(200)
                .setDebug(CustomerConfig.ISDEBUG)
                .setUsbDb(CustomerConfig.USE_DB)
                .builder();
    }
}
