package com.ist.otaservice;


import android.util.Log;

import com.ist.android.tv.IstBoardInfo;
import com.ist.httplib.InvokeManager;

/**
 * Created by zhengshaorui
 * Time on 2018/9/13
 */

public class CustomerConfig {
    private static final String TAG = "CustomerConfig";
    public static boolean ISDEBUG = false;
    //http://ota-updates.i3sw.i3-technologies.com/store/ws/update/getUpdateInfo/PX86_0/2020:02:28-00:00
    private static final String URL = "http://ota-updates.i3sw.i3-technologies.com/store/ws/update/getUpdateInfo/";
    //是否启动断点续传，即数据库保存下载进度
    public static boolean USE_DB = true;

    /**
     * url = "http://ota-updates.i3sw.i3-technologies.com/store/ws/update/getUpdateInfo/" + ModelName + "/";
     * @return
     */
    public static String getBaseUrl() {
        if (BuildConfig.DEBUG) {
            Log.e("Keven", "getBaseUrl: url ==" + BuildConfig.URL_FOR_CHECK_UPDATABLE);
            return BuildConfig.URL_FOR_CHECK_UPDATABLE;//debug包用于测试
        }
        String url = "";
        //url = InvokeManager.get("persist.sys.ota_url","");
        String time = InvokeManager.get("ro.build.date", "201912251430_r1982");
        String timeYear = time.substring(0, 4);
        String timeMonth = time.substring(4, 6);
        String timeDate = time.substring(6, 8);
        String timeHour = time.substring(8, 10);
        String timeMintue = time.substring(10, 12);
//        url = URL+timeYear+":"+timeMonth+":"+timeDate+"-"+timeHour+":"+timeMintue;
        url = BuildConfig.URL_FOR_CHECK_UPDATABLE + getModelName() + "/" + timeYear + ":" + timeMonth + ":" + timeDate + "-" + timeHour + ":" + timeMintue;
        //yyyy:MM:dd-hh:mm
        Log.e("Keven", "getBaseUrl: url ==" + url);
        return url;
    }

    private static String getModelName() {
        return IstBoardInfo.getInstance().getTerminalModel("i3");
    }
}


