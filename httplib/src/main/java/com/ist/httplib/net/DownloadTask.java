package com.ist.httplib.net;

import android.util.Log;

import com.ist.httplib.LitepalManager;
import com.ist.httplib.OtaLibConfig;
import com.ist.httplib.bean.DownloadBean;
import com.ist.httplib.bean.ThreadBean;
import com.ist.httplib.net.callback.UpdateListener;
import com.ist.httplib.net.retrofit.HttpCreate;
import com.ist.httplib.utils.RxUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by zhengshaorui
 * Time on 2018/9/12
 */

public class DownloadTask {
    private static final String TAG = "DownloadTask";
    private long mFileDownloadSize = 0; //方便显示多线程的进度
    private List<DownloadThread> mDownloadTasks = new ArrayList<>();//方便检测多线程是否下载完成
    private ThreadPoolExecutor mExecutorService = new ThreadPoolExecutor(
            OtaLibConfig.getBuilder().getThreadCount(),
            OtaLibConfig.getBuilder().getThreadCount(),
          6, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>());
    private UpdateListener mListener;
    private volatile boolean isPause = false;
    private volatile boolean isRunning = false;
    private DownloadBean mDownloadBean;




    private static class Holder {
        static final DownloadTask INSTANCE = new DownloadTask();
    }

    public static DownloadTask getInstance() {
        return Holder.INSTANCE;
    }

    private DownloadTask() {

    }

    public void startDownload(DownloadBean downloadBean){
        isRunning = true;
        mDownloadBean = downloadBean;
        mListener = downloadBean.listener;
        mDownloadTasks.clear();
        mFileDownloadSize = 0;
        isPause = false;
        long blocksize = downloadBean.fileLength / downloadBean.threadCount;
        //先看数据库是否已经存在
        if (OtaLibConfig.getBuilder().isUsbDb()) {
            List<ThreadBean> threadBeans = LitepalManager.getInstance().getAllThreadBean();
            if (threadBeans != null && !threadBeans.isEmpty()) {
                //  mFileDownloadSize = 0;
                for (int i = 0; i < threadBeans.size(); i++) {
                    long end = (i + 1) * blocksize - 1;

                    if (i == downloadBean.threadCount - 1) { //最后一个除不尽，用文件长度代替
                        end = downloadBean.fileLength;
                    }
                    ThreadBean bean = threadBeans.get(i);
                    bean.startPos += bean.threadLength;
                    bean.endPos = end;
                    mFileDownloadSize += bean.threadLength;
                    DownloadThread downloadthread = new DownloadThread(bean);
                    mExecutorService.execute(downloadthread);
                    mDownloadTasks.add(downloadthread);
                }
                Log.d(TAG, "zsr --> 有数据库啦: " + mFileDownloadSize * 100f / downloadBean.fileLength);

            } else { //若不存在
                for (int i = 0; i < downloadBean.threadCount; i++) {
                    long start = i * blocksize;
                    long end = (i + 1) * blocksize - 1;

                    if (i == downloadBean.threadCount - 1) { //最后一个除不尽，用文件长度代替
                        end = downloadBean.fileLength;
                    }
                    ThreadBean bean = new ThreadBean();
                    bean.url = downloadBean.fileUrl;
                    bean.startPos = start;
                    bean.endPos = end;
                    bean.fileLength = downloadBean.fileLength;
                    bean.theadId = i;
                    bean.name = downloadBean.fileName;
                    //先保存数据库
                    LitepalManager.getInstance().saveOrUpdate(bean);
                    DownloadThread downloadThread = new DownloadThread(bean);
                    mExecutorService.execute(downloadThread);
                    mDownloadTasks.add(downloadThread);
                }
            }
        }else{
            mFileDownloadSize = 0;
            for (int i = 0; i < downloadBean.threadCount; i++) {
                long start = i * blocksize;
                long end = (i + 1) * blocksize - 1;

                if (i == downloadBean.threadCount - 1) { //最后一个除不尽，用文件长度代替
                    end = downloadBean.fileLength;
                }
                ThreadBean bean = new ThreadBean();
                bean.url = downloadBean.fileUrl;
                bean.startPos = start;
                bean.endPos = end;
                bean.fileLength = downloadBean.fileLength;
                bean.theadId = i;
                bean.name = downloadBean.fileName;
                DownloadThread downloadThread = new DownloadThread(bean);
                mExecutorService.execute(downloadThread);
                mDownloadTasks.add(downloadThread);
            }
        }


    }



