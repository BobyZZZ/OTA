package com.ist.httplib.mvp.present;

import com.ist.httplib.mvp.base.BasePresent;
import com.ist.httplib.mvp.contract.OtaContract;
import com.ist.httplib.mvp.model.DownloadModel;


/**
 * Created by zhengshaorui
 * Time on 2018/9/26
 */

public class DownloadPresent extends BasePresent<OtaContract.IMainView> {

    private OtaContract.IDownloadView mIView;
    private final DownloadModel mModel;

    public static DownloadPresent create(OtaContract.IDownloadView iView){
        return new DownloadPresent(iView);
    }
    private DownloadPresent(OtaContract.IDownloadView iView){
        mIView = iView;
        mModel = DownloadModel.getInstance().configListener(iView);

    }

    public void startDownload(){
        mModel.startDownload();
    }

    public void pauseDownload(){
        mModel.pauseDownload();
    }

    public void onDestroy(){
        mModel.onDestroy();
    }

    public void deleteDownload(){
        mModel.delete();
    }
    public void reDownload(){
        mModel.reDownload();
    }


    public boolean isPause() {
        return mModel.isPause();
    }

    public boolean isRunning() {
        return mModel.isRunning();
    }
}
