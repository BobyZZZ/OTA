package com.ist.httplib.net;

import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

import com.ist.httplib.CustomConverManager;
import com.ist.httplib.LitepalManager;
import com.ist.httplib.OtaLibConfig;
import com.ist.httplib.bean.BaseOtaRespone;
import com.ist.httplib.bean.DownloadBean;
import com.ist.httplib.bean.customer.AppVersion;
import com.ist.httplib.net.callback.UpdateListener;
import com.ist.httplib.net.callback.UpdateListenerAdapter;
import com.ist.httplib.net.retrofit.HttpCreate;
import com.ist.httplib.net.retrofit.HttpServer;
import com.ist.httplib.utils.RxUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by zhengshaorui
 * Time on 2018/9/12
 */

public class HttpTaskManager {
    public static boolean isDowning = false;

    private static final String TAG = "HttpTaskManager";
    private HttpServer mHttpServer;
    private UpdateListener mUpdateListener;
    private BaseOtaRespone  mOtaBean;
    private OtaLibConfig.Builder mBuilder;

    private static class Holder {
        static final HttpTaskManager INSTANCE = new HttpTaskManager();
    }

    public static HttpTaskManager getInstance() {
        return Holder.INSTANCE;
    }

    private HttpTaskManager() {
        mHttpServer = HttpCreate.getService();
        mBuilder = OtaLibConfig.getBuilder();

    }


    public HttpTaskManager registerListener(UpdateListener listener){
        mUpdateListener = listener;

        return this;
    }

    /**
     * 检测是否升级
     */
    public void  checkUpdate(final UpdateListenerAdapter listenerAdapter){
        CustomConverManager.getInstance()
                .config(mBuilder.getUrl(),listenerAdapter);
        CustomConverManager.getInstance().I3Check();
    }


    /**
     * 开始下载
     */
    public void startDownload() {
        isDowning = true;
        Log.d(TAG, "Keven --> startDownload: "+mBuilder.getUrl());
        mHttpServer.get(mBuilder.getUrl())
                .compose(RxUtils.<BaseOtaRespone>rxScheduers())
                .subscribeWith(new ResourceObserver<BaseOtaRespone>() {
                    @Override
                    public void onNext(BaseOtaRespone otaRespone) {
                        mOtaBean = otaRespone;
                        checkStart();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUpdateListener.error(NetErrorMsg.OTHERS,e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * 检查更新
     */
    private void checkStart() {
        String url = null;
        url = mBuilder.getFileDownloadUrl();
        mHttpServer.getFileLength(url)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        DownloadBean bean = new DownloadBean();
                        bean.fileName = mBuilder.getFileName();
                        //  bean.fileUrl = mOtaBean.fileUrl;
                        bean.threadCount = mBuilder.getThreadCount();
                        bean.fileUrl =  mBuilder.getFileDownloadUrl();
                        bean.filePath = mBuilder.getFilePath();
                        bean.listener = mUpdateListener;
                        bean.fileLength = responseBody.contentLength();
                        //内部存储至少要大于ota包
                        Log.d(TAG, "Keven --> accept: "+ Formatter.formatFileSize(mBuilder.getContext(),bean.fileLength)+
                                "  "+ Formatter.formatFileSize(mBuilder.getContext(),getAvailDiskSize(mBuilder.getFilePath())));
                        if (!LitepalManager.getInstance().isDbExsits()) {
                            if (getAvailDiskSize(mBuilder.getFilePath()) > bean.fileLength) {
                                DownloadTask.getInstance().startDownload(bean);

                            } else {
                                mUpdateListener.error(NetErrorMsg.CACHE_NOT_ENOUGH, "cache not enough");
                            }
                        }else{
                            DownloadTask.getInstance().startDownload(bean);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUpdateListener.error(NetErrorMsg.SERVER_NOT_FOUND,e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public void pause(){
        DownloadTask.getInstance().pauseDownload();
    }



    public boolean isPause(){
        return DownloadTask.getInstance().isPause();
    }

    public  boolean isRunning(){
        return DownloadTask.getInstance().isRunning();
    }


    /**
     * 获取已经存储的大小
     * @return
     */
    private  long getAvailDiskSize(String path){
        StatFs sf = new StatFs(path);
        long blockSize = sf.getBlockSizeLong();
        long availCount = sf.getAvailableBlocksLong();

        return blockSize * availCount;
    }
}
