package com.ist.httplib;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ist.httplib.net.DownloadTask;
import com.ist.httplib.net.HttpTaskManager;


public class HttpService extends Service {
    private static final String TAG = "HttpServer";

    private MyBinder mMyBinder = new MyBinder();
    private DownloadTask mDownloadTask;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        mDownloadTask = DownloadTask.getInstance();
        return mMyBinder;
    }


    public class MyBinder extends Binder {


        public boolean isPause() {
           // return HttpTaskManager.getInstance().isPause();
           return mDownloadTask.isPause();
        }

        public boolean isRunning() {
            // return HttpTaskManager.getInstance().isPause();
            return mDownloadTask.isRunning();
        }

        public void startDownlaod() {
            HttpTaskManager.getInstance().startDownload();
            if (mDownloadTask != null){
                Log.d(TAG, "zsr --> startDownlaod: "+mDownloadTask.isRunning()+" ");
            }
           // mDownloadTask.startDownload();
        }

        public void pauseDownlaod() {
            HttpTaskManager.getInstance().pause();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "zsr --> onDestroy: ");
    }
}
