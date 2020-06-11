package com.ist.httplib;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.litepal.LitePal;

/**
 * Created by zhengshaorui
 * Time on 2018/9/26
 */

public class OtaLibConfig {
    public static final String ACTION_DOWNLOAD_MSG = "action_download_msg";

    private static final String TAG = "OtaLibConfig";
    //公版
    public static final String TEST = "IST_110";
    //创维
    public static final String SKYWORKTH = "IST_12";
    //康佳
    public static final String KANGJIA = "IST_03";
    //v811
    public static final String V811 = "IST_01";
    public static final String V811_KANGJIA = "IST_11";
    public static final String V848_KANGJIA = "IST_16";

    public static final String V848_I3_848 = "IST_25";


    private static Builder sBuilder;

    private OtaLibConfig(Builder builder) {
        sBuilder = builder;
        LitePal.initialize(sBuilder.getContext());
    }

    public static boolean isDebug() {
        return sBuilder.isDebug();
    }

    public static Builder getBuilder() {
        return sBuilder;
    }

    public static Builder config() {
        return new Builder();
    }

    public static Context getContext() {
        return sBuilder.getContext();
    }

    public static Handler getHandler() {
        return sBuilder.getHandler();
    }

    public static long getSaveTime() {
        return sBuilder.saveTime;
    }

    public static class Builder {
        Context context;
        Handler handler;
        String url;
        String filePath;
        String fileName;
        int threadCount;
        int updateTime;
        String customerId;
        String fileDownloadUrl;
        boolean isDebug;
        long saveTime;
        boolean usbDb;

        public Builder setCustomerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setHandler(Handler handler) {
            this.handler = handler;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setThreadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }

        public Builder setUpdateTime(int updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Builder setDebug(boolean debug) {
            isDebug = debug;
            Log.d(TAG, "zsr --> setDebug: " + isDebug());
            return this;
        }

        public Builder setUsbDb(boolean usbDb) {
            this.usbDb = usbDb;
            return this;
        }

        public Builder setSaveTime(long saveTime) {
            this.saveTime = saveTime;
            return this;
        }

        public OtaLibConfig builder() {
            return new OtaLibConfig(this);
        }

        public String getUrl() {
            return url;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getFileName() {
            return fileName;
        }

        public int getThreadCount() {
            return threadCount;
        }

        public int getUpdateTime() {
            return updateTime;
        }

        public Context getContext() {
            return context;
        }

        public Handler getHandler() {
            return handler;
        }

        public String getCustomerId() {
            return customerId;
        }

        public boolean isDebug() {
            return isDebug;
        }

        public String getFileDownloadUrl() {
            return fileDownloadUrl;
        }

        public void setFileDownloadUrl(String fileDownloadUrl) {
            this.fileDownloadUrl = fileDownloadUrl;
        }

        public long getSaveTime() {
            return saveTime;
        }

        public boolean isUsbDb() {
            return usbDb;
        }
    }

}
