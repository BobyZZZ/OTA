package com.ist.httplib.mvp.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.ist.httplib.HttpService;
import com.ist.httplib.LitepalManager;
import com.ist.httplib.OtaLibConfig;
import com.ist.httplib.bean.BaseOtaRespone;
import com.ist.httplib.mvp.contract.OtaContract;
import com.ist.httplib.net.HttpTaskManager;
import com.ist.httplib.net.NetErrorMsg;
import com.ist.httplib.net.callback.UpdateListener;
import com.ist.httplib.utils.RxUtils;

/**
 * Created by zhengshaorui
 * Time on 2018/9/26
 */

public class DownloadModel implements UpdateListener {
    private static final String TAG = "DownloadModel";
    private OtaLibConfig.Builder mBuilder;
    private HttpService.MyBinder mBinder;
    private OtaContract.IDownloadView mIView;

    /**
     * logic
     */
    private long mProgressTime;
    private long mDownloadTime;



    private static class Holder {
        static final DownloadModel INSTANCE = new DownloadModel();
    }

    public static DownloadModel getInstance() {
        Log.d(TAG, "getInstance: ");
//        return Holder.INSTANCE;
        return new DownloadModel();
    }

    private DownloadModel() {
        mBuilder = OtaLibConfig.getBuilder();
        Intent intent = new Intent(mBuilder.getContext(), HttpService.class);
        mBuilder.getContext().bindService(intent,mServiceConnection, Context.BIND_AUTO_CREATE);
        HttpTaskManager.getInstance().registerListener(this);
    }
    public DownloadModel configListener(OtaContract.IDownloadView iView){
        mIView =iView;
        return this;
    }


    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (HttpService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
        }
    };

    public void startDownload() {
        //绑定服务需要时间
        mBuilder.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBinder != null){
                    mBinder.startDownlaod();
                }
            }
        },150);
        
    }

    public void pauseDownload() {
        if (mBinder != null){
            mBinder.pauseDownlaod();
        }else{
            Log.d(TAG, "zsr --> mBuilder is null");
        }
    }

    public void reDownload(){
        if (mBinder != null){
            mBinder.pauseDownlaod();
            RxUtils.deleteDbFile();
            mBuilder.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBinder.startDownlaod();
                }
            },150);
        }else{
            Log.d(TAG, "zsr --> mBuilder is null");
        }
    }

    public void delete(){
        mBinder.pauseDownlaod();
        if (OtaLibConfig.getBuilder().isUsbDb()) {
            LitepalManager.getInstance().deleteall();
        }
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        mBuilder.getContext().unbindService(mServiceConnection);
    }

    @Override
    public void checkUpdate(boolean isCanUpdate, BaseOtaRespone respone) {

    }

    @Override
    public void complete(String fileName) {
        mBuilder.getHandler().post(new Runnable() {
            @Override
            public void run() {
                mIView.downloadSuccess();
            }
        });

    }

    @Override
    public void doProgress(final int progress,final long currentSize,final long totalSize) {
        if (System.currentTimeMillis() - mProgressTime > mBuilder.getUpdateTime()){
            mBuilder.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    mIView.updateProgress(progress);

                }
            });

            mProgressTime = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - mDownloadTime > 1000 ){
            mBuilder.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    mIView.updateOtherInfo(currentSize,totalSize);
                }
            });
            mDownloadTime = System.currentTimeMillis();
        }
    }

    @Override
    public void error(final NetErrorMsg status,final String errorMsg) {
        mBuilder.getHandler().post(new Runnable() {
            @Override
            public void run() {
                mIView.error(status,errorMsg);
            }
        });

    }


    public boolean isPause() {
        return mBinder != null && mBinder.isPause();
    }

    public boolean isRunning() {
        return mBinder != null && mBinder.isRunning();
    }
}
