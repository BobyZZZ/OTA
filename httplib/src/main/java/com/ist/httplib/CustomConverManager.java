package com.ist.httplib;

import android.util.Log;

import com.ist.httplib.bean.BaseOtaRespone;
import com.ist.httplib.bean.customer.AppVersion;
import com.ist.httplib.net.CommonSubscribe;
import com.ist.httplib.net.NetErrorMsg;
import com.ist.httplib.net.callback.UpdateListenerAdapter;
import com.ist.httplib.net.retrofit.HttpCreate;
import com.ist.httplib.net.retrofit.HttpServer;
import com.ist.httplib.utils.RxUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhengshaorui
 * Time on 2018/10/17
 */

public class CustomConverManager {
    private static final String TAG = "CustomConverManager";
    private String mUrl;
    private UpdateListenerAdapter mListenerAdapter;
    public static String URL_DOWNLOAD="http://ota-updates.i3sw.i3-technologies.com/store/iip/downloadFullZip.html?zipPath=";
    private HttpServer mHttpServer;
    private static class Holder {
        static final CustomConverManager INSTANCE = new CustomConverManager();
    }

    public static CustomConverManager getInstance() {
        return Holder.INSTANCE;
    }

    private CustomConverManager() {
        mHttpServer = HttpCreate.getService();
    }

    public CustomConverManager config(String url, UpdateListenerAdapter listenerAdapter){
        mUrl = url;
        mListenerAdapter = listenerAdapter;
        return this;
    }



    /*
    * I3服务器规则校验
    * */

    public void I3Check() {
        Log.e("Keven", "I3Check: =="+mHttpServer.getI3Json(mUrl));
        mHttpServer.getI3Json(mUrl)
                .compose(RxUtils.<BaseOtaRespone<AppVersion>>rxScheduers())
                .subscribeWith(new CommonSubscribe<BaseOtaRespone<AppVersion>>(mListenerAdapter) {
                    @Override
                    public void onResponse(BaseOtaRespone<AppVersion> otaRespone) {
                        AppVersion appVersion = otaRespone.appVersion;
                        Log.e("Keven", "onResponse: otaRespone.appVersion.fullPath =="+otaRespone.appVersion.fullPath);
                        OtaLibConfig.getBuilder().setFileDownloadUrl(BuildConfig.DEBUG ? otaRespone.appVersion.fullPath : URL_DOWNLOAD+otaRespone.appVersion.fullPath);
                        Log.e("Keven", "onResponse: otaRespone.statusCode =="+otaRespone.statusCode);
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmm");
                        String buildDate =appVersion.buildDate.substring(0,12);
                        String buildDateLocal="";
                        try{
                            buildDateLocal =  InvokeManager.get("ro.build.date", "201912251430_r1982").substring(0,12);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Log.e("Keven", "onResponse: appVersion.buildDate=="+buildDate);
                        Log.e("Keven", "onResponse: buildDateLocal =="+buildDateLocal);
                        if (otaRespone.statusCode.equals("200")&&Long.parseLong(buildDateLocal)<Long.parseLong(buildDate)) {
                            mListenerAdapter.checkUpdate(true, otaRespone);
                        } else {
                            mListenerAdapter.error(NetErrorMsg.OTHERS, "others");
                        }
                    }
                });
    }
}