    public void pauseDownload(){
        isPause = true;
    }

    public boolean isPause(){
        return isPause;
    }
    public boolean isRunning(){
        return isRunning;
    }


    public void isThreadRunning(){

    }


    /**
     * 实际下载类
     */
    class DownloadThread extends Thread{

        boolean isTheadFinished = false;
        ThreadBean bean;
        public DownloadThread(ThreadBean bean){
            this.bean = bean;
        }

        @Override
        public void run() {
            super.run();
            InputStream is = null;
            RandomAccessFile raf = null;
            try {
                Call<ResponseBody> call = HttpCreate.getService().download(bean.url,"bytes=" + bean.startPos + "-" + bean.endPos);
                Response<ResponseBody> response = call.execute();
                if (response != null && response.isSuccessful()){
                    is = response.body().byteStream();
                    //设置本地的存储
                    File file = new File(mDownloadBean.filePath,mDownloadBean.fileName);
                    raf = new RandomAccessFile(file, "rwd");
                    raf.seek(bean.startPos);
                    byte[] bytes = new byte[4 * 1024];
                    int len;
                    while ((len = is.read(bytes)) != -1) {
                        raf.write(bytes, 0, len);
                        mFileDownloadSize += len;

                        final int progress = (int) (mFileDownloadSize * 100.0f / bean.fileLength);
                        mListener.doProgress(progress,mFileDownloadSize,bean.fileLength);

                        //记录每个线程的结束点的值
                        bean.threadLength += len;
                        if (isPause){
                            //保存到数据库
                            isRunning = false;
                            if (OtaLibConfig.getBuilder().isUsbDb()) {
                                LitepalManager.getInstance().saveOrUpdate(bean);
                            }
                            return;
                        }


                    }
                    isTheadFinished = true;
                }else{
                    RxUtils.deleteDbFile();
                    mFileDownloadSize = 0;
                    mListener.error(NetErrorMsg.RESPONSE_IS_NULL,"response.body() == null");
                }
                isRunning = true;
                checkFinish(bean.fileLength);

            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //不保存啦
                RxUtils.deleteDbFile();
                String errorMsg = e.toString();
                isRunning = false;
                isPause = true;
                if (errorMsg.contains("Connection timed out")){
                    mListener.error(NetErrorMsg.TIME_OUT,e.toString());
                }else {
                    mListener.error(NetErrorMsg.OTHERS, e.toString());
                }

            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private synchronized void checkFinish(long fileLength){
        boolean isFinish = true;
        for (DownloadThread downloadThread : mDownloadTasks) {
            if (!downloadThread.isTheadFinished){
                isFinish = false;
                break;
            }
        }
        if (isFinish){

            mFileDownloadSize = 0;
            isRunning = false;
            File file = new File(mDownloadBean.filePath,mDownloadBean.fileName);
            if (file.exists()){
                if (file.length() == fileLength){
                    mListener.complete(null);
                    //删除线程
                    Log.d(TAG, "zsr --> 全部完成啦");
                    if (OtaLibConfig.getBuilder().isUsbDb()) {
                        LitepalManager.getInstance().deleteall();
                    }
                }else{
                    mListener.error(NetErrorMsg.FILE_LENGTH_NOT_SAME,"file length not same");
                }
            }


        }
    }

}
