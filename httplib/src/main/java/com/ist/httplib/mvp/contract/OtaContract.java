package com.ist.httplib.mvp.contract;

import com.ist.httplib.bean.BaseOtaRespone;
import com.ist.httplib.mvp.base.IBaseView;
import com.ist.httplib.status.UsbCheckStatus;

import java.io.File;

/**
 * Created by zhengshaorui
 * Time on 2018/9/26
 */

public interface OtaContract {

    /**
     * 这是一个契约类
     * 用来管理 不同view 的接口
     */

    // main
    interface IMainView extends IBaseView{
        void isLocalHasFile(boolean isLocalhasFile, File file);
        void checkUsbFile(UsbCheckStatus usbCheckStatus,File file);
        void checkNetUpdate(boolean isCanUpdate, BaseOtaRespone respone);
    }

    interface IDownloadView extends IBaseView{
        void updateProgress(int progress);
        void updateOtherInfo(long currentSize, long totalSize);
        void downloadSuccess();
    }

}
